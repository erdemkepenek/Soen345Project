package org.springframework.samples.petclinic;

import forklift.ShadowRead;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.samples.petclinic.owner.Owner;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;

public class ShadowReadTest {

    ResultSet rs;
    Owner owner;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void findOwnerByID() {

        owner = ShadowRead.findOwnerByID(1);
        System.out.println(owner.getLastName());
        assertEquals("Franklin",
            owner.getLastName());

    }

    @Test
    public void findOwnerByLastName() {

        owner = ShadowRead.findOwnerByLastName("Franklin");
        System.out.println(owner.getFirstName());
        assertEquals("George",
            owner.getFirstName());

    }

    @Test
    public void findOwnersByLastNameWithInput() {

        ArrayList<Owner> owners = ShadowRead.findOwnersByLastName("Davis");
        System.out.println(owners.size());
        String[] expected = {"Betty", "Harold"};
        int index = 0;
        Iterator<Owner> ownerIterator = owners.iterator();

        while (ownerIterator.hasNext() && index < expected.length) {
            Owner owner = ownerIterator.next();
            assertEquals(expected[index++], owner.getFirstName());

        }
    }

    @Test
    public void findOwnersByLastNameWithoutInput() {

        ArrayList<Owner> owners = ShadowRead.findOwnersByLastName("");
        String[] expected = {"George","Betty", "Eduardo", "Harold", "Peter", "Jean", "Jeff", "Maria", "David", "Carlos"};
        int expectedSize = expected.length;
        int index = 0;
        Iterator<Owner> ownerIterator;

        System.out.println(owners.size());
        //assertEquals(expectedSize,owners.size());
        ownerIterator = owners.iterator();
        while (ownerIterator.hasNext() && index < expected.length) {
            Owner owner = ownerIterator.next();
            assertEquals(expected[index++], owner.getFirstName());

        }

        owners = ShadowRead.findOwnersByLastName(null);
        System.out.println(owners.size());
        //assertEquals(expectedSize,owners.size());
        ownerIterator = owners.iterator();
        while (ownerIterator.hasNext() && index < expected.length) {
            Owner owner = ownerIterator.next();
            assertEquals(expected[index++], owner.getFirstName());

        }
    }
}
