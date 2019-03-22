package forklift;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.springframework.samples.petclinic.owner.*;;

public class OwnerCRUD {

	 
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
        String sql = "SELECT id, first_name, last_name FROM owners";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + 
                                   rs.getString("first_name") + "\t" +
                                   rs.getString("last_name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    public void selectOwnerById(int id){
        String sql = "SELECT id, first_name, last_name FROM owners WHERE ID = "+id+"";
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" + 
                                   rs.getString("first_name") + "\t" +
                                   rs.getString("last_name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    

    
    public void insert(Owner owner) {
   	 String sql = "INSERT INTO owners VALUES ('"+owner.getId()+"', '"+owner.getFirstName()+"', '"+owner.getLastName()+"', '"+owner.getAddress()+"', '"+owner.getCity()+"', '"+owner.getTelephone()+"')";
   	 				
   	 System.out.println(sql); 
   	 try (Connection conn = this.connect();
               PreparedStatement pstmt = conn.prepareStatement(sql)) {
           pstmt.executeUpdate();
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }
    
    public void update(int id, Owner owner) {
     	 String sql = "UPDATE owners SET first_name='"+owner.getFirstName()+"', last_name='"+owner.getLastName()+"', address='"+owner.getAddress()+"', city='"+owner.getCity()+"', telephone='"+owner.getTelephone()+"' WHERE (id='"+id+"')";
     	 				
     	 System.out.println(sql); 
     	 try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.executeUpdate();
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
     }
    
    public void delete(Owner owner) {
      	 String sql = "DELETE FROM owners WHERE (id='"+owner.getId()+"')";
      	 				
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
    	OwnerCRUD app = new OwnerCRUD();
        app.selectAll();
        app.selectOwnerById(1);
        
        //Example on how to call the insert methods
        Owner owner = new Owner();
       
        owner.setId(99);
        System.out.println(owner.getId());
        
        owner.setFirstName("johnhhj");
        System.out.println(owner.getFirstName());

        owner.setLastName("Salib");
        System.out.println(owner.getLastName());
        
        owner.setAddress("vgjhbnhere");
        System.out.println(owner.getAddress());
        
        owner.setCity("Montreal");
        System.out.println(owner.getCity());
        
        owner.setTelephone("6166171");
        System.out.println(owner.getTelephone());
        
        app.update(5,owner);
    }
}