import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminPanel extends JFrame {
    private JTextField txtFlightNumber, txtOrigin, txtDestination, txtDate, txtTime, txtSeats, txtPrice;
    private JButton btnAdd, btnUpdate, btnDelete;

    public AdminPanel() {
        setTitle("Admin Panel - Manage Flights");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));

        JLabel lblFlightNumber = new JLabel("Flight Number:");
        txtFlightNumber = new JTextField(20);
        JLabel lblOrigin = new JLabel("Origin:");
        txtOrigin = new JTextField(20);
        JLabel lblDestination = new JLabel("Destination:");
        txtDestination = new JTextField(20);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        txtDate = new JTextField(20);
        JLabel lblTime = new JLabel("Time (HH:MM:SS):");
        txtTime = new JTextField(20);
        JLabel lblSeats = new JLabel("Available Seats:");
        txtSeats = new JTextField(20);
        JLabel lblPrice = new JLabel("Price:");
        txtPrice = new JTextField(20);
        btnAdd = new JButton("Add Flight");
        btnUpdate = new JButton("Update Flight");
        btnDelete = new JButton("Delete Flight");

        panel.add(lblFlightNumber);
        panel.add(txtFlightNumber);
        panel.add(lblOrigin);
        panel.add(txtOrigin);
        panel.add(lblDestination);
        panel.add(txtDestination);
        panel.add(lblDate);
        panel.add(txtDate);
        panel.add(lblTime);
        panel.add(txtTime);
        panel.add(lblSeats);
        panel.add(txtSeats);
        panel.add(lblPrice);
        panel.add(txtPrice);
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);

        add(panel);

        btnAdd.addActionListener(e -> addFlight());
        btnUpdate.addActionListener(e -> updateFlight());
        btnDelete.addActionListener(e -> deleteFlight());
    }

    private void addFlight() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO flights (flight_number, origin, destination, departure_date, departure_time, available_seats, price) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, txtFlightNumber.getText());
            stmt.setString(2, txtOrigin.getText());
            stmt.setString(3, txtDestination.getText());
            stmt.setString(4, txtDate.getText());
            stmt.setString(5, txtTime.getText());
            stmt.setInt(6, Integer.parseInt(txtSeats.getText()));
            stmt.setDouble(7, Double.parseDouble(txtPrice.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Flight added successfully!");
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight!");
        }
    }

    private void updateFlight() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE flights SET origin = ?, destination = ?, departure_date = ?, departure_time = ?, available_seats = ?, price = ? WHERE flight_number = ?")) {
            stmt.setString(1, txtOrigin.getText());
            stmt.setString(2, txtDestination.getText());
            stmt.setString(3, txtDate.getText());
            stmt.setString(4, txtTime.getText());
            stmt.setInt(5, Integer.parseInt(txtSeats.getText()));
            stmt.setDouble(6, Double.parseDouble(txtPrice.getText()));
            stmt.setString(7, txtFlightNumber.getText());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Flight updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Flight not found!");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating flight!");
        }
    }

    private void deleteFlight() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM flights WHERE flight_number = ?")) {
            stmt.setString(1, txtFlightNumber.getText());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Flight deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Flight not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting flight!");
        }
    }
}