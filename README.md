# CS212-Assignment-01.Tamkeen_Sara
# Library Management System

## Description
This project is a library management system implemented in Java. It allows users to perform various operations such as adding books and users, checking out and returning books, searching for books by title or author, displaying user information, updating book and user information, and deleting users from the system. The system interacts with a MySQL database to store and retrieve information about books and users.

## Table of Contents
- [Installation](#installation)
- [Prerequisites](#prerequisites)
- [Usage](#usage)
- [Features](#features)
- [Dependencies](#dependencies)

## Installation
To install and run the project locally, follow these steps:

1. Clone the GitHub repository:
   ```bash
   git clone https://github.com/your_username/library-management-system.git
2. Set up a MySQL database named LIBRARY_MANAGEMENT.

3. Create the necessary tables in the database using the provided SQL schema.

4. Configure the database connection details (URL, username, password) in the LibraryManagementSystem class.

5. Compile the Java files in the project:
   javac *.java
   
## Prerequisites
Before running the project, make sure you have the following software installed:

1. Java Development Kit (JDK)
2. MySQL database server
3. MySQL Connector/J (JDBC driver for MySQL)
4. IDE for Java development (e.g., IntelliJ IDEA, Eclipse)


## Usage
Follow these steps to run the project:

1. Open a terminal or command prompt.
2. Navigate to the project directory.
3.Run the LibraryManagementSystem class:
java LibraryManagementSystem
4. Follow the on-screen instructions to use the library management system.

## Features

1. Adding books and users to the library database.
2. Checking out and returning books for users.
3. Searching for books by title or author.
4. Displaying user information, including the number of borrowed books.
5. Updating book and user information, such as title, author, and contact information.
6. Deleting users from the system.
7. Error handling for duplicate entries and invalid inputs.

## Dependencies
The program requires some components to run properly. To run on any computer, it would require the JVM.It would also require a valid connection with a mysql server. 
The database should contain the following tables 
Books Table
Must contain columns: book_ID,title, author, genre, availability_status
Users Table
Must contain columns: user_ID, user_name, contact_info,borrowed_books
