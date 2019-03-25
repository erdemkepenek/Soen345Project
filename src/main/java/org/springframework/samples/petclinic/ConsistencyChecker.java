package org.springframework.samples.petclinic;

import forklift.OwnerCRUD;
import forklift.PetsCRUD;
import forklift.SimplePet;
import forklift.SimpleVisit;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class ConsistencyChecker {
    Connection oldConn = null;
    private static ConsistencyChecker consistencyChecker = new ConsistencyChecker();
    private int inconsistency = 0;


    public static ConsistencyChecker getInstance(){
         consistencyChecker.resetCounter();
        try {
            if (consistencyChecker.oldConn.isClosed()) {
                consistencyChecker.oldConn = DriverManager.getConnection("jdbc:mysql://eglencecaj.mysql.database.azure.com/petclinic?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "eglen@eglencecaj", "Soen344room3");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return consistencyChecker;
    }

    private ConsistencyChecker() {
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
    }

    public void close(){
        try{
            this.oldConn.close();
        }catch(SQLException e){
            System.out.println("The Connection wasn't open");
        }
    }

    public void checkOwners() throws SQLException{
        Owner expected = new Owner();
        Owner actual;
        OwnerCRUD ownerCRUD= new OwnerCRUD();

        ResultSet owners;

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
            else if (!expected.getFirstName().equals(actual.getFirstName())
                || !expected.getLastName().equals(actual.getLastName())
                || !expected.getAddress().equals(actual.getAddress())
                ||  !expected.getCity().equals(actual.getCity())
                || !expected.getTelephone().equals(actual.getTelephone())) {
                ownerCRUD.update(expected.getId(), expected);
                inconsistency++;
                violation("Owner", expected.getId());
            }
        }
    }

    public void checkPets() throws SQLException{
        SimplePet expected = new SimplePet();
        SimplePet actual;
        PetsCRUD petsCRUD= new PetsCRUD();

        ResultSet pets;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }

        pets = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.pets");

        while (pets.next()){
            expected.setId(pets.getInt("id"));
            expected.setName(pets.getString("name"));
            expected.setDate(pets.getDate("birth_date"));
            expected.setType(pets.getInt("type_id"));
            expected.setOwner(pets.getInt("owner_id"));

            actual = petsCRUD.selectPetById(expected.getId());
            if (actual==null){
            	petsCRUD.insert(expected);
                inconsistency++;
                violation("Owner", expected.getId());
            }

            else if (!expected.getName().equals(actual.getName())
                || !expected.getDate().equals(actual.getDate())
                || expected.getType()!=(actual.getType())
                ||  expected.getOwner()!=(actual.getOwner())) {
            	petsCRUD.update(expected.getId(), expected);

                inconsistency++;
                violation("Pet", expected.getId());
            }
        }
    }



    public void checkVisits() throws SQLException{
        SimpleVisit expected = new SimpleVisit();
        SimpleVisit actual;
        VisitsCRUD visitsCRUD= new VisitsCRUD();

        ResultSet visits;
        Connection oldConn = null;
        try {
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }

        visits = oldConn.createStatement().executeQuery("SELECT * FROM petclinic.visits");

        while (visits.next()){
        	expected.setId(visits.getInt("id"));
        	expected.setPetId(visits.getInt("pet_id"));
        	expected.setDate(visits.getDate("visist_date"));
        	expected.setDescription(visits.getString("description"));

            actual = visitsCRUD.selectVisitById(expected.getId());
            if (actual==null){
            	visitsCRUD.insert(expected);
                inconsistency++;
                violation("Owner", expected.getId());
            }

            else if (
            		(expected.getId() != actual.getId())
            	||	(expected.getPetId() != actual.getPetId())
                || (!expected.getDate().equals(actual.getDate()))
                || (!expected.getDescription().equals(actual.getDescription()))
                	)
                	{
            	visitsCRUD.update(expected.getId(), expected);

                inconsistency++;
                violation("Pet", expected.getId());
            }
        }
    }


    public int checkConsistency() throws SQLException{
        this.checkOwners();
      //  this.checkPets();
        //this.checkVisits();
        return inconsistency;
    }

    public void resetCounter(){
        inconsistency = 0;
    }


    private void violation(String type, int id) {
        System.out.println("Consistency Violation: " + type + " with ID " + id);
    }

    private void violation(String type, String date) {
        System.out.println("Consistency Violation: " + type + " with Date " + date);
    }
    public static void main(String[] args) throws SQLException{
        System.out.println("There are "+ConsistencyChecker.getInstance().checkConsistency()+" inconsistencies.");
    }

}
