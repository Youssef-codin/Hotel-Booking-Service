/*
 * check availability logic needs
 * to be done through a db query
 */

package com.fivestarhotel;
import java.util.ArrayList;
import java.util.List;

public class Room {

    public enum RoomType {
        SINGLE,
        DOUBLE,
        SUITE
    }

    private int number;
    private int floor;
    public RoomType roomType;
    private int rate;
    public List<Room> rooms;
    private boolean isOccupied;
    


    public Room(int number, int floor, RoomType type, int rate, boolean status) {

        this.number = number;
        this.floor = floor;
        this.roomType = type;
        this.rate = rate;
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
}