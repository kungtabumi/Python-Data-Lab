# PRODUCT REQUIREMENTS DOCUMENT (PRD)
## PYTHON DATALAB: LABORATORIUM STRUKTUR DATA INTERAKTIF
**SMA Negeri 1 Kepanjen — Mata Pelajaran Informatika (Fase F / Kelas XI)**  
*Guru Pengampu & Konseptor: Hermawan Hidayat, S.Kom.*

---

## 1. PENDAHULUAN & RINGKASAN EKSEKUTIF

### 1.1 Latar Belakang
Pemrograman dan Struktur Data merupakan materi inti dalam Kurikulum Merdeka mata pelajaran Informatika Fase F (Kelas XI SMA). Selama ini, pembelajaran struktur data abstrak seperti **List**, **Tuple**, **Set**, dan **Dictionary** sering menghadapi kendala pedagogis:
1. **Abstraksi Memori**: Siswa kesulitan membayangkan alokasi memori berurutan (List), sifat kekekalan data (*immutability* pada Tuple), eliminasi duplikasi otomatis (*hashing* pada Set), serta pasangan kunci-nilai (*key-value mapping* pada Dictionary).
2. **Keterpisahan Antara Koding dan Visualisasi**: Pada platform konvensional, siswa menulis kode di IDE/terminal yang terpisah dari diagram visualisasi data, sehingga korelasi perubahan memori tidak dapat diamati secara *real-time*.
3. **Keterbatasan Akses Lab Komputer**: Pembelajaran koding membutuhkan perangkat mobile yang mandiri, ringan, dapat diakses luring (*offline*), dan tidak bergantung sepenuhnya pada koneksi internet.

### 1.2 Visi Produk
**Python DataLab** diciptakan sebagai aplikasi edukasi mobile (*Android Native*) kelas industri yang menggabungkan:
* **Visualizer Memori Real-Time**: Representasi visual dua arah yang interaktif untuk setiap manipulasi data.
* **In-Lab Code Execution Engine**: Kemampuan menulis dan menjalankan kode Python secara langsung di halaman visualizer tanpa berpindah layar.
* **Kurikulum Komprehensif SMAN 1 Kepanjen**: Silabus modular yang terstruktur dari konsep dasar hingga teknik lanjutan (*nested structures*).
* **AI Tutor Pendamping**: Asisten cerdas bertenaga Gemini API (dengan *offline educational rule-based fallback*) yang siap mendiagnosis error dan membimbing siswa 24/7.

---

## 2. PROFIL PENGGUNA & TARGET AUDIENS

| Persona | Karakteristik & Kebutuhan | Peran dalam Aplikasi |
| :--- | :--- | :--- |
| **Siswa SMA Kelas XI (Fase F)** | Mempelajari dasar logika pemrograman; membutuhkan visualisasi grafis, penjelasan ramah pemula, feedback instan atas error sintaks, dan gamifikasi belajar. | Pengguna utama: Membaca modul, bereksperimen di Lab, berlatih di Playground, mengikuti evaluasi Kuis, dan berkonsultasi dengan AI Tutor. |
| **Guru Informatika (Hermawan Hidayat, S.Kom.)** | Membutuhkan media pembelajaran interaktif yang terstandarisasi, alat peraga visual di kelas, serta monitoring progres dan ketuntasan belajar siswa. | Pengarah materi & konseptor pedagogis: Menetapkan silabus, kunci kompetensi, indikator pencapaian, dan bank evaluasi kuis. |

---

## 3. ARSITEKTUR TEKNOLOGI & SISTEM

