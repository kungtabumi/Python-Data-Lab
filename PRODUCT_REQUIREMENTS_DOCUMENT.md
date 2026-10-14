# PRODUCT REQUIREMENTS DOCUMENT (PRD)
## Python DataLab: Media Pembelajaran Interaktif Struktur Data Python

| Informasi Dokumen | Keterangan |
| :--- | :--- |
| **Nama Produk** | Python DataLab (Android Application) |
| **Versi Produk** | 1.1.0 (Production / Release Ready) |
| **Status Dokumen** | Approved & Fully Verified |
| **Target Platform** | Android 8.0+ (API 26+) — Jetpack Compose & Kotlin |
| **Institusi & Kurikulum** | SMAN 1 Kepanjen — Kurikulum Merdeka Informatika Fase F (Kelas XI) |
| **Guru Pembimbing** | Bapak Hermawan Hidayat, S.Kom. |
| **Arsitektur Teknis** | MVVM + Jetpack Compose (M3) + Room Database + Local Python Engine + Gemini AI Tutor |

---

## 1. Executive Summary & Visi Produk

### 1.1 Latar Belakang Masalah
Struktur Data dalam pemrograman Python (*List, Tuple, Set, dan Dictionary*) adalah materi inti kurikulum Informatika SMA Fase F. Siswa sering mengalami kesulitan konseptual dalam memahami:
1. **Mutabilitas vs Immutabilitas:** Membedakan kapan data dapat diubah langsung di memori (*mutable* pada List/Set/Dict) dan kapan data konstan disegel (*immutable* pada Tuple).
2. **Model Pengindeksan & Memori:** Memahami indeks linier `[0]`, slicing `[start:stop:step]`, serta mengapa *Set* dan *Dictionary* mengandalkan algoritma Hash Table tanpa indeks urutan numerik.
3. **Aljabar Himpunan & Diagram Venn:** Menerapkan operasi Union `|`, Intersection `&`, Difference `-`, dan Symmetric Difference `^` secara komputasional.
4. **Struktur Data Bersarang (*Nested Structures*):** Mengakses data berdimensi banyak seperti *List of Dictionaries* dan matriks 2D tanpa salah indeks.
5. **Keterbatasan Media Konvensional:** Modul teks cetak atau PDF statis tidak menyediakan visualisasi memori langsung, minim wadah latihan *hands-on*, dan tidak dapat mendiagnosa pesan error seketika.

### 1.2 Visi Produk
Menjadi laboratorium digital interaktif dalam genggaman yang menggabungkan:
- **Kurikulum Komprehensif:** 4 Modul Struktur Data, Struktur Bersarang, dan Fondasi Pemrograman Python 3.11.
- **Visual State Engine:** Visualisasi diagram RAM, pointer array, diagram Venn, dan Compact Hash Table interaktif.
- **Local Python Engine:** Interpreter Python lokal pada perangkat Android tanpa memerlukan server eksternal.
- **Anti-Hallucination AI Tutor:** Asisten belajar pintar berbasis Gemini API dengan basis pengetahuan CPython 3.11 yang terverifikasi dan *instant offline fallback*.
- **Gamifikasi Edukatif:** Sistem XP, Leveling, Daily Streak, dan Lencana Prestasi (*Badges*).

---

## 2. Sasaran Pengguna (User Persona) & User Journey

### 2.1 Persona Pengguna
* **Siswa SMA Kelas XI (Primary Learner):**
  * Membutuhkan media belajar mandiri yang visual, interaktif, dan mudah diakses dari smartphone.
  * Membutuhkan feedback cepat saat kode mengalami error (*SyntaxError, IndexError, KeyError, TypeError*).
  * Termotivasi dengan pencapaian gamifikasi (XP, Badge, Kuis).
* **Guru Informatika SMAN 1 Kepanjen (Educator / Mentor):**
  * Membutuhkan instrumen diferensiasi pembelajaran digital yang selaras dengan Capaian Pembelajaran (CP) Kurikulum Merdeka.
  * Memastikan jawaban materi teknis 100% akurat sesuai spesifikasi resmi Python 3.11 tanpa halusinasi.

