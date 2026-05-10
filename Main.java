import java.sql.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Create and show login screen
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Method to get a list of available trains from the database
    public static String getAvailableTrains() {
        StringBuilder trains = new StringBuilder();

        try (Connection conn = DBConfig.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM trains");
            while (rs.next()) {
                trains.append("Train ID: ").append(rs.getInt("train_id"))
                        .append(", Name: ").append(rs.getString("train_name"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains.toString();
    }

    // Method to get available seats for a specific train from the database
    public static String getAvailableSeats(int trainId) {
        StringBuilder seats = new StringBuilder();

        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT * FROM seats WHERE train_id = ? AND is_booked = FALSE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.append("Seat No: ").append(rs.getInt("seat_number")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats.toString();
    }
}
