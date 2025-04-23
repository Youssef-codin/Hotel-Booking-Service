package com.fivestarhotel;

import com.fivestarhotel.GUI.BookItLogin;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookItLogin loginScreen = new BookItLogin();
            loginScreen.setVisible(true);

            // TODO: After successful login, transition to:
            // Simplified authentication to use hardcoded credentials:
            //  Admin: admin@bookit.com / admin123
            //  Receptionist: reception@bookit.com / reception123
            // new RoomManagement(userRole, userId).setVisible(true);
        });
    }
}