package forklift;


import java.util.Date;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.samples.petclinic.owner.*;

public class PetsCRUD {


	 
    /**
     * Connect to the  database
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
    	String url = "jdbc:sqlite:src/main/resources/db/sqlite/sqlite.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
 
    
    /**
     * select all rows in the owners table
     */
    public void selectAll(){
        String sql = "SELECT * FROM pets";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + 
                				   rs.getString("name") +  "\t" + 
                                   rs.getDate("birth_date") + "\t" +
                                   rs.getInt("type_id") + "\t" +
                                   rs.getInt("owner_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    
    
    public SimplePet selectPetById(int id) {
    	String sql = "SELECT * FROM pets WHERE ID = "+id+"";
    	System.out.println(sql); 
    	SimplePet pet = new SimplePet();
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
            	
            	//------------date conversion, string to localdate to date-------------
            	
            	String dateString = rs.getString("birth_date");
            	
            	System.out.println(dateString);
            	LocalDate localDate = LocalDate.parse(dateString);
            	Date birth_date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            	System.out.println("I am ready to be put in the pet object"+birth_date);
            	
             	//------------end date conversion--------
             	
            	pet.setId(rs.getInt("id"));
            	pet.setName(rs.getString("name"));
            	pet.setDate(birth_date);
            	pet.setType(rs.getInt("type_id"));
            	pet.setOwner(rs.getInt("owner_id"));
           
            //put all the values in a simplePet object
            

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pet;
    }
    

    
    public void insert(SimplePet pet) {
    	//Converting from java date to String
    	String birth_date = new SimpleDateFormat("yyyy-MM-dd").format(pet.getDate());
   	 
    	String sql = "INSERT INTO pets VALUES ('"+pet.getId()+"','"+pet.getName()+"', '"+birth_date+"', '"+pet.getType()+"', '"+pet.getOwner()+"')";
   	 				
   	 System.out.println(sql); 
   	 try (Connection conn = this.connect();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.executeUpdate();
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }
    
    public void update(int id, SimplePet pet) {
    	//Converting from java date to String
    	String birth_date = new SimpleDateFormat("yyyy-MM-dd").format(pet.getDate());
    	
    	String sql = "UPDATE pets SET name='"+pet.getName()+"', birth_date='"+birth_date+"', type_id='"+pet.getType()+"', owner_id='"+pet.getOwner()+"' WHERE (id='"+id+"')";
     	 				
     	 System.out.println(sql); 
     	 try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.executeUpdate();
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
     }
    
    public void delete(int id) {
      	 String sql = "DELETE FROM pets WHERE (id='"+id+"')";
      	 				
      	 System.out.println(sql); 
      	 try (Connection conn = this.connect();
                  PreparedStatement pstmt = conn.prepareStatement(sql)) {
              pstmt.executeUpdate();
          } catch (SQLException e) {
              System.out.println(e.getMessage());
          }
      }
    
    public static void main(String[] args) {
    	//Owner boss = new Owner();
    	//boss.setFirstName("john");
    	PetsCRUD app = new PetsCRUD();
        //app.selectAll();
    	SimplePet test = app.selectPetById(13);
    	SimplePet test1 = app.selectPetById(99);
    	test1.setId(1781);
    	app.update(10,test1);
    	app.delete(4);
    	
    	System.out.println(test.getId());
		System.out.println(test.getName());
		System.out.println(test.getDate());
		System.out.println(test.getType());
		System.out.println(test.getOwner());
		
		//Date d1 = Date.valueOf(test.getDate("birth_date").toString()).getTime() != java.sql.Date.valueOf(matchingPet.getString("birth_date")).getTime();
			
		

        
    }

}
