package forklift;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.lang.reflect.Method;
import java.sql.*;

import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ForklifterTests {

    private Connection testNewDb;
    private Connection testOldDb;

    @Before
    public void setup(){
        try {
            testNewDb = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db");
            testOldDb = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
    }

    @After
    public void teardown(){
        try {
            testNewDb.close();
            testOldDb.close();
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
    }

    @Test
    public void dropAllTablesTestSuccess() throws Exception {
        try (PreparedStatement pstmt = testNewDb.prepareStatement("SELECT name FROM sqlite_master WHERE type = 'table'")) {
            Method method = Forklifter.class.getDeclaredMethod("dropAllTables", Connection.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testNewDb);
            ResultSet rs = pstmt.executeQuery();
            assertFalse(rs.next());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void dropAllTablesTestWrongDb() throws Exception {
        try {
            Method method = Forklifter.class.getDeclaredMethod("dropAllTables", Connection.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testOldDb);
            ResultSet rs = testOldDb.createStatement().executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'petclinic'");
            assertTrue(rs.next());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addDataTestSuccess() throws Exception {
        try {
            Method method = Forklifter.class.getDeclaredMethod("addData", Connection.class, Connection.class);
            method.setAccessible(true);
            method.invoke(Forklifter.class, testOldDb, testNewDb);
            ResultSet ownersRs = testNewDb.createStatement().executeQuery("SELECT * FROM owners");
            ResultSet petsRs = testNewDb.createStatement().executeQuery("SELECT * FROM pets");
            ResultSet visitsRs = testNewDb.createStatement().executeQuery("SELECT * FROM visits");
            assertTrue(ownersRs.next() && petsRs.next() && visitsRs.next());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void doYouEvenForkLiftTestSuccess() throws Exception {
        Forklifter.doYouEvenForkLift();
        ResultSet ownersRs = testNewDb.createStatement().executeQuery("SELECT * FROM owners");
        ResultSet petsRs = testNewDb.createStatement().executeQuery("SELECT * FROM pets");
        ResultSet visitsRs = testNewDb.createStatement().executeQuery("SELECT * FROM visits");
        assertTrue(ownersRs.next() && petsRs.next() && visitsRs.next());
    }
}
