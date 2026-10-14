package com.example.ai

/**
 * Validated Ground Truth Knowledge Base for Python 3.11 Data Structures.
 * Specially curated for Kurikulum Merdeka Informatika SMA Kelas XI - SMAN 1 Kepanjen
 * Pembimbing: Hermawan Hidayat, S.Kom.
 *
 * This knowledge base provides verifiable, official Python specifications
 * to eliminate hallucination, bias, and speculative answers in the AI Tutor.
 */
object PythonDataLabKnowledgeBase {

    // =========================================================================
    // 1. OFFICIAL SPECIFICATIONS: 4 CORE PYTHON DATA STRUCTURES
    // =========================================================================

    data class StructureSpec(
        val name: String,
        val syntax: String,
        val isMutable: Boolean,
        val isOrdered: Boolean,
        val allowsDuplicates: Boolean,
        val isHashable: Boolean,
        val indexingSupport: String,
        val underlyingImplementation: String,
        val timeComplexitySummary: String,
        val validMethods: List<String>,
        val invalidMethodsCommonMistakes: List<String>,
        val primaryUseCases: List<String>
    )

    val LIST_SPEC = StructureSpec(
        name = "List",
        syntax = "[item1, item2, ...]",
        isMutable = true,
        isOrdered = true, // Python maintains insertion sequence
        allowsDuplicates = true,
        isHashable = false, // Cannot be used as dict key or set element
        indexingSupport = "Zero-indexed integer access list[i], negative indexing list[-1], slicing list[start:stop:step]",
        underlyingImplementation = "CPython dynamic array of object references (pointer array). Over-allocates capacity proportionally for amortized O(1) appending.",
        timeComplexitySummary = "Access O(1), Append O(1) amortized, Pop from end O(1), Insert/Delete middle O(n), Search O(n), Reverse O(n), Sort O(n log n) [Timsort]",
        validMethods = listOf("append(x)", "extend(iterable)", "insert(i, x)", "remove(x)", "pop([i])", "clear()", "index(x)", "count(x)", "sort(key=None, reverse=False)", "reverse()", "copy()"),
        invalidMethodsCommonMistakes = listOf(
            "list.add() TIDAK ADA -> Gunakan list.append(x) atau list.extend()",
            "list.push() TIDAK ADA -> Gunakan list.append(x)",
            "list.delete() TIDAK ADA -> Gunakan list.remove(x) atau del list[i] atau list.pop(i)"
        ),
        primaryUseCases = listOf(
            "Menyimpan kumpulan item berurutan yang sering ditambah, diubah, atau diurutkan",
            "Daftar nilai siswa, antrian tugas, riwayat aktivitas berurutan"
        )
    )

    val TUPLE_SPEC = StructureSpec(
        name = "Tuple",
        syntax = "(item1, item2, ...) atau item1, item2 (trailing comma untuk 1 elemen: (x,))",
        isMutable = false, // IMMUTABLE: cannot be changed in place
        isOrdered = true,
        allowsDuplicates = true,
        isHashable = true, // Hashable IF AND ONLY IF all items inside are hashable
        indexingSupport = "Zero-indexed integer access tuple[i], negative indexing tuple[-1], slicing tuple[start:stop:step]",
        underlyingImplementation = "CPython fixed-size array of object references. Tanpa over-allocation buffer, lebih hemat memori (smaller memory footprint) dan alokasi lebih cepat dari list.",
        timeComplexitySummary = "Access O(1), Search O(n), Slicing O(k), Immutability enforces zero in-place mutations",
        validMethods = listOf("count(x)", "index(x)"),
        invalidMethodsCommonMistakes = listOf(
            "tuple.append() TIDAK ADA -> Tuple bersifat Immutable!",
            "tuple.insert() TIDAK ADA -> Tuple tidak dapat diubah!",
            "tuple.remove() / tuple.pop() TIDAK ADA -> Elemen tuple konstan!",
            "tuple[0] = x TIDAK BISA -> Menghasilkan TypeError: 'tuple' object does not support item assignment",
            "(1) BUKAN TUPLE melainkan integer -> Harus ada koma: (1,)"
        ),
        primaryUseCases = listOf(
            "Data konstan yang tidak boleh dimodifikasi (koordinat GPS, konstanta warna RGB, konfigurasi)",
            "Kunci Dictionary (sebagai compound key seperti (x, y)) karena bersifat hashable",
            "Data unpacking (a, b = get_coordinates())"
        )
    )

    val SET_SPEC = StructureSpec(
        name = "Set",
        syntax = "{item1, item2, ...} atau set(iterable). CATATAN: {} kosong adalah dict, bukan set! Set kosong: set()",
        isMutable = true, // frozenset adalah versi immutable
        isOrdered = false, // Python sets are unordered collections of unique elements
        allowsDuplicates = false, // Duplicates are automatically eliminated
        isHashable = false, // Set mutable tidak hashable (frozenset hashable)
        indexingSupport = "TIDAK MENDUKUNG INDEXING! set[0] menghasilkan TypeError: 'set' object is not subscriptable",
        underlyingImplementation = "CPython Hash Table dengan teknik open addressing dan dummy deletion markers. Membutuhkan setiap elemen mengimplementasikan __hash__() dan __eq__().",
        timeComplexitySummary = "Membership testing ('x in set') O(1) rata-rata (O(n) worst case jika hash collision ekstrem), Add O(1), Remove O(1), Union/Intersection O(len(s) + len(t))",
        validMethods = listOf("add(elem)", "remove(elem)", "discard(elem)", "pop()", "clear()", "union(*others) atau |", "intersection(*others) atau &", "difference(*others) atau -", "symmetric_difference(other) atau ^", "issubset(other) atau <=", "issuperset(other) atau >=", "isdisjoint(other)"),
        invalidMethodsCommonMistakes = listOf(
            "set[0] TIDAK BISA -> TypeError: 'set' object is not subscriptable",
            "set.append() TIDAK ADA -> Gunakan set.add(elem)",
            "{} membuat Dictionary kosong, BUKAN Set kosong! Gunakan set()",
            "Memasukkan List ke dalam Set TIDAK BISA -> TypeError: unhashable type: 'list'"
        ),
        primaryUseCases = listOf(
            "Menghapus duplikat dari koleksi data (list(set(data)))",
            "Pengecekan keanggotaan cepat (membership test 'in') skala besar",
            "Operasi relasi matematika (Venn: irisan siswa ekskul, gabungan anggota, selisih)"
        )
    )

