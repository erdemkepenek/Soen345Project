package forklift;

import java.sql.*;
import java.util.Calendar;

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

    public ResultSet selectVisitById(int id) {
        String sql = "SELECT * FROM visits WHERE ID = "+id+"";
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

    public void insert(int id, int pet_id, java.sql.Date visit_date, String description) {
        String sql = "INSERT INTO visits VALUES ('"+id+"','"+pet_id+"', "+visit_date+", '"+description+"')";

        System.out.println(sql);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(int id, int pet_id, java.sql.Date visit_date, String description) {
        String sql = "UPDATE visits SET pet_id='"+pet_id+"', visit_date='"+visit_date+"', description='"+description+"' WHERE (id='"+id+"')";

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

        java.sql.Date d = new Date(Calendar.getInstance().getTimeInMillis());

        app.update(1, 9, d, "rabies shot");

    }
}
