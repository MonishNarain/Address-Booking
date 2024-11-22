import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddressBookApp {

    // Database Connection Utility
    private static Connection getConnection() throws SQLException {
        String URL = "jdbc:mysql://localhost:3306/address"; // Ensure this matches your database
        String USER = "root";
        String PASSWORD = ""; // Replace with your actual MySQL password
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Contact Model Class
    static class Contact {
        private String name;
        private String email;
        private String phone;
        private String address;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    // DAO Methods for CRUD Operations
    static class ContactDAO {
        public void addContact(Contact contact) throws SQLException {
            String query = "INSERT INTO contacts (name, email, phone, address) VALUES (?, ?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, contact.getName());
                stmt.setString(2, contact.getEmail());
                stmt.setString(3, contact.getPhone());
                stmt.setString(4, contact.getAddress());
                stmt.executeUpdate();
            }
        }

        public List<Contact> getAllContacts() throws SQLException {
            List<Contact> contacts = new ArrayList<>();
            String query = "SELECT * FROM contacts";
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Contact contact = new Contact();
                    contact.setName(rs.getString("name"));
                    contact.setEmail(rs.getString("email"));
                    contact.setPhone(rs.getString("phone"));
                    contact.setAddress(rs.getString("address"));
                    contacts.add(contact);
                }
            }
            return contacts;
        }

        public void updateContact(Contact contact, String oldEmail) throws SQLException {
            String query = "UPDATE contacts SET name = ?, email = ?, phone = ?, address = ? WHERE email = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, contact.getName());
                stmt.setString(2, contact.getEmail());
                stmt.setString(3, contact.getPhone());
                stmt.setString(4, contact.getAddress());
                stmt.setString(5, oldEmail);
                stmt.executeUpdate();
            }
        }

        public void deleteContact(String email) throws SQLException {
            String query = "DELETE FROM contacts WHERE email = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.executeUpdate();
            }
        }
    }

    // Main Application
    public static void main(String[] args) {
        ContactDAO contactDAO = new ContactDAO();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nAddress Book");
            System.out.println("1. Add Contact");
            System.out.println("2. View All Contacts");
            System.out.println("3. Update Contact");
            System.out.println("4. Delete Contact");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            try {
                switch (choice) {
                    case 1: // Add Contact
                        Contact newContact = new Contact();
                        scanner.nextLine(); // Clear the buffer
                        System.out.print("Enter name: ");
                        newContact.setName(scanner.nextLine());
                        System.out.print("Enter email: ");
                        newContact.setEmail(scanner.nextLine());
                        System.out.print("Enter phone: ");
                        String phone = scanner.nextLine();
                        if (phone.length() > 15) {
                            System.out.println("Phone number is too long. Please enter a valid phone number.");
                            break;
                        }
                        newContact.setPhone(phone);
                        System.out.print("Enter address: ");
                        newContact.setAddress(scanner.nextLine());
                        contactDAO.addContact(newContact);
                        System.out.println("Contact added successfully!");
                        break;

                    case 2: // View All Contacts
                        List<Contact> contacts = contactDAO.getAllContacts();
                        if (contacts.isEmpty()) {
                            System.out.println("No contacts found.");
                        } else {
                            for (Contact contact : contacts) {
                                System.out.println(contact.getName() + " - " +
                                        contact.getEmail() + " - " + contact.getPhone() + " - " +
                                        contact.getAddress());
                            }
                        }
                        break;

                    case 3: // Update Contact
                        System.out.print("Enter email of the contact to update: ");
                        String oldEmail = scanner.next();
                        scanner.nextLine(); // Clear the buffer
                        Contact updatedContact = new Contact();
                        System.out.print("Enter new name: ");
                        updatedContact.setName(scanner.nextLine());
                        System.out.print("Enter new email: ");
                        updatedContact.setEmail(scanner.nextLine());
                        System.out.print("Enter new phone: ");
                        phone = scanner.nextLine();
                        if (phone.length() > 15) {
                            System.out.println("Phone number is too long. Please enter a valid phone number.");
                            break;
                        }
                        updatedContact.setPhone(phone);
                        System.out.print("Enter new address: ");
                        updatedContact.setAddress(scanner.nextLine());
                        contactDAO.updateContact(updatedContact, oldEmail);
                        System.out.println("Contact updated successfully!");
                        break;

                    case 4: // Delete Contact
                        System.out.print("Enter email of the contact to delete: ");
                        String email = scanner.next();
                        contactDAO.deleteContact(email);
                        System.out.println("Contact deleted successfully!");
                        break;

                    case 5: // Exit
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }
}