package tokokelontong.model;

/**
 * Entitas pendukung: pelanggan toko.
 * Poin bertambah 1 untuk setiap kelipatan Rp10.000 belanja.
 */
public class Pelanggan {
    private static final double NILAI_PER_POIN = 10000.0;

    private String id;
    private String nama;
    private int poin;
    private boolean member;
    private double totalBelanja;

    /** Konstruktor default. */
    public Pelanggan() {
        this("CUS000", "Umum", false);
    }

    /** Konstruktor berparameter. */
    public Pelanggan(String id, String nama, boolean member) {
        setId(id);
        setNama(nama);
        this.member = member;
        this.poin = 0;
        this.totalBelanja = 0;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public int getPoin() { return poin; }
    public boolean isMember() { return member; }
    public double getTotalBelanja() { return totalBelanja; }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID pelanggan tidak boleh kosong.");
        }
        this.id = id.trim().toUpperCase();
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pelanggan tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    /** Mencatat belanja: menambah total belanja dan poin. */
    public void catatBelanja(double nilai) {
        if (nilai < 0) {
            throw new IllegalArgumentException("Nilai belanja tidak boleh negatif.");
        }
        this.totalBelanja += nilai;
        this.poin += (int) (nilai / NILAI_PER_POIN);
    }

    public void jadikanMember() {
        this.member = true;
    }

    public String tampilkanInfo() {
        return String.format("%-8s %-18s poin=%-4d total belanja=%-12s %s",
                id, nama, poin, Produk.rupiah(totalBelanja), member ? "[member]" : "[umum]");
    }
}