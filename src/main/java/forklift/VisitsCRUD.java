package forklift;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class VisitsCRUD {

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
        String sql = "SELECT * FROM visits";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                    rs.getInt("pet_id") +  "\t" +
                    rs.getDate("visit_date") + "\t" +
                    rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public ResultSet readAll() {
        String sql = "SELECT id, pet_id, visit_date, description FROM visits";
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

    public SimpleVisit selectVisitById(int id) {
        String sql = "SELECT * FROM visits WHERE ID = "+id+"";
        System.out.println(sql); 
    	SimpleVisit visit = new SimpleVisit();
        
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
            	
            	//------------date conversion, string to localdate to date-------------
            	
            	String dateString = rs.getString("visit_date");
            	
            	System.out.println(dateString);
            	LocalDate localDate = LocalDate.parse(dateString);
            	Date visit_date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            	System.out.println("I am ready to be put in the pet object"+visit_date);
            	
             	//------------end date conversion--------
             	
            	visit.setId(rs.getInt("id"));
            	visit.setPetId(rs.getInt("pet_id"));
            	visit.setDate(visit_date);
            	visit.setDescription(rs.getString("description"));
            	
           
            //put all the values in a simplePet object
            

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return visit;
    }

    public void insert(SimpleVisit visit) {
    	
    	//Converting from java date to String
    	String visit_date = new SimpleDateFormat("yyyy-MM-dd").format(visit.getDate());
    	
        String sql = "INSERT INTO visits VALUES ('"+visit.getId()+"','"+visit.getPetId()+"', '"+visit_date+"', '"+visit.getDescription()+"')";

        System.out.println(sql);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(int id, SimpleVisit visit) {
    	
    	//Converting from java date to String
    	String visit_date = new SimpleDateFormat("yyyy-MM-dd").format(visit.getDate());
    	
    	String sql = "UPDATE visits SET pet_id='"+visit.getPetId()+"', visit_date='"+visit_date+"', description='"+visit.getDescription()+"' WHERE (id='"+id+"')";

        System.out.println(sql);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM visits WHERE (id='"+id+"')";

        System.out.println(sql);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args){

        VisitsCRUD app = new VisitsCRUD();
        SimpleVisit visit = app.selectVisitById(1);
        System.out.println(visit.getId());
        System.out.println(visit.getPetId());
        System.out.println(visit.getDate());
        System.out.println(visit.getDescription());
        
        visit.setId(101);
        //app.insert(visit);
        app.delete(101);
  
    }
}
