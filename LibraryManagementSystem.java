import java.sql.*;
import java.util.Scanner;

// Book class
class Book {
    private int bookID;
    private String title;
    private String author;
    private String genre;
    private boolean availabilityStatus;

    // Constructor
    public Book(int bookID, String title, String author, String genre, boolean availabilityStatus) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availabilityStatus = availabilityStatus;
    }

    // Getters
    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean getAvailabilityStatus() {
        return availabilityStatus;
    }
}

// User class
class User {
    private int userID;
    private String userName;
    private String contactInfo;
    private int borrowedBooks;

    // Constructor
    public User(int userID, String userName, String contactInfo, int borrowedBooks) {
        this.userID = userID;
        this.userName = userName;
        this.contactInfo = contactInfo;
        this.borrowedBooks = borrowedBooks;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public int getBorrowedBooks() {
        return borrowedBooks;
    }
}

// Library class
class Library implements AutoCloseable {
    private final Connection connection;
    private PreparedStatement addBookStatement;
    private PreparedStatement addUserStatement;
    private PreparedStatement checkOutBookStatement;
    private PreparedStatement returnBookStatement;
    private PreparedStatement searchBooksStatement;
    private PreparedStatement displayUserInfoStatement;
    private PreparedStatement updateBookInfoStatement;
    private PreparedStatement updateUserInfoStatement;
    private PreparedStatement deleteUserStatement;

    // Constructor
    public Library(Connection connection) {
        this.connection = connection;
        prepareStatements();
    }

