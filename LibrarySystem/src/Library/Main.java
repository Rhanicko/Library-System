package Library;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize and start the login process
            Login login = new Login();
            login.login();
        } catch (Exception e) {
            // Catch any unexpected errors
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
