package com.fivestarhotel.security;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Crypto {

    private Crypto() {

    }

    public static String encoderHelper(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decoderHelper(String string) {
        return Base64.getDecoder().decode(string);
    }

    public static String makeSalt() {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return encoderHelper(salt);
    }

    public static String stringToHash(String password, String salt) throws Exception {

        byte[] saltBytes = decoderHelper(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hashBytes = factory.generateSecret(spec).getEncoded();
        return encoderHelper(hashBytes);
    }

    public static void main(String[] args) throws Exception {

        // just made this to demonstrate the class
        String salt = makeSalt();
        String password = stringToHash("correctpass", salt);

        String checkPass = stringToHash("correctpass", salt);
        String wrongPass = stringToHash("password8alat", salt);

        boolean correct = password.equals(checkPass);
        boolean wrong = password.equals(wrongPass);

        System.out.println("Correct pass: " + correct);
        System.out.println("Wrong pass: " + wrong);

    }
}
