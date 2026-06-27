import java.sql.Connection;

public class Main {
    public static void main(String[] args) {

        System.out.println("Checking Database Connection...");

        // DatabaseConnection class ko call kar ke connection test kar rahe hain
        Connection testConn = DatabaseConnection.getConnection();

        if (testConn != null) {
            // Agar connection successful ho gaya
            System.out.println("✅ ZABARDAST! Database IntelliJ ke sath successfully connect ho gayi hai.");

            // Ab GUI Dashboard ko launch karo
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        } else {
            // Agar connection fail ho gaya
            System.out.println("❌ CONNECTION FAILED! Please apna MySQL chal raha hai ya nahi, aur username/password check karo.");
        }
    }
}