package Library;

import java.io.*;
import java.util.ArrayList;

public class BookDatabase {

    private static final String FILE_PATH = "books.txt";

    static {
        initializeFile();
    }

    private static void initializeFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error initializing the book database file: " + e.getMessage());
            }
        }
    }

    public static ArrayList<String[]> readBooksFromFile() {
        ArrayList<String[]> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length == 4) { // Adjusted for new structure
                    books.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the book database: " + e.getMessage());
        }
        return books;
    }

    public static void writeBooksToFile(ArrayList<String[]> books) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String[] book : books) {
                bw.write(String.join(" | ", book)); // Adjusted for new structure
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to the book database: " + e.getMessage());
        }
    }

}