    val DICTIONARY_SPEC = StructureSpec(
        name = "Dictionary",
        syntax = "{key1: value1, key2: value2, ...} atau dict(key1=value1)",
        isMutable = true,
        isOrdered = true, // Dijamin mempertahankan insertion-order sejak Python 3.7+
        allowsDuplicates = false, // Kunci (Keys) WAJIB UNIK. Nilai (Values) boleh duplikat.
        isHashable = false, // Dict sendiri tidak hashable. Kuncinya WAJIB hashable (string, int, float, tuple).
        indexingSupport = "Key-based lookup dict[key] atau dict.get(key, default). TIDAK menggunakan nomor index urutan!",
        underlyingImplementation = "CPython Compact Hash Table (kombinasi sparse array index dan dense entries array). Sangat efisien memori dan menjaga urutan penambahan.",
        timeComplexitySummary = "Key lookup O(1) average, Insertion O(1) average, Deletion O(1) average, Iteration O(n)",
        validMethods = listOf("get(key[, default])", "keys()", "values()", "items()", "pop(key[, default])", "popitem()", "update([other])", "clear()", "setdefault(key[, default])", "copy()"),
        invalidMethodsCommonMistakes = listOf(
            "dict.add() TIDAK ADA -> Gunakan dict[key] = value atau dict.update()",
            "dict.append() TIDAK ADA -> Dictionary berpasangan Key-Value",
            "Menggunakan List sebagai Key TIDAK BISA -> TypeError: unhashable type: 'list'",
            "Mengakses key yang tidak ada dict['salah'] menghasilkan KeyError -> Gunakan dict.get('salah', default)"
        ),
        primaryUseCases = listOf(
            "Representasi entitas/objek dunia nyata (Database record: siswa = {'nis': 101, 'nama': 'Budi', 'kelas': 'XI'})",
            "Tabel pencarian cepat (Lookup table / Cache)",
            "Frekuensi kemunculan kata / data agregasi"
        )
    )

    // =========================================================================
    // 2. ERROR DIAGNOSER & ANTI-HALLUCINATION TRUTH TABLE
    // =========================================================================

    data class PythonErrorExplanation(
        val errorType: String,
        val triggeringPattern: String,
        val rootCause: String,
        val verifiableExplanation: String,
        val solution: String
    )

