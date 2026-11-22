package tokokelontong.model;

/**
 * Entitas transaksi: menghubungkan satu Pelanggan dengan satu Produk.
 * Invarian: jumlah > 0, diskon 0..MAKS_DISKON persen, stok berkurang saat transaksi diproses.
 */
public class Transaksi {
    /** Diskon maksimal yang diizinkan (persen). */
    public static final double MAKS_DISKON = 50.0;

    private String idTransaksi;
    private Produk produk;
    private Pelanggan pelanggan;
    private int jumlah;
    private double diskonPersen;
    private double totalBayar;
    private boolean selesai;

    /** Konstruktor default (transaksi kosong, belum bisa diproses). */
    public Transaksi() {
        this.idTransaksi = "TRX000";
        this.jumlah = 1;
        this.diskonPersen = 0;
        this.selesai = false;
    }

    /** Konstruktor berparameter; seluruh nilai divalidasi. */
    public Transaksi(String idTransaksi, Produk produk, Pelanggan pelanggan,
                     int jumlah, double diskonPersen) {
        if (produk == null || pelanggan == null) {
            throw new IllegalArgumentException("Produk dan pelanggan wajib diisi.");
        }
        this.idTransaksi = idTransaksi;
        this.produk = produk;
        this.pelanggan = pelanggan;
        setJumlah(jumlah);
        setDiskonPersen(diskonPersen);
        this.selesai = false;
    }

    public String getIdTransaksi() { return idTransaksi; }
    public Produk getProduk() { return produk; }
    public Pelanggan getPelanggan() { return pelanggan; }
    public int getJumlah() { return jumlah; }
    public double getDiskonPersen() { return diskonPersen; }
    public double getTotalBayar() { return totalBayar; }
    public boolean isSelesai() { return selesai; }

    public void setJumlah(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah beli harus lebih dari 0.");
        }
        this.jumlah = jumlah;
    }

    public void setDiskonPersen(double diskonPersen) {
        if (diskonPersen < 0 || diskonPersen > MAKS_DISKON) {
            throw new IllegalArgumentException(
                    "Diskon harus 0 sampai " + (int) MAKS_DISKON + " persen.");
        }
        this.diskonPersen = diskonPersen;
    }

    public double hitungSubtotal() {
        return produk == null ? 0 : produk.getHarga() * jumlah;
    }

    public double hitungDiskon() {
        return hitungSubtotal() * diskonPersen / 100.0;
    }

    public double hitungTotal() {
        return hitungSubtotal() - hitungDiskon();
    }

    /**
     * Memproses transaksi: mengurangi stok produk dan mencatat belanja pelanggan.
     * @return true jika berhasil, false jika data belum lengkap, sudah selesai, atau stok kurang.
     */
    public boolean proses() {
        if (selesai || produk == null || pelanggan == null) {
            return false;
        }
        if (!produk.isAktif() || !produk.kurangiStok(jumlah)) {
            return false;
        }
        this.totalBayar = hitungTotal();
        pelanggan.catatBelanja(totalBayar);
        this.selesai = true;
        return true;
    }

    public String tampilkanStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- STRUK ").append(idTransaksi).append(" ---\n");
        sb.append("Pelanggan : ").append(pelanggan.getNama()).append('\n');
        sb.append("Produk    : ").append(produk.getNama()).append(" x").append(jumlah).append('\n');
        sb.append("Subtotal  : ").append(Produk.rupiah(hitungSubtotal())).append('\n');
        sb.append("Diskon    : ").append(diskonPersen).append("% (")
                .append(Produk.rupiah(hitungDiskon())).append(")\n");
        sb.append("TOTAL     : ").append(Produk.rupiah(selesai ? totalBayar : hitungTotal()));
        return sb.toString();
    }

    public String ringkas() {
        return String.format("%-7s %-18s %-18s x%-3d diskon=%4.1f%%  total=%s",
                idTransaksi, pelanggan.getNama(), produk.getNama(), jumlah,
                diskonPersen, Produk.rupiah(totalBayar));
    }
}