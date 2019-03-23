package org.springframework.samples.petclinic;

import forklift.ShadowRead;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.samples.petclinic.owner.Owner;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void findOwnerById() {

        owner = ShadowRead.findOwnerByLastName("Franklin");
        System.out.println(owner.getFirstName());
        assertEquals("George",
            owner.getFirstName());

    }
}