    val ERROR_KNOWLEDGE = listOf(
        PythonErrorExplanation(
            errorType = "TypeError: 'tuple' object does not support item assignment",
            triggeringPattern = "tuple[i] = val",
            rootCause = "Tuple adalah tipe data immutable (konstan) yang dirancang untuk integritas data read-only.",
            verifiableExplanation = "Di level CPython, slot memori Tuple bersifat statis setelah instansiasi. Menulis ulang indeks langsung melanggar protokol memori objek tuple.",
            solution = "Ubah menjadi List dengan list(t) jika butuh dimutasi, atau buat tuple baru: t = t[:i] + (val,) + t[i+1:]"
        ),
        PythonErrorExplanation(
            errorType = "TypeError: unhashable type: 'list'",
            triggeringPattern = "set([ [1, 2] ]) atau dict_key = [1, 2]",
            rootCause = "List bersifat mutable, sehingga representasi hash-nya dapat berubah sewaktu-waktu. Hash Table Python memerlukan hash konstan.",
            verifiableExplanation = "Set dan Kunci Dictionary menghitung hash(key) untuk menentukan bucket memori. Karena List mutable, __hash__ diset ke None oleh Python.",
            solution = "Gunakan Tuple (1, 2) sebagai ganti List jika ingin menjadikannya elemen Set atau Kunci Dictionary."
        ),
        PythonErrorExplanation(
            errorType = "TypeError: 'set' object is not subscriptable",
            triggeringPattern = "set_data[0] atau set_data[i]",
            rootCause = "Set adalah himpunan tanpa urutan indeks integer (unordered hash table).",
            verifiableExplanation = "Python Set tidak mengalokasikan array linier berindeks 0, 1, 2. Posisinya bergantung pada algoritma hash bucket.",
            solution = "Iterasi dengan 'for item in set_data:', atau gunakan 'in' untuk mencari, atau konversi sementara ke list: list(set_data)[0]"
        ),
        PythonErrorExplanation(
            errorType = "IndexError: list index out of range",
            triggeringPattern = "list_data[i] di mana i >= len(list_data) atau i < -len(list_data)",
            rootCause = "Mengakses alamat slot memori di luar batas panjang elemen list.",
            verifiableExplanation = "Indeks valid berkisar dari 0 sampai len(data)-1, atau dari -len(data) sampai -1. Di luar itu Python melempar IndexError.",
            solution = "Gunakan len(data) untuk memastikan batas, atau gunakan slicing data[i:i+1] yang tidak melempar error jika kosong."
        ),
        PythonErrorExplanation(
            errorType = "KeyError: 'key_name'",
            triggeringPattern = "dict[key] ketika key tidak ada di dalam dict",
            rootCause = "Key tidak ditemukan dalam bucket Hash Table dictionary.",
            verifiableExplanation = "Akses kurung siku dict['key'] mengasumsikan kunci pasti ada. Jika tidak ada, eksekusi langsung terhenti dengan KeyError.",
            solution = "Gunakan dict.get('key_name', default_value) atau cek keberadaan dengan 'if key_name in dict:' terlebih dahulu."
        ),
        PythonErrorExplanation(
            errorType = "ValueError: list.remove(x): x not in list",
            triggeringPattern = "list.remove(item) ketika item tidak ada di dalam list",
            rootCause = "Metode remove() mencari nilai dari kiri ke kanan. Jika sampai ujung list tidak ditemukan, Python melempar ValueError.",
            verifiableExplanation = "remove() hanya menghapus kemunculan pertama. Jika item tidak ada, proses gagal.",
            solution = "Gunakan 'if item in my_list: my_list.remove(item)' untuk pengecekan aman."
        ),
        PythonErrorExplanation(
            errorType = "AttributeError: 'tuple' object has no attribute 'append'",
            triggeringPattern = "tuple.append(x)",
            rootCause = "Mencoba memanggil mutator method pada objek immutable.",
            verifiableExplanation = "Tuple hanya memiliki 2 metode bawaan: count() dan index(). Tidak ada append, insert, remove, atau pop.",
            solution = "Gunakan List jika butuh menambah item, atau buat tuple baru: my_tuple = my_tuple + (x,)"
        ),
        PythonErrorExplanation(
            errorType = "AttributeError: 'dict' object has no attribute 'add'",
            triggeringPattern = "dict.add(key, value) atau dict.add(val)",
            rootCause = "Metode add() hanya milik objek Set.",
            verifiableExplanation = "Dictionary di Python menambahkan data dengan sintaks assignment: dict[key] = value, atau dict.update({key: value}).",
            solution = "Ganti dengan 'my_dict[key] = value'."
        ),
        PythonErrorExplanation(
            errorType = "ZeroDivisionError: division by zero",
            triggeringPattern = "/ 0 atau // 0 atau % 0",
            rootCause = "Membagi bilangan dengan angka nol tidak terdefinisi secara matematis.",
            verifiableExplanation = "Semua operator pembagian (/, //, %) melempar ZeroDivisionError jika pembagi (operan kanan) bernilai nol.",
            solution = "Lakukan validasi sebelum operasi: 'if pembagi != 0: hasil = a / pembagi'."
        ),
        PythonErrorExplanation(
            errorType = "NameError: name 'variable' is not defined",
            triggeringPattern = "menggunakan variabel sebelum deklarasi atau salah ejaan (typo)",
            rootCause = "Identifier belum terdaftar pada namespace lokal maupun global Python.",
            verifiableExplanation = "Python mengeksekusi secara berurutan. Jika variabel dipanggil sebelum baris deklarasinya, atau terdapat perbedaan huruf besar/kecil (case sensitivity), Python melempar NameError.",
            solution = "Pastikan variabel sudah diinisialisasi sebelum digunakan dan periksa ketepatan ejaan namanya."
        ),
        PythonErrorExplanation(
            errorType = "IndentationError: expected an indented block",
            triggeringPattern = "setelah tanda titik dua (:) pada if, elif, else, for, while, atau def tidak ada spasi indentasi",
            rootCause = "Python mengandalkan indentasi whitespace untuk menentukan struktur blok kode.",
            verifiableExplanation = "Standar resmi PEP 8 Python mewajibkan 4 spasi untuk setiap level blok setelah klausa berkepala titik dua (:).",
            solution = "Tambahkan indentasi konsisten (4 spasi) pada baris di dalam blok if, for, while, atau def."
        ),
        PythonErrorExplanation(
            errorType = "SyntaxError: invalid syntax",
            triggeringPattern = "tanda titik dua (:) tertinggal, kurung belum tertutup, atau tanda petik string tidak klop",
            rootCause = "Interpreter parser CPython gagal mengurai token sintaks program.",
            verifiableExplanation = "Klausa kondisi dan perulangan selalu membutuhkan titik dua (:). Pasangan tanda kurung (), [], {} dan tanda petik string harus tertutup rapi.",
            solution = "Periksa tanda titik dua (:) di akhir baris if/elif/else/for/while/def dan pastikan tanda kurung serta tanda petik tertutup."
        )
    )

    // =========================================================================
    // 2.5 FUNDAMENTAL PYTHON TOPICS (Variable, Data Types, Operators, I/O, Branching, Loops, Functions)
    // =========================================================================

    data class FundamentalTopic(
        val id: String,
        val title: String,
        val keywords: List<String>,
        val summary: String,
        val officialRules: List<String>,
        val verifiedCodeSnippet: String,
        val commonMistakes: List<String>,
        val teacherHermawanTip: String
    )

