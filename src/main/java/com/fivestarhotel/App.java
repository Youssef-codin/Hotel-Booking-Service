package com.fivestarhotel;

public class App {

public static class Room {

        public enum RoomType {
            SINGLE,
            DOUBLE,
            SUITE
        }
    
        private int number;
        private int floor;
        public RoomType roomType;
        private int rate;
        private boolean status;
        public List<Room> rooms;
        private boolean isOccupied;
        

    
        public Room(int number, int floor, RoomType type, int rate, boolean status) {
    
            this.number = number;
            this.floor = floor;
            this.roomType = type;
            this.rate = rate;
            this.status = status;
            this.isOccupied = false;
            this.rooms = new ArrayList<>();

            
        }

        public void occupy(){
             isOccupied = true; 
            }


        public void notoccupied(){
             isOccupied = false; 
            }

        public boolean isOccupied(){
             return isOccupied;
             }


        public void addRoom(Room room) {

            if (this.rooms == null) {
                this.rooms = new ArrayList<>();
            }
            this.rooms.add(room);
            System.out.println("Room added: " + room.getNum());

        }
        
        public int getNum() {
            return number;
        }
    
        public void setNum(int num) {
            number = num;
        }
    
        public int getFloor() {
            return floor;
        }
    
        public void setFloor(int num) {
            floor = num;
        }
    
        public RoomType getRoomType() {
            return roomType;
        }
    
        public void setRoomType(RoomType type) {
            roomType = type;
        }
    
        public int getRate() {
            return rate;
        }
    
        public void setRate(int newRate) {
            rate = newRate;
        }
    
        public boolean getStatus() {
            return status;
        }
    
        public void setStatus(boolean newStatus) {
            status = newStatus;
        }

    }




    public static class Booking {
        public enum BookingStatus {
            PENDING,
            CONFIRMED,
            CANCELLED
        }
        
        private Room room;
        private Customer customer;
        private BookingStatus status;

        public Booking(Room room, Customer customer, BookingStatus status) {
            this.room = room;
            this.customer = customer;
            this.status = status;
            
        }

        public Customer getCustomer() {
            return customer;
        }
        public void setStatus(BookingStatus status) {
             this.status = status; 
            }
        public BookingStatus getStatus() {
             return status; 
            }
        public Room getRoom() { 
            return room; 
            }
        public void setRoom(Room room) { 
            this.room = room;
        }

    }

    public static class BookingManager {
        private List<Booking> bookings;
        private List<Room> rooms;

        public BookingManager() {
            this.bookings = new ArrayList<>();
            this.rooms = new ArrayList<>();
        }



        public Booking bookRoom(int roomNumber, Customer customer) {
                Room room = null;

                for (Room r : rooms) {
                    if (r.getNum() == roomNumber) {
                        room = r; 
                        break;
                    }
                }

                if (room != null && !room.isOccupied()) {
                    
                    Booking booking = new Booking(room, customer,Booking.BookingStatus.PENDING);
                    bookings.add(booking);
                    room.occupy();
                    return booking;
                }
                return null;
            }

    }



    public static  class Payment{
        public enum PaymentMethod {
            CASH,
            CREDIT_CARD,
            DEBIT_CARD,
            ONLINE_PAYMENT
        }
        private final double amount;
        private final PaymentMethod method;
        private boolean isProcessed;

        public Payment(double amount, PaymentMethod method){
            this.amount = amount;
            this.method = method;
            this.isProcessed = false;
        }

        public void process() {
            if (!isProcessed) {
                System.out.println("Processing payment...");
                isProcessed = true;
            }
    }
}   


    
    public static void main(String[] args) {
<<<<<<< HEAD
        System.out.println("Omar has been here");
=======

>>>>>>> c842df9d4e8802cf2efae19ddcca0682bb5331d2
    }
}