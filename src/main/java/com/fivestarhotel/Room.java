package com.fivestarhotel;

public class Room {

    public enum RoomType {
        SINGLE,
        DOUBLE,
        SUITE
    }

    private int number;
    private int floor;
    private RoomType roomType;
    private int rate;
    private boolean status;

    public Room(int number, int floor, RoomType type, int rate, boolean status) {

        this.number = number;
        this.floor = floor;
        this.roomType = type;
        this.rate = rate;
        this.status = status;
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
