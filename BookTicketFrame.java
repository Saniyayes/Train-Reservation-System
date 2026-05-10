import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class BookTicketFrame extends JFrame {
    private JComboBox<String> trainList;
    private JComboBox<Integer> seatList;
    private JButton bookButton;

    public BookTicketFrame(int userId) {
        setTitle("Reserve a Seat");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Header
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Book Your Ticket", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        contentPanel.add(titleLabel, gbc);

        // Train selection
        gbc.gridy++;
        contentPanel.add(createStyledLabel("Select Train"), gbc);
        gbc.gridy++;
        trainList = createStyledComboBox();
        contentPanel.add(trainList, gbc);

        // Seat selection
        gbc.gridy++;
        contentPanel.add(createStyledLabel("Available Seats"), gbc);
        gbc.gridy++;
        seatList = new JComboBox<>(); // I'll style it similarly
        seatList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        seatList.setBackground(Color.WHITE);
        contentPanel.add(seatList, gbc);

        // Book Button
        gbc.gridy++;
        gbc.insets = new Insets(30, 30, 20, 30);
        bookButton = new JButton("Confirm Booking");
        customizeButton(bookButton);
        contentPanel.add(bookButton, gbc);

        add(contentPanel, BorderLayout.CENTER);

        loadTrains();

        trainList.addActionListener(e -> loadSeats());
        bookButton.addActionListener(e -> bookSeat(userId));

        setVisible(true);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(108, 117, 125));
        return label;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font("SansSerif", Font.PLAIN, 15));
        cb.setBackground(Color.WHITE);
        return cb;
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(new Color(40, 167, 69)); // Success Green
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 45));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 167, 69));
            }
        });
    }

    private void loadTrains() {
        trainList.removeAllItems();
        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT train_id, train_name FROM trains";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("train_id");
                String name = rs.getString("train_name");
                trainList.addItem(id + " - " + name);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSeats() {
        seatList.removeAllItems();
        if (trainList.getSelectedItem() == null)
            return;

        int trainId = Integer.parseInt(trainList.getSelectedItem().toString().split(" - ")[0]);

        try (Connection conn = DBConfig.getConnection()) {
            String sql = "SELECT seat_number FROM seats WHERE train_id = ? AND is_booked = FALSE";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, trainId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                seatList.addItem(rs.getInt("seat_number"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void bookSeat(int userId) {
        if (trainList.getSelectedItem() == null || seatList.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select both train and seat.");
            return;
        }

        int trainId = Integer.parseInt(trainList.getSelectedItem().toString().split(" - ")[0]);
        int seatNumber = (Integer) seatList.getSelectedItem();

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);

            String seatQuery = "SELECT seat_id FROM seats WHERE train_id = ? AND seat_number = ? AND is_booked = FALSE";
            PreparedStatement pst = conn.prepareStatement(seatQuery);
            pst.setInt(1, trainId);
            pst.setInt(2, seatNumber);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int seatId = rs.getInt("seat_id");

                String reserveSql = "INSERT INTO reservations (user_id, train_id, seat_id, booking_date) VALUES (?, ?, ?, ?)";
                PreparedStatement insert = conn.prepareStatement(reserveSql);
                insert.setInt(1, userId);
                insert.setInt(2, trainId);
                insert.setInt(3, seatId);
                insert.setDate(4, Date.valueOf(LocalDate.now()));
                insert.executeUpdate();

                String updateSql = "UPDATE seats SET is_booked = TRUE WHERE seat_id = ?";
                PreparedStatement update = conn.prepareStatement(updateSql);
                update.setInt(1, seatId);
                update.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Seat booked successfully!");
                loadSeats();
            } else {
                JOptionPane.showMessageDialog(this, "Seat already booked.");
                conn.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
