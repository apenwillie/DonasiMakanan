package com.example.donasimakanan.manager;

import android.content.Context;

import io.realm.Realm;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.User;
import com.example.donasimakanan.util.SessionManager;

import java.util.UUID;


public class UserManager {
    private Realm realm;
    private SessionManager sessionManager;

   
    public UserManager(Context context) {
        this.realm = DatabaseManager.getInstance().getRealm();
        this.sessionManager = new SessionManager(context);
    }

    
    public User registerUser(String email, String password, String fullName) {
        // Cek apakah email sudah ada sebelumnya
        User existingUser = getUserByEmail(email);
        if (existingUser != null) {
            return null; // Email sudah terdaftar
        }

        realm.beginTransaction();
        try {
            String userId = UUID.randomUUID().toString();
            User newUser = realm.createObject(User.class, userId);
            newUser.setEmail(email);
            newUser.setPassword(password); // Password akan di-hash otomatis di dalam kelas User
            newUser.setFullName(fullName);

            realm.commitTransaction();
            return newUser;
        } catch (Exception e) {
            realm.cancelTransaction();
            return null;
        }
    }

    
    public User getUserByEmail(String email) {
        return realm.where(User.class)
                .equalTo("email", email)
                .equalTo("isActive", true)
                .findFirst();
    }

    
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.verifyPassword(password)) {
            return user;
        }
        return null;
    }

    
    public User getUserById(String userId) {
        return realm.where(User.class).equalTo("userId", userId).findFirst();
    }

    
    public User getCurrentUser() {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            return getUserById(userId);
        }
        return null; // Tidak ada pengguna yang login
    }

    
    public void addPoints(int points) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser(); // Menggunakan getCurrentUser untuk lebih ringkas
            if (user != null) {
                user.addPoints(points);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e; // Lemparkan kembali error untuk ditangani di pemanggil
        }
    }

    
    public void decreasePoints(int points) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null && user.getTotalPoints() >= points) {
                user.setTotalPoints(user.getTotalPoints() - points);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
                if (user != null) throw new IllegalArgumentException("Poin tidak mencukupi");
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    
    public void addBalance(int balance) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null) {
                user.setBalance(user.getBalance() + balance);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    
    public void decreaseBalance(int balance) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null && user.getBalance() >= balance) {
                user.setBalance(user.getBalance() - balance);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
                if (user != null) throw new IllegalArgumentException("Saldo tidak mencukupi");
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
