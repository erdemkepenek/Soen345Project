package migration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.samples.petclinic.ConsistencyChecker;

import java.lang.reflect.Method;
import java.sql.Connection;

import static org.junit.Assert.*;


public class ConsistencyCheckerTests {

    private ConsistencyChecker testChecker;

    @Before
    public void setup(){
        testChecker = ConsistencyChecker.getInstance();
    }

    @After
    public void teardown(){
        testChecker.resetCounter();
    }

    @Test
    public void checkConsistencyTest() throws Exception {
        testChecker.checkConsistency();
        assertEquals(0, testChecker.checkConsistency());
    }
}
