package org.springframework.samples.petclinic;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;
import org.springframework.samples.petclinic.visit.Visit;

import java.util.Collection;


public class ConsistencyChecker {
    private static ConsistencyChecker consistencyChecker = new ConsistencyChecker();
    private int inconsistency = 0;
    private VetRepository vets;

    public static ConsistencyChecker getInstance() {
        return consistencyChecker;
    }

    private ConsistencyChecker() {
    }

    public void checkVets(){
        Collection<Vet> vetRepo = vets.findAll();
        Vet actual;
        Vet expected;

        for (Vet v : vetRepo) {
            expected= v;
            actual= null;
            if (!expected.equals(actual)){
                inconsistency++;
                violation(expected.toString(), actual.toString());
            }
        }
    }

    public void checkSpecialties(){
        Specialty expected;
        Specialty actual;

        if (!expected.equals(actual)){
            inconsistency++;
            violation(expected.toString(), actual.toString());
        }

    }

    public void checkVetSpecialties(){

    }

    public void checkTypes(){
        PetType expected;
        PetType actual;

        if (!expected.equals(actual)){
            inconsistency++;
            violation(expected.toString(), actual.toString());
        }
    }

    public void checkOwners(){
        Owner expected;
        Owner actual;

        if (!expected.equals(actual)){
            inconsistency++;
            violation(expected.toString(), actual.toString());
        }
    }

    public void checkPets(){
        Pet expected;
        Pet actual;

        if (!expected.equals(actual)){
            inconsistency++;
            violation(expected.toString(), actual.toString());
        }
    }

    public void checkVisits(){
        Visit expected;
        Visit actual;

        if (!expected.equals(actual)){
            inconsistency++;
            violation(expected.toString(), actual.toString());
        }
    }

    public int checkConsistency(){
        this.checkVets();
        this.checkSpecialties();
        this.checkVetSpecialties();
        this.checkTypes();
        this.checkOwners();
        this.checkPets();
        this.checkVisits();
        return inconsistency;
    }

    private void violation(String expected, String actual) {
        System.out.println("Consistency Violation!\n" +
            "\n\t expected = " + expected
            + "\n\t actual = " + actual);
    }
}
