-- Membangun schema database untuk Garage Management System
DROP DATABASE IF EXISTS garage_management;
CREATE DATABASE garage_management;
USE garage_management;

-- Tabel Users (Sudah ada di user_migration.sql)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin',
    nama_lengkap VARCHAR(100)
);

-- Tabel Client
CREATE TABLE IF NOT EXISTS client (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat TEXT,
    telepon VARCHAR(20),
    email VARCHAR(100),
    tanggal_daftar DATE
);

-- Tabel Mekanik
CREATE TABLE IF NOT EXISTS mekanik (
    mekanik_id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    telepon VARCHAR(20),
    spesialis VARCHAR(100)
);

-- Tabel Supplier
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat TEXT,
    telepon VARCHAR(20),
    email VARCHAR(100)
);

-- Tabel Sparepart
CREATE TABLE IF NOT EXISTS sparepart (
    sparepart_id INT AUTO_INCREMENT PRIMARY KEY,
    kode_sparepart VARCHAR(50) UNIQUE NOT NULL,
    nama_sparepart VARCHAR(100) NOT NULL,
    satuan VARCHAR(20),
    stok INT DEFAULT 0,
    harga_beli DOUBLE DEFAULT 0,
    harga_jual DOUBLE DEFAULT 0,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id) ON DELETE SET NULL
);

-- Tabel Vehicle
CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    no_polisi VARCHAR(20) UNIQUE NOT NULL,
    merk VARCHAR(50),
    tipe VARCHAR(50),
    cc INT,
    tipe_kendaraan VARCHAR(50),
    tahun INT,
    no_rangka VARCHAR(50),
    no_mesin VARCHAR(50),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

-- Tabel Service Transaction
CREATE TABLE IF NOT EXISTS service_transaction (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATETIME,
    client_id INT,
    vehicle_id INT,
    mekanik_id INT,
    keluhan TEXT,
    status_servis VARCHAR(50),
    total_jasa DOUBLE DEFAULT 0,
    total_sparepart DOUBLE DEFAULT 0,
    grand_total DOUBLE DEFAULT 0,
    bayar DOUBLE DEFAULT 0,
    kembali DOUBLE DEFAULT 0,
    user_kasir VARCHAR(50),
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id),
    FOREIGN KEY (mekanik_id) REFERENCES mekanik(mekanik_id)
);

-- Tabel Transaction Detail
CREATE TABLE IF NOT EXISTS transaction_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    trans_id INT NOT NULL,
    sparepart_id INT,
    qty INT DEFAULT 1,
    harga DOUBLE DEFAULT 0,
    subtotal DOUBLE DEFAULT 0,
    FOREIGN KEY (trans_id) REFERENCES service_transaction(trans_id) ON DELETE CASCADE,
    FOREIGN KEY (sparepart_id) REFERENCES sparepart(sparepart_id) ON DELETE SET NULL
);

-- ==========================================
-- DUMMY DATA INSERTION
-- ==========================================

-- Dummy Users (10)
INSERT INTO users (username, password_hash, role, nama_lengkap) VALUES 
('admin', 'admin123', 'admin', 'Administrator Utama'),
('kasir1', 'kasir123', 'kasir', 'Kasir Bulan'),
('kasir2', 'kasir123', 'kasir', 'Kasir Bintang'),
('manager1', 'manager123', 'manager', 'Manager Surya'),
('kasir3', 'kasir123', 'kasir', 'Kasir Bumi'),
('kasir4', 'kasir123', 'kasir', 'Kasir Mars'),
('admin2', 'admin123', 'admin', 'Admin Kedua'),
('superadmin', 'super123', 'admin', 'Super Admin'),
('kasir5', 'kasir123', 'kasir', 'Kasir Jupiter'),
('kasir6', 'kasir123', 'kasir', 'Kasir Saturnus')
ON DUPLICATE KEY UPDATE username=username;

-- Dummy Client (10)
INSERT INTO client (nama, alamat, telepon, email, tanggal_daftar) VALUES 
('Budi Santoso', 'Jl. Merdeka No 1, Jakarta', '081234567890', 'budi@email.com', CURDATE()),
('Siti Aminah', 'Jl. Sudirman No 2, Bandung', '081298765432', 'siti@email.com', CURDATE()),
('Agus Setiawan', 'Jl. Thamrin No 3, Surabaya', '081311223344', 'agus@email.com', CURDATE()),
('Dewi Lestari', 'Jl. Gatot Subroto No 4, Medan', '081344556677', 'dewi@email.com', CURDATE()),
('Reza Rahadian', 'Jl. Asia Afrika No 5, Makassar', '081455667788', 'reza@email.com', CURDATE()),
('Ayu Tingting', 'Jl. Pahlawan No 6, Semarang', '081566778899', 'ayu@email.com', CURDATE()),
('Raffi Ahmad', 'Jl. Diponegoro No 7, Yogyakarta', '081677889900', 'raffi@email.com', CURDATE()),
('Luna Maya', 'Jl. Hasanuddin No 8, Bali', '081788990011', 'luna@email.com', CURDATE()),
('Vino Bastian', 'Jl. Imam Bonjol No 9, Padang', '081899001122', 'vino@email.com', CURDATE()),
('Dian Sastro', 'Jl. Teuku Umar No 10, Palembang', '081900112233', 'dian@email.com', CURDATE());