* **Sistem Operasi Target**: Android 7.0+ (API Level 24+) hingga Android 15/16 (compileSdk 36).
* **Bahasa & Framework**: Kotlin 2.2.10, Jetpack Compose (Declarative UI), Material Design 3 (M3).
* **Pola Arsitektur**: MVVM (Model-View-ViewModel) + Clean Architecture + Unidirectional Data Flow (UDF).
* **Local Python Engine**: `LocalPythonInterpreter` (Embedded AST-based execution engine untuk Python 3 data structures).
* **Persistensi Data**: Android Room Database (`AppDatabase`, `PythonLabRepository`, SQLite offline storage).
* **Kecerdasan Buatan (AI)**: Google Gemini API via Firebase AI Client / REST API dengan Fallback Offline Educator.
* **Desain Visual & Tema**: *Sleek Deep Navy Canvas* (`#0A0F1D`) berpadu dengan aksen segar *Vibrant Cyber Lime* (`#84CC16`), *Python Blue* (`#3B82F6`), *Python Gold* (`#F59E0B`), dan *Syntax Dark Palette*.

---

## 4. BEDAH MENDALAM MENU-MENU APLIKASI

Aplikasi Python DataLab memiliki sistem navigasi terpadu berbasis **Bottom Navigation Bar (6 Tab Utama)** dan **Contextual Stack Routing**. Berikut rincian fungsionalitas setiap menu:

```
[ BOTTOM NAVIGATION BAR ]
  ├── 1. BERANDA (Home Dashboard)
  ├── 2. MATERI (Learning Modules & Lesson Detail)
  ├── 3. LAB (Interactive Memory Lab & Live Console)
  ├── 4. CODE (Full Python Playground & IDE)
  ├── 5. KUIS (Evaluasi 20 Soal Tantangan & Streak)
  └── 6. AI TUTOR (Konsultasi Pedagogis & Diagnosis Error)
```

---

### MENU 1: BERANDA (HOME DASHBOARD)
Pusat kontrol dan ringkasan pencapaian belajar siswa yang menyajikan data secara *gamified* dan intuitif.

#### Fitur & Komponen Utama:
1. **Header Identitas & Metrik Gamifikasi**:
   * Menampilkan nama sekolah: **SMAN 1 Kepanjen**.
   * **Daily Streak Counter**: Menghitung konsistensi belajar harian siswa dengan ikon api oranye menyala.
   * **Level & Total XP**: Kalkulasi dinamis poin pengalaman (`UserProgressEntity.totalXp`) dan kenaikan level otomatis setiap kelipatan 150 XP.
2. **Progress Ring Tracker**:
   * Indikator melingkar persentase ketuntasan kurikulum (misal: *8/14 Modul Selesai - 57%*).
   * Status lencana penghargaan (*Badges Unlocked*) yang dapat diklik untuk melihat koleksi prestasi.
3. **Struktur Data Quick Cards (Akses Pintasan)**:
   * **List**: Kartu dinamis beraksen Python Blue dengan keterangan sifat *Ordered, Mutable, [ ]*.
   * **Tuple**: Kartu beraksen Python Gold dengan indikator *Immutable, Constant, ( )*.
   * **Set**: Kartu beraksen Emerald Green dengan indikator *Unik, Himpunan, { }*.
   * **Dictionary**: Kartu beraksen Tangerine Orange dengan indikator *Key-Value, O(1), {k:v}*.
   * *Aksi*: Mengklik kartu langsung menyaring modul materi ke topik yang dipilih dan membuka menu Materi dengan filter otomatis.
4. **Action Hub Bawah**:
   * Akses 1-sentuhan menuju **Laboratorium Memori**, **Code Playground**, **Kuis Evaluasi**, dan **Tanya AI Tutor**.
5. **Reward Celebration Dialog**:
   * Pop-up perayaan beranimasi konfeti saat siswa menyelesaikan milestone atau meraih badge prestasi baru.

---

### MENU 2: MATERI (LEARNING MODULES & LESSON DETAIL)
Katalog kurikulum terstruktur yang menyajikan teori mendalam, analogi konseptual, dan visualisasi interaktif per topik.

#### Sub-Halaman 2.1: Daftar Modul (Lesson List Screen)
* **Kategori Tab Filter**: Siswa dapat menyaring materi: *Semua*, *List*, *Tuple*, *Set*, *Dictionary*, *Nested*, dan *Komparasi*.
* **Status Penyelesaian**: Setiap item modul memiliki badge *SELESAI (Checkmark Hijau)* atau *BELUM SELESAI* beserta perolehan XP.
* **Cerdas Navigasi Kembali (Smart Back)**: Jika siswa membuka modul lewat filter dari Beranda, menekan tombol kembali akan mereset filter terlebih dahulu (*tampilkan semua*) sebelum keluar ke Beranda.

