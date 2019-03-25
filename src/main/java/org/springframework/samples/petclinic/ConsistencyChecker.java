package org.springframework.samples.petclinic;

import forklift.OwnerCRUD;
import forklift.PetsCRUD;
import forklift.SimplePet;
import forklift.SimpleVisit;
import forklift.VisitsCRUD;
import org.springframework.samples.petclinic.owner.Owner;

import java.sql.*;
import java.util.Collection;
import java.util.Iterator;


public class ConsistencyChecker {
    Connection oldConn = null;
    private static ConsistencyChecker consistencyChecker = new ConsistencyChecker();
    private int inconsistency = 0;
    boolean write= false;
    boolean read = false;
    boolean isSwapped = false;


    public static ConsistencyChecker getInstance() {
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
            oldConn = DriverManager.getConnection("jdbc:mysql://eglencecaj.mysql.database.azure.com/petclinic?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "eglen@eglencecaj", "Soen344room3");
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


    public int readConsistencyChecking(Owner expected, Owner actual) {

        OwnerCRUD ownerCRUD = new OwnerCRUD();
        if (expected == null && (actual == null || actual.getId() == null)) {
            return 0;
        }
        if (expected == null) {
            ownerCRUD.delete(actual);
            return 1;
        }
        if (actual == null || actual.getId() == null){
            ownerCRUD.insert(expected);
            return 1;
        }
        if (expected.equals(actual)) {
            return 0;
        }

        ownerCRUD.update(expected.getId(),expected);
        return 1;
    }

    public int readConsistencyChecking(Collection<Owner> expected, Collection<Owner> actual) {

        int readInconsistency = 0; // reset
        OwnerCRUD ownerCRUD = new OwnerCRUD();

        // To handle null inputs
        if (expected == null && actual == null) {
            return readInconsistency;
        }
        if (expected == null) {
            Iterator<Owner> actualIterator = actual.iterator();
            while (actualIterator.hasNext()) {
                ++readInconsistency;
//                Owner actualOwner = actualIterator.next();
                ownerCRUD.delete(actualIterator.next());
            }
            return readInconsistency;
        }
        if (actual == null) {
            Iterator<Owner> expectedIterator = expected.iterator();
            while (expectedIterator.hasNext()) {
                ++readInconsistency;
//                Owner expectedOwner = expectedIterator.next();
                ownerCRUD.insert(expectedIterator.next());
            }
        }


        String flag = "MATCH";

        // update or insert the inconsistent records
        Iterator<Owner> expectedIterator = expected.iterator();
        while (expectedIterator.hasNext()) {
            Owner expectedOwner = expectedIterator.next();
            if (!checkIfOwnerExists(expectedOwner, actual)) {
                flag = "MISS";
                ++readInconsistency;
                ownerCRUD.insert(expectedOwner);
                violation("Owner", expectedOwner.getId());
            } else {
                if (!checkIfOwnerAttributesTheSame(expectedOwner, actual)){
                    flag = "WRONG";
                    ++readInconsistency;
                    ownerCRUD.update(expectedOwner.getId(), expectedOwner);
                    violation("Owner", expectedOwner.getId());
                }
            }
        }

        // delete the records not included in expected
        Iterator<Owner> actualIterator = actual.iterator();
        while (actualIterator.hasNext()) {
            Owner actualOwner = actualIterator.next();
            if (!checkIfOwnerExists(actualOwner, expected)) {
                ++readInconsistency;
                ownerCRUD.delete(actualOwner);
                violation("Owner", actualOwner.getId());
            }
        }

        return readInconsistency;
    }

    private Boolean checkIfOwnerExists(Owner expected, Collection<Owner> actual) {

        Iterator<Owner> actualIterator = actual.iterator();
        // check if the same id exist
        while(actualIterator.hasNext()) {
            Owner actualOwner = actualIterator.next();
            if (expected.getId() == actualOwner.getId()) {
                return true;
            }
        }
        return false;
    }

    private Boolean checkIfOwnerAttributesTheSame(Owner expected, Collection<Owner> actual) {

        Iterator<Owner> actualIterator = actual.iterator();
        // check attributes
        while (actualIterator.hasNext()) {
            Owner actualOwner = actualIterator.next();
            if (expected.equals(actualOwner)) {
                return true;
            }
        }
        return false;
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

    public boolean getWrite(){
        return write;
    }

    public void setWrite(boolean write){
        this.write = write;
    }

    public boolean getRead(){
        return read;
    }

    public void setRead(boolean read){
        this.read = read;
    }

    public boolean getSwapped() {
        return this.isSwapped;
    }

    public void setSwapped(boolean isSwapped) {
        this.isSwapped = isSwapped;
    }


    public static void main(String[] args) throws SQLException{
        System.out.println("There are "+ConsistencyChecker.getInstance().checkConsistency()+" inconsistencies.");
    }

}
