CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin',
    nama_lengkap VARCHAR(100)
);

INSERT INTO users (username, password_hash, role, nama_lengkap)
VALUES ('admin', 'admin123', 'admin', 'Administrator Utama')
ON DUPLICATE KEY UPDATE username=username;