#### Sub-Halaman 2.2: Detail Pelajaran (Lesson Detail Screen)
* **Distraction-Free Reading Mode**: Bottom Navigation Bar otomatis disembunyikan pada layar ini demi kenyamanan membaca dan bereksperimen.
* **Bagian Teori & Karakteristik**: Penjelasan bahasa Indonesia yang ramah siswa SMA dengan analogi kehidupan nyata (misal: List = Antrean belanja, Tuple = Titik koordinat GPS sekolah, Set = Daftar nomor induk siswa unik, Dictionary = Buku telepon/buku induk).
* **Embedded Memory Visualizer**: Komponen visualisasi interaktif disematkan langsung di dalam materi (misal: slot indeks memori, kotak terkunci tuple, diagram Venn, atau tabel pasangan kunci-nilai).
* **Annotated Python Code Box**: Blok kode beranotasi dengan *Syntax Highlighting* warna-warni yang membedakan kata kunci, string, angka, dan komentar.
* **Tombol "Buka di Editor"**: Mengirim contoh kode langsung ke Code Playground dengan menyertakan *Origin Context Banner* sehingga siswa dapat kembali ke materi dengan sekali ketuk.

---

### MENU 3: LAB (LABORATORIUM INTERAKTIF & VISUALIZER MEMORI)
Menu unggulan aplikasi tempat siswa bereksperimen memanipulasi struktur data dan mengamati pergerakan memori komputer secara langsung.

#### Sub-Tab Struktur Data:
1. **Lab 1: List (Daftar Dinamis & Slicing)**:
   * **Visual Memory Grid**: Menampilkan kotak-kotak elemen list yang dapat digeser secara horizontal.
   * **Dual-Index Badging**: Tombol sakelar untuk menampilkan indeks positif `[0, 1, 2...]` atau indeks negatif `[-3, -2, -1...]`.
   * **Interactive Operations**: Tombol operasional langsung untuk `append()`, `insert(index, val)`, `pop()`, `remove(val)`, `sort()`, dan `reverse()`.
   * **Interactive Slicing Bar**: Slider rentang `[start : end]` yang memberikan efek sorot (*glow highlight*) pada elemen terpilih.
2. **Lab 2: Tuple (Keamanan Memori & Immutability)**:
   * **Locked Container Visualizer**: Wadah memori berikon gembok emas yang merepresentasikan sifat konstan (*read-only*).
   * **Uji Immutability (Stress Test)**: Tombol interaktif untuk mencoba mengubah elemen tuple (`titik[0] = 'Baru'`). Sistem memicu animasi getar (*shake animation*) dan menampilkan *TypeError alert* untuk membuktikan kekekalan Tuple.
   * **Tuple Unpacking Demo**: Memvisualisasikan pemecahan tuple ke beberapa variabel terpisah (`nama, lat, lon = titik`).
3. **Lab 3: Set (Himpunan Matematika & Eliminasi Duplikat)**:
   * **Interactive Venn Diagram**: Representasi visual dua himpunan (Set A dan Set B) dengan area irisan di tengah.
   * **Operasi Himpunan 4 Arah**: Tab instan untuk **Gabungan (`|`)**, **Irisan (`&`)**, **Selisih (`-`)**, dan **Beda Simetris (`^`)**.
   * **Auto-Deduplicate Test**: Input teks untuk mencoba memasukkan nama yang sama ke Set A; visualizer mendemonstrasikan bagaimana duplikasi otomatis diabaikan.
4. **Lab 4: Dictionary (Pemetaan Kunci-Nilai O(1))**:
   * **Tabel Pasangan Interaktif**: Menampilkan baris `KEY` (berwarna kuning monospaced) dan `VALUE` (berwarna hijau).
   * **Formulir Mutasi Dinamis**: Input pasangan key-value baru dengan tombol `dict[k] = v` serta tombol hapus per baris.