    val FUNDAMENTAL_TOPICS = listOf(
        FundamentalTopic(
            id = "variable_naming",
            title = "Variabel & Aturan Penamaan (Variable & Identifiers)",
            keywords = listOf("variabel", "variable", "identifier", "penamaan", "dynamic typing", "reassign", "snake_case", "nama variabel"),
            summary = "Variabel adalah pointer penyimpanan data berlabel di RAM komputer. Python menggunakan Dynamic Typing di mana tipe data terdeteksi otomatis.",
            officialRules = listOf(
                "Dynamic Typing: Tidak perlu deklarasi tipe data eksplisit (seperti int atau String di bahasa C/Java).",
                "Aturan Identifier: Wajib diawali huruf (a-z, A-Z) atau garis bawah (_). Dilarang diawali angka.",
                "Karakter: Hanya boleh huruf, angka, dan underscore. Dilarang menggunakan spasi atau simbol (!, @, #, %, ^, &, *).",
                "Case-Sensitive: 'nilai', 'Nilai', dan 'NILAI' adalah 3 variabel yang berbeda di memori.",
                "Reserved Keywords: Dilarang menggunakan kata kunci bawaan Python (if, else, while, for, def, class, import, in, is, None, True, False).",
                "Standar PEP 8: Gunakan gaya penulisan snake_case (huruf kecil dipisah garis bawah, misal: total_nilai_siswa)."
            ),
            verifiedCodeSnippet = """
                # 1. Deklarasi Variabel
                nama_siswa = "Rian Pratama"   # str
                skor_informatika = 95         # int
                rata_rata = 87.5              # float
                status_lulus = True           # bool

                # 2. Dynamic Re-assignment
                data = 100
                data = "Seratus"  # Sah di Python (objek baru dibuat di memori)
            """.trimIndent(),
            commonMistakes = listOf(
                "Menamai variabel dengan angka di awal: 1juara = 'Andi' -> SyntaxError!",
                "Menggunakan spasi dalam nama variabel: nama siswa = 'Andi' -> SyntaxError!",
                "Menggunakan keyword Python: for = 10 -> SyntaxError: invalid syntax"
            ),
            teacherHermawanTip = "Gunakan nama variabel yang deskriptif dan mencerminkan tujuannya, seperti 'daftar_siswa' alih-alih hanya huruf tunggal 'x'."
        ),
        FundamentalTopic(
            id = "data_types_casting",
            title = "Tipe Data Primitif & Konversi Tipe (Type Casting)",
            keywords = listOf("tipe data", "data type", "int", "float", "str", "bool", "casting", "konversi", "type()", "primitif"),
            summary = "Python memiliki 4 tipe data primitif utama (int, float, str, bool) dan fungsi konversi tipe eksplisit seperti int(), float(), str(), bool().",
            officialRules = listOf(
                "int: Bilangan bulat berpresisi tak terbatas (tidak ada overflow seperti di C/Java).",
                "float: Bilangan desimal pecahan berbasis IEEE 754 floating point.",
                "str: Sekumpulan karakter berurutan (sequence) yang bersifat immutable.",
                "bool: Subtipe dari int yang hanya memiliki dua kemungkinan nilai: True (1) atau False (0).",
                "Aturan Truthy & Falsy: Nilai 0, 0.0, string kosong '', list kosong [], tuple kosong (), set kosong set(), dict kosong {}, dan None selalu dievaluasi sebagai False. Nilai selain itu adalah True.",
                "Type Casting: int('10') -> 10, float('3.5') -> 3.5, str(100) -> '100', bool(1) -> True."
            ),
            verifiedCodeSnippet = """
                # Pengecekan tipe data
                x = 42
                print(type(x))  # <class 'int'>

                # Type Casting / Konversi Tipe
                teks_angka = "85"
                angka = int(teks_angka)       # Konversi ke int
                desimal = float(angka)        # 85.0

                # Truthy & Falsy
                print(bool(0))           # False
                print(bool(""))          # False
                print(bool("Kepanjen"))  # True
            """.trimIndent(),
            commonMistakes = listOf(
                "Melakukan operasi matematika langsung dari hasil input(): teks = '10'; teks + 5 -> TypeError: can only concatenate str to str",
                "Mengonversi string huruf ke angka: int('abc') -> ValueError: invalid literal for int()",
                "Mengira set kosong ditulis '{}' -> '{}' adalah dictionary kosong! Set kosong wajib ditulis 'set()'."
            ),
            teacherHermawanTip = "Selalu periksa tipe data menggunakan fungsi bawaan type(data) jika kamu ragu apakah suatu variabel bernilai string atau angka."
        ),
        FundamentalTopic(
            id = "operators_operands",
            title = "Operator & Operan (Operators & Operands)",
            keywords = listOf("operator", "operan", "operand", "aritmatika", "modulo", "pangkat", "floor division", "perbandingan", "logika", "penugasan", "presedensi", "is vs ==", "urutan operator"),
            summary = "Operan adalah data/nilai yang diproses, sedangkan Operator adalah simbol operasi komputasi. Mencakup Aritmatika, Perbandingan, Logika, Penugasan, Keanggotaan, dan Identitas.",
            officialRules = listOf(
                "Definisi: Pada ekspresi 'x + y', 'x' dan 'y' adalah OPERAN, sedangkan '+' adalah OPERATOR.",
                "Aritmatika: + (tambah), - (kurang), * (kali), / (pembagian float selalu hasil desimal), // (floor division/pembagian bulat), % (modulo/sisa hasil bagi), ** (pangkat).",
                "Perbandingan (Relasional): == (sama dengan), != (tidak sama dengan), >, <, >=, <=. Menghasilkan tipe bool.",
                "Logika: and (True jika keduanya True), or (True jika salah satu True), not (membalikkan logika). Mendukung Short-circuit evaluation.",
                "Penugasan (Assignment): =, +=, -=, *=, /=, //=, %=, **=.",
                "Keanggotaan: 'in' dan 'not in' (mengecek elemen di dalam koleksi). O(1) pada Set & Dict, O(n) pada List & Tuple.",
                "Identitas: 'is' mengecek apakah dua variabel menunjuk ke objek memori yang SAMA (id(a) == id(b)). Beda dengan '==' yang hanya mengecek kesamaan nilai!",
                "Presedensi Operator: Kurung () -> Pangkat ** -> Perkalian/Pembagian/Modulo/Floor *, /, //, % -> Penjumlahan/Pengurangan +, - -> Perbandingan -> Logika not, and, or."
            ),
            verifiedCodeSnippet = """
                a = 17
                b = 5

                # 1. Perbedaan Pembagian, Floor Division, dan Modulo
                print(a / b)    # 3.4 (Float)
                print(a // b)   # 3   (Floor Division: bulat ke bawah)
                print(a % b)    # 2   (Modulo: sisa 17 dibagi 5)
                print(b ** 2)   # 25  (Pangkat: 5 pangkat 2)

                # 2. Operator Logika
                nilai = 85
                hadir = 90
                lulus = (nilai >= 75) and (hadir >= 80)
                print("Lulus:", lulus)  # True

                # 3. Perbedaan '==' vs 'is'
                list_a = [1, 2, 3]
                list_b = [1, 2, 3]
                print(list_a == list_b)  # True (Nilainya sama)
                print(list_a is list_b)  # False (Dua objek berbeda di memori RAM!)
            """.trimIndent(),
            commonMistakes = listOf(
                "Mengira pembagian tunggal '/' menghasilkan integer: 10 / 2 menghasilkan 5.0 (float), gunakan '//' untuk hasil int 5.",
                "Tertukar antara '=' (penugasan nilai) dan '==' (perbandingan kesamaan): if a = 5: -> SyntaxError!",
                "Mengabaikan presedensi: 2 + 3 * 4 menghasilkan 14, bukan 20. Gunakan kurung (2 + 3) * 4 jika ingin mendahulukan penjumlahan."
            ),
            teacherHermawanTip = "Ingat baik-baik: operator '%' (modulo) sangat berguna untuk menentukan bilangan ganjil/genap (n % 2 == 0) dan membuat pola kelipatan!"
        ),
        FundamentalTopic(
            id = "input_output",
            title = "Input & Output (print, f-string, sep, end & input)",
            keywords = listOf("input", "output", "print", "f-string", "sep", "end", "baca input", "input()", "mencetak"),
            summary = "Interaksi data pada terminal menggunakan print() untuk keluaran dan input() untuk menerima masukan pengguna.",
            officialRules = listOf(
                "print(*objects, sep=' ', end='\\n'):",
                "- Parameter sep: Mengatur pemisah antar argumen (default adalah spasi tunggal ' ').",
                "- Parameter end: Mengatur karakter akhir cetakan (default adalah newline '\\n').",
                "Format String (f-string) sejak Python 3.6+: Sintaks modern diawali huruf 'f', ekspresi di dalam kurung kurawal {nama_variabel} atau {nilai:.2f}.",
                "input(prompt): Menerima input teks dari pengguna. ATURAN WAJIB: input() SELALU menghasilkan data bertipe str (string)!",
                "Type Casting Input: Jika membutuhkan input numerik, bungkus dengan int(input()) atau float(input())."
            ),
            verifiedCodeSnippet = """
                # 1. Parameter sep dan end
                print("SMAN", "1", "Kepanjen", sep="-")  # SMAN-1-Kepanjen
                print("Menghitung", end="...")
                print(" Berhasil!")  # Menghitung... Berhasil!

                # 2. Modern f-string dengan formatting angka desimal
                nama = "Dewi Anjani"
                nilai = 89.654
                print(f"Siswa: {nama} | Rata-rata: {nilai:.2f}")

                # 3. Input dengan konversi tipe data
                # umur = int(input("Masukkan umurmu: "))
                # tahun_lahir = 2026 - umur
            """.trimIndent(),
            commonMistakes = listOf(
                "Lupa casting input angka: umur = input('Umur: '); print(umur + 5) -> TypeError: can only concatenate str to str",
                "Lupa huruf 'f' di depan petik string: '{nama}' akan tercetak literal sebagai teks '{nama}', bukan nilai variabelnya.",
                "Mengira print() mengembalikan nilai: x = print('Halo') -> x akan bernilai None!"
            ),
            teacherHermawanTip = "Gunakan selalu f-string karena jauh lebih bersih, mudah dibaca, dan berkinerja lebih cepat dibanding operator format lama '%' atau '.format()'."
        ),
        FundamentalTopic(
            id = "percabangan_branching",
            title = "Percabangan / Seleksi Kondisi (if, elif, else & Ternary)",
            keywords = listOf("percabangan", "kondisi", "if", "elif", "else", "nested if", "ternary", "seleksi kondisi", "indentasi", "percabangan bersarang"),
            summary = "Struktur kendali alur eksekusi berdasarkan evaluasi ekspresi Boolean (True/False) dengan sintaks if, elif, dan else.",
            officialRules = listOf(
                "Sintaks Baku: Setiap baris if, elif, dan else WAJIB diakhiri tanda titik dua (:).",
                "Indentasi Wajib: Kode di dalam blok kondisi harus menjorok ke dalam (4 spasi PEP 8).",
                "Evaluasi Sekuensial: Kondisi dievaluasi dari atas ke bawah. Saat kondisi pertama bernilai True, bloknya dieksekusi dan seluruh elif/else berikutnya dilewati.",
                "Nested if: Percabangan di dalam percabangan lain untuk memeriksa kondisi bertingkat.",
                "Ternary Operator (Conditional Expression): nilai_a if kondisi else nilai_b (bentuk 1 baris)."
            ),
            verifiedCodeSnippet = """
                nilai = 82

                # 1. Struktur if - elif - else
                if nilai >= 90:
                    predikat = "A (Sangat Baik)"
                elif nilai >= 80:
                    predikat = "B (Baik)"
                elif nilai >= 70:
                    predikat = "C (Cukup)"
                else:
                    predikat = "D (Perlu Bimbingan)"

                print(f"Predikat: {predikat}")

                # 2. Percabangan Bersarang (Nested if)
                status_aktif = True
                if status_aktif:
                    if nilai >= 75:
                        print("Selamat, Anda tuntas KKM!")

                # 3. Ternary Operator (One-line)
                keterangan = "Lulus" if nilai >= 75 else "Remidi"
                print("Keterangan:", keterangan)
            """.trimIndent(),
            commonMistakes = listOf(
                "Lupa titik dua (:) di akhir if atau elif -> SyntaxError: expected ':'",
                "Salah penulisan 'else if': Python menggunakan kata kunci 'elif', bukan 'else if'!",
                "Indentasi tidak sejajar -> IndentationError: unindent does not match any outer indentation level"
            ),
            teacherHermawanTip = "Susun kondisi dari yang paling spesifik ke yang paling umum (misal dari nilai tertinggi 90, 80, 70) agar seleksi tidak salah sasaran."
        ),
        FundamentalTopic(
            id = "perulangan_looping",
            title = "Perulangan / Iterasi (for loop, while loop, range & loop control)",
            keywords = listOf("perulangan", "loop", "looping", "for loop", "while loop", "range", "break", "continue", "pass", "infinite loop", "iterasi"),
            summary = "Mengeksekusi blok kode berulang kali menggunakan for loop (koleksi/range) atau while loop (berdasarkan kondisi).",
            officialRules = listOf(
                "for item in iterable: Digunakan saat jumlah perulangan diketahui atau untuk mengiterasi urutan elemen (list, tuple, string, set, dict).",
                "range(start, stop[, step]): Menghasilkan urutan integer. Ingat: Nilai 'stop' bersifat EKSKLUSIF (berhenti sebelum nilai stop!). range(1, 5) menghasilkan [1, 2, 3, 4].",
                "while <kondisi>: Berjalan terus selama kondisi bernilai True. Membutuhkan pengubah kondisi agar tidak terjadi infinite loop.",
                "Pernyataan Kendali Loop:",
                "- break: Menghentikan dan keluar dari loop seketika.",
                "- continue: Melewati sisa baris di putaran saat ini dan langsung melompat ke putaran berikutnya.",
                "- pass: Placeholder pernyataan kosong (tidak melakukan apa-apa).",
                "- else pada loop: Dieksekusi hanya jika loop selesai secara normal (bukan karena dipaksa berhenti oleh break)."
            ),
            verifiedCodeSnippet = """
                # 1. for loop dengan range(start, stop, step)
                for i in range(1, 6):
                    print(f"Langkah ke-{i}")

                # 2. Iterasi pada elemen List dengan enumerate()
                daftar_siswa = ["Bayu", "Citra", "Dedi"]
                for no, nama in enumerate(daftar_siswa, start=1):
                    print(f"{no}. {nama}")

                # 3. while loop dengan break dan continue
                counter = 0
                while counter < 10:
                    counter += 1
                    if counter == 3:
                        continue  # Lewati angka 3
                    if counter == 7:
                        break     # Hentikan loop saat mencapai 7
                    print("Hitungan:", counter)
            """.trimIndent(),
            commonMistakes = listOf(
                "Lupa increment pada while loop (misal lupa counter += 1) sehingga program macet dalam infinite loop!",
                "Mengira range(1, 10) menghasilkan angka sampai 10. Nilai stop 10 tidak diikutkan (hanya sampai 9).",
                "Memodifikasi panjang List saat sedang diiterasi for item in my_list: my_list.remove(item) -> Menyebabkan elemen terlewati."
            ),
            teacherHermawanTip = "Gunakan for loop untuk iterasi struktur data (List, Tuple, Set, Dict) karena lebih aman dan bebas dari risiko infinite loop dibanding while loop."
        ),
        FundamentalTopic(
            id = "functions_scope",
            title = "Fungsi & Modularitas (Functions, def, return & Variable Scope)",
            keywords = listOf("fungsi", "function", "def", "parameter", "argumen", "return", "scope", "variabel lokal", "global", "modular"),
            summary = "Fungsi adalah blok kode modular yang dapat dipanggil berulang kali dengan kata kunci def dan menghasilkan nilai kembali via return.",
            officialRules = listOf(
                "Definisi: def nama_fungsi(parameter): ... return hasil",
                "Parameter vs Argumen: Parameter adalah variabel di baris 'def'. Argumen adalah nilai nyata yang dikirim saat fungsi dipanggil.",
                "return vs print: return mengembalikan nilai ke pemanggil sehingga bisa disimpan ke variabel lain. print() hanya mencetak ke layar dan mengembalikan None.",
                "Default Argument: Parameter dengan nilai bawaan, wajib diletakkan setelah parameter tanpa default.",
                "Scope Variabel: Variabel di dalam fungsi bersifat LOKAL (hilang setelah fungsi selesai). Variabel di luar fungsi bersifat GLOBAL."
            ),
            verifiedCodeSnippet = """
                # Definisi fungsi dengan default argument
                def hitung_nilai_akhir(tugas, ujian, bobot_ujian=0.6):
                    # Menghitung nilai berbobot siswa SMAN 1 Kepanjen
                    bobot_tugas = 1 - bobot_ujian
                    nilai_akhir = (tugas * bobot_tugas) + (ujian * bobot_ujian)
                    return nilai_akhir

                # Pemanggilan fungsi dan penampungan hasil return
                hasil_siswa = hitung_nilai_akhir(80, 90)
                print(f"Nilai Akhir: {hasil_siswa:.1f}")  # 86.0
            """.trimIndent(),
            commonMistakes = listOf(
                "Lupa pernyataan return: Fungsi yang tidak memiliki return akan mengembalikan None secara implisit.",
                "Meletakkan default parameter sebelum parameter wajib: def f(a=1, b): -> SyntaxError: non-default argument follows default argument",
                "Mencoba membaca variabel lokal di luar fungsinya -> NameError: name 'x' is not defined"
            ),
            teacherHermawanTip = "Pecah kodemu menjadi fungsi-fungsi kecil yang fokus melakukan 1 tugas saja. Ini adalah inti dari pilar Dekomposisi pada Berpikir Komputasional!"
        )
    )

