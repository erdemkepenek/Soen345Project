package forklift;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.*;


public class ForklifterTests {

    private Connection testConn;
    private Connection testOldConn;

    @Before
    public void setup(){
        try {
            testConn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db");
            testOldConn = DriverManager.getConnection("jdbc:mysql://eglencecaj.mysql.database.azure.com/petclinic?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "eglen@eglencecaj", "Soen344room3");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
    }

    @After
    public void teardown(){
        try {
            testConn.close();
            testOldConn.close();
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
    }

    @Test
    public void doYouEvenForkLiftTest() throws Exception {
        Forklifter.doYouEvenForkLift();
        ResultSet ownersRs = testConn.createStatement().executeQuery("SELECT * FROM owners");
        ResultSet petsRs = testConn.createStatement().executeQuery("SELECT * FROM pets");
        ResultSet visitsRs = testConn.createStatement().executeQuery("SELECT * FROM visits");
        assertTrue(ownersRs.next() && petsRs.next() && visitsRs.next());
    }

    @Test
    public void buildDatabaseTestFalseReset() throws Exception {
        try {
            Method method = Forklifter.class.getDeclaredMethod("buildDatabase", Connection.class, boolean.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testConn, false);
            ResultSet rs = testOldConn.createStatement().executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'petclinic'");
            assertTrue(rs.next());
        } catch (SQLException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void dropAllTablesTestWrongDb() throws Exception {
        try {
            Method method = Forklifter.class.getDeclaredMethod("dropAllTables", Connection.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testOldConn);
            ResultSet rs = testOldConn.createStatement().executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'petclinic'");
            assertTrue(rs.next());
        } catch (SQLException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void addDataTestWrongDbs() throws Exception {
        try {
            Method method = Forklifter.class.getDeclaredMethod("addData", Connection.class, Connection.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testConn, testOldConn);
            ResultSet ownersRs = testOldConn.createStatement().executeQuery("SELECT * FROM owners");
            ResultSet petsRs = testOldConn.createStatement().executeQuery("SELECT * FROM pets");
            ResultSet visitsRs = testOldConn.createStatement().executeQuery("SELECT * FROM visits");
            assertTrue(ownersRs.next() && petsRs.next() && visitsRs.next());
        } catch (SQLException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void parseSQLTestNoFilePath() throws Exception {
        String notAFilePath = "I am not a filepath.";
        Method method = Forklifter.class.getDeclaredMethod("parseSQL", String.class);
        method.setAccessible(true);
        ArrayList<String> testCommand = (ArrayList<String>) method.invoke(Forklifter.class, notAFilePath);
        assertTrue(testCommand.isEmpty());
    }
}
