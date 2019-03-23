package forklift;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.PreparedStatement;

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
                                   rs.getString("birth_date") + "\t" +
                                   rs.getInt("type_id") + "\t" +
                                   rs.getInt("owner_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    
    
    public ResultSet selectPetById(int id) {
    	String sql = "SELECT * FROM pets WHERE ID = "+id+"";
        ResultSet rs;

        try {
            Connection conn = this.connect();
            Statement stmt  = conn.createStatement();
            rs    = stmt.executeQuery(sql);
            return rs;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
    

    
    public void insert(int id, String name, String birth_date, int type_id, int owner_id) {
   	 String sql = "INSERT INTO pets VALUES ('"+id+"','"+name+"', "+birth_date+", '"+type_id+"', '"+owner_id+"')";
   	 				
   	 System.out.println(sql); 
   	 try (Connection conn = this.connect();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.executeUpdate();
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }
    
    public void update(int id, String name, String birth_date, int type_id, int owner_id) {
     	 String sql = "UPDATE pets SET name='"+name+"', birth_date='"+birth_date+"', type_id='"+type_id+"', owner_id='"+owner_id+"' WHERE (id='"+id+"')";
     	 				
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
 
        /*
    	try {
			System.out.println(app.selectPetById(13).getInt("id"));
			System.out.println(app.selectPetById(13).getString("name"));
			System.out.println(app.selectPetById(13).getString("birth_date"));
			System.out.println(app.selectPetById(13).getInt("type_id"));
			System.out.println(app.selectPetById(13).getInt("owner_id"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        
        
        String birth_date = "2019-02-01";
   
        app.update(99,"john",birth_date,1,1);
        
        
    }

}