-- Dummy Mekanik (10)
INSERT INTO mekanik (nama, telepon, spesialis) VALUES 
('Joko', '082111223344', 'Mesin Motor Matic'),
('Andi', '082155667788', 'Kelistrikan'),
('Tono', '082233445566', 'Mesin Motor Sport'),
('Bambang', '082344556677', 'Kaki-kaki dan Rem'),
('Rudi', '082455667788', 'Injeksi dan ECU'),
('Eko', '082566778899', 'Cat dan Modifikasi'),
('Hadi', '082677889900', 'Mesin Motor Bebek'),
('Iwan', '082788990011', 'Overhaul Mesin'),
('Dani', '082899001122', 'Kelistrikan Lanjut'),
('Feri', '082900112233', 'Servis Ringan / Fast Track');

-- Dummy Supplier (10)
INSERT INTO supplier (nama, alamat, telepon, email) VALUES 
('PT Astra Otoparts', 'Gedung Astra, Sunter', '0215556661', 'sales@astra.co.id'),
('CV Maju Mundur', 'Ruko Blok M, Kebayoran', '0217778882', 'info@majumundur.com'),
('Toko Abadi Motor', 'Jl. Kebon Jeruk No. 5', '0218889993', 'cs@abadimotor.com'),
('PT Yuasa Battery', 'Kawasan Industri Cikarang', '0219990004', 'order@yuasa.co.id'),
('CV Gajah Mada', 'Jl. Gajah Mada No 12', '0212223335', 'gajahmada@part.com'),
('PT IRC Tire', 'Tangerang', '0213334446', 'sales@irc.co.id'),
('Toko Makmur Jaya', 'Pasar Senen Blok B', '0214445557', 'makmurjaya@gmail.com'),
('PT NGK Busi', 'Kawasan Industri Pulo Gadung', '0215551118', 'sales@ngk.co.id'),
('Bintang Racing Team (BRT)', 'Cibinong, Bogor', '0216662229', 'sales@brt.co.id'),
('Aneka Baut & Mur', 'Glodok Makmur', '0217773330', 'anekabaut@gmail.com');

-- Dummy Sparepart (10)
INSERT INTO sparepart (kode_sparepart, nama_sparepart, satuan, stok, harga_beli, harga_jual, supplier_id) VALUES 
('OLI-001', 'Oli Mesin Yamalube 0.8L', 'Botol', 50, 45000, 52000, 1),
('OLI-002', 'Oli Gardan Yamalube 100ml', 'Botol', 60, 15000, 18000, 1),
('BUSI-001', 'Busi NGK CPR9EA-9', 'Pcs', 4, 15000, 25000, 8), -- Stok kritis <= 5
('KAMPAS-001', 'Kampas Rem Depan Honda Vario', 'Set', 3, 40000, 55000, 2), -- Stok kritis <= 5
('KAMPAS-002', 'Kampas Rem Belakang NMAX', 'Set', 2, 55000, 75000, 2), -- Stok kritis <= 5
('BAN-001', 'Ban IRC 90/90-14 Tubeless', 'Pcs', 20, 180000, 210000, 6),
('BAN-002', 'Ban IRC 100/90-14 Tubeless', 'Pcs', 1, 230000, 265000, 6), -- Stok kritis <= 5
('AKI-001', 'Aki Yuasa YTZ5S', 'Pcs', 10, 180000, 220000, 4),
('FILT-001', 'Filter Udara Honda Beat FI', 'Pcs', 40, 45000, 60000, 1),
('VBLT-001', 'V-Belt Honda Vario 150', 'Pcs', 15, 120000, 150000, 1);

-- Dummy Vehicle (10)
INSERT INTO vehicle (client_id, no_polisi, merk, tipe, cc, tipe_kendaraan, tahun, no_rangka, no_mesin) VALUES 
(1, 'B 1234 ABC', 'Honda', 'Vario 150', 150, 'Lebih dari Roda 2', 2020, 'MHK123456789A', 'JF123456A'),
(2, 'D 5678 DEF', 'Yamaha', 'NMAX 155', 155, 'Lebih dari Roda 2', 2021, 'MHY987654321B', 'YG987654B'),
(3, 'L 9012 GHI', 'Honda', 'PCX 160', 160, 'Lebih dari Roda 2', 2022, 'MHK112233445C', 'JF112233C'),
(4, 'BK 3456 JKL', 'Yamaha', 'Aerox 155', 155, 'Lebih dari Roda 2', 2021, 'MHY223344556D', 'YG223344D'),
(5, 'DD 7890 MNO', 'Honda', 'Beat Street', 110, 'Lebih dari Roda 2', 2019, 'MHK334455667E', 'JF334455E'),
(6, 'H 1122 PQR', 'Kawasaki', 'KLX 150', 150, 'Roda 2', 2018, 'MHK445566778F', 'JF445566F'),
(7, 'AB 3344 STU', 'Suzuki', 'GSX-R150', 150, 'Roda 2', 2020, 'MHS556677889G', 'SG556677G'),
(8, 'DK 5566 VWX', 'Honda', 'CBR 150R', 150, 'Roda 2', 2021, 'MHK667788990H', 'KH667788H'),
(9, 'BA 7788 YZA', 'Yamaha', 'MT-15', 150, 'Roda 2', 2022, 'MHY778899001I', 'YG778899I'),
(10, 'BG 9900 BCD', 'Honda', 'Scoopy', 110, 'Lebih dari Roda 2', 2020, 'MHK889900112J', 'JF889900J');

