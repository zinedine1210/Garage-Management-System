package com.mycompany.garagemanagementsystem.model;

/**
 * Model User = representasi data user/pengguna aplikasi (untuk login).
 * 
 * Field sesuai kolom tabel 'users' di database.
 * passwordHash = password yang disimpan (idealnya ter-enkripsi).
 * role = peran user (contoh: "admin").
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private String namaLengkap;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
}
