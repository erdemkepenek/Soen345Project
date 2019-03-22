package org.springframework.samples.petclinic;

import forklift.OwnerCRUD;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;
import org.springframework.samples.petclinic.visit.Visit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


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
                violation(expected.toString());
            }
            else if (!expected.equals(actual)) {
                ownerCRUD.update(expected.getId(), expected);
                inconsistency++;
                violation(expected.toString(), actual.toString());
            }
        }
    }

    public void checkPets() throws SQLException{
        ResultSet pets;
        ResultSet matchingPet
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        pets = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.pets");

        while(pets.next()){
            // GET MATCHING PET FROM EGLEN
            if (pets.getString("name").equals(matchingPet.getString("name"))
                || pets.getDate("birth_date") != matchingPet.getDate("birth_date")
                || pets.getInt("type_id") != matchingPet.getInt("type_id")
                || pets.getInt("owner_id") != matchingPet.getInt("owner_id"))
                inconsistency++;
        }
    }

    public void checkVisits() throws SQLException{
        ResultSet visits;
        ResultSet matchingVisit;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        visits = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.visits");

        while(visits.next()){
            // GET MATCHING VISIT FROM EGLEN
            if (visits.getInt("pet_id") != matchingVisit.getInt("pet_id")
                || visits.getDate("visit_date") != matchingVisit.getDate("visit_date")
                || visits.getString("description").equals(matchingVisit.getString("description")))
                inconsistency++;
        }
    }

    public int checkConsistency() throws SQLException{
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

    private void violation(String expected) {
        System.out.println("Consistency Violation!\n" +
            "\n\t expected = " + expected
            + "\n\t actual = null");
    }
}
