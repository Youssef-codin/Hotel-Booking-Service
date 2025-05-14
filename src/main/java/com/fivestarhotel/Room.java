/*
 * check availability logic needs
 * to be done through a db query
 */

package com.fivestarhotel;

public class Room {

    public enum RoomType {
        SINGLE {
            @Override
            public String toString() {
                return "Single";
            }
        },
        DOUBLE {
            @Override
            public String toString() {
                return "Double";
            }
        },
        SUITE {
            @Override
            public String toString() {
                return "Suite";
            }
        }
    }

    private int number;
    private int floor;
    private RoomType roomType;
    private static int srate = 750, drate = 1200, Srate = 2200; // you can load the rates from the Db.select.loadRates()
    private boolean isBooked;

    public Room(int number, RoomType type) {

        this.number = number;
        this.floor = ((number - 1) / 100) + 1;
        roomType = type;
        this.isBooked = false;
    }

    public Room(int number, RoomType type, boolean isBooked) {

        this.number = number;
        this.floor = ((number - 1) / 100) + 1;
        roomType = type;
        this.isBooked = isBooked;
    }

    public static Room.RoomType convertStr(String roomType) {
        switch (roomType.toLowerCase()) {
            case "single" -> {
                return Room.RoomType.SINGLE;
            }
            case "double" -> {
                return Room.RoomType.DOUBLE;
            }
            case "suite" -> {
                return Room.RoomType.SUITE;
            }
            default -> {
                return null;
            }
        }
    }

    public static String convertRm(RoomType roomType) {
        switch (roomType) {
            case SINGLE -> {
                return "single";
            }
            case DOUBLE -> {
                return "double";
            }
            case SUITE -> {
                return "suite";
            }
            default -> {
                return null;
            }
        }
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

    public static int getRate(RoomType roomType) {
        switch (roomType) {
            case SINGLE -> {
                return srate;
            }
            case DOUBLE -> {
                return drate;
            }
            case SUITE -> {
                return Srate;
            }
            default -> {
                return 0;
            }
        }
    }

    public static void setRate(RoomType roomType, int newRate) {
        switch (roomType) {
            case SINGLE -> srate = newRate;
            case DOUBLE -> drate = newRate;
            case SUITE -> Srate = newRate;
        }
    }

    public boolean getStatus() {
        return isBooked;
    }

    public void setStatus(boolean newStatus) {
        isBooked = newStatus;
    }

    public void getData() {
        System.out.println("-------------");
        System.out.println("number: " + this.number);
        System.out.println("floor: " + this.floor);
        System.out.println("type: " + this.roomType);
        System.out.println("booked: " + this.isBooked);

    }

}
