package org.springframework.samples.petclinic;

import forklift.OwnerCRUD;
import forklift.PetsCRUD;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;
import org.springframework.samples.petclinic.visit.Visit;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class ConsistencyChecker {
    private static ConsistencyChecker consistencyChecker = new ConsistencyChecker();
    private int inconsistency = 0;


    public static ConsistencyChecker getInstance() {
        return consistencyChecker;
    }

    private ConsistencyChecker() {
    }

    public void checkOwners() throws SQLException{
        Owner expected = new Owner();
        Owner actual;
        OwnerCRUD ownerCRUD= new OwnerCRUD();

        ResultSet owners;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        owners = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.owners");

        while (owners.next()){
            expected.setId(owners.getInt("id"));
            expected.setFirstName(owners.getString("first_name"));
            expected.setLastName(owners.getString("last_name"));
            expected.setAddress(owners.getString("address"));
            expected.setCity(owners.getString("city"));
            expected.setTelephone(owners.getString("telephone"));

            actual = ownerCRUD.selectOwnerById(expected.getId());
            if (actual==null){
                ownerCRUD.insert(expected);
                inconsistency++;
                violation("Owner", expected.getId());
            }
            else if (!expected.equals(actual)) {
                ownerCRUD.update(expected.getId(), expected);
                inconsistency++;
                violation("Owner", expected.getId());
            }
        }
    }

    public void checkPets() throws SQLException{
        PetsCRUD petCRUD= new PetsCRUD();
        ResultSet pets;
        ResultSet matchingPet;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        pets = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.pets");

        while(pets.next()){
            matchingPet = petCRUD.selectPetById(pets.getInt("id"));
            // GET MATCHING PET FROM EGLEN AND CHECK NULL
            if (pets.getString("name").equals(matchingPet.getString("name"))
                || pets.getDate("birth_date") != matchingPet.getDate("birth_date")
                || pets.getInt("type_id") != matchingPet.getInt("type_id")
                || pets.getInt("owner_id") != matchingPet.getInt("owner_id")){
                inconsistency++;
                violation("Pet", pets.getInt("id"));}
        }
    }

    public void checkVisits() throws SQLException{
        ResultSet visits;
        HashMap<String, List> matchingVisit  ;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        visits = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.visits");

        while(visits.next()){
            // GET MATCHING VISIT FROM EGLEN AND CHECK IF NULL
            if (visits.getInt("pet_id") != (int) matchingVisit.get("pet_id").get(0)
                || visits.getDate("visit_date") !=  (Date) matchingVisit.get("visit_date").get(0)
                || visits.getString("description").equals((String) matchingVisit.get("description").get(0))){
                inconsistency++;
                violation("Visit", visits.getInt("id"));}
        }
    }

    public int checkConsistency() throws SQLException{
        this.checkOwners();
        this.checkPets();
        this.checkVisits();
        return inconsistency;
    }

    private void violation(String type, int id) {
        System.out.println("Consistency Violation: " + type + " with ID " + id);
    }
}
