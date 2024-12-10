package Library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Login {
    private static final HashMap<String, String> credentials = new HashMap<>();

    static {
        // Initialize credentials with secure passwords
        credentials.put("admin", ""); // Let the password clear for now
        credentials.put("user", ""); // Let the password clear for now
    }

    public void login() {
        JFrame frame = new JFrame("Library System Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon("loginbackground.jpg");
        Image backgroundImg = backgroundIcon.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(backgroundImg));
        backgroundLabel.setLayout(new GridBagLayout());

        // Components
        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField(20);
        userLabel.setForeground(Color.black); // Set text color to black

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.black); // Set text color to black
        JPasswordField passText = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("");

        // Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundLabel.add(userLabel, gbc);

        gbc.gridx = 1;
        backgroundLabel.add(userText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundLabel.add(passLabel, gbc);

        gbc.gridx = 1;
        backgroundLabel.add(passText, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        backgroundLabel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        backgroundLabel.add(messageLabel, gbc);

        frame.add(backgroundLabel);

        // Login button action listener
        loginButton.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword());

            if (credentials.containsKey(username) && credentials.get(username).equals(password)) {
                frame.dispose();
                if (username.equals("admin")) {
                    new Admin().admin();
                } else {
                    new User(username).user();
                }
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password. Try again.");
            }
        });

        frame.setVisible(true);
    }


    public static void main(String[] args) {
        new Login().login();
    }
}
