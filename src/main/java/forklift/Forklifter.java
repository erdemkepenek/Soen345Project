package forklift;


import org.springframework.samples.petclinic.SQLiteJDBCDriverConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Forklifter {
    static ResultSet owners;
    static ResultSet pets;
    static ResultSet visits;

    public static void doYouEvenForkLift(){
        Connection conn = null;
        Connection oldConn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db");
            oldConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/petclinic", "root", "petclinic");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        buildDatabase(conn, true);
        addData(oldConn,conn);
        System.out.println("Forklift complete!, Enjoy 😉");

    }


    //if reset is true, all tables will be dropped and recreated.
    private static void buildDatabase(Connection db, boolean reset) {
        if(reset){
            dropAllTables(db);
        }
        ArrayList<String> commands = parseSQL("src/main/resources/db/sqlite/schema.sql");
        ArrayList<String> fkCommands = parseSQL("src/main/resources/db/sqlite/data.sql");
        for(String s : commands){
            try{
                Statement stmnt = db.createStatement();
                stmnt.execute(s);
            }catch(SQLException e){
                System.out.println("Unable to excecute the queries");
            }
        }
        for(String s : fkCommands){
            try{
                Statement stmnt = db.createStatement();
                stmnt.execute(s);
            }catch(SQLException e){
                System.out.println("Unable to excecute the queries");
            }
        }
    }

    //works for SQLite Only.
    private static void dropAllTables(Connection db) {
        try{
            Statement resetAll = db.createStatement();
            resetAll.execute("DROP TABLE IF EXISTS owners");
            resetAll.execute("DROP TABLE IF EXISTS pets");
            resetAll.execute("DROP TABLE IF EXISTS specialties");
            resetAll.execute("DROP TABLE IF EXISTS types");
            resetAll.execute("DROP TABLE IF EXISTS vet_specialties");
            resetAll.execute("DROP TABLE IF EXISTS vets");
            resetAll.execute("DROP TABLE IF EXISTS visits");
            resetAll.execute("DROP TABLE IF EXISTS owners");
        }
        catch(SQLException e){
            System.out.println("Couldn't reset the DB");
        }
    }

    private static void addData(Connection dbFrom, Connection dbTo) {

        try{
            owners = dbFrom.createStatement().executeQuery("SELECT * FROM petclinic.owners");
            pets = dbFrom.createStatement().executeQuery("SELECT * FROM petclinic.pets");
            visits = dbFrom.createStatement().executeQuery("SELECT * FROM petclinic.visits");
            insertOwners(owners, dbTo);
            insertPets(pets, dbTo);
            insertVisits(visits, dbTo);

        }
        catch(SQLException e){
            System.out.println("Couldn't complete the queries");
        }
    }

    private static void insertVisits(ResultSet rs, Connection dbTo) throws SQLException{
        while(rs.next()){
            int id = rs.getInt("id");
            int pet_id = rs.getInt("pet_id");
            Date birth_date = rs.getDate("visit_date");
            String description = rs.getString("description");
            dbTo.createStatement().execute("INSERT OR REPLACE INTO visits" +
                " VALUES (\""+id+"\", \""+pet_id+"\", \""+birth_date+"\", \""+description+"\")");
        }
    }

    private static void insertPets(ResultSet rs, Connection dbTo) throws SQLException{
        while(rs.next()){
            int id = rs.getInt("id");
            String name = rs.getString("name");
            Date birth_date = rs.getDate("birth_date");
            int type_id = rs.getInt("type_id");
            int city = rs.getInt("owner_id");
            dbTo.createStatement().execute("INSERT OR REPLACE INTO pets" +
                " VALUES (\""+id+"\", \""+name+"\", \""+birth_date+"\", \""+type_id+"\", \""+city+"\")");
        }
    }

    private static void insertOwners(ResultSet rs, Connection dbTo) throws SQLException{
        while(rs.next()){
            int id = rs.getInt("id");
            String first_name = rs.getString("first_name");
            String last_name = rs.getString("last_name");
            String address = rs.getString("address");
            String city = rs.getString("city");
            String telephone = rs.getString("telephone");
            dbTo.createStatement().execute("INSERT OR REPLACE INTO owners" +
                " VALUES (\""+id+"\", \""+first_name+"\", \""+last_name+"\", \""+address+"\", \""+city+"\", \""+telephone+"\")");
        }
    }

    private static ArrayList<String> parseSQL(String sqlFilePath) {
        File sqlSchema = new File(sqlFilePath);
        ArrayList<String> commands = new ArrayList<String>();

        try{
            Scanner sqlReader = new Scanner(sqlSchema);
            sqlReader.useDelimiter(";");

            while(sqlReader.hasNext()){
                commands.add(sqlReader.next().trim()+";");
            }

            commands.remove(commands.size()-1);
        }
            catch(FileNotFoundException e){
            System.out.println("The Schema File Wasn't found!");
        }

        return commands;
    }

    public static void main(String[] args){
        doYouEvenForkLift();
    }
}
