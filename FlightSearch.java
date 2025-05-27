import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightSearch extends JFrame {
    private JTextField txtOrigin, txtDestination, txtDate;
    private JButton btnSearch, btnBook;
    private JList<Flight> flightList;
    private DefaultListModel<Flight> listModel;
    private String username;

    public FlightSearch(String username) {
        this.username = username;
        setTitle("Flight Search");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblOrigin = new JLabel("Origin:");
        txtOrigin = new JTextField(20);
        JLabel lblDestination = new JLabel("Destination:");
        txtDestination = new JTextField(20);
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        txtDate = new JTextField(20);
        btnSearch = new JButton("Search Flights");
        btnBook = new JButton("Book Selected Flight");

        panel.add(lblOrigin);
        panel.add(txtOrigin);
        panel.add(lblDestination);
        panel.add(txtDestination);
        panel.add(lblDate);
        panel.add(txtDate);
        panel.add(btnSearch);
        panel.add(btnBook);

        listModel = new DefaultListModel<>();
        flightList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(flightList);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> searchFlights());
        btnBook.addActionListener(e -> bookFlight());
    }

    private void searchFlights() {
        String origin = txtOrigin.getText();
        String destination = txtDestination.getText();
        String date = txtDate.getText();

        listModel.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM flights WHERE origin = ? AND destination = ? AND departure_date = ?")) {
            stmt.setString(1, origin);
            stmt.setString(2, destination);
            stmt.setString(3, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Flight flight = new Flight(
                    rs.getInt("flight_id"),
                    rs.getString("flight_number"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("departure_date"),
                    rs.getString("departure_time"),
                    rs.getInt("available_seats"),
                    rs.getDouble("price")
                );
                listModel.addElement(flight);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching flights!");
        }
    }

    private void bookFlight() {
        Flight selectedFlight = flightList.getSelectedValue();
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight!");
            return;
        }

        if (selectedFlight.getAvailableSeats() <= 0) {
            JOptionPane.showMessageDialog(this, "No seats available!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get user_id
            PreparedStatement userStmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            int userId = userRs.next() ? userRs.getInt("user_id") : -1;

            // Insert booking
            PreparedStatement bookingStmt = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, flight_id) VALUES (?, ?)");
            bookingStmt.setInt(1, userId);
            bookingStmt.setInt(2, selectedFlight.getFlightId());
            bookingStmt.executeUpdate();

            // Update available seats
            PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats - 1 WHERE flight_id = ?");
            updateStmt.setInt(1, selectedFlight.getFlightId());
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking successful!");
            searchFlights(); // Refresh flight list
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error booking flight!");
        }
    }
}