### 2.2 Alur Pengalaman Siswa (User Journey)
1. **Eksplorasi Modul:** Siswa memilih topik struktur data (misal: List) di Tab Materi, membaca konsep, dan melihat diagram visualisasi memori yang bereaksi terhadap perubahan data.
2. **Praktik Mandiri di Lab:** Siswa mencoba latihan snippet atau membuka Tab Lab Interaktif untuk menulis, memodifikasi, dan menjalankan kode Python secara langsung.
3. **Konsultasi AI Tutor:** Jika menemui kendala atau galat interpreter, siswa menekan tombol "Tanya AI Tutor" dengan kode aktif yang terlampir untuk mendapatkan bedah masalah kontekstual.
4. **Evaluasi & Reward:** Siswa mengerjakan kuis formatif berbobot XP dan membuka lencana prestasi (*badges*) saat menguasai setiap modul.

---

## 3. Spesifikasi Kebutuhan Fungsional (Functional Requirements)

### 3.1 Modul Kurikulum & Materi Terstruktur
* **FR-01 (Katalog Pembelajaran 4 Struktur Data):**
  * **List (14 Materi):** Pengertian, inisialisasi, indexing & negative indexing, slicing `[start:stop:step]`, panjang list (`len`), penambahan (`append`, `insert`, `extend`), penghapusan (`pop`, `remove`, `del`, `clear`), sorting & reverse (`sort`, `sorted`, `reverse`), pencarian elemen, list bersarang (2D matrix), dan *list comprehension*.
  * **Tuple (9 Materi):** Sifat immutability, keamanan data (*defensive programming*), tuple 1 elemen dengan trailing comma `(1,)`, tuple unpacking & variable swapping (`a, b = b, a`), hashability sebagai kunci dictionary, serta metode `count()` dan `index()`.
  * **Set (10 Materi):** Koleksi unik tanpa duplikat, jebakan `{}` (dict) vs `set()`, eliminasi duplikat cepat O(n), operasi aljabar Venn (`|`, `&`, `-`, `^`), metode aman `.discard()` vs `.remove()`, dan larangan indexing `set[0]`.
  * **Dictionary (12 Materi):** Pasangan Key-Value, aturan kunci *hashable/immutable*, akses aman `.get(k, default)` vs `dict[k]`, manipulasi penugasan & `.update()`, penghapusan (`.pop()`, `.popitem()`, `del`, `.clear()`), iterasi `.items()`/`.keys()`/`.values()`, penggabungan modern operator `|` (Python 3.9+), serta pengurutan dictionary.
  * **Struktur Bersarang & Perbandingan (5 Materi):** *List of Dictionaries*, *Dictionary of Lists*, Matriks 2D, Matriks Perbandingan 4 Struktur Data, dan Panduan Alur Pemilihan (*Decision Flowchart*).
* **FR-02 (Konten Modul):** Penjelasan materi komprehensif, analogi dunia nyata, contoh kode terverifikasi, tabel referensi, dan *Tips Pedagogis Guru Hermawan*.
* **FR-03 (Pencatatan Progres Pembelajaran):** Tombol "Tandai Selesai" yang mencatat status ke Room Database dan memberikan reward +30 XP.

### 3.2 Visual State Engine (Visualisasi Memori Interaktif)
* **FR-04 (List & Tuple Memory Visualizer):** Representasi blok memori linear dengan pointer indeks positif (0, 1, 2) dan negatif (-1, -2, -3), menampilkan animasi penambahan, penyisipan, dan penghapusan item.
* **FR-05 (Venn Diagram Set Visualizer):** Representasi grafis diagram Venn dua lingkaran (Himpunan A dan B) dengan penyorotan warna interaktif untuk Union, Irisan, dan Selisih.
* **FR-06 (Dictionary Hash Map Visualizer):** Visualisasi slot Compact Hash Table yang memetakan label Kunci (*Key*) ke Nilai (*Value*).
* **FR-07 (Nested Tree & Matrix Visualizer):** Visualisasi kartu bertingkat dan representasi tabel matriks berindeks baris-kolom `[i][j]`.