-- Dummy Service Transaction (10+) untuk Dashboard
INSERT INTO service_transaction (tanggal, client_id, vehicle_id, mekanik_id, keluhan, status_servis, total_jasa, total_sparepart, grand_total, bayar, kembali, user_kasir) VALUES 
-- Trans 1: Hari ini - Selesai Lunas (Omzet Hari ini, > 80k Jasa alias Servis Berat)
(NOW(), 1, 1, 1, 'Ganti oli dan bongkar mesin', 'Selesai Lunas', 100000, 52000, 152000, 200000, 48000, 'kasir1'),
-- Trans 2: Hari ini - Menunggu (Pengecekan Antrean Hari ini, < 50k Jasa alias Servis Kecil)
(NOW(), 2, 2, 2, 'Lampu depan mati', 'Menunggu', 25000, 0, 25000, 0, 0, 'kasir2'),
-- Trans 3: Hari ini - Dikerjakan (Pengerjaan Hari ini, > 50k & <= 80k Jasa alias Servis Sedang)
(NOW(), 3, 3, 3, 'Servis CVT tarikan berat', 'Dikerjakan', 60000, 150000, 210000, 0, 0, 'kasir1'),
-- Trans 4: Bulan Lalu - Selesai Lunas (Servis Kecil)
(NOW() - INTERVAL 1 MONTH, 4, 4, 1, 'Ganti ban depan', 'Selesai Lunas', 20000, 210000, 230000, 250000, 20000, 'kasir3'),
-- Trans 5: Bulan Lalu 2 - Selesai Lunas (Servis Kecil)
(NOW() - INTERVAL 2 MONTH, 5, 5, 4, 'Rem blong dan ganti kanvas', 'Selesai Lunas', 30000, 55000, 85000, 100000, 15000, 'kasir2'),
-- Trans 6: Awal Tahun Ini - Selesai Lunas (Servis Sedang)
(LAST_DAY(NOW() - INTERVAL 5 MONTH), 6, 6, 5, 'Motor brebet susut gigi', 'Selesai Lunas', 75000, 25000, 100000, 100000, 0, 'kasir1'),
-- Trans 7: 65 Hari Lalu - Selesai Lunas (Untuk Reminder Servis > 60 hari)
(NOW() - INTERVAL 65 DAY, 7, 7, 2, 'Kelistrikan rewel aki tekor', 'Selesai Lunas', 40000, 220000, 260000, 300000, 40000, 'kasir3'),
-- Trans 8: 100 Hari Lalu - Selesai Lunas (Untuk Reminder Servis > 60 hari)
(NOW() - INTERVAL 100 DAY, 8, 8, 3, 'Ganti filter udara dan oli', 'Selesai Lunas', 45000, 112000, 157000, 200000, 43000, 'kasir2'),
-- Trans 9: Tahun Lalu - Selesai Lunas (Chart Tahunan, Servis Sedang)
(NOW() - INTERVAL 1 YEAR, 9, 9, 10, 'Servis ringan saja', 'Selesai Lunas', 60000, 0, 60000, 60000, 0, 'kasir1'),
-- Trans 10: Dua Tahun Lalu - Selesai Lunas (Chart Tahunan, Servis Berat)
(NOW() - INTERVAL 2 YEAR, 10, 10, 8, 'Turun mesin overheat', 'Selesai Lunas', 300000, 0, 300000, 500000, 200000, 'kasir3');

-- Dummy Transaction Detail (only for spareparts, as Jasa is stored in total_jasa of header)
INSERT INTO transaction_detail (trans_id, sparepart_id, qty, harga, subtotal) VALUES 
-- Trans 1 (Oli Mesin Yamalube 0.8L -> id 1, Rp 52000)
(1, 1, 1, 52000, 52000),
-- Trans 3 (V-Belt -> id 10, Rp 150000)
(3, 10, 1, 150000, 150000),
-- Trans 4 (Ban IRC 90/90 -> id 6, Rp 210000)
(4, 6, 1, 210000, 210000),
-- Trans 5 (Kampas Rem Depan -> id 4, Rp 55000)
(5, 4, 1, 55000, 55000),
-- Trans 6 (Busi NGK -> id 3, Rp 25000)
(6, 3, 1, 25000, 25000),
-- Trans 7 (Aki Yuasa -> id 8, Rp 220000)
(7, 8, 1, 220000, 220000),
-- Trans 8 (Oli Mesin id 1, Filter Udara id 9)
(8, 1, 1, 52000, 52000),
(8, 9, 1, 60000, 60000);
