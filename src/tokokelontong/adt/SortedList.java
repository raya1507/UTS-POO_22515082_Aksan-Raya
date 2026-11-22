package tokokelontong.adt;

import tokokelontong.model.Produk;

/**
 * ADT Sorted List berbasis array berkapasitas tetap.
 * Elemen disimpan terurut naik berdasarkan atribut kunci {@code kode} produk.
 * Struktur internal (array dan size) bersifat private; akses hanya lewat operasi publik.
 *
 * Kompleksitas: insert O(n), remove O(n), search O(log n), get O(1), size O(1).
 */
public class SortedList {
    /** Kapasitas bawaan rak produk. */
    public static final int KAPASITAS_DEFAULT = 5;

    private final Produk[] data;
    private int size;

    /** Membuat list dengan kapasitas bawaan. */
    public SortedList() {
        this(KAPASITAS_DEFAULT);
    }

    /** Membuat list dengan kapasitas tertentu (harus > 0). */
    public SortedList(int kapasitas) {
        if (kapasitas <= 0) {
            throw new IllegalArgumentException("Kapasitas harus lebih dari 0.");
        }
        this.data = new Produk[kapasitas];
        this.size = 0;
    }

    public int size() { return size; }
    public int capacity() { return data.length; }
    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == data.length; }

    /**
     * Menyisipkan produk pada posisi yang menjaga keterurutan.
     * Prasyarat: produk tidak null, list tidak penuh, kode belum ada.
     * @return true jika berhasil; false jika syarat tidak terpenuhi (list tidak berubah).
     */
    public boolean insert(Produk p) {
        if (p == null || isFull()) {
            return false;
        }
        int pos = lowerBound(p.getKode());
        if (pos < size && data[pos].getKode().equals(p.getKode())) {
            return false; // kode duplikat
        }
        for (int i = size; i > pos; i--) {
            data[i] = data[i - 1];
        }
        data[pos] = p;
        size++;
        return true;
    }

    /**
     * Menghapus produk berdasarkan kode.
     * @return true jika ditemukan dan dihapus; false jika list kosong atau kode tidak ada.
     */
    public boolean remove(String kode) {
        int idx = indexOf(kode);
        if (idx < 0) {
            return false;
        }
        for (int i = idx; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;
        return true;
    }

    /** Mencari produk berdasarkan kode (binary search). @return produk atau null. */
    public Produk search(String kode) {
        int idx = indexOf(kode);
        return idx < 0 ? null : data[idx];
    }

    /** Mengambil elemen pada indeks tertentu. @return produk, atau null bila indeks di luar 0..size-1. */
    public Produk get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return data[index];
    }

    /** Mengembalikan teks seluruh isi list (terurut), atau pesan bila kosong. */
    public String tampilkan() {
        if (isEmpty()) {
            return "(list kosong)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(String.format("[%d] %s", i, data[i].tampilkanInfo()));
            if (i < size - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    // ===== Helper privat =====

    /** Mengembalikan indeks kode, atau -1 bila tidak ada. */
    private int indexOf(String kode) {
        if (kode == null || isEmpty()) {
            return -1;
        }
        String key = kode.trim().toUpperCase();
        int pos = lowerBound(key);
        if (pos < size && data[pos].getKode().equals(key)) {
            return pos;
        }
        return -1;
    }

    /** Indeks pertama yang kodenya >= key (binary search). */
    private int lowerBound(String key) {
        int lo = 0;
        int hi = size;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (data[mid].getKode().compareTo(key) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}