5. **Lab 5: Nested Structures (Hierarki Data Bersarang)**:
   * **Tree Branch Visualizer**: Pohon bertingkat yang membedah akses dari Dictionary induk `siswa` ➔ List nilai `[80, 90, 85]` ➔ Nilai individual `siswa['nilai'][1]`.
6. **Lab 6: Tabel Komparasi Matriks**:
   * Komparasi menyeluruh antara List, Tuple, Set, dan Dict berdasarkan *Order*, *Mutability*, *Duplicates*, *Indexing*, dan *Skenario Penggunaan Nyata*.

#### Inovasi Utama: `LabCodeExecutionConsole` (Koding Langsung di Lab)
* **Tanpa Dialihkan ke Luar**: Proses pengujian kode tetap 100% berada di menu Lab agar relevansi antara kode dan visualizer memori tidak terputus.
* **Mode Otomatis vs Mode Edit Sendiri**:
  * *Mode Otomatis*: Kode Python terbentuk otomatis mengikuti tombol manipulasi visual yang ditekan.
  * *Mode Edit*: Siswa dapat mengklik tombol pensil untuk mengetik kode Python kustom sendiri langsung di dalam Lab.
* **Tombol "▶ Jalankan Kode di Lab (Live Output)"**:
  * Menjalankan interpreter Python lokal secara instan.
  * Menampilkan **Live Terminal Console** di bawah kode: output `stdout` berwarna hijau terang monospaced, penunjuk runtime (`ms`), status sukses/error, dan prompt `$ python3 lab_visualizer.py`.
* **Sinkronisasi Balik Dua Arah**: Jika kode yang dijalankan memodifikasi variabel (misal `buah = ['Semangka']`), kotak visualizer memori di atasnya langsung diperbarui secara otomatis.

---

### MENU 4: CODE (CODE PLAYGROUND & INTERACTIVE IDE)
Lingkungan pengembangan Python mandiri yang dirancang khusus untuk pembelajaran mobile siswa SMA.

#### Fitur & Komponen Utama:
1. **Origin Context Banner**:
   * Saat dibuka dari modul Materi atau Lab, muncul banner konteks:  
     `"Konteks: Materi [Judul Modul] • [← Kembali ke Materi]"`.
   * Menjamin siswa tidak tersesat dan dapat kembali ke halaman asal dengan satu ketukan.
2. **Dual Mode IDE**:
   * **Mode Latihan Snippet**: Memuat template siap pakai (Studi Kasus Nilai Rapor Siswa, Titik Koordinat Sekolah, Data Ekskul, dll.).
   * **Mode Editor Bebas (Full IDE)**: Editor teks bebas dengan penomoran baris, indentasi otomatis, dan tombol pintasan karakter pemrograman cepat (`:`, `[`, `]`, `{`, `}`, `(`, `)`, `"`, `'`, `=`, `_`).
3. **Eksekusi Lokal Tanpa Kuota**:
   * Menggunakan `LocalPythonInterpreter` yang berjalan lokal di CPU smartphone tanpa memerlukan server luar.
   * Mendukung percabangan `if-elif-else`, perulangan `for` dan `while`, operasi matematika, manipulasi string, serta metode struktur data bawaan.
4. **Terminal Output & Variabel Inspector**:
   * Menampilkan hasil cetak `print()`, pesan error traceback yang informatif, serta tabel inspeksi nilai variabel terakhir di memori.
5. **Tombol "Tanya AI Guru tentang Error Ini"**:
   * Jika eksekusi menghasilkan error, muncul tombol darurat AI yang langsung memindahkan siswa ke AI Tutor dengan menyertakan kode dan pesan error secara otomatis.
6. **Manajemen Snippet Lokal (Room Database)**:
   * Fitur simpan dan muat kembali kode latihan karya siswa ke database offline perangkat.

---

### MENU 5: KUIS (EVALUASI & TANTANGAN 20 SOAL)
Instrumen evaluasi pemahaman formatif dan sumatif yang mengukur kompetensi struktur data siswa secara komprehensif.

