package org.springframework.samples.petclinic;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Forklifter {

    public static void doYouEvenForkLift(){
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }
        setSchema(conn);
        addData(conn);

    }


    private static void setSchema(Connection db) {
        ArrayList<String> commands = parseSQL("src/main/resources/db/sqlite/schema.sql");

        for(String s : commands){
            try{
                Statement stmnt = db.createStatement();
                stmnt.execute(s);
            }catch(SQLException e){
                System.out.println("Unable to excecute the queries");
            }
        }
    }

    private static void addData(Connection db) {
        //TODO Add new Data
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
