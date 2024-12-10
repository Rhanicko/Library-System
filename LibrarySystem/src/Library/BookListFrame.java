package Library;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BookListFrame extends JFrame {
    private final ArrayList<String> books;

    public BookListFrame(ArrayList<String> books) {
        this.books = books;
        setTitle("Library Inventory");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("<html><h2>Library Books</h2></html>"));
        add(titlePanel, BorderLayout.NORTH);

        // Book List Display
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);

        if (books.isEmpty()) {
            JLabel emptyLabel = new JLabel("No books available in the library.");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            listPanel.add(emptyLabel);
        } else {
            for (String book : books) {
                JLabel bookLabel = new JLabel("<html><p style='margin:5px;'>" + book + "</p></html>");
                bookLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
                listPanel.add(bookLabel);
            }
        }

        add(scrollPane, BorderLayout.CENTER);
    }
}