    // Prepare SQL statements
    private void prepareStatements() {
        try {
            addBookStatement = connection.prepareStatement("INSERT INTO Books (book_ID, title, author, genre, availability_status) VALUES (?, ?, ?, ?, ?)");
            addUserStatement = connection.prepareStatement("INSERT INTO Users (user_ID, user_name, contact_info, borrowed_books) VALUES (?, ?, ?, ?)");
            checkOutBookStatement = connection.prepareStatement("UPDATE Books SET availability_status = FALSE WHERE book_ID = ?");
            returnBookStatement = connection.prepareStatement("UPDATE Books SET availability_status = TRUE WHERE book_ID = ?");
            searchBooksStatement = connection.prepareStatement("SELECT * FROM Books WHERE title LIKE ? OR author LIKE ?");
            displayUserInfoStatement = connection.prepareStatement("SELECT * FROM Users WHERE user_ID = ?");
            updateBookInfoStatement = connection.prepareStatement("UPDATE Books SET title = ?, author = ?, genre = ?, availability_status = ? WHERE book_ID = ?");
            updateUserInfoStatement = connection.prepareStatement("UPDATE Users SET user_name = ?, contact_info = ? WHERE user_ID = ?");
            deleteUserStatement = connection.prepareStatement("DELETE FROM Users WHERE user_ID = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to add new book
    public void addBook(Book book) {
        try {
            addBookStatement.setInt(1, book.getBookID());
            addBookStatement.setString(2, book.getTitle());
            addBookStatement.setString(3, book.getAuthor());
            addBookStatement.setString(4, book.getGenre());
            addBookStatement.setBoolean(5, book.getAvailabilityStatus());
            addBookStatement.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Entry with this value already exists. Please enter a valid ID.");
            } else {
                e.printStackTrace();
                System.err.println("Failed to add book.");
            }
        }
    }

    // Method to add new user
    public void addUser(User user) {
        try {
            addUserStatement.setInt(1, user.getUserID());
            addUserStatement.setString(2, user.getUserName());
            addUserStatement.setString(3, user.getContactInfo());
            addUserStatement.setInt(4, user.getBorrowedBooks());
            addUserStatement.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Entry with this value already exists. Please enter a valid ID.");
            } else {
                e.printStackTrace();
                System.err.println("Failed to add user.");
            }
        }
    }

    // Method to check out book to user
    public void checkOutBook(int userID, int bookID) {
        try {
            checkOutBookStatement.setInt(1, bookID);
            checkOutBookStatement.executeUpdate();

            PreparedStatement updateUserStatement = connection.prepareStatement("UPDATE Users SET borrowed_books = borrowed_books + 1 WHERE user_ID = ?");
            updateUserStatement.setInt(1, userID);
            updateUserStatement.executeUpdate();

            System.out.println("Book checked out successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to check out book.");
        }
    }

    // Method to return book
    public void returnBook(int bookID) {
        try {
            returnBookStatement.setInt(1, bookID);
            returnBookStatement.executeUpdate();

            System.out.println("Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to return book.");
        }
    }

    // Method to search for books by title or author
    public void searchBooks(String searchQuery) {
        try {
            searchBooksStatement.setString(1, "%" + searchQuery + "%");
            searchBooksStatement.setString(2, "%" + searchQuery + "%");
            ResultSet resultSet = searchBooksStatement.executeQuery();

            System.out.println("Search results:");
            while (resultSet.next()) {
                int bookID = resultSet.getInt("book_ID");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                boolean availabilityStatus = resultSet.getBoolean("availability_status");

                System.out.println("Book ID: " + bookID);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Genre: " + genre);
                System.out.println("Availability Status: " + (availabilityStatus ? "Available" : "Not Available"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to search for books.");
        }
    }

    // Method to display user information
    public void displayUserInfo(int userID) {
        try {
            displayUserInfoStatement.setInt(1, userID);
            ResultSet resultSet = displayUserInfoStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_ID");
                String name = resultSet.getString("user_name");
                String contactInfo = resultSet.getString("contact_info");
                int borrowedBooks = resultSet.getInt("borrowed_books");

                System.out.println("User ID: " + userId);
                System.out.println("Name: " + name);
                System.out.println("Contact Information: " + contactInfo);
                System.out.println("Number of Borrowed Books: " + borrowedBooks);
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to display user information.");
        }
    }

    // Method to update book information
    public void updateBookInfo(Book updatedBook) {
        try {
            updateBookInfoStatement.setString(1, updatedBook.getTitle());
            updateBookInfoStatement.setString(2, updatedBook.getAuthor());
            updateBookInfoStatement.setString(3, updatedBook.getGenre());
            updateBookInfoStatement.setBoolean(4, updatedBook.getAvailabilityStatus());
            updateBookInfoStatement.setInt(5, updatedBook.getBookID());
            int rowsUpdated = updateBookInfoStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Book information updated successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to update book information.");
        }
    }

    // Method to update user information
    public void updateUserInfo(User updatedUser) {
        try {
            updateUserInfoStatement.setString(1, updatedUser.getUserName());
            updateUserInfoStatement.setString(2, updatedUser.getContactInfo());
            updateUserInfoStatement.setInt(3, updatedUser.getUserID());
            int rowsUpdated = updateUserInfoStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User information updated successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to update user information.");
        }
    }

    // Method to delete user by user ID
    public void deleteUser(int userID) {
        try {
            deleteUserStatement.setInt(1, userID);
            int rowsDeleted = deleteUserStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to delete user.");
        }
    }

    // Implementing the close method of AutoCloseable interface
    public void close() {
        try {
            if (addBookStatement != null) addBookStatement.close();
            if (addUserStatement != null) addUserStatement.close();
            if (checkOutBookStatement != null) checkOutBookStatement.close();
            if (returnBookStatement != null) returnBookStatement.close();
            if (searchBooksStatement != null) searchBooksStatement.close();
            if (displayUserInfoStatement != null) displayUserInfoStatement.close();
            if (updateBookInfoStatement != null) updateBookInfoStatement.close();
            if (updateUserInfoStatement != null) updateUserInfoStatement.close();
            if (deleteUserStatement != null) deleteUserStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Main class
public class LibraryManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome To Library Management System");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/LIBRARY_MANAGEMENT", "root", "MySQL@123");
             Library library = new Library(connection)) {

            displayMenu(library);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database.");
        }
    }

    // Display menu
    private static void displayMenu(Library library) {
        int choice;
        do {
            System.out.println("Library Management System Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Add User");
            System.out.println("3. Check Out Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Display User Information");
            System.out.println("7. Update Book Information");
            System.out.println("8. Update User Information");
            System.out.println("9. Delete User");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline character
            handleMenuChoice(choice, library);
        } while (choice != 10);
    }

    // Handle menu choice
    private static void handleMenuChoice(int choice, Library library) {
        switch (choice) {
            case 1:
                addBook(library);
                break;
            case 2:
                addUser(library);
                break;
            case 3:
                checkOutBook(library);
                break;
            case 4:
                returnBook(library);
                break;
            case 5:
                searchBooks(library);
                break;
            case 6:
                displayUserInfo(library);
                break;
            case 7:
                updateBookInfo(library);
                break;
            case 8:
                updateUserInfo(library);
                break;
            case 9:
                deleteUser(library);
                break;
            case 10:
                System.out.println("Exiting the program.");
                break;
            default:
                System.out.println("Invalid choice. Please enter a number between 1 and 10.");
        }
    }

    // Method to add new book
    private static void addBook(Library library) {
        System.out.println("Enter book details:");
        System.out.print("ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Availability Status (true/false): ");
        boolean availabilityStatus = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline character

        Book book = new Book(bookID, title, author, genre, availabilityStatus);
        library.addBook(book);
    }

    // Method to add new user
    private static void addUser(Library library) {
        System.out.println("Enter user details:");
        System.out.print("ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        System.out.print("Name: ");
        String userName = scanner.nextLine();
        System.out.print("Contact Info: ");
        String contactInfo = scanner.nextLine();
        System.out.print("Borrowed Books: ");
        int borrowedBooks = scanner.nextInt();

        User user = new User(userID, userName, contactInfo, borrowedBooks);
        library.addUser(user);
    }

    // Method to check out book to user
    private static void checkOutBook(Library library) {
        System.out.print("Enter User ID: ");
        int userID = scanner.nextInt();
        System.out.print("Enter Book ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        library.checkOutBook(userID, bookID);
    }

    // Method to return book
    private static void returnBook(Library library) {
        System.out.print("Enter Book ID: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        library.returnBook(bookID);
    }

    // Method to search for books by title or author
    private static void searchBooks(Library library) {
        System.out.print("Enter search query (title or author): ");
        String searchQuery = scanner.nextLine();
        library.searchBooks(searchQuery);
    }

    // Method to display user information
    private static void displayUserInfo(Library library) {
        System.out.print("Enter User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        library.displayUserInfo(userID);
    }

    // Method to update book information
    private static void updateBookInfo(Library library) {
        System.out.print("Enter Book ID to update: ");
        int bookID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();
        System.out.print("Enter new author: ");
        String newAuthor = scanner.nextLine();
        System.out.print("Enter new genre: ");
        String newGenre = scanner.nextLine();
        System.out.print("Enter new availability status (true/false): ");
        boolean newAvailabilityStatus = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline character

        Book updatedBook = new Book(bookID, newTitle, newAuthor, newGenre, newAvailabilityStatus);
        library.updateBookInfo(updatedBook);
    }

    // Method to update user information
    private static void updateUserInfo(Library library) {
        System.out.print("Enter User ID to update: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new contact information: ");
        String newContactInfo = scanner.nextLine();

        User updatedUser = new User(userID, newName, newContactInfo, 0); // Assuming no need to update borrowed books count
        library.updateUserInfo(updatedUser);
    }

    // Method to delete user by user ID
    private static void deleteUser(Library library) {
        System.out.print("Enter User ID to delete: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        library.deleteUser(userID);
    }
}
