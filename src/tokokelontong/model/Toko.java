package tokokelontong.model;

import java.util.ArrayList;
import java.util.List;
import tokokelontong.adt.SortedList;

/**
 * Entitas pengelola: Toko memiliki rak produk (ADT SortedList, komposisi),
 * daftar pelanggan, dan riwayat transaksi.
 * Pelanggaran aturan bisnis dilaporkan lewat exception agar program tidak berhenti.
 */
public class Toko {
    private String nama;
    private String alamat;
    private double saldoKas;
    private boolean buka;
    private final SortedList rak;
    private final List<Pelanggan> daftarPelanggan;
    private final List<Transaksi> daftarTransaksi;
    private int nomorTransaksi;

    /** Konstruktor default. */
    public Toko() {
        this("Toko Kelontong Sejahtera", "Kendari", SortedList.KAPASITAS_DEFAULT);
    }

    /** Konstruktor berparameter. */
    public Toko(String nama, String alamat, int kapasitasRak) {
        setNama(nama);
        this.alamat = alamat;
        this.rak = new SortedList(kapasitasRak);
        this.daftarPelanggan = new ArrayList<>();
        this.daftarTransaksi = new ArrayList<>();
        this.saldoKas = 0;
        this.buka = true;
        this.nomorTransaksi = 0;
    }

    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public double getSaldoKas() { return saldoKas; }
    public boolean isBuka() { return buka; }
    public SortedList getRak() { return rak; }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama toko tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public void setBuka(boolean buka) {
        this.buka = buka;
    }

    // ===== Produk (lewat ADT) =====
    public void tambahProduk(Produk p) {
        if (rak.isFull()) {
            throw new IllegalStateException("Rak penuh (kapasitas " + rak.capacity() + "). Produk ditolak.");
        }
        if (rak.search(p.getKode()) != null) {
            throw new IllegalArgumentException("Kode produk " + p.getKode() + " sudah ada.");
        }
        rak.insert(p);
    }

    public boolean hapusProduk(String kode) {
        return rak.remove(kode);
    }

    public Produk cariProduk(String kode) {
        return rak.search(kode);
    }

    /** Pencarian nama secara linear (mengandung kata kunci, tidak peka huruf besar/kecil). */
    public List<Produk> cariProdukByNama(String kataKunci) {
        List<Produk> hasil = new ArrayList<>();
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        for (int i = 0; i < rak.size(); i++) {
            Produk p = rak.get(i);
            if (p.getNama().toLowerCase().contains(kunci)) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    // ===== Pelanggan =====
    public void tambahPelanggan(Pelanggan c) {
        if (cariPelanggan(c.getId()) != null) {
            throw new IllegalArgumentException("ID pelanggan " + c.getId() + " sudah ada.");
        }
        daftarPelanggan.add(c);
    }

    public Pelanggan cariPelanggan(String id) {
        if (id == null) {
            return null;
        }
        String key = id.trim().toUpperCase();
        for (Pelanggan c : daftarPelanggan) {
            if (c.getId().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public List<Pelanggan> cariPelangganByNama(String kataKunci) {
        List<Pelanggan> hasil = new ArrayList<>();
        String kunci = kataKunci == null ? "" : kataKunci.trim().toLowerCase();
        for (Pelanggan c : daftarPelanggan) {
            if (c.getNama().toLowerCase().contains(kunci)) {
                hasil.add(c);
            }
        }
        return hasil;
    }

    public List<Pelanggan> getDaftarPelanggan() {
        return new ArrayList<>(daftarPelanggan);
    }

    // ===== Transaksi =====
    /**
     * Membuat dan memproses transaksi. Aturan bisnis:
     * produk/pelanggan harus ada, jumlah > 0, diskon 0..50%, stok cukup;
     * stok berkurang saat transaksi berhasil.
     */
    public Transaksi buatTransaksi(String kodeProduk, String idPelanggan, int jumlah, double diskon) {
        if (!buka) {
            throw new IllegalStateException("Toko sedang tutup.");
        }
        Produk produk = cariProduk(kodeProduk);
        if (produk == null) {
            throw new IllegalArgumentException("Produk dengan kode '" + kodeProduk + "' tidak ditemukan.");
        }
        Pelanggan pelanggan = cariPelanggan(idPelanggan);
        if (pelanggan == null) {
            throw new IllegalArgumentException("Pelanggan dengan ID '" + idPelanggan + "' tidak ditemukan.");
        }
        if (!produk.isAktif()) {
            throw new IllegalStateException("Produk " + produk.getNama() + " tidak aktif.");
        }
        // Validasi jumlah & diskon terjadi di konstruktor Transaksi.
        String id = String.format("TRX%03d", nomorTransaksi + 1);
        Transaksi t = new Transaksi(id, produk, pelanggan, jumlah, diskon);
        if (!produk.isStokCukup(jumlah)) {
            throw new IllegalStateException("Stok " + produk.getNama() + " tidak cukup (sisa "
                    + produk.getStok() + ", diminta " + jumlah + ").");
        }
        t.proses();
        nomorTransaksi++;
        saldoKas += t.getTotalBayar();
        daftarTransaksi.add(t);
        return t;
    }

    public List<Transaksi> getDaftarTransaksi() {
        return new ArrayList<>(daftarTransaksi);
    }

    // ===== Laporan =====
    public String buatLaporan() {
        int unitTerjual = 0;
        for (Transaksi t : daftarTransaksi) {
            unitTerjual += t.getJumlah();
        }
        double nilaiStok = 0;
        for (int i = 0; i < rak.size(); i++) {
            nilaiStok += rak.get(i).hitungNilaiStok();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN ").append(nama.toUpperCase()).append(" ===\n");
        sb.append("Jumlah jenis produk : ").append(rak.size()).append(" / ").append(rak.capacity()).append('\n');
        sb.append("Jumlah pelanggan    : ").append(daftarPelanggan.size()).append('\n');
        sb.append("Jumlah transaksi    : ").append(daftarTransaksi.size()).append('\n');
        sb.append("Total unit terjual  : ").append(unitTerjual).append('\n');
        sb.append("Total pendapatan    : ").append(Produk.rupiah(saldoKas)).append('\n');
        sb.append("Nilai stok tersisa  : ").append(Produk.rupiah(nilaiStok));
        return sb.toString();
    }
}