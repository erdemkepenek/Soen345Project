package org.springframework.samples.petclinic;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Forklifter {

    public static void doYouEvenForkLift(){
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db");
        }catch (SQLException e){
            System.out.println("Connection failed");
        }

    }

    public static void main(String[] args){
        doYouEvenForkLift();
    }
}
