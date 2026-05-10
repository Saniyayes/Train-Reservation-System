import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public LoginFrame() {
        // Frame Settings
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Background color
        getContentPane().setBackground(Color.WHITE);

        // Main content panel with padding and center layout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Header / Title
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Train Reservation System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        contentPanel.add(titleLabel, gbc);

        // Reset gridwidth for fields
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Email Label
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        emailLabel.setForeground(new Color(108, 117, 125));
        contentPanel.add(emailLabel, gbc);

        // Email Field
        gbc.gridy++;
        emailField = new JTextField(20);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        contentPanel.add(emailField, gbc);

        // Password Label
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passwordLabel.setForeground(new Color(108, 117, 125));
        contentPanel.add(passwordLabel, gbc);

        // Password Field
        gbc.gridy++;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        contentPanel.add(passwordField, gbc);

        // Button Panel
        gbc.gridy++;
        gbc.insets = new Insets(25, 20, 10, 20);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        loginButton = new JButton("Login");
        signupButton = new JButton("Create Account");
        customizeButton(loginButton);
        customizeButton(signupButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        contentPanel.add(buttonPanel, gbc);

        // Add content to frame
        add(contentPanel, BorderLayout.CENTER);
        setSize(450, 420);

        // Button actions
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
        });
    }

    // Customize buttons with a premium "ghost" style for better compatibility and look
    private void customizeButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(new Color(0, 123, 255)); // Primary Blue text
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Compound border for padding and rounded line border
        button.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(0, 123, 255), 2, true),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        // Hover effect: slight background change and color swap
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
                button.setBackground(new Color(0, 123, 255));
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(0, 123, 255));
            }
        });
    }

    // Handle login logic
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        int userId = authenticateUser(email, password);
        if (userId != -1) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            new MainDashboard(userId).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }

    // Authenticate and return user ID or -1 if invalid
    private int authenticateUser(String email, String password) {
        try (Connection conn = DBConfig.getConnection()) {
                String query = "SELECT user_id FROM users WHERE email = ? AND password = ?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, email);
                    pst.setString(2, password);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("user_id");
                        }
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }

        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
