package tokokelontong.model;

import java.util.Locale;

/**
 * Entitas utama: produk yang dijual di toko kelontong.
 * Invarian: harga harus lebih dari 0 dan stok tidak boleh negatif.
 */
public class Produk {
    private String kode;
    private String nama;
    private double harga;
    private int stok;
    private boolean aktif;

    /** Konstruktor default dengan nilai awal yang valid. */
    public Produk() {
        this("PRD000", "Tanpa Nama", 1.0, 0);
    }

    /** Konstruktor berparameter; semua nilai divalidasi lewat setter. */
    public Produk(String kode, String nama, double harga, int stok) {
        setKode(kode);
        setNama(nama);
        setHarga(harga);
        setStok(stok);
        this.aktif = true;
    }

    // ===== Getter =====
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public boolean isAktif() { return aktif; }

    // ===== Setter dengan validasi =====
    public void setKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong.");
        }
        this.kode = kode.trim().toUpperCase();
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama produk tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public void setHarga(double harga) {
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih dari 0.");
        }
        this.harga = harga;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
        }
        this.stok = stok;
    }

    public void ubahStatus(boolean aktif) {
        this.aktif = aktif;
    }

    // ===== Method perilaku =====
    /** Menghitung nilai persediaan produk (harga x stok). */
    public double hitungNilaiStok() {
        return harga * stok;
    }

    /** Memeriksa apakah stok mencukupi untuk jumlah yang diminta. */
    public boolean isStokCukup(int jumlah) {
        return jumlah > 0 && stok >= jumlah;
    }

    /** Mengurangi stok; mengembalikan false bila stok tidak cukup. */
    public boolean kurangiStok(int jumlah) {
        if (!isStokCukup(jumlah)) {
            return false;
        }
        this.stok -= jumlah;
        return true;
    }

    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah tambah stok harus lebih dari 0.");
        }
        this.stok += jumlah;
    }

    public String tampilkanInfo() {
        return String.format("%-8s %-20s %12s  stok=%-4d %s",
                kode, nama, rupiah(harga), stok, aktif ? "[aktif]" : "[nonaktif]");
    }

    /** Format angka menjadi mata uang rupiah, misalnya Rp65.000. */
    public static String rupiah(double nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", nilai);
    }
}