package com.fivestarhotel.Database;
import com.fivestarhotel.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Create {
    int updates = 0;
    public boolean addRoom(Room.RoomType roomType) {
        try (Connection connection = Db.connect()) {
            int lastID = Db.select.lastRoomId();
            if(lastID >= 1){
                assert connection != null;
                PreparedStatement ps = connection.prepareStatement("INSERT INTO room(room_floor,room_type,room_rate,room_status)Values(?,?,?,?)");
                ps.setInt(1,1);
                ps.setString(2, Room.convertRm(roomType));
                ps.setInt(3, Room.getRate(roomType));
                ps.setBoolean(4, false);
                updates = ps.executeUpdate();
            } else if (lastID == 0) {
                Db.update.resetIncrement();
                assert connection != null;
                PreparedStatement ps = connection.prepareStatement("INSERT INTO room(room_number,room_floor,room_type,room_rate,room_status)Values(?,?,?,?,?)");
                ps.setInt(1,1);
                ps.setInt(2, 1);
                ps.setString(3, Room.convertRm(roomType));
                ps.setInt(4, Room.getRate(roomType));
                ps.setBoolean(5, false);
                updates = ps.executeUpdate();
            }
            System.out.println("Added " + updates + " column");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return false;
        }
    }
    public void addRoom(int roomNumber, Room.RoomType roomType) {
        try (Connection connection = Db.connect()) {
            try {
                assert connection != null;
                PreparedStatement ps = connection.prepareStatement("INSERT INTO room(room_number,room_floor,room_type,room_rate,room_status)Values(?,?,?,?,?)");
                ps.setInt(1, roomNumber);
                ps.setInt(2, 1);
                ps.setString(3, Room.convertRm(roomType));
                ps.setInt(4, Room.getRate(roomType));
                ps.setBoolean(5, false);
                updates = ps.executeUpdate();
                System.out.println("Inserted " + updates + " column");
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    System.err.println("Duplicate primary key detected: use override or empty index.");
                }
            }
        } catch (SQLException e){
            System.err.println("Connection error: Can't connect to server");
        }
    }
    public void overrideRoom(int roomNumber, Room.RoomType roomType) {
        try (Connection connection = Db.connect()) {
            try{
                assert connection != null;
                PreparedStatement ps = connection.prepareStatement("DELETE FROM room WHERE room_number = ?");
                ps.setInt(1, roomNumber);
                ps.executeUpdate();
                ps = connection.prepareStatement("INSERT INTO room(room_number,room_floor,room_type,room_rate,room_status)Values(?,?,?,?,?)");
                ps.setInt(1, roomNumber);
                ps.setInt(2, 1);
                ps.setString(3, Room.convertRm(roomType));
                ps.setInt(4, Room.getRate(roomType));
                ps.setBoolean(5, false);
                updates = ps.executeUpdate();
                System.out.println("overrode " + updates + " column");
            }catch (SQLException e){
                System.err.println("SQL: Error");
            }
        } catch (SQLException e){
            System.err.println("Connection error: Can't connect to server");
        }
    }
    public boolean addRooms(Room.RoomType roomType, int amount) {
        try (Connection connection = Db.connect()) {
            for (int i = 0; i < amount; i++) {
                int lastID = Db.select.lastRoomId();
                if(lastID >= 1){
                    assert connection != null;
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO room(room_floor,room_type,room_rate,room_status)Values(?,?,?,?)");
                    ps.setInt(1,1);
                    ps.setString(2, Room.convertRm(roomType));
                    ps.setInt(3, Room.getRate(roomType));
                    ps.setBoolean(4, false);
                    updates += ps.executeUpdate();
                } else if (lastID == 0) {
                    Db.update.resetIncrement();
                    assert connection != null;
                    PreparedStatement ps = connection.prepareStatement("INSERT INTO room(room_number,room_floor,room_type,room_rate,room_status)Values(?,?,?,?,?)");
                    ps.setInt(1,1);
                    ps.setInt(2, 1);
                    ps.setString(3, Room.convertRm(roomType));
                    ps.setInt(4, Room.getRate(roomType));
                    ps.setBoolean(5, false);
                    updates += ps.executeUpdate();
                }
            }
            System.out.println("Added " + updates + " columns");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            return false;
        }
    }
}
