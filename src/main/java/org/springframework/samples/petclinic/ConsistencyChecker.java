package org.springframework.samples.petclinic;

import forklift.OwnerCRUD;
import forklift.PetsCRUD;
import forklift.VisitsCRUD;
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
            if (matchingPet == null){
                petCRUD.insert(pets.getInt("id"), pets.getString("name"), pets.getDate("birth_date").toString(), pets.getInt("type_id"), pets.getInt("owner_id"));
            }
            else if (pets.getString("name").equals(matchingPet.getString("name"))
                || pets.getDate("birth_date") != matchingPet.getDate("birth_date")
                || pets.getInt("type_id") != matchingPet.getInt("type_id")
                || pets.getInt("owner_id") != matchingPet.getInt("owner_id")){
                inconsistency++;
                violation("Pet", pets.getInt("id"));
                petCRUD.update(pets.getInt("id"), pets.getString("name"), pets.getDate("birth_date").toString(), pets.getInt("type_id"), pets.getInt("owner_id"));
                }
        }
    }

    public void checkVisits() throws SQLException{
        ResultSet visits;
        ResultSet matchingVisit;
        VisitsCRUD visitsCRUD = new VisitsCRUD();
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        visits = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.visits");

        while(visits.next()){
            matchingVisit= visitsCRUD.selectVisitById(visits.getInt("id"));
            if (matchingVisit == null){
                visitsCRUD.insert(visits.getInt("id"), visits.getInt("pet_id"), visits.getString("visit_date"), visits.getString("description"));
            }
            else if (visits.getInt("pet_id") != matchingVisit.getInt("pet_id")
                || visits.getDate("visit_date") != matchingVisit.getDate("visit_date")
                || visits.getString("description").equals(matchingVisit.getString("description"))){
                inconsistency++;
                violation("Visit", visits.getInt("id"));
                visitsCRUD.update(visits.getInt("id"), visits.getInt("pet_id"), visits.getString("visit_date"), visits.getString("description"));}
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
