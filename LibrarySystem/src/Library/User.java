package Library;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class User {

    private final String username;

    public User(String username) {
        this.username = username;
    }

    public void user() {
        JFrame frame = new JFrame("User Panel( Library System)");
        frame.setSize(500, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 123, 255)); // Blue background for the title
        JLabel titleLabel = new JLabel("Library Syste");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE); // White text for better contrast
        titlePanel.add(titleLabel);

        // Clock Panel
        JPanel clockPanel = new JPanel();
        clockPanel.setBackground(new Color(200, 230, 255)); // Light blue
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateClock(clockLabel); // Initialize clock display
        startClock(clockLabel); // Start updating the clock
        clockPanel.add(clockLabel);

        // Combined Panel for Title and Clock
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(clockPanel, BorderLayout.SOUTH);

        // Main panel for buttons
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10)); // Adjusted to fit buttons
        mainPanel.setBackground(new Color(230, 230, 255)); // Light blue background

        // Font for buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        // Buttons
        JButton viewAvailableBooksButton = new JButton("View Available Books");
        JButton borrowBookButton = new JButton("Borrow Book");
        JButton returnBookButton = new JButton("Return Book");
        JButton viewBorrowedBooksButton = new JButton("View Borrowed Books");
        JButton logoutButton = new JButton("Logout");

        // Style buttons
        styleButton(viewAvailableBooksButton, buttonFont);
        styleButton(borrowBookButton, buttonFont);
        styleButton(returnBookButton, buttonFont);
        styleButton(viewBorrowedBooksButton, buttonFont);
        styleButton(logoutButton, buttonFont);

        // Add buttons to the panel
        mainPanel.add(viewAvailableBooksButton);
        mainPanel.add(borrowBookButton);
        mainPanel.add(returnBookButton);
        mainPanel.add(viewBorrowedBooksButton);
        mainPanel.add(logoutButton);

        // Add panels to the frame
        frame.add(topPanel, BorderLayout.NORTH); // Add combined title and clock panel to the top
        frame.add(mainPanel, BorderLayout.CENTER); 

        // Action Listeners
        viewAvailableBooksButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String[]> availableBooks = new ArrayList<>();

            for (String[] book : books) {
                if (book[3].equalsIgnoreCase("Available")) {
                    availableBooks.add(new String[]{book[0], book[1], book[2]});
                }
            }

            if (availableBooks.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books are currently available.", "Available Books", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Sort books alphabetically by title
            availableBooks.sort((a, b) -> a[0].compareToIgnoreCase(b[0]));

            // Create a table to display the books
            String[] columnNames = {"Title", "Author", "Year"};
            String[][] data = availableBooks.toArray(new String[0][0]);
            JTable table = new JTable(data, columnNames);
            table.setEnabled(false); // Make the table non-editable
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.setRowHeight(25);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            // Display the table in a dialog
            JOptionPane.showMessageDialog(frame, scrollPane, "Available Books", JOptionPane.PLAIN_MESSAGE);
        });


        borrowBookButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String> availableBooks = new ArrayList<>();
            for (String[] book : books) {
                if (book[3].equalsIgnoreCase("Available")) {
                    availableBooks.add(book[0]); // Add book title
                }
            }

            if (availableBooks.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No books are available for borrowing.");
                return;
            }

            // Create a custom panel for search and selection with better readability
            JPanel panel = new JPanel(new BorderLayout(20, 20));  // More padding between components
            JLabel searchLabel = new JLabel("Search Book:");
            JTextField searchField = new JTextField(25);  // Set width of search field for better readability
            JList<String> bookList = new JList<>(availableBooks.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(bookList);

            // Customize the scroll pane and list for a better look
            bookList.setVisibleRowCount(10);  // Show more rows in the list
            bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane.setPreferredSize(new Dimension(350, 200));  // Increase the size of the scroll pane for better view

            // Set text field properties
            searchField.setPreferredSize(new Dimension(250, 30));  // Set a fixed width for the text field

            panel.add(searchLabel, BorderLayout.NORTH);
            panel.add(searchField, BorderLayout.CENTER);
            panel.add(scrollPane, BorderLayout.SOUTH);

            // Filter the book list based on the search field input
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

    
            int result = JOptionPane.showConfirmDialog(frame, panel, "Select Book to Borrow", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String selectedBook = bookList.getSelectedValue();
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(frame, "No book selected.");
                    return;
                }

                // Borrowing process
                for (String[] book : books) {
                    if (book[0].equalsIgnoreCase(selectedBook.trim()) && book[3].equalsIgnoreCase("Available")) {
                        book[3] = username; // Assign book to the current user
                        BookDatabase.writeBooksToFile(books);
                        JOptionPane.showMessageDialog(frame, "Book borrowed: " + selectedBook);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(frame, "Something went wrong. Please try again.");
            }
        });







        returnBookButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String> borrowedBooks = new ArrayList<>();
            
            for (String[] book : books) {
                if (book[3].equalsIgnoreCase(username)) {
                    borrowedBooks.add(book[0]);
                }
            }

            if (borrowedBooks.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "You have no books to return.");
                return;
            }

            // Create a new dialog for returning books
            JDialog dialog = new JDialog(frame, "Return Book", true);
            dialog.setSize(400, 300);
            dialog.setLayout(new BorderLayout());
            dialog.setLocationRelativeTo(frame);

            // List of borrowed books
            JLabel label = new JLabel("Select a book to return:");
            JList<String> bookList = new JList<>(borrowedBooks.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(bookList);

            // Buttons
            JButton returnSelectedButton = new JButton("Return Selected");
            JButton returnAllButton = new JButton("Return All Books");
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            buttonPanel.add(returnSelectedButton);
            buttonPanel.add(returnAllButton);

            // Add components to the dialog
            dialog.add(label, BorderLayout.NORTH);
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            // ActionListener for "Return Selected"
            returnSelectedButton.addActionListener(ae -> {
                String selectedBook = bookList.getSelectedValue();
                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(dialog, "No book selected.");
                    return;
                }

                for (String[] book : books) {
                    if (book[0].equalsIgnoreCase(selectedBook.trim()) && book[3].equalsIgnoreCase(username)) {
                        book[3] = "Available";
                        BookDatabase.writeBooksToFile(books);
                        JOptionPane.showMessageDialog(dialog, "Book returned: " + selectedBook);
                        dialog.dispose(); // Close the dialog after returning the book
                        return;
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Something went wrong. Please try again.");
            });

            // ActionListener for "Return All Books"
            returnAllButton.addActionListener(ae -> {
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                        "Are you sure you want to return all your borrowed books?", 
                        "Confirm Return All", 
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    for (String[] book : books) {
                        if (book[3].equalsIgnoreCase(username)) {
                            book[3] = "Available";
                        }
                    }
                    BookDatabase.writeBooksToFile(books);
                    JOptionPane.showMessageDialog(dialog, "All books have been returned.");
                    dialog.dispose(); // Close the dialog after returning all books
                }
            });

            dialog.setVisible(true); // Show the dialog
        });




        viewBorrowedBooksButton.addActionListener(e -> {
            ArrayList<String[]> books = BookDatabase.readBooksFromFile();
            ArrayList<String[]> borrowedBooksList = new ArrayList<>();

            // Collect borrowed books by the user
            for (String[] book : books) {
                if (book[3].equalsIgnoreCase(username)) {
                    borrowedBooksList.add(book);
                }
            }

            // Check if there are borrowed books and display them
            if (borrowedBooksList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "You have no borrowed books.");
            } else {
                // Create a table model with data
                String[] columnNames = {"Title", "Author", "Year"};
                String[][] data = new String[borrowedBooksList.size()][3];

                for (int i = 0; i < borrowedBooksList.size(); i++) {
                    data[i][0] = borrowedBooksList.get(i)[0];
                    data[i][1] = borrowedBooksList.get(i)[1];
                    data[i][2] = borrowedBooksList.get(i)[2];
                }

                JTable borrowedBooksTable = new JTable(data, columnNames);
                borrowedBooksTable.setFillsViewportHeight(true);  // Makes the table fill the scroll pane

                // Add the table to a scroll pane to allow scrolling if the list is long
                JScrollPane scrollPane = new JScrollPane(borrowedBooksTable);
                scrollPane.setPreferredSize(new Dimension(400, 200));

                // Display the table in a JOptionPane
                JOptionPane.showMessageDialog(frame, scrollPane, "Your Borrowed Books", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new Login().login();
        });

        frame.setVisible(true);
    }

    private void styleButton(JButton button, Font font) {
        button.setFont(font);
        button.setBackground(new Color(0, 123, 255));
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
