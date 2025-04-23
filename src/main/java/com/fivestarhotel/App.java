package com.fivestarhotel;

import com.fivestarhotel.Database.Db;
import com.fivestarhotel.GUI.BookItLogin;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.net.URL;

public class App {
    public static void main(String[] args) {
        /*
         * WELCOME TO Db!! You may do "limited" sql functionalities in Java by using
         * Db.!!
         * START BY CONNECTING TO YOUR OWN DATABASE SERVER, Use Db.connect() and add
         * your username and password
         *
         * Afterwards use Db.select.loadRates(); to load in the rates from the database
         *
         * Db.connect("Enter username here (probably is just root)",
         * "Enter password here");
         *
         * Use Db.select.loadRates(); to load the latest rates from the DB to the
         * program
         *
         * NEW GUI INTERFACE ADDED, Make sure to add an admin/receptionist account before running the program
         *
         */
        // Database configuration
        String dbUser = "root";
        String dbPassword = "mimimi45";

        try {
            // Initialize database connection
            System.out.println("Connecting to database...");
            Db.connect(dbUser, dbPassword);
            System.out.println("Database connection established");

            // Load application icon
            System.out.println("Loading application icon...");
            ImageIcon appIcon = loadApplicationIcon();

            if (appIcon == null) {
                System.err.println("Warning: Running without application icon");
            }

            // Launch the application
            launchLoginScreen(appIcon);

        } catch (Exception e) {
            System.err.println("Fatal error during initialization:");
            e.printStackTrace();
            showFatalErrorDialog("Failed to initialize application: " + e.getMessage());
            System.exit(1);
        }
    }

    private static ImageIcon loadApplicationIcon() {
        try {
            // Get the resource URL (works in both IDE and JAR)
            URL iconUrl = App.class.getResource("/logo.png");

            if (iconUrl == null) {
                System.err.println("Icon not found at: /logo.png");
                System.err.println("Ensure the file exists in src/main/resources/");
                return null;
            }

            System.out.println("Icon found at: " + iconUrl);
            return new ImageIcon(iconUrl);

        } catch (Exception e) {
            System.err.println("Error loading application icon:");
            e.printStackTrace();
            return null;
        }
    }

    private static void launchLoginScreen(ImageIcon appIcon) {
        SwingUtilities.invokeLater(() -> {
            try {
                BookItLogin loginScreen = new BookItLogin();

                // Set application icon if loaded
                if (appIcon != null) {
                    setApplicationIcons(loginScreen, appIcon);
                }

                // Configure window
                loginScreen.setVisible(true);
                System.out.println("Login screen launched successfully");

            } catch (Exception e) {
                System.err.println("Failed to launch login screen:");
                e.printStackTrace();
                showFatalErrorDialog("Failed to create login window: " + e.getMessage());
                System.exit(1);
            }
        });
    }

    private static void setApplicationIcons(JFrame frame, ImageIcon icon) {
        try {
            // Standard icon setting
            frame.setIconImage(icon.getImage());

            // Multiple resolutions for better display quality
            ArrayList<Image> icons = new ArrayList<>();
            icons.add(icon.getImage());
            frame.setIconImages(icons);

            // Modern taskbar/dock icon (Java 9+)
            try {
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar.getTaskbar().setIconImage(icon.getImage());
                }
            } catch (UnsupportedOperationException e) {
                System.err.println("Taskbar icon not supported on this platform");
            }

        } catch (Exception e) {
            System.err.println("Warning: Could not set some window icons");
            e.printStackTrace();
        }
    }

    private static void showFatalErrorDialog(String message) {
        // Ensure we're on the EDT for showing dialogs
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showFatalErrorDialog(message));
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                message,
                "Fatal Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}