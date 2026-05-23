-- Membangun schema database untuk Garage Management System
DROP DATABASE IF EXISTS garage_management;
CREATE DATABASE garage_management;
USE garage_management;

-- ==========================================
-- MASTER DATA TABLES
-- ==========================================

-- Tabel Users
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

-- ==========================================
-- TRANSAKSI TABLES (prefix: transaksi_)
-- ==========================================

-- Tabel Transaksi Pendaftaran Servis
CREATE TABLE IF NOT EXISTS transaksi_pendaftaran (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id INT NOT NULL,
    client_id INT NOT NULL,
    keluhan TEXT,
    mekanik_id INT NULL,
    status VARCHAR(50) DEFAULT 'Registered',
    tanggal_daftar DATETIME NOT NULL,
    tanggal_mulai DATETIME,
    catatan TEXT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (mekanik_id) REFERENCES mekanik(mekanik_id)
);

-- Tabel Transaksi Servis (header)
CREATE TABLE IF NOT EXISTS transaksi_servis (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATETIME,
    client_id INT,
    vehicle_id INT,
    mekanik_id INT,
    registration_id INT NULL,
    keluhan TEXT,
    status_servis VARCHAR(50),
    total_jasa DOUBLE DEFAULT 0,
    total_sparepart DOUBLE DEFAULT 0,
    grand_total DOUBLE DEFAULT 0,
    bayar DOUBLE DEFAULT 0,
    kembali DOUBLE DEFAULT 0,
    metode_bayar VARCHAR(50) DEFAULT 'Cash',
    user_kasir VARCHAR(50),
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id),
    FOREIGN KEY (mekanik_id) REFERENCES mekanik(mekanik_id),
    FOREIGN KEY (registration_id) REFERENCES transaksi_pendaftaran(registration_id) ON DELETE SET NULL
);

-- Tabel Transaksi Servis Detail (sparepart)
CREATE TABLE IF NOT EXISTS transaksi_servis_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    trans_id INT NOT NULL,
    sparepart_id INT,
    qty INT DEFAULT 1,
    harga DOUBLE DEFAULT 0,
    subtotal DOUBLE DEFAULT 0,
    FOREIGN KEY (trans_id) REFERENCES transaksi_servis(trans_id) ON DELETE CASCADE,
    FOREIGN KEY (sparepart_id) REFERENCES sparepart(sparepart_id) ON DELETE SET NULL
);

-- Tabel Transaksi Servis Jasa (layanan/jasa per transaksi)
CREATE TABLE IF NOT EXISTS transaksi_servis_jasa (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    trans_id INT NOT NULL,
    nama_jasa VARCHAR(100),
    harga DOUBLE NOT NULL DEFAULT 0,
    qty INT NOT NULL DEFAULT 1,
    subtotal DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (trans_id) REFERENCES transaksi_servis(trans_id) ON DELETE CASCADE
);

