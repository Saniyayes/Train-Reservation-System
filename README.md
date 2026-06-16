# 🚂 Train Reservation System
# Train Reservation System

Developed as a collaborative project.

Contributors:
- Saniya Patil
- Rishit
A modern, desktop-based **Train Reservation System** built with **Java Swing** and **MySQL**. This application provides a comprehensive solution for managing train bookings, user authentication, and real-time seat availability.

---

## 🌟 Key Features

- **🔐 Secure Authentication**: Integrated Login and Sign-up system for passengers.
- **📊 Interactive Dashboard**: A central hub to view available trains and manage bookings.
- **🎟️ Real-time Booking**: Select specific trains and book available seats instantly.
- **🗄️ Database Driven**: Fully persistent data storage using MySQL for users, trains, and reservations.
- **🎨 Modern UI**: A clean and intuitive user interface designed with Java Swing.

---

## 🛠️ Technology Stack

- **Language**: Java 8+
- **GUI Framework**: Java Swing
- **Database**: MySQL 8.0
- **Connectivity**: JDBC (MySQL Connector/J)
- **Containerization**: Docker (optional for database)

---

## 📂 Project Structure

- `Main.java`: The entry point of the application.
- `LoginFrame.java`: Handles user authentication.
- `SignupFrame.java`: Allows new users to register.
- `MainDashboard.java`: Displays available trains and booking options.
- `BookTicketFrame.java`: Facilitates the seat selection and booking process.
- `DBConfig.java`: Centralized database connection configuration.
- `lib/`: Contains the necessary JDBC driver for MySQL.
- `SQL CODE.txt`: SQL scripts for schema creation and data seeding.

---

## 🚀 Quick Start

### 1. Database Setup
Ensure you have MySQL installed or use the provided Docker setup:
```bash
# Using Docker
docker run -d --name mysql-reservation -p 3306:3306 -e MYSQL_ROOT_PASSWORD=cse -e MYSQL_DATABASE=reservation_system mysql:8.0
```
Run the scripts in `SQL CODE.txt` to initialize the tables and sample data.

### 2. Compilation
Compile the project from the root directory:
```bash
javac -cp ".:lib/mysql-connector-j-9.3.0.jar" *.java
```

### 3. Run
Launch the application:
```bash
java -cp ".:lib/mysql-connector-j-9.3.0.jar" Main
```

> [!TIP]
> For a detailed walkthrough on environment setup, check out [SETUP.md](SETUP.md).

---

## 🗺️ Database Architecture

The system relies on a relational schema consisting of:
- **Users**: Passenger profiles and credentials.
- **Trains**: Details about routes and schedules.
- **Seats**: Real-time tracking of seat availability for each train.
- **Reservations**: Mapping of users to their booked seats.

Refer to the database schema for structural details.

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the UI or add new features like PDF ticket generation or payment gateway simulation, feel free to fork the repo and submit a PR.

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).