### 3.3 Engine Eksekusi Python & Code Playground
* **FR-08 (On-Device Python Interpreter):** Interpreter Python lokal berkecepatan tinggi yang mengeksekusi sintaks Python langsung di dalam perangkat Android tanpa latensi jaringan.
* **FR-09 (Tantangan Koding Terintegrasi):** 13 tantangan studi kasus interaktif per modul dilengkapi validasi output otomatis, tombol petunjuk (*hint*), dan kunci solusi berbobot +15 XP.
* **FR-10 (Playground Bebas):** Editor kode Python dengan syntax highlighting, tombol Jalankan (*Run*), Reset Kode, Simpan Snippet, dan Template Snippet bawaan.
* **FR-11 (Output Console & Error Interceptor):** Menampilkan output standar (*stdout*) dan menangkap pesan error (*stderr*) dengan diagnosis tipe Exception persis CPython 3.11.

### 3.4 Asisten Belajar Cerdas (AI Python Tutor)
* **FR-12 (Context-Aware Multi-Turn Consultation):** Layanan tanya jawab kontekstual yang mengenali modul materi aktif (`topicTitle`), riwayat percakapan sebelumnya, serta kode playground aktif siswa.
* **FR-13 (Bedah Error Otomatis):** Tombol pintas "Bedah Error Kode Saya" yang menganalisis pesan error interpreter dan memberikan solusi kode langkah demi langkah.
* **FR-14 (Anti-Hallucination Ground-Truth Engine):** Penjelasan teknis selalu berpijak pada dokumentasi CPython 3.11 dan aturan memori valid.
* **FR-15 (Streaming & Multi-Model Resilience):** Dukungan streaming teks seketika dengan fallback otomatis antara `gemini-3.5-flash`, `gemini-3.1-pro-preview`, `gemini-3.1-flash-lite-preview`, dan *offline knowledge engine*.
* **FR-16 (Quick Suggested Chips):** Pilihan cepat pertanyaan populer seputar konsep List, Tuple, Set, Dict, dan perbandingan.

### 3.5 Gamifikasi, Kuis & Evaluasi
* **FR-17 (Sistem XP & Leveling):** Akumulasi poin pengalaman (XP) dengan tingkatan level: *Novice (0 XP)* $\to$ *Apprentice (100 XP)* $\to$ *Pythonista (300 XP)* $\to$ *Code Wizard (600+ XP)*.
* **FR-18 (Kuis Evaluasi Terstandar):** Kuis pilihan ganda interaktif per topik dengan evaluasi skor instan dan pembahasan lengkap.
* **FR-19 (Lencana Prestasi / Achievement Badges):** 9 lencana pencapaian (*First Step, List Master, Tuple Guardian, Set Maestro, Dict Wizard, Quiz Champion, Python Expert, Code Innovator, Lab Legend*).
* **FR-20 (Celebration Dialog & Streak Tracker):** Dialog perayaan animasi saat membuka lencana baru dan pelacak konsistensi belajar harian.

---

## 4. Spesifikasi Kebutuhan Non-Fungsional (Non-Functional Requirements)

* **NFR-01 (Desain UI/UX & Aksesibilitas):**
  * Mengikuti pedoman Material Design 3 (M3) dengan skema warna modern (*Python Blue, Emerald Green, Amber Gold, Sleek Navy*).
  * Minimum touch target 48dp $\times$ 48dp untuk seluruh tombol dan elemen interaktif.
  * Penanganan *WindowInsets* dan *imePadding* agar antarmuka tidak tertutup keyboard virtual.
* **NFR-02 (Performa & Kecepatan):**
  * Waktu peluncuran awal (*cold start*) $< 1.2$ detik.
  * Eksekusi kode lokal dan visualisasi memori berlangsung instan ($< 100$ ms).
  * Streaming AI Tutor memiliki *Time-to-First-Token* yang sangat cepat.
