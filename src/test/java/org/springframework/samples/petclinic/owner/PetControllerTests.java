package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetController;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeFormatter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = PetController.class,
    includeFilters = @ComponentScan.Filter(
                            value = PetTypeFormatter.class,
                            type = FilterType.ASSIGNABLE_TYPE))
public class PetControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository pets;

    @MockBean
    private OwnerRepository owners;

    @Before
    public void setup() {
        PetType cat = new PetType();
        cat.setId(3);
        cat.setName("hamster");
        given(this.pets.findPetTypes()).willReturn(Lists.newArrayList(cat));
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(new Owner());
        given(this.pets.findById(TEST_PET_ID)).willReturn(new Pet());
    }

    @Test
    public void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"))
            .andExpect(model().attributeExists("pet"));
    }

    @Test
    public void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Betty")
            .param("type", "hamster")
            .param("birthDate", "2015-02-12")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }
    
    @Test
    public void testProcessCreationFormDuplicate_Error() throws Exception {
        // Make a dummy owner
    	Owner dummyOwner = new Owner();
    	
    	// Make a dummy pet
    	Pet dummyPet = new Pet();
    	dummyPet.setName("Betty");
    	
    	dummyOwner.addPet(dummyPet);
    	dummyPet.setId(33);
    	
    	given(this.owners.findById(TEST_OWNER_ID)).willReturn(dummyOwner);
    	
    	mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Betty")
            .param("type", "hamster")
            .param("birthDate", "2015-02-12")
        )
    		.andExpect(model().attributeHasErrors("pet"))
    		.andExpect(model().attributeHasFieldErrorCode("pet","name","duplicate"));
//            .andExpect(status().is3xxRedirection())
//            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }


    @Test
    public void testProcessCreationFormDuplicate_Error_Statement() throws Exception {
        // Make a dummy owner
        Owner dummyOwner = new Owner();

        // Make a dummy pet
        Pet dummyPet = new Pet();
        dummyPet.setName("Betty");

        dummyOwner.addPet(dummyPet);
        dummyPet.setId(33);

        given(this.owners.findById(TEST_OWNER_ID)).willReturn(dummyOwner);

        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Betty")
            .param("type", "hamster")
            .param("birthDate", "2015-02-12")
            .param("id","45")
        )
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrorCode("pet","name","duplicate"));
//            .andExpect(status().is3xxRedirection())
//            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    public void CreatePetWithNoType_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Betty")
            .param("birthDate", "2015-02-12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoName_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("type", "hamster")
            .param("birthDate", "2015-02-12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoInfo_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoBirthday_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "petName")
            .param("type", "hamster")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoNameOrType_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("birthDate", "2015-02-12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoBirthdayOrName_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("type", "hamster")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void CreatePetWithNoTypeOrBirthday_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "hamster")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void testInitUpdateForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("pet"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "Betty")
            .param("type", "hamster")
            .param("birthDate", "2015-02-12")
            .param("id", "1")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    public void updatePetWithNoType_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "Betty")
            .param("birthDate", "2015/02/12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void updatePetWithNoNameOrType_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("birthDate", "2015/02/12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void updatePetWithNoNameOrBirthday_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("type", "hamster")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void updatePetWithNoTypeOrBirthday_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "betty")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "type"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void updatePetWithNoName_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("type", "hamster")
            .param("birthDate", "2015/02/12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    public void updatePetWithNoBirthday_Expect_Error() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "betty")
            .param("type", "hamster")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "required"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

}
