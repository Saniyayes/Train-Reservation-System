import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MainDashboard extends JFrame {
    private int userId;

    public MainDashboard(int userId) {
        this.userId = userId;

        setTitle("Passenger Dashboard");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(248, 249, 250));
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        contentPanel.add(createMenuCard("Book Ticket", "Browse trains and reserve your seat", new Color(0, 123, 255), e -> bookTicket()));
        contentPanel.add(createMenuCard("Cancel Ticket", "Cancel your existing reservations", new Color(108, 117, 125), e -> cancelTicket()));
        contentPanel.add(createMenuCard("View My Bookings", "Check your booking history and status", new Color(40, 167, 69), e -> viewMyBookings()));

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuCard(String title, String subtitle, Color color, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 37, 41));
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLabel.setForeground(new Color(108, 117, 125));
        
        textPanel.add(titleLabel);
        textPanel.add(subLabel);
        
        card.add(textPanel, BorderLayout.CENTER);

        JButton btn = new JButton("Go →");
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 1));
        btn.setPreferredSize(new Dimension(80, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        card.add(btn, BorderLayout.EAST);

        // Add hover effect to the whole card if possible, but let's keep it simple for now
        return card;
    }

    private void bookTicket() {
        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT * FROM trains";
            try (PreparedStatement pst = conn.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {

                DefaultListModel<String> trainListModel = new DefaultListModel<>();
                while (rs.next()) {
                    trainListModel.addElement(rs.getString("train_name"));
                }

                JList<String> trainList = createStyledList(trainListModel);
                int option = showStyledDialog("Select a Train", trainList);

                if (option == JOptionPane.OK_OPTION) {
                    String selectedTrain = trainList.getSelectedValue();
                    if (selectedTrain == null) return;

                    query = "SELECT seat_number FROM seats WHERE train_id = ? AND is_booked = FALSE";
                    try (PreparedStatement pstSeats = conn.prepareStatement(query)) {
                        pstSeats.setInt(1, getTrainId(conn, selectedTrain));
                        ResultSet seatRs = pstSeats.executeQuery();

                        DefaultListModel<String> seatListModel = new DefaultListModel<>();
                        while (seatRs.next()) {
                            seatListModel.addElement("Seat " + seatRs.getInt("seat_number"));
                        }

                        JList<String> seatList = createStyledList(seatListModel);
                        int seatOption = showStyledDialog("Select a Seat", seatList);

                        if (seatOption == JOptionPane.OK_OPTION) {
                            String selectedSeat = seatList.getSelectedValue();
                            if (selectedSeat == null) return;
                            int seatNumber = Integer.parseInt(selectedSeat.split(" ")[1]);
                            reserveSeat(conn, selectedTrain, seatNumber);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Database error: " + ex.getMessage());
        }
    }

    private JList<String> createStyledList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        list.setSelectionBackground(new Color(232, 240, 254));
        list.setSelectionForeground(new Color(0, 123, 255));
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Custom Renderer for a "Card" look in the list
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel panel = new JPanel(new BorderLayout(10, 5));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                panel.setBackground(isSelected ? list.getSelectionBackground() : Color.WHITE);

                String text = (String) value;
                String[] parts = text.split(" \\| ");
                
                // Left Side: Train Info
                JLabel trainLabel = new JLabel(parts[0]);
                trainLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                trainLabel.setForeground(isSelected ? list.getSelectionForeground() : new Color(33, 37, 41));
                panel.add(trainLabel, BorderLayout.NORTH);

                // Sub-info: Seat and Date/ID
                if (parts.length > 1) {
                    String subText = parts[1];
                    if (parts.length > 2) subText += " • " + parts[2];
                    JLabel subLabel = new JLabel(subText);
                    subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    subLabel.setForeground(new Color(108, 117, 125));
                    panel.add(subLabel, BorderLayout.SOUTH);
                }

                // Separator line
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 243, 244)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));

                return panel;
            }
        });
        
        return list;
    }

    private int showStyledDialog(String title, JList<String> list) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        
        JLabel header = new JLabel(title);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        container.add(header, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(350, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224), 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);

        return JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int getTrainId(Connection conn, String trainName) throws SQLException {
        String query = "SELECT train_id FROM trains WHERE train_name = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, trainName);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("train_id");
            }
        }
        return -1;
    }

    private int getSeatId(Connection conn, int trainId, int seatNumber) throws SQLException {
        String query = "SELECT seat_id FROM seats WHERE train_id = ? AND seat_number = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, trainId);
            pst.setInt(2, seatNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt("seat_id");
            }
        }
        return -1;
    }

    private void reserveSeat(Connection conn, String trainName, int seatNumber) throws SQLException {
        int trainId = getTrainId(conn, trainName);
        int seatId = getSeatId(conn, trainId, seatNumber);

        if (seatId != -1) {
            String reserveQuery = "INSERT INTO reservations (user_id, train_id, seat_id, booking_date) VALUES (?, ?, ?, CURDATE())";
            try (PreparedStatement pstReserve = conn.prepareStatement(reserveQuery)) {
                pstReserve.setInt(1, userId);
                pstReserve.setInt(2, trainId);
                pstReserve.setInt(3, seatId);
                pstReserve.executeUpdate();

                String updateSeatQuery = "UPDATE seats SET is_booked = TRUE WHERE seat_id = ?";
                try (PreparedStatement pstUpdateSeat = conn.prepareStatement(updateSeatQuery)) {
                    pstUpdateSeat.setInt(1, seatId);
                    pstUpdateSeat.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Ticket booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void cancelTicket() {
        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT trains.train_name, seats.seat_number, reservations.reservation_id " +
                    "FROM reservations " +
                    "JOIN seats ON reservations.seat_id = seats.seat_id " +
                    "JOIN trains ON reservations.train_id = trains.train_id " +
                    "WHERE reservations.user_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    DefaultListModel<String> bookingListModel = new DefaultListModel<>();
                    while (rs.next()) {
                        bookingListModel.addElement(rs.getString("train_name") + " | Seat " + rs.getInt("seat_number") + " | ID: " + rs.getInt("reservation_id"));
                    }

                    JList<String> bookingList = createStyledList(bookingListModel);
                    int option = showStyledDialog("Select a Booking to Cancel", bookingList);

                    if (option == JOptionPane.OK_OPTION && bookingList.getSelectedValue() != null) {
                        String selected = bookingList.getSelectedValue();
                        int reservationId = Integer.parseInt(selected.split("ID: ")[1]);
                        
                        // Execute cancel
                        String cancelQuery = "DELETE FROM reservations WHERE reservation_id = ?";
                        try (PreparedStatement cpst = conn.prepareStatement(cancelQuery)) {
                            cpst.setInt(1, reservationId);
                            cpst.executeUpdate();
                            JOptionPane.showMessageDialog(this, "Booking canceled successfully.");
                            viewMyBookings();
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Database error.");
        }
    }

    private void viewMyBookings() {
        try (Connection conn = DBConfig.getConnection()) {
            String query = "SELECT trains.train_name, seats.seat_number, reservations.booking_date " +
                    "FROM reservations " +
                    "JOIN seats ON reservations.seat_id = seats.seat_id " +
                    "JOIN trains ON reservations.train_id = trains.train_id " +
                    "WHERE reservations.user_id = ? " +
                    "ORDER BY reservations.booking_date DESC";

            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, userId);
                try (ResultSet rs = pst.executeQuery()) {
                    DefaultListModel<String> bookingListModel = new DefaultListModel<>();
                    while (rs.next()) {
                        bookingListModel.addElement(rs.getString("train_name") + " | Seat: " + rs.getInt("seat_number") + " | " + rs.getDate("booking_date"));
                    }

                    JList<String> bookingList = createStyledList(bookingListModel);
                    showStyledDialog("Your Bookings", bookingList);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Database error.");
        }
    }
}
