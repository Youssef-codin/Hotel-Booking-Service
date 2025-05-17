package com.fivestarhotel.security;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.IOException;
import java.nio.file.Path;

public class SecureCredentials {

    private static final Path CREDENTIALS_PATH = Paths.get(System.getProperty("user.home"), ".hotel_booking",
            "credentials.txt");

    public static void saveCredentials(String email, String password) {
        try {
            Files.createDirectories(CREDENTIALS_PATH.getParent());
            String encoded = Base64.getEncoder()
                    .encodeToString((email + ":" + password).getBytes(StandardCharsets.UTF_8));
            Files.write(CREDENTIALS_PATH, encoded.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadCredentials() {
        try {
            if (Files.exists(CREDENTIALS_PATH)) {
                String encoded = Files.readString(CREDENTIALS_PATH, StandardCharsets.UTF_8);
                String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
                return decoded.split(":");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearCredentials() {
        try {
            Files.deleteIfExists(CREDENTIALS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