    // =========================================================================
    // 3. STATIC CODE ANALYZER (Anti-Bias & Contextual Grounding)
    // =========================================================================

    data class CodeDiagnostic(
        val issueFound: Boolean,
        val probableError: String? = null,
        val lineSnippet: String? = null,
        val explanation: String? = null,
        val correctionCode: String? = null,
        val verifiedPythonRule: String? = null
    )

    fun analyzeCode(code: String): List<CodeDiagnostic> {
        val diagnostics = mutableListOf<CodeDiagnostic>()
        val lines = code.lines()

        for ((index, line) in lines.withIndex()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#") || trimmed.isEmpty()) continue

            // 1. Detect tuple mutation attempt like x[0] = ... where x is tuple
            if (trimmed.contains(Regex("""\w+\[\s*\d+\s*\]\s*="""))) {
                // If earlier defined with parenthesis
                val varName = trimmed.substringBefore("[").trim()
                val isTupleDefinition = lines.take(index).any { prevLine ->
                    prevLine.trim().startsWith("$varName = (") || prevLine.trim().startsWith("$varName=(")
                }
                if (isTupleDefinition) {
                    diagnostics.add(
                        CodeDiagnostic(
                            issueFound = true,
                            probableError = "TypeError: 'tuple' object does not support item assignment",
                            lineSnippet = "Baris ${index + 1}: $trimmed",
                            explanation = "Variabel '$varName' didefinisikan sebagai Tuple (...). Tuple bersifat konstan (Immutable), sehingga nilainya tidak dapat diubah menggunakan indeks assignment.",
                            correctionCode = "# Ubah inisialisasi $varName menjadi List [...] jika perlu dimutasi:\n$varName = list($varName)\n$trimmed",
                            verifiedPythonRule = "Spesifikasi CPython: Objek tuple tidak menyediakan tp_as_mapping->mp_ass_subscript."
                        )
                    )
                }
            }

            // 2. Detect tuple.append / tuple.insert
            if (trimmed.contains(Regex("""\w+\.(append|insert|remove|pop)\s*\("""))) {
                val varName = trimmed.substringBefore(".").trim()
                val isTuple = lines.take(index).any { prev ->
                    val t = prev.trim()
                    (t.startsWith("$varName = (") || t.startsWith("$varName=(")) && !t.contains("[")
                }
                if (isTuple) {
                    diagnostics.add(
                        CodeDiagnostic(
                            issueFound = true,
                            probableError = "AttributeError: 'tuple' object has no attribute 'append'",
                            lineSnippet = "Baris ${index + 1}: $trimmed",
                            explanation = "Tuple tidak memiliki metode manipulasi seperti append, insert, remove, atau pop karena sifatnya yang immutable.",
                            correctionCode = "# Solusi 1: Gunakan List jika data dinamis:\n# $varName = [...]\n# Solusi 2: Buat tuple baru melalui konkatenasi:\n$varName = $varName + (nilai_baru,)",
                            verifiedPythonRule = "Tuple hanya memiliki 2 metode bawaan: count() dan index()."
                        )
                    )
                }
            }

            // 3. Detect set indexing s[0]
            if (trimmed.contains(Regex("""\w+\[\s*\d+\s*\]""")) && !trimmed.contains("=") && !trimmed.contains("def ")) {
                val varName = trimmed.substringBefore("[").trim()
                val isSet = lines.take(index).any { prev ->
                    val t = prev.trim()
                    (t.startsWith("$varName = {") || t.startsWith("$varName={")) && !t.contains(":")
                }
                if (isSet) {
                    diagnostics.add(
                        CodeDiagnostic(
                            issueFound = true,
                            probableError = "TypeError: 'set' object is not subscriptable",
                            lineSnippet = "Baris ${index + 1}: $trimmed",
                            explanation = "Set tidak mendukung indexing angka (seperti $varName[0]) karena elemen Set disimpan dalam Hash Table tanpa urutan indeks tetap.",
                            correctionCode = "# Ambil elemen pertama setelah konversi ke list:\nitem = list($varName)[0]\n# Atau lakukan perulangan:\nfor elem in $varName:\n    print(elem)",
                            verifiedPythonRule = "Python Set adalah unordered collection yang tidak mengimplementasikan Sequence protocol (__getitem__)."
                        )
                    )
                }
            }

            // 4. Detect dict.add() mistake
            if (trimmed.contains(Regex("""\w+\.add\s*\("""))) {
                val varName = trimmed.substringBefore(".").trim()
                val isDict = lines.take(index).any { prev ->
                    val t = prev.trim()
                    t.startsWith("$varName = {") && t.contains(":")
                }
                if (isDict) {
                    diagnostics.add(
                        CodeDiagnostic(
                            issueFound = true,
                            probableError = "AttributeError: 'dict' object has no attribute 'add'",
                            lineSnippet = "Baris ${index + 1}: $trimmed",
                            explanation = "Dictionary tidak memiliki method .add(). Penambahan data pada dictionary dilakukan dengan sintaks key-value: dict[kunci] = nilai.",
                            correctionCode = "$varName[\"kunci_baru\"] = nilai_baru",
                            verifiedPythonRule = "Penambahan data dictionary menggunakan mapping assignment dict[key] = value atau dict.update()."
                        )
                    )
                }
            }

            // 5. Detect mutable list as set element or dict key
            if (trimmed.contains("{[") || trimmed.contains("{ [") || trimmed.contains(":[") && trimmed.startsWith("{")) {
                diagnostics.add(
                    CodeDiagnostic(
                        issueFound = true,
                        probableError = "TypeError: unhashable type: 'list'",
                        lineSnippet = "Baris ${index + 1}: $trimmed",
                        explanation = "List tidak dapat dimasukkan sebagai elemen Set atau Kunci Dictionary karena List mutable dan tidak memiliki nilai hash konstan.",
                        correctionCode = "# Ganti kurung siku [] menjadi kurung bulat () agar menjadi Tuple:\n# Contoh: {(1, 2), (3, 4)}",
                        verifiedPythonRule = "Hanya objek immutable (int, float, str, tuple) yang mengimplementasikan __hash__ yang sah sebagai kunci dict atau anggota set."
                    )
                )
            }
        }

        return diagnostics
    }