#### Fitur & Komponen Utama:
1. **Bank Soal Komprehensif (20 Soal Kurikulum Informatika XI)**:
   * Mencakup soal konsep teoritis, analisis output kode (*code tracing*), deteksi error sintaks/tipe data, hingga pemilihan struktur data optimal untuk kasus nyata.
2. **Mekanisme Ujian Interaktif**:
   * Pilihan ganda dengan umpan balik instan: warna hijau untuk jawaban benar dan merah untuk jawaban salah.
   * **Pembahasan Edukatif Lengkap**: Setiap soal dilengkapi penjelasan mendalam mengenai alasan mengapa jawaban tersebut benar serta konsep komputasi di baliknya.
3. **Streak Tracker & Sound/Visual Effect**:
   * Menghitung jawaban benar beruntun (*streak*) dengan pengganda skor XP.
4. **Layar Ringkasan Hasil & Reward**:
   * Menampilkan persentase akurasi, waktu pengerjaan, total perolehan XP, dan opsi mengulang evaluasi (*Retake Quiz*).

---

### MENU 6: AI TUTOR (ASISTEN BELAJAR CERDAS GEMINI)
Tutor virtual berkepribadian guru informatika ramah (*"Pak Hermawan"*) yang membimbing siswa memahami konsep komputasi tanpa memberikan contekan jawaban langsung secara instan.

#### Fitur & Komponen Utama:
1. **Persona Pedagogis**:
   * Berkomunikasi dalam Bahasa Indonesia santun, menyemangati, dan menggunakan metode Sokratik (mengajak siswa berpikir langkah-demi-langkah).
2. **Diagnosis Error Python Cerdas**:
   * Menganalisis penyebab umum error siswa seperti `IndexError: list index out of range`, `TypeError: 'tuple' object does not support item assignment`, `KeyError`, atau `IndentationError`, lalu memberikan solusi perbaikan yang mudah dipahami.
3. **Dual Engine (Online & Offline Robustness)**:
   * *Mode Online*: Memanfaatkan model Gemini API untuk penalaran kontekstual yang kaya dan luwes.
   * *Mode Offline*: Dilengkapi basis pengetahuan bawaan (*offline educational rule-base*) untuk menjawab pertanyaan fundamental seputar sintaks Python, percabangan, perulangan, operator, fungsi, dan tipe data tanpa memerlukan internet sama sekali.
4. **Quick Prompt Chips**:
   * Tombol pertanyaan cepat seperti: *"Apa beda List vs Tuple?"*, *"Kapan memakai Dictionary?"*, *"Bagaimana cara menghindari duplikasi data?"*, dan *"Mengapa terjadi IndentationError?"*.
5. **Contextual Origin Tracking**:
   * Menyimpan riwayat asal saat dibuka dari Code Playground dan menyediakan tombol kembali langsung ke sesi koding sebelumnya.

---

## 5. SKEMA DATA & ENTITAS ROOM DATABASE

Aplikasi mengimplementasikan penyimpanan lokal SQLite melalui Room Database untuk menjamin seluruh progres belajar tetap aman dan persisten:

### 5.1 Entitas `UserProgressEntity`
Tabel tunggal untuk merekam status gamifikasi dan perjalanan belajar siswa:
```kotlin
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val completedLessonIds: String = "",       // Daftar ID materi selesai (koma terpisah)
    val unlockedBadgeIds: String = "",         // Daftar ID lencana prestasi yang diraih
    val totalXp: Int = 0,                      // Akumulasi poin pengalaman
    val currentStreak: Int = 1,                // Jumlah hari konsistensi belajar aktif
    val lastStudyDateMillis: Long = 0L,        // Timestamp terakhir aktivitas belajar
    val quizHighScore: Int = 0,                // Skor kuis tertinggi yang dicapai
    val quizTotalAttempts: Int = 0             // Jumlah percobaan kuis
)
```

### 5.2 Entitas `SavedSnippetEntity`
Tabel untuk menyimpan portofolio kode hasil karya siswa:
```kotlin
@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,                         // Judul snippet kode
    val codeContent: String,                   // Isi script Python
    val category: String,                      // Kategori: LIST, TUPLE, SET, DICT, CUSTOM
    val createdAtMillis: Long = System.currentTimeMillis()
)
```