* **NFR-03 (Offline-First Data Persistence):**
  * Seluruh progres materi, riwayat kuis, XP, lencana, dan snippet koding tersimpan aman di Android Room Database lokal.
  * Aplikasi tetap berfungsi 100% untuk belajar, mencoba visualizer, dan menjalankan kode meskipun tanpa koneksi internet.
* **NFR-04 (Keamanan Kredensial & Zero Leaks):**
  * Kunci API Gemini dikonfigurasi melalui *Secrets Panel / BuildConfig* tanpa pernah di-*hardcode* di berkas sumber kode.

---

## 5. Arsitektur Teknis & Struktur Modul

```
app/src/main/java/com/example/
├── ai/
│   ├── GeminiTutorService.kt          # Engine AI Tutor & Contextual Matcher
│   └── PythonDataLabKnowledgeBase.kt   # Basis Pengetahuan Terverifikasi CPython 3.11
├── data/
│   ├── CurriculumData.kt              # Modul Kurikulum & Kuis Terstruktur
│   └── local/
│       ├── AppDatabase.kt             # Room Database Definition
│       ├── ProgressDao.kt             # Data Access Object
│       ├── UserProgressEntity.kt      # Entitas Progres & XP
│       ├── SavedSnippetEntity.kt      # Entitas Snippet Tersimpan
│       └── PythonLabRepository.kt     # Repository Pola Akses Data
├── engine/
│   ├── LocalPythonInterpreter.kt      # Interpreter Python Lokal & Simulator Memori
│   └── ExecutionResult.kt             # Model Data Hasil Eksekusi (stdout, stderr)
├── model/
│   ├── DataStructureType.kt           # Enum: LIST, TUPLE, SET, DICTIONARY, NESTED, COMPARISON
│   ├── TopicLesson.kt                 # Model Materi Pembelajaran
│   ├── QuizQuestion.kt                # Model Kuis Evaluasi
│   ├── ChatMessage.kt                 # Model Percakapan AI Tutor
│   └── AchievementBadge.kt            # Model Lencana Prestasi
├── navigation/
│   └── NavRoutes.kt                   # Rute Navigasi Tipe-Aman Jetpack Compose
└── ui/
    ├── components/                    # Visualizer (List, Tuple, Set, Dict, Tree) & Runner
    ├── screens/                       # Layanan Antarmuka (Materi, Lab, AI Tutor, Kuis, Profil)
    ├── theme/                         # Skema Warna M3, Tipografi, dan Bentuk
    └── viewmodel/
        └── PythonLabViewModel.kt      # State Management & Logika Bisnis Terpusat
```

---

## 6. Kriteria Penerimaan & Verifikasi (Acceptance Criteria)

| Fitur | Kriteria Keberhasilan | Status |
| :--- | :--- | :---: |
| **Kurikulum 4 Struktur Data** | Seluruh 50+ topik materi dapat diakses, dibaca, dan ditandai selesai dengan reward XP. | **PASSED** |
| **Visual State Engine** | Diagram memori List, Tuple, Set, Dict, dan Nested merespons interaksi perubahan data secara real-time. | **PASSED** |
| **Local Python Engine** | Kode Python (List, Tuple, Set, Dict, loop, condition, def, print) dieksekusi tepat di perangkat Android. | **PASSED** |
| **AI Tutor Contextuality** | AI Tutor memahami pertanyaan seputar List, Tuple, Set, Dict, pesan error, dan sapaan secara kontekstual. | **PASSED** |
| **Multi-turn & Streaming** | AI streaming berfungsi dengan baik, tidak terpotong di tengah jalan, dan blok kode selalu tertutup rapi. | **PASSED** |
| **Gamifikasi & Lencana** | XP bertambah otomatis, level naik sesuai target, dan lencana baru memicu dialog selebrasi. | **PASSED** |
| **Unit Testing & Build** | Seluruh test suite (27+ unit tests) lulus 100% dan `compile_applet` sukses tanpa galat. | **PASSED** |

---

*Dokumen ini merupakan spesifikasi resmi pengembangan aplikasi **Python DataLab** SMAN 1 Kepanjen (Dibimbing oleh: Bapak Hermawan Hidayat, S.Kom.).*