-- Tabel Transaksi Pembelian Sparepart
CREATE TABLE IF NOT EXISTS transaksi_pembelian (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATETIME,
    supplier_id INT,
    sparepart_id INT,
    qty INT DEFAULT 1,
    harga_beli DOUBLE DEFAULT 0,
    total_harga DOUBLE DEFAULT 0,
    keterangan TEXT,
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (sparepart_id) REFERENCES sparepart(sparepart_id)
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

-- Dummy Client (25)
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
('Dian Sastro', 'Jl. Teuku Umar No 10, Palembang', '081900112233', 'dian@email.com', CURDATE()),
('Andi Pratama', 'Jl. Kebon Sirih No 11, Jakarta Pusat', '081201112233', 'andi.pratama@email.com', CURDATE() - INTERVAL 10 DAY),
('Rina Wulandari', 'Jl. Cikini Raya No 15, Jakarta', '081302223344', 'rina.w@email.com', CURDATE() - INTERVAL 20 DAY),
('Hendra Gunawan', 'Jl. Pemuda No 22, Surabaya', '081403334455', 'hendra.g@email.com', CURDATE() - INTERVAL 30 DAY),
('Maya Sari', 'Jl. Veteran No 8, Malang', '081504445566', 'maya.sari@email.com', CURDATE() - INTERVAL 40 DAY),
('Fajar Nugroho', 'Jl. Jend. Sudirman No 45, Semarang', '081605556677', 'fajar.n@email.com', CURDATE() - INTERVAL 50 DAY),
('Putri Handayani', 'Jl. Ahmad Yani No 33, Bandung', '081706667788', 'putri.h@email.com', CURDATE() - INTERVAL 5 DAY),
('Yoga Firmansyah', 'Jl. Gajah Mada No 19, Tangerang', '081807778899', 'yoga.f@email.com', CURDATE() - INTERVAL 15 DAY),
('Sari Dewi Utami', 'Jl. Mangga Besar No 77, Jakarta', '081908889900', 'sari.du@email.com', CURDATE() - INTERVAL 25 DAY),
('Rizky Ramadhan', 'Jl. Pasar Baru No 5, Bogor', '082009990011', 'rizky.r@email.com', CURDATE() - INTERVAL 35 DAY),
('Nur Aini', 'Jl. Raya Serpong No 101, Tangerang Selatan', '082100112233', 'nur.aini@email.com', CURDATE() - INTERVAL 45 DAY),
('Bayu Anggara', 'Jl. RE Martadinata No 55, Bandung', '082211223344', 'bayu.a@email.com', CURDATE() - INTERVAL 7 DAY),
('Indah Permata', 'Jl. Siliwangi No 12, Cirebon', '082322334455', 'indah.p@email.com', CURDATE() - INTERVAL 14 DAY),
('Doni Kusuma', 'Jl. Braga No 28, Bandung', '082433445566', 'doni.k@email.com', CURDATE() - INTERVAL 21 DAY),
('Ratna Sari', 'Jl. Pasir Kaliki No 66, Bandung', '082544556677', 'ratna.s@email.com', CURDATE() - INTERVAL 28 DAY),
('Ahmad Fauzi', 'Jl. Cipaganti No 88, Bandung', '082655667788', 'ahmad.f@email.com', CURDATE() - INTERVAL 3 DAY);

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

-- Dummy Sparepart (25)
INSERT INTO sparepart (kode_sparepart, nama_sparepart, satuan, stok, harga_beli, harga_jual, supplier_id) VALUES 
('OLI-001', 'Oli Mesin Yamalube 0.8L', 'Botol', 50, 45000, 52000, 1),
('OLI-002', 'Oli Gardan Yamalube 100ml', 'Botol', 60, 15000, 18000, 1),
('BUSI-001', 'Busi NGK CPR9EA-9', 'Pcs', 4, 15000, 25000, 8),
('KAMPAS-001', 'Kampas Rem Depan Honda Vario', 'Set', 3, 40000, 55000, 2),
('KAMPAS-002', 'Kampas Rem Belakang NMAX', 'Set', 2, 55000, 75000, 2),
('BAN-001', 'Ban IRC 90/90-14 Tubeless', 'Pcs', 20, 180000, 210000, 6),
('BAN-002', 'Ban IRC 100/90-14 Tubeless', 'Pcs', 1, 230000, 265000, 6),
('AKI-001', 'Aki Yuasa YTZ5S', 'Pcs', 10, 180000, 220000, 4),
('FILT-001', 'Filter Udara Honda Beat FI', 'Pcs', 40, 45000, 60000, 1),
('VBLT-001', 'V-Belt Honda Vario 150', 'Pcs', 15, 120000, 150000, 1),
('OLI-003', 'Oli Mesin Castrol Power1 1L', 'Botol', 35, 68000, 85000, 1),
('OLI-004', 'Oli Mesin Shell Advance AX7 0.8L', 'Botol', 25, 55000, 70000, 1),
('BUSI-002', 'Busi Iridium NGK CR8EIX', 'Pcs', 12, 65000, 85000, 8),
('KAMPAS-003', 'Kampas Rem Depan Honda Beat', 'Set', 18, 35000, 50000, 2),
('KAMPAS-004', 'Kampas Kopling Honda Vario 125', 'Set', 8, 80000, 110000, 2),
('BAN-003', 'Ban Michelin Pilot Street 80/90-14', 'Pcs', 6, 250000, 310000, 6),
('BAN-004', 'Ban Dunlop D115 70/90-17', 'Pcs', 10, 195000, 240000, 6),
('AKI-002', 'Aki GS GTZ5S MF', 'Pcs', 7, 160000, 200000, 4),
('FILT-002', 'Filter Udara NMAX Original', 'Pcs', 15, 55000, 75000, 1),
('RANTAI-001', 'Rantai SSS 428 HSB 130L', 'Set', 5, 145000, 185000, 9),
('GEAR-001', 'Gear Set SSS Honda Supra X 125', 'Set', 4, 195000, 250000, 9),
('ROLLER-001', 'Roller Dr. Pulley Honda Vario 10g', 'Set', 20, 85000, 120000, 3),
('CDI-001', 'CDI Racing BRT Honda Beat FI', 'Pcs', 3, 350000, 450000, 9),
('LAMPU-001', 'Bohlam LED H6 Motor AC/DC 35W', 'Pcs', 25, 45000, 65000, 7),
('GASKET-001', 'Gasket Full Set Honda Beat', 'Set', 10, 75000, 100000, 3);

-- Dummy Vehicle (25)
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
(10, 'BG 9900 BCD', 'Honda', 'Scoopy', 110, 'Lebih dari Roda 2', 2020, 'MHK889900112J', 'JF889900J'),
(11, 'B 2345 KLM', 'Honda', 'ADV 160', 160, 'Lebih dari Roda 2', 2023, 'MHK990011223K', 'JF990011K'),
(12, 'B 3456 NOP', 'Yamaha', 'Lexi 125', 125, 'Lebih dari Roda 2', 2022, 'MHY001122334L', 'YG001122L'),
(13, 'L 4567 QRS', 'Honda', 'Supra X 125 FI', 125, 'Roda 2', 2020, 'MHK112233445M', 'JF112234M'),
(14, 'N 5678 TUV', 'Yamaha', 'R15 V4', 155, 'Roda 2', 2023, 'MHY223344556N', 'YG223345N'),
(15, 'AG 6789 WXY', 'Honda', 'CRF 150L', 150, 'Roda 2', 2021, 'MHK334455667O', 'JF334456O'),
(16, 'B 7890 ZAB', 'Kawasaki', 'Ninja ZX-25R', 250, 'Roda 2', 2022, 'MHK445566778P', 'KH445567P'),
(17, 'D 8901 CDE', 'Honda', 'Genio', 110, 'Lebih dari Roda 2', 2021, 'MHK556677889Q', 'JF556678Q'),
(18, 'B 9012 FGH', 'Yamaha', 'Mio M3', 125, 'Lebih dari Roda 2', 2020, 'MHY667788990R', 'YG667789R'),
(19, 'F 0123 IJK', 'Honda', 'Revo FI', 110, 'Roda 2', 2019, 'MHK778899001S', 'JF778890S'),
(20, 'B 1235 LMN', 'Yamaha', 'XSR 155', 155, 'Roda 2', 2023, 'MHY889900112T', 'YG889901T'),
(21, 'D 2346 OPQ', 'Honda', 'Vario 125', 125, 'Lebih dari Roda 2', 2022, 'MHK990011223U', 'JF990012U'),
(22, 'B 3457 RST', 'Suzuki', 'Address FI', 110, 'Lebih dari Roda 2', 2021, 'MHS001122334V', 'SG001123V'),
(23, 'D 4568 UVW', 'Honda', 'Beat FI', 110, 'Lebih dari Roda 2', 2020, 'MHK112233446W', 'JF112235W'),
(24, 'B 5679 XYZ', 'Yamaha', 'XMAX 250', 250, 'Lebih dari Roda 2', 2022, 'MHY223344557X', 'YG223346X'),
(25, 'B 6780 ABC', 'Honda', 'CB150 Verza', 150, 'Roda 2', 2021, 'MHK334455668Y', 'JF334457Y');

-- Dummy Transaksi Pendaftaran (12)
INSERT INTO transaksi_pendaftaran (vehicle_id, client_id, keluhan, mekanik_id, status, tanggal_daftar, tanggal_mulai, catatan) VALUES
(1, 1, 'Servis berkala 2.000 km dan cek rem', 1, 'Registered', NOW(), NULL, 'Customer minta selesai hari ini'),
(2, 2, 'Ganti oli, cek CVT, suara kasar saat akselerasi', 3, 'Registered', NOW() - INTERVAL 30 MINUTE, NULL, 'Tunggu approval sparepart'),
(3, 3, 'Tarikan berat, minta cek roller dan v-belt', 5, 'InProgress', NOW() - INTERVAL 1 DAY, NOW(), 'Estimasi selesai sore'),
(4, 4, 'Overheat ringan dan flushing coolant', 2, 'Completed', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 2 DAY, 'Sudah diambil customer'),
(11, 11, 'Servis berkala pertama 1000 km', 1, 'Registered', NOW() - INTERVAL 1 HOUR, NULL, 'Motor baru'),
(12, 12, 'Bunyi kasar di CVT saat jalan pelan', 3, 'InProgress', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 3 HOUR, 'Perlu ganti roller'),
(13, 13, 'Rantai kendor dan bunyi berisik', 4, 'InProgress', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 HOUR, 'Ganti rantai set'),
(14, 14, 'Servis tune-up mesin sport', 3, 'Registered', NOW() - INTERVAL 15 MINUTE, NULL, 'Customer tunggu'),
(15, 15, 'Kopling berat dan selip', 5, 'Completed', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY, 'Selesai ganti kampas kopling'),
(17, 17, 'Mesin brebet saat idle', 7, 'Registered', NOW() - INTERVAL 45 MINUTE, NULL, 'Cek injektor'),
(18, 18, 'Ganti oli dan tune up ringan', 10, 'Completed', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY, 'Fast track'),
(21, 21, 'Ban depan bocor halus, minta ganti baru', 4, 'InProgress', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 2 HOUR, 'Stok ban tersedia');

-- Dummy Transaksi Servis (25)
INSERT INTO transaksi_servis (tanggal, client_id, vehicle_id, mekanik_id, keluhan, status_servis, total_jasa, total_sparepart, grand_total, bayar, kembali, user_kasir) VALUES 
(NOW(), 1, 1, 1, 'Ganti oli dan bongkar mesin', 'Selesai Lunas', 100000, 52000, 152000, 200000, 48000, 'kasir1'),
(NOW(), 2, 2, 2, 'Lampu depan mati', 'Menunggu', 25000, 0, 25000, 0, 0, 'kasir2'),
(NOW(), 3, 3, 3, 'Servis CVT tarikan berat', 'Dikerjakan', 60000, 150000, 210000, 0, 0, 'kasir1'),
(NOW() - INTERVAL 1 MONTH, 4, 4, 1, 'Ganti ban depan', 'Selesai Lunas', 20000, 210000, 230000, 250000, 20000, 'kasir3'),
(NOW() - INTERVAL 2 MONTH, 5, 5, 4, 'Rem blong dan ganti kanvas', 'Selesai Lunas', 30000, 55000, 85000, 100000, 15000, 'kasir2'),
(LAST_DAY(NOW() - INTERVAL 5 MONTH), 6, 6, 5, 'Motor brebet susut gigi', 'Selesai Lunas', 75000, 25000, 100000, 100000, 0, 'kasir1'),
(NOW() - INTERVAL 65 DAY, 7, 7, 2, 'Kelistrikan rewel aki tekor', 'Selesai Lunas', 40000, 220000, 260000, 300000, 40000, 'kasir3'),
(NOW() - INTERVAL 100 DAY, 8, 8, 3, 'Ganti filter udara dan oli', 'Selesai Lunas', 45000, 112000, 157000, 200000, 43000, 'kasir2'),
(NOW() - INTERVAL 1 YEAR, 9, 9, 10, 'Servis ringan saja', 'Selesai Lunas', 60000, 0, 60000, 60000, 0, 'kasir1'),
(NOW() - INTERVAL 2 YEAR, 10, 10, 8, 'Turun mesin overheat', 'Selesai Lunas', 300000, 0, 300000, 500000, 200000, 'kasir3'),
-- Recent transactions for better dashboard data
(NOW() - INTERVAL 1 DAY, 11, 11, 1, 'Servis berkala pertama motor baru', 'Selesai Lunas', 50000, 52000, 102000, 150000, 48000, 'kasir1'),
(NOW() - INTERVAL 1 DAY, 12, 12, 3, 'Ganti roller dan per CVT', 'Dikerjakan', 80000, 120000, 200000, 0, 0, 'kasir2'),
(NOW() - INTERVAL 2 DAY, 13, 13, 4, 'Ganti rantai set lengkap', 'Selesai Lunas', 35000, 185000, 220000, 250000, 30000, 'kasir1'),
(NOW() - INTERVAL 2 DAY, 14, 14, 3, 'Tune-up R15 full check', 'Selesai Lunas', 120000, 85000, 205000, 210000, 5000, 'kasir3'),
(NOW() - INTERVAL 3 DAY, 15, 15, 5, 'Ganti kampas kopling set', 'Selesai Lunas', 75000, 110000, 185000, 200000, 15000, 'kasir2'),
(NOW() - INTERVAL 3 DAY, 16, 16, 2, 'Cek kelistrikan dan ganti busi', 'Selesai Lunas', 50000, 85000, 135000, 150000, 15000, 'kasir1'),
(NOW() - INTERVAL 4 DAY, 17, 17, 7, 'Servis injektor dan bersih throttle body', 'Selesai Lunas', 100000, 0, 100000, 100000, 0, 'kasir3'),
(NOW() - INTERVAL 5 DAY, 18, 18, 10, 'Ganti oli dan tune up ringan', 'Selesai Lunas', 35000, 70000, 105000, 110000, 5000, 'kasir2'),
(NOW() - INTERVAL 6 DAY, 19, 19, 7, 'Servis berkala dan ganti busi', 'Selesai Lunas', 40000, 50000, 90000, 100000, 10000, 'kasir1'),
(NOW() - INTERVAL 7 DAY, 20, 20, 3, 'Cek suara mesin kasar', 'Selesai Lunas', 80000, 0, 80000, 80000, 0, 'kasir3'),
(NOW() - INTERVAL 10 DAY, 21, 21, 4, 'Ganti ban depan tubeless', 'Selesai Lunas', 25000, 310000, 335000, 350000, 15000, 'kasir2'),
(NOW() - INTERVAL 12 DAY, 22, 22, 10, 'Servis ringan + cek aki', 'Selesai Lunas', 30000, 200000, 230000, 250000, 20000, 'kasir1'),
(NOW() - INTERVAL 14 DAY, 23, 23, 1, 'Ganti oli + filter udara', 'Selesai Lunas', 40000, 145000, 185000, 200000, 15000, 'kasir3'),
(NOW() - INTERVAL 20 DAY, 24, 24, 8, 'Servis besar CVT XMAX', 'Selesai Lunas', 200000, 150000, 350000, 400000, 50000, 'kasir2'),
(NOW() - INTERVAL 25 DAY, 25, 25, 4, 'Ganti kampas rem depan belakang', 'Selesai Lunas', 40000, 105000, 145000, 150000, 5000, 'kasir1');

-- Dummy Transaksi Servis Detail (30)
INSERT INTO transaksi_servis_detail (trans_id, sparepart_id, qty, harga, subtotal) VALUES 
(1, 1, 1, 52000, 52000),
(3, 10, 1, 150000, 150000),
(4, 6, 1, 210000, 210000),
(5, 4, 1, 55000, 55000),
(6, 3, 1, 25000, 25000),
(7, 8, 1, 220000, 220000),
(8, 1, 1, 52000, 52000),
(8, 9, 1, 60000, 60000),
-- New detail rows for recent transactions
(11, 1, 1, 52000, 52000),
(12, 22, 1, 120000, 120000),
(13, 20, 1, 185000, 185000),
(14, 13, 1, 85000, 85000),
(15, 15, 1, 110000, 110000),
(16, 13, 1, 85000, 85000),
(18, 12, 1, 70000, 70000),
(19, 14, 1, 50000, 50000),
(21, 16, 1, 310000, 310000),
(22, 18, 1, 200000, 200000),
(23, 1, 1, 52000, 52000),
(23, 9, 1, 60000, 60000),
(23, 24, 1, 65000, 65000),
(24, 10, 1, 150000, 150000),
(25, 4, 1, 55000, 55000),
(25, 14, 1, 50000, 50000);

-- Dummy Transaksi Pembelian Sparepart (15)
INSERT INTO transaksi_pembelian (tanggal, supplier_id, sparepart_id, qty, harga_beli, total_harga, keterangan) VALUES
(NOW() - INTERVAL 30 DAY, 1, 1, 20, 45000, 900000, 'Restock oli mesin'),
(NOW() - INTERVAL 25 DAY, 8, 3, 10, 15000, 150000, 'Restock busi NGK'),
(NOW() - INTERVAL 20 DAY, 6, 6, 5, 180000, 900000, 'Restock ban IRC 90/90'),
(NOW() - INTERVAL 15 DAY, 4, 8, 3, 180000, 540000, 'Restock aki Yuasa'),
(NOW() - INTERVAL 10 DAY, 2, 4, 8, 40000, 320000, 'Restock kampas rem depan'),
(NOW() - INTERVAL 8 DAY, 1, 11, 10, 68000, 680000, 'Restock oli Castrol Power1'),
(NOW() - INTERVAL 7 DAY, 1, 12, 8, 55000, 440000, 'Restock oli Shell Advance'),
(NOW() - INTERVAL 6 DAY, 8, 13, 15, 65000, 975000, 'Restock busi Iridium NGK'),
(NOW() - INTERVAL 5 DAY, 2, 14, 20, 35000, 700000, 'Restock kampas rem Beat'),
(NOW() - INTERVAL 4 DAY, 6, 16, 4, 250000, 1000000, 'Restock ban Michelin'),
(NOW() - INTERVAL 3 DAY, 9, 20, 6, 145000, 870000, 'Restock rantai SSS'),
(NOW() - INTERVAL 2 DAY, 9, 21, 5, 195000, 975000, 'Restock gear set SSS'),
(NOW() - INTERVAL 1 DAY, 3, 22, 10, 85000, 850000, 'Restock roller Dr. Pulley'),
(NOW(), 7, 24, 30, 45000, 1350000, 'Restock bohlam LED motor'),
(NOW() - INTERVAL 12 DAY, 3, 25, 8, 75000, 600000, 'Restock gasket set Beat');
