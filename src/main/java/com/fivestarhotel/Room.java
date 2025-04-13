/*
 * check availability logic needs
 * to be done through a db query
 */

package com.fivestarhotel;

import com.fivestarhotel.Database.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private static int srate = 750, drate = 1200, Srate = 2200;
    private boolean isBooked;

    public Room(int number, int floor, RoomType type) {

        this.number = number;
        this.floor = floor;
        roomType = type;
        switch (type){
            case SINGLE -> this.rate = srate;
            case DOUBLE -> this.rate = drate;
            case SUITE -> this.rate = srate;
        }
        this.isBooked = false;
    }

    public static Room.RoomType convertStr(String roomType){
        switch (roomType.toLowerCase()){
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
    public static String convertRm(RoomType roomType){
        switch (roomType){
            case SINGLE -> {
                return "single";
            }
            case DOUBLE -> {
                return "double";
            }
            case SUITE -> {
                return "suite" ;
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
        switch (roomType){
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

    public static int getRate() {
        try {
            switch (roomType){
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
        } catch (NullPointerException e){
            System.err.println("Room Error: room doesn't exist.");
            return 0;
        }
    }

    public static void setRate(RoomType roomType, int newRate){
        switch (roomType){
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
}
