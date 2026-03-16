import com.mycompany.garagemanagementsystem.util.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class AlterDB {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {
            
            try {
                st.execute("ALTER TABLE vehicle ADD cc INT DEFAULT 0 AFTER tipe");
                System.out.println("cc added");
            } catch(Exception e) { System.out.println("cc already exists or error: " + e.getMessage()); }
            
            try {
                st.execute("ALTER TABLE vehicle ADD tipe_kendaraan VARCHAR(50) DEFAULT 'Roda 2' AFTER cc");
                System.out.println("tipe_kendaraan added");
            } catch(Exception e) { System.out.println("tipe_kendaraan already exists or error: " + e.getMessage()); }
            
            try {
                st.execute("ALTER TABLE vehicle MODIFY no_rangka VARCHAR(50) NULL");
                System.out.println("no_rangka modified to NULL");
            } catch(Exception e) { }
            
            try {
                st.execute("ALTER TABLE vehicle MODIFY no_mesin VARCHAR(50) NULL");
                System.out.println("no_mesin modified to NULL");
            } catch(Exception e) { }
            
            System.out.println("Done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
