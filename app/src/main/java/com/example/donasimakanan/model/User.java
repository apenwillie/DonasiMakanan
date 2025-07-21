package com.example.donasimakanan.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User extends RealmObject {

    
    @PrimaryKey
    @Required
    private String userId;

    
    @Required
    private String email;

    
    @Required
    private String passwordHash;

    
    @Required
    private String fullName;

    
    private String phoneNumber;

    
    private String address;

    
    private int totalPoints;

   
    private int balance;

    
    private boolean isActive;

    
    public User() {
        this.totalPoints = 0;
        this.balance = 0;
        this.isActive = true;
    }

    
    public User(String userId, String email, String password, String fullName) {
        this(); // Memanggil konstruktor kosong untuk inisialisasi default
        this.userId = userId;
        this.email = email;
        this.passwordHash = hashPassword(password);  // Password langsung di-hash
        this.fullName = fullName;
    }

    

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public int getTotalPoints() { return totalPoints; }
    public boolean isActive() { return isActive; }
    public int getBalance() {return balance;}

    

    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setBalance(int balance) {this.balance = balance;}
    public void setTotalPoints(int points) {this.totalPoints = points;}

    
    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    
    public boolean verifyPassword(String inputPassword) {
        if (inputPassword == null || this.passwordHash == null) {
            return false;
        }
        String inputHash = hashPassword(inputPassword);
        return this.passwordHash.equals(inputHash);
    }

    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Konversi array byte ke dalam format string heksadesimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
           
            return password; 
        }
    }

    
    public void addPoints(int points) {
        if (points > 0) this.totalPoints += points;
    }

   
    public void decreaseBalance(int amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
        }
    }

    
    public boolean usePoints(int points) {
        if (points > 0 && this.totalPoints >= points) {
            this.totalPoints -= points;
            return true;
        }
        return false;
    }
}
