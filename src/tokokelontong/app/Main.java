package tokokelontong.app;

import java.util.List;
import java.util.Scanner;
import tokokelontong.model.Pelanggan;
import tokokelontong.model.Produk;
import tokokelontong.model.Toko;
import tokokelontong.model.Transaksi;

/**
 * Kelas Main/Menu aplikasi console Toko Kelontong.
 * Seluruh input divalidasi; kesalahan ditampilkan sebagai pesan, program tidak berhenti.
 */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    /** Bila ECHO_INPUT=1, input ditampilkan ulang (dipakai saat input berasal dari file/pipe). */
    private static final boolean ECHO = "1".equals(System.getenv("ECHO_INPUT"));

    private static final Toko TOKO = new Toko("Toko Kelontong Sejahtera", "Kendari", 10);

    /**
     * Mengisi toko dengan data contoh dunia nyata (produk kebutuhan sehari-hari,
     * pelanggan, dan dua transaksi). Dilewati bila program dijalankan dengan argumen --kosong.
     */
    private static void isiDataAwal() {
        try {
            // Produk: kode, nama, harga (Rp), stok
            TOKO.tambahProduk(new Produk("BRS001", "Beras Premium 5kg", 68000, 25));
            TOKO.tambahProduk(new Produk("MYK001", "Minyak Goreng 1L", 18000, 40));
            TOKO.tambahProduk(new Produk("GLA001", "Gula Pasir 1kg", 17500, 30));
            TOKO.tambahProduk(new Produk("TLR001", "Telur Ayam 1kg", 29000, 20));
            TOKO.tambahProduk(new Produk("MIE001", "Mi Instan Goreng", 3500, 100));
            TOKO.tambahProduk(new Produk("KPI001", "Kopi Bubuk 165g", 14000, 24));
            TOKO.tambahProduk(new Produk("SBN001", "Sabun Mandi Batang", 4500, 50));

            // Pelanggan: id, nama, member?
            TOKO.tambahPelanggan(new Pelanggan("C001", "Budi Santoso", true));
            TOKO.tambahPelanggan(new Pelanggan("C002", "Siti Aminah", true));
            TOKO.tambahPelanggan(new Pelanggan("C003", "La Ode Rahman", false));
            TOKO.tambahPelanggan(new Pelanggan("C004", "Wayan Sudiarta", false));

            // Transaksi contoh: kode produk, id pelanggan, jumlah, diskon (%)
            TOKO.buatTransaksi("BRS001", "C001", 2, 5);
            TOKO.buatTransaksi("MYK001", "C002", 3, 0);
            System.out.println("[INFO] Data awal dimuat: 7 produk, 4 pelanggan, 2 transaksi.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[GAGAL] Data awal tidak lengkap: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length == 0 || !args[0].equals("--kosong")) {
            isiDataAwal();
        }
        boolean jalan = true;
        while (jalan) {
            tampilkanMenu();
            int pilihan = bacaInt("Pilih menu: ");
            switch (pilihan) {
                case 1: aksiTambahProduk(); break;
                case 2: tampilkanProduk(); break;
                case 3: aksiTambahPelanggan(); break;
                case 4: tampilkanPelanggan(); break;
                case 5: aksiTransaksi(); break;
                case 6: aksiCari(); break;
                case 7: menuAdt(); break;
                case 8: System.out.println(TOKO.buatLaporan()); break;
                case 9: tampilkanRiwayat(); break;
                case 0:
                    System.out.println("Terima kasih. Program selesai.");
                    jalan = false;
                    break;
                default:
                    System.out.println("[GAGAL] Menu " + pilihan + " tidak tersedia. Pilih 0-9.");
            }
        }
    }

    private static void tampilkanMenu() {
        System.out.println();
        System.out.println("===== " + TOKO.getNama().toUpperCase() + " =====");
        System.out.println("1. Tambah produk");
        System.out.println("2. Tampilkan semua produk");
        System.out.println("3. Tambah pelanggan");
        System.out.println("4. Tampilkan semua pelanggan");
        System.out.println("5. Buat transaksi");
        System.out.println("6. Cari data (produk/pelanggan)");
        System.out.println("7. Operasi ADT Sorted List (rak produk)");
        System.out.println("8. Laporan ringkasan");
        System.out.println("9. Riwayat transaksi");
        System.out.println("0. Keluar");
    }

    // ===== Aksi menu utama =====
    private static void aksiTambahProduk() {
        String kode = bacaTeks("Kode produk   : ");
        String nama = bacaTeks("Nama produk   : ");
        double harga = bacaDouble("Harga         : ");
        int stok = bacaInt("Stok awal      : ");
        try {
            TOKO.tambahProduk(new Produk(kode, nama, harga, stok));
            System.out.println("[OK] Produk " + kode.toUpperCase() + " ditambahkan ke rak.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    private static void tampilkanProduk() {
        System.out.println("--- Daftar Produk (terurut kode) ---");
        System.out.println(TOKO.getRak().tampilkan());
    }

    private static void aksiTambahPelanggan() {
        String id = bacaTeks("ID pelanggan  : ");
        String nama = bacaTeks("Nama pelanggan: ");
        boolean member = bacaYaTidak("Member? (y/t)  : ");
        try {
            TOKO.tambahPelanggan(new Pelanggan(id, nama, member));
            System.out.println("[OK] Pelanggan " + id.toUpperCase() + " ditambahkan.");
        } catch (IllegalArgumentException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    private static void tampilkanPelanggan() {
        System.out.println("--- Daftar Pelanggan ---");
        List<Pelanggan> list = TOKO.getDaftarPelanggan();
        if (list.isEmpty()) {
            System.out.println("(belum ada pelanggan)");
            return;
        }
        for (Pelanggan c : list) {
            System.out.println(c.tampilkanInfo());
        }
    }

    private static void aksiTransaksi() {
        String kode = bacaTeks("Kode produk   : ");
        String id = bacaTeks("ID pelanggan  : ");
        int jumlah = bacaInt("Jumlah beli   : ");
        double diskon = bacaDouble("Diskon (%) 0-50: ");
        try {
            Transaksi t = TOKO.buatTransaksi(kode, id, jumlah, diskon);
            System.out.println("[OK] Transaksi berhasil.");
            System.out.println(t.tampilkanStruk());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    private static void aksiCari() {
        System.out.println("Cari: 1) Produk by kode  2) Produk by nama  3) Pelanggan by ID  4) Pelanggan by nama");
        int jenis = bacaInt("Pilih: ");
        switch (jenis) {
            case 1: {
                Produk p = TOKO.cariProduk(bacaTeks("Kode produk: "));
                System.out.println(p == null ? "[INFO] Produk tidak ditemukan." : p.tampilkanInfo());
                break;
            }
            case 2: {
                List<Produk> hasil = TOKO.cariProdukByNama(bacaTeks("Kata kunci nama: "));
                if (hasil.isEmpty()) {
                    System.out.println("[INFO] Tidak ada produk yang cocok.");
                }
                for (Produk p : hasil) {
                    System.out.println(p.tampilkanInfo());
                }
                break;
            }
            case 3: {
                Pelanggan c = TOKO.cariPelanggan(bacaTeks("ID pelanggan: "));
                System.out.println(c == null ? "[INFO] Pelanggan tidak ditemukan." : c.tampilkanInfo());
                break;
            }
            case 4: {
                List<Pelanggan> hasil = TOKO.cariPelangganByNama(bacaTeks("Kata kunci nama: "));
                if (hasil.isEmpty()) {
                    System.out.println("[INFO] Tidak ada pelanggan yang cocok.");
                }
                for (Pelanggan c : hasil) {
                    System.out.println(c.tampilkanInfo());
                }
                break;
            }
            default:
                System.out.println("[GAGAL] Pilihan pencarian tidak valid.");
        }
    }

    private static void tampilkanRiwayat() {
        System.out.println("--- Riwayat Transaksi ---");
        List<Transaksi> list = TOKO.getDaftarTransaksi();
        if (list.isEmpty()) {
            System.out.println("(belum ada transaksi)");
            return;
        }
        for (Transaksi t : list) {
            System.out.println(t.ringkas());
        }
    }

    // ===== Submenu ADT =====
    private static void menuAdt() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println();
            System.out.println("--- Operasi ADT Sorted List ---");
            System.out.println("1. insert  2. remove  3. search  4. get(index)  5. tampilkan  6. status (size/isEmpty/isFull)  0. kembali");
            int pilih = bacaInt("Pilih operasi: ");
            switch (pilih) {
                case 1: aksiTambahProduk(); break;
                case 2: {
                    String kode = bacaTeks("Kode yang dihapus: ");
                    boolean ok = TOKO.hapusProduk(kode);
                    System.out.println(ok ? "[OK] Produk dihapus."
                            : "[GAGAL] Tidak ada yang dihapus (list kosong atau kode tidak ditemukan).");
                    break;
                }
                case 3: {
                    Produk p = TOKO.getRak().search(bacaTeks("Kode dicari: "));
                    System.out.println(p == null ? "[INFO] search -> null (tidak ditemukan)." : "search -> " + p.tampilkanInfo());
                    break;
                }
                case 4: {
                    int idx = bacaInt("Indeks: ");
                    Produk p = TOKO.getRak().get(idx);
                    System.out.println(p == null ? "[GAGAL] Indeks " + idx + " di luar jangkauan (size="
                            + TOKO.getRak().size() + ")." : "get(" + idx + ") -> " + p.tampilkanInfo());
                    break;
                }
                case 5: tampilkanProduk(); break;
                case 6:
                    System.out.println("size=" + TOKO.getRak().size()
                            + ", capacity=" + TOKO.getRak().capacity()
                            + ", isEmpty=" + TOKO.getRak().isEmpty()
                            + ", isFull=" + TOKO.getRak().isFull());
                    break;
                case 0: kembali = true; break;
                default: System.out.println("[GAGAL] Operasi tidak tersedia. Pilih 0-6.");
            }
        }
    }

    // ===== Helper input tervalidasi =====
    private static String bacaBaris(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            System.out.println("\n[INFO] Input berakhir. Program selesai.");
            System.exit(0);
        }
        String baris = SCANNER.nextLine().trim();
        if (ECHO) {
            System.out.println(baris);
        }
        return baris;
    }

    private static String bacaTeks(String prompt) {
        while (true) {
            String s = bacaBaris(prompt);
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("[GAGAL] Input tidak boleh kosong.");
        }
    }

    private static int bacaInt(String prompt) {
        while (true) {
            String s = bacaBaris(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("[GAGAL] Input harus berupa angka bulat.");
            }
        }
    }

    private static double bacaDouble(String prompt) {
        while (true) {
            String s = bacaBaris(prompt);
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("[GAGAL] Input harus berupa angka.");
            }
        }
    }

    private static boolean bacaYaTidak(String prompt) {
        while (true) {
            String s = bacaBaris(prompt).toLowerCase();
            if (s.equals("y") || s.equals("ya")) {
                return true;
            }
            if (s.equals("t") || s.equals("tidak")) {
                return false;
            }
            System.out.println("[GAGAL] Jawab dengan y atau t.");
        }
    }
}