---

## 6. SISTEM DESAIN ANTARMUKA & AKSESIBILITAS (UI/UX)

1. **Palet Warna M3 Bertema Laboratorium Modern**:
   * *Background*: `SleekNavyBg` (`#070B14`) & `SleekNavySurface` (`#0F172A`).
   * *Primary Brand*: `LimeSoftDark` (`#65A30D`) & `LimeSoftContainer` (`#ECFCCB`).
   * *Syntax Editor*: Background gelap pekat (`#0D1527`) dengan teks sintaks pastel kontras tinggi sesuai standar WCAG AAA.
2. **Standar Aksesibilitas**:
   * Seluruh elemen interaktif (tombol, tab navigasi, item daftar) memiliki ukuran sentuh minimal **48dp × 48dp**.
   * Dukungan pembaca layar (*TalkBack*) melalui `contentDescription` yang jelas pada seluruh ikon grafis.
   * Tipografi responsif berbasis skala `sp` dan tata letak адапtif berbasis `dp` tanpa ketergantungan pada ukuran layar statis.
3. **Edge-to-Edge & Inset Handling**:
   * Menggunakan `enableEdgeToEdge()` dengan penanganan `statusBarsPadding()` dan `navigationBarsPadding()` yang presisi sehingga tampilan tidak terpotong oleh bilah status maupun navigasi gestur Android.

---

## 7. MATRIKS SPESIFIKASI PERSYARATAN NON-FUNGSIONAL

| Parameter | Kebutuhan & Standar | Implementasi Teknis |
| :--- | :--- | :--- |
| **Keandalan Offline** | 100% modul, lab interaktif, playground, kuis, dan basic tutor harus berfungsi tanpa internet. | Room Database + Embedded AST `LocalPythonInterpreter` + Local Curriculum Assets. |
| **Kecepatan Eksekusi** | Eksekusi kode Python di Lab dan Playground harus selesai dalam < 100 milidetik untuk script kurikulum SMA. | Evaluasi ekspresi langsung pada memory heap JVM Kotlin tanpa proses fork OS berat. |
| **Stabilitas & Crash-Free** | Bebas dari crash kompilasi dan runtime (misal: penanganan KSP2, NullPointer, dan Lifecycle State). | KSP2 enabled (`ksp.useKSP2=true`), Robolectric CUJ unit tests pass 100%. |
| **Ukuran APK** | Ringan dan ramah untuk smartphone siswa dengan kapasitas penyimpanan terbatas. | Proguard rules aktif, dependensi yang tidak terpakai dikomentari, optimasi aset vektor XML. |

---

## 8. ROADMAP PENGEMBANGAN LANJUTAN (FUTURE MILESTONES)

* **Fase 1 (Saat Ini - Selesai & Terverifikasi)**:
  * Rilis Core Engine: 14 Modul List, Materi Tuple/Set/Dict/Nested, Lab Interaktif dengan In-Lab Coding Console, Code Playground, Kuis 20 Soal, Room Database, dan Integrasi AI Tutor.
* **Fase 2 (Rencana Semester Berikutnya)**:
  * Fitur Export Portofolio Koding: Siswa dapat mengekspor seluruh script latihan dan kartu catatan rangkuman materi ke format PDF/HTML untuk penilaian tugas portofolio Informatika.
  * Fitur Peer Code Sharing via QR Code: Memungkinkan siswa saling bertukar snippet kode eksperimen lab secara luring di kelas tanpa koneksi internet.
* **Fase 3**:
  * Integrasi modul Algoritma Lanjutan: Visualizer Algoritma Pencarian (*Binary Search*) dan Pengurutan (*Bubble Sort, Merge Sort*) berbasis struktur data List yang sudah dipelajari.

---

**Dokumen ini disahkan sebagai panduan teknis dan fungsionalitas resmi aplikasi Python DataLab SMAN 1 Kepanjen.**  
*Versi: 2.0 | Status: Production Ready*
