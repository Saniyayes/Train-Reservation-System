public class ReservationSystem {
    private int[] seats;  // Array representing seats (0 = available, 1 = booked)

    // Constructor to initialize seats
    public ReservationSystem(int totalSeats) {
        seats = new int[totalSeats];  // Initializes all seats to 0 (available)
    }

    // Reserve a seat
    public String reserveSeat(int seatNumber) {
        if (seatNumber >= seats.length || seatNumber < 0) {
            return "Invalid seat number.";
        }

        if (seats[seatNumber] == 0) {  // Seat is available
            seats[seatNumber] = 1;  // Mark the seat as booked
            return "Seat reserved successfully!";
        } else {
            return "Seat already booked.";
        }
    }

    // Display all seats with status
    public void displaySeats() {
        for (int i = 0; i < seats.length; i++) {
            System.out.println("Seat " + (i + 1) + ": " + (seats[i] == 0 ? "Available" : "Booked"));
        }
    }

    // Get the status of a specific seat
    public int getSeatStatus(int seatNumber) {
        return seats[seatNumber];  // Return 0 for available, 1 for booked
    }
}
