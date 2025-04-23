package com.fivestarhotel;

import com.fivestarhotel.GUI.BookItLogin;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookItLogin loginScreen = new BookItLogin();
            loginScreen.setVisible(true);

            // TODO: After successful login, transition to:
            // new RoomManagement(userRole, userId).setVisible(true);
        });
    }
}