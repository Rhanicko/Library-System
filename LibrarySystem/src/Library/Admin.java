				package Library;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Admin {

	public void admin() {
	    JFrame frame = new JFrame("Admin Panel");
	    frame.setSize(400, 500);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setLocationRelativeTo(null);
	    frame.setLayout(new BorderLayout(10, 10));

	    // Title Panel
	    JPanel titlePanel = new JPanel();
	    titlePanel.setBackground(new Color(0, 123, 255)); // Blue background for the title
	    JLabel titleLabel = new JLabel("Library System");
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    titleLabel.setForeground(Color.WHITE); // White text for better contrast
	    titlePanel.add(titleLabel);

	    // Clock Panel
	    JPanel clockPanel = new JPanel();
	    clockPanel.setBackground(new Color(200, 230, 255)); // Light blue background
	    JLabel clockLabel = new JLabel();
	    clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
	    updateClock(clockLabel); // Initialize clock display
	    startClock(clockLabel); // Start updating the clock
	    clockPanel.add(clockLabel);

	    // Combine Title and Clock in Header Panel
	    JPanel headerPanel = new JPanel();
	    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS)); // Vertical stacking
	    headerPanel.add(titlePanel);
	    headerPanel.add(clockPanel);

	    // Main panel for buttons
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Adjusted for 4 buttons
	    mainPanel.setBackground(new Color(230, 230, 255)); // Light blue background

	    // Font for buttons
	    Font buttonFont = new Font("Arial", Font.BOLD, 14);

	    // Buttons
	    JButton addBookButton = new JButton("Add Book");
	    JButton removeBookButton = new JButton("Remove Book");
	    JButton viewInventoryButton = new JButton("View Inventory");
	    JButton logoutButton = new JButton("Logout");

	    // Style buttons
	    styleButton(addBookButton, buttonFont);
	    styleButton(removeBookButton, buttonFont);
	    styleButton(viewInventoryButton, buttonFont);
	    styleButton(logoutButton, buttonFont);

	    // Add buttons to the panel
	    mainPanel.add(addBookButton);
	    mainPanel.add(removeBookButton);
	    mainPanel.add(viewInventoryButton);
	    mainPanel.add(logoutButton);

	    // Add panels to the frame
	    frame.add(headerPanel, BorderLayout.NORTH); // Header (Title + Clock) at the top
	    frame.add(mainPanel, BorderLayout.CENTER);
        // Action Listeners
        addBookButton.addActionListener(e -> {
            JTextField bookNameField = new JTextField();
            JTextField authorField = new JTextField();
            JTextField yearField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Book Name:"));
            panel.add(bookNameField);
            panel.add(new JLabel("Author:"));
            panel.add(authorField);
            panel.add(new JLabel("Year of Publication:"));
            panel.add(yearField);
            	
         // Create the JOptionPane and JDialog
            JOptionPane result = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = result.createDialog(frame, "Enter Book Details");
            dialog.setSize(400, 180); // Set custom size
            dialog.setVisible(true);

            // Get the user's choice from the JOptionPane
            Object selectedValue = result.getValue();
            if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
                // Retrieve input values
                String bookName = bookNameField.getText().trim();
                String author = authorField.getText().trim();
                String year = yearField.getText().trim();

                if (!bookName.isEmpty() && !author.isEmpty() && !year.isEmpty()) {
                    try {
                        Integer.parseInt(year); // Validate year as a number
                        ArrayList<String[]> books = BookDatabase.readBooksFromFile();
                        boolean exists = books.stream().anyMatch(book -> book[0].equalsIgnoreCase(bookName));
                        if (exists) {
                            JOptionPane.showMessageDialog(frame, "Book already exists in the database.");
                        } else {
                            books.add(new String[]{bookName, author, year, "Available"});
                            BookDatabase.writeBooksToFile(books);
                            JOptionPane.showMessageDialog(frame, "Book added successfully!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Year must be a valid number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "All fields are required.");
                }
            }

        });

        removeBookButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String> availableBooks = new ArrayList<>();
            for (String[] book : books) {
                if (book[3].equalsIgnoreCase("Available")) {
                    availableBooks.add(book[0]); // Add book title
                }
            }

            if (availableBooks.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No available books to remove.");
                return;
            }

            // Create a panel for search and selection
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JLabel searchLabel = new JLabel("Search Book:");
            JTextField searchField = new JTextField();
            JList<String> bookList = new JList<>(availableBooks.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(bookList);

            panel.add(searchLabel, BorderLayout.NORTH);
            panel.add(searchField, BorderLayout.CENTER);
            panel.add(scrollPane, BorderLayout.SOUTH);

            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterList(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterList(); }

                private void filterList() {
                    String searchText = searchField.getText().toLowerCase();
                    DefaultListModel<String> filteredModel = new DefaultListModel<>();
                    for (String book : availableBooks) {
                        if (book.toLowerCase().contains(searchText)) {
                            filteredModel.addElement(book);
                        }
                    }
                    bookList.setModel(filteredModel);
                }
            });

            JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(frame, "Select Book to Remove");
            dialog.setSize(400, 300); // Set custom size
            dialog.setVisible(true);

            // Get the user's choice
            Object selectedValue = optionPane.getValue();
            if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
                String selectedBook = bookList.getSelectedValue();
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(frame, "No book selected.");
                    return;
                }

                boolean isRemoved = books.removeIf(book -> book[0].equalsIgnoreCase(selectedBook.trim()) && book[3].equalsIgnoreCase("Available"));
                if (isRemoved) {
                    BookDatabase.writeBooksToFile(books);
                    JOptionPane.showMessageDialog(frame, "Book removed: " + selectedBook);
                } else {
                    JOptionPane.showMessageDialog(frame, "Something went wrong.");
                }
            }

        });


        viewInventoryButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();

            // Sort books alphabetically by title
            books.sort((a, b) -> a[0].compareToIgnoreCase(b[0]));

            // Create a table for displaying inventory
            String[] columnNames = {"Title", "Author", "Year", "Status"};
            String[][] data = books.toArray(new String[0][0]);
            JTable table = new JTable(data, columnNames);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            // Show inventory in a dialog
            JOptionPane.showMessageDialog(frame, scrollPane, "Book Inventory", JOptionPane.PLAIN_MESSAGE);
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new Login().login();
        });

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Font font) {
        button.setFont(font);
        button.setBackground(new Color(0, 123, 255)); // Blue color
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 40));
    }

    private void updateClock(JLabel clockLabel) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        clockLabel.setText(format.format(new Date()));
    }

    private void startClock(JLabel clockLabel) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateClock(clockLabel));
            }
        }, 0, 1000);
    }
}