    // =========================================================================
    // 4. CONTEXTUAL RELEVANCE RETRIEVER FOR PROMPTS
    // =========================================================================

    /**
     * Retrieves verifiable knowledge base facts strictly relevant to user query, code context, and active topic.
     */
    fun retrieveRelevantGroundTruth(query: String, code: String?, topic: String? = null): String {
        val q = query.lowercase()
        val c = (code ?: "").lowercase()
        val t = (topic ?: "").lowercase()
        val sb = StringBuilder()

        sb.appendLine("=== BASIS DATA VALID SPESIFIKASI PYTHON 3.11 (GROUND TRUTH) ===")
        sb.appendLine("Institusi: SMAN 1 Kepanjen - Mata Pelajaran: Informatika Kelas XI (Guru: Hermawan Hidayat, S.Kom.)")

        val mentionsList = q.contains("list") || q.contains("daftar") || q.contains("array") ||
                c.contains("[") || q.contains("append") || q.contains("extend") || q.contains("slice") ||
                q.contains("pop") || q.contains("insert") || q.contains("sort") || t.contains("list")

        val mentionsTuple = q.contains("tuple") || q.contains("tupple") || q.contains("immutable") ||
                q.contains("unpack") || q.contains("swap") || q.contains("trailing comma") ||
                q.contains("(1,)") || c.contains("(") || t.contains("tuple")

        val mentionsSet = q.contains("set") || q.contains("himpunan") || q.contains("duplikat") ||
                q.contains("venn") || q.contains("irisan") || q.contains("gabungan") || q.contains("discard") ||
                q.contains("union") || q.contains("intersection") || q.contains("frozenset") ||
                q.contains("set()") || q.contains("set{}") || t.contains("set")

        val mentionsDict = q.contains("dict") || q.contains("dictionary") || q.contains("kamus") ||
                q.contains("key") || q.contains("value") || q.contains("kunci") || q.contains("items()") ||
                q.contains("keys()") || q.contains("values()") || q.contains("popitem") || q.contains("get(") ||
                q.contains(".get") || c.contains("{") || t.contains("dict")

        val isGeneralStructureInquiry = !mentionsList && !mentionsTuple && !mentionsSet && !mentionsDict

        if (mentionsList || isGeneralStructureInquiry) {
            appendSpec(sb, LIST_SPEC)
        }
        if (mentionsTuple || isGeneralStructureInquiry) {
            appendSpec(sb, TUPLE_SPEC)
        }
        if (mentionsSet || isGeneralStructureInquiry) {
            appendSpec(sb, SET_SPEC)
        }
        if (mentionsDict || isGeneralStructureInquiry) {
            appendSpec(sb, DICTIONARY_SPEC)
        }

        // Match Fundamental Topics
        val matchedFundamentals = FUNDAMENTAL_TOPICS.filter { topic ->
            topic.keywords.any { kw -> q.contains(kw) || c.contains(kw) }
        }
        if (matchedFundamentals.isNotEmpty()) {
            sb.appendLine("\n--- MATERI & KAIDAH DASAR PEMROGRAMAN PYTHON 3.11 ---")
            for (topic in matchedFundamentals) {
                appendFundamental(sb, topic)
            }
        }

        // Relevant error knowledge
        val relevantErrors = ERROR_KNOWLEDGE.filter { err ->
            q.contains(err.errorType.substringBefore(":").lowercase()) ||
            q.contains(err.triggeringPattern.lowercase()) ||
            c.contains(err.triggeringPattern.lowercase())
        }

        if (relevantErrors.isNotEmpty()) {
            sb.appendLine("\n--- DIAGNOSTIK ERROR RESMI TERKAIT ---")
            for (err in relevantErrors) {
                sb.appendLine("• Jenis Error: ${err.errorType}")
                sb.appendLine("  Penyebab Internal: ${err.rootCause}")
                sb.appendLine("  Penjelasan Resmi: ${err.verifiableExplanation}")
                sb.appendLine("  Solusi Baku: ${err.solution}")
            }
        }

        // Run static diagnostics on student code if provided
        if (!code.isNullOrBlank()) {
            val codeDiagnostics = analyzeCode(code)
            if (codeDiagnostics.isNotEmpty()) {
                sb.appendLine("\n--- HASIL DIAGNOSA KODE SISWA OLEH SISTEM INTERPRETER ---")
                for (diag in codeDiagnostics) {
                    sb.appendLine("⚠️ Terdeteksi Potensi Error: ${diag.probableError}")
                    sb.appendLine("   Lokasi: ${diag.lineSnippet}")
                    sb.appendLine("   Penjelasan: ${diag.explanation}")
                    sb.appendLine("   Solusi Rekomendasi:\n${diag.correctionCode}")
                    sb.appendLine("   Kaidah Python: ${diag.verifiedPythonRule}")
                }
            }
        }

        return sb.toString()
    }

