/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import forklift.ShadowRead;
import org.springframework.samples.petclinic.ConsistencyChecker;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
    private final OwnerRepository owners;


    public OwnerController(OwnerRepository clinicService) {
        this.owners = clinicService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @GetMapping("/owners/new")
    public String initCreationForm(Map<String, Object> model) {
        Owner owner = new Owner();
        model.put("owner", owner);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/owners/new")
    public String processCreationForm(@Valid Owner owner, BindingResult result) throws SQLException {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            ConsistencyChecker consistencyChecker = ConsistencyChecker.getInstance();
            if (!consistencyChecker.getWrite()){
            this.owners.save(owner);
            return "redirect:/owners/" + owner.getId();}
            else {
                this.owners.save(owner);
                consistencyChecker.resetCounter();
                System.out.println(consistencyChecker.checkConsistency());
                return "redirect:/owners/" + owner.getId();
            }
        }
    }

    @GetMapping("/owners/find")
    public String initFindForm(Map<String, Object> model) {
        model.put("owner", new Owner());
        return "owners/findOwners";
    }

    @GetMapping("/owners")
    public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

        // allow parameterless GET request for /owners to return all records
        if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies broadest possible search
        }

        // find owners by last name
        Collection<Owner> results;
        ConsistencyChecker cc = ConsistencyChecker.getInstance();
        if (!cc.getSwapped()) {
            results = this.owners.findByLastName(owner.getLastName());
        }
        else {
            results = ShadowRead.findOwnersByLastName(owner.getLastName());
        }
        if (!cc.getSwapped() && cc.getRead()) {
            // use Timer to delay consistency checking to simulate asynchronous execution
            Collection<Owner> shadowResults = ShadowRead.findOwnersByLastName(owner.getLastName());
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Read inconsistency for two collections: "+cc.readConsistencyChecking(results, shadowResults));
                }
            }, 3000);
        }

        if (results.isEmpty()) {
            // no owners found
            result.rejectValue("lastName", "notFound", "not found");
            return "owners/findOwners";
        } else if (results.size() == 1) {
            // 1 owner found
            owner = results.iterator().next();
            return "redirect:/owners/" + owner.getId();
        } else {
            // multiple owners found
            model.put("selections", results);
            return "owners/ownersList";
        }
    }

    @GetMapping("/owners/{ownerId}/edit")
    public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
        Owner owner;
        ConsistencyChecker cc = ConsistencyChecker.getInstance();
        if(!cc.getSwapped()) {
            owner = this.owners.findById(ownerId);
        }
        else {
            owner = ShadowRead.findOwnerByID(ownerId);
        }
        if (!cc.getSwapped() && cc.getRead()) {
            // use Timer to delay consistency checking to simulate asynchronous execution
            Owner shadowOwner = ShadowRead.findOwnerByID(ownerId);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Read inconsistency for two objects: "+cc.readConsistencyChecking(owner,shadowOwner));
                }
            }, 3000);

        }

        model.addAttribute(owner);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/owners/{ownerId}/edit")
    public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, @PathVariable("ownerId") int ownerId) throws SQLException{
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            ConsistencyChecker consistencyChecker = ConsistencyChecker.getInstance();
            if (!consistencyChecker.getWrite()){
            owner.setId(ownerId);
            this.owners.save(owner);
            return "redirect:/owners/{ownerId}";}
            else{
                owner.setId(ownerId);
                this.owners.save(owner);
                consistencyChecker.resetCounter();
                System.out.println(consistencyChecker.checkConsistency());
                return "redirect:/owners/{ownerId}";
            }
        }
    }

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return a ModelMap with the model attributes for the view
     */
    @GetMapping("/owners/{ownerId}")
    public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
        ModelAndView mav = new ModelAndView("owners/ownerDetails");
        mav.addObject(this.owners.findById(ownerId));
        return mav;
    }

}
