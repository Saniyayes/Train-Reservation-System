# Train Reservation System Setup Guide

This project is a Java Swing application for managing train reservations. It uses a MySQL database to store information about users, trains, seats, and bookings.

## 1. Prerequisites

Before you start, ensure you have the following installed on your system:
- **Java Development Kit (JDK)**: Version 8 or higher.
- **MySQL Server**: Running on `localhost:3306`.
- **MySQL Connector/J**: Already provided in the `lib` folder.

## 2. Database Setup

You need to set up the database and tables for the application to work.

1.  **Open your MySQL Terminal** or a GUI like MySQL Workbench.
2.  **Create the database**:
    ```sql
    CREATE DATABASE reservation_system;
    USE reservation_system;
    ```
3.  **Run the SQL commands**: Copy and paste the contents of [SQL CODE.txt](file:///Users/rishit/Downloads/ReservationSystem/SQL%20CODE.txt) into your MySQL terminal to create the tables (`users`, `trains`, `seats`, `reservations`) and seed some initial data.

> [!IMPORTANT]
> The application is configured with the following credentials:
> - **Username**: `root`
> - **Password**: `cse`
> 
> If your MySQL password is different, you will need to update it in several files: `Main.java`, `MainDashboard.java`, `SignupFrame.java`, `BookTicketFrame.java`, and `LoginFrame.java`.

## 3. How to Run the Application

You can compile and run the application from your terminal.

### Step 1: Compile the Code
Run the following command to compile all Java files. This command includes the MySQL connector in the classpath.

```bash
javac -cp ".:lib/mysql-connector-j-9.3.0.jar" *.java
```

### Step 2: Start the Application
Once compiled, run the `Main` class:

```bash
java -cp ".:lib/mysql-connector-j-9.3.0.jar" Main
```

## 4. Using the Application

1.  **Login**: Once the application starts, you'll see a Login screen. You can use one of the seeded users from the database (e.g., Email: `ravi@example.com`, Password: `1234`).
2.  **Dashboard**: After logging in, you'll see the Main Dashboard where you can view available trains.
3.  **Booking**: You can select a train and book a seat if available.
4.  **Signup**: If you don't have an account, you can create one using the "Sign Up" button on the login screen.

## 5. Project Structure
- `Main.java`: Entry point of the application.
- `LoginFrame.java`: User authentication screen.
- `MainDashboard.java`: Main interface showing trains and options.
- `BookTicketFrame.java`: UI for selecting and booking seats.
- `lib/`: Contains the MySQL JDBC driver.
- `SQL CODE.txt`: Database schema and sample data.
