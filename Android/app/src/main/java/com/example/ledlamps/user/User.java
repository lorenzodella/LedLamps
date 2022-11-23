// Della Matera Lorenzo 5E

package com.example.ledlamps.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private static int id;
    private static String username;
    private static String password;
    private static String name;
    private static String surname;
    private static String mail;
    private static String idLamp;

    public static void setUser(int id, String username, String password, String name, String surname, String mail, String idLamp) {
        User.id = id;
        User.username = username;
        User.password = password;
        User.name = name;
        User.surname = surname;
        User.mail = mail;
        User.idLamp = idLamp;
    }

    public static int getId() {
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) { User.password = password; }

    public static String getName() {
        return name;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getMail() {
        return mail;
    }

    public static String getIdLamp() {
        return idLamp;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}