    private fun appendSpec(sb: StringBuilder, spec: StructureSpec) {
        sb.appendLine("\n[SPESIFIKASI RESMI: ${spec.name.uppercase()}]")
        sb.appendLine("- Sintaks Baku: ${spec.syntax}")
        sb.appendLine("- Sifat Mutabilitas: ${if (spec.isMutable) "Mutable (Dapat diubah)" else "IMMUTABLE (Konstan/Tidak dapat diubah)"}")
        sb.appendLine("- Urutan (Ordered): ${if (spec.isOrdered) "Ordered (Urutan dipertahankan)" else "Unordered (Tidak terurut)"}")
        sb.appendLine("- Duplikasi Nilai: ${if (spec.allowsDuplicates) "Diizinkan (Boleh duplikat)" else "DILARANG (Otomatis Dieliminasi/Kunci Unik)"}")
        sb.appendLine("- Dukungan Index: ${spec.indexingSupport}")
        sb.appendLine("- Implementasi CPython: ${spec.underlyingImplementation}")
        sb.appendLine("- Kompleksitas Waktu (Big-O): ${spec.timeComplexitySummary}")
        sb.appendLine("- Metode Sah: ${spec.validMethods.joinToString(", ")}")
        sb.appendLine("- Kesalahan Umum yang DILARANG:")
        for (mistake in spec.invalidMethodsCommonMistakes) {
            sb.appendLine("  * $mistake")
        }
    }

    private fun appendFundamental(sb: StringBuilder, topic: FundamentalTopic) {
        sb.appendLine("\n[TOPIK DASAR: ${topic.title.uppercase()}]")
        sb.appendLine("- Ringkasan: ${topic.summary}")
        sb.appendLine("- Kaidah Resmi Python 3.11:")
        for (rule in topic.officialRules) {
            sb.appendLine("  * $rule")
        }
        sb.appendLine("- Contoh Kode Terverifikasi:\n${topic.verifiedCodeSnippet}")
        sb.appendLine("- Kesalahan Umum Siswa:")
        for (mistake in topic.commonMistakes) {
            sb.appendLine("  * $mistake")
        }
        sb.appendLine("- Tips Pedagogis Guru: ${topic.teacherHermawanTip}")
    }
}
