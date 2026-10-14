package com.example.model

import com.example.engine.ExecutionResult

/**
 * Model for interactive Python Data Structure snippet practice challenges.
 */
data class SnippetChallenge(
    val id: String,
    val category: DataStructureType,
    val title: String,
    val difficulty: String, // "Mudah", "Sedang", "Tantangan"
    val objective: String,
    val initialCode: String,
    val solutionCode: String,
    val hint: String,
    val expectedExplanation: String,
    val quickOps: List<Pair<String, String>> = emptyList(),
    val validator: (ExecutionResult) -> Boolean
)

object SnippetPracticeRepository {

    val allChallenges: List<SnippetChallenge> = listOf(
        // ==================== 1. LIST OPERATIONS ====================
        SnippetChallenge(
            id = "list_append_len",
            category = DataStructureType.LIST,
            title = "Operasi .append() & len()",
            difficulty = "Mudah",
            objective = "Tambahkan buah 'Mangga' ke dalam list `buah` menggunakan .append(), lalu cetak panjang list menggunakan len()!",
            initialCode = """
                # List Buah Segar
                buah = ["Apel", "Jeruk", "Pisang"]

                # TULIS KODEMU DI SINI:
                # 1. Tambahkan "Mangga" ke list buah
                # 2. Cetak total buah dengan print(len(buah))
            """.trimIndent(),
            solutionCode = """
                buah = ["Apel", "Jeruk", "Pisang"]
                buah.append("Mangga")
                print("Total Buah:", len(buah))
                print("List Akhir:", buah)
            """.trimIndent(),
            hint = "Gunakan buah.append('Mangga'), lalu panggil print(len(buah)).",
            expectedExplanation = ".append() menambahkan elemen baru di indeks paling akhir dengan kompleksitas waktu amortized O(1).",
            quickOps = listOf(
                "append()" to "buah.append(\"\")",
                "len()" to "len(buah)",
                "print()" to "print()",
                "[]" to "[]"
            ),
            validator = { result ->
                val stdout = result.stdout.lowercase()
                val buahList = result.variables["buah"] as? List<*>
                val hasMangga = buahList?.any { it.toString().lowercase() == "mangga" } == true
                (hasMangga && (buahList?.size ?: 0) >= 4) || (stdout.contains("4") && stdout.contains("mangga"))
            }
        ),

        SnippetChallenge(
            id = "list_insert_index",
            category = DataStructureType.LIST,
            title = "Operasi .insert() pada Indeks",
            difficulty = "Sedang",
            objective = "Sisipkan angka 99 pada indeks ke-0 (paling depan) dari list `antrian`, lalu cetak list tersebut!",
            initialCode = """
                antrian = [10, 20, 30, 40]

                # TULIS KODEMU:
                # Sisipkan 99 ke indeks 0 menggunakan .insert()
                # Cetak list antrian
            """.trimIndent(),
            solutionCode = """
                antrian = [10, 20, 30, 40]
                antrian.insert(0, 99)
                print(antrian)
            """.trimIndent(),
            hint = "Format metode insert adalah list.insert(indeks, nilai). Gunakan antrian.insert(0, 99).",
            expectedExplanation = ".insert(0, x) menggeser seluruh elemen yang sudah ada ke kanan, memerlukan waktu komputasi O(n).",
            quickOps = listOf(
                "insert(0)" to "antrian.insert(0, 99)",
                "print()" to "print(antrian)",
                "[0]" to "[0]"
            ),
            validator = { result ->
                val list = result.variables["antrian"] as? List<*>
                list?.firstOrNull()?.toString() == "99" || result.stdout.contains("[99, 10,") || result.stdout.contains("[99, 10")
            }
        ),

        SnippetChallenge(
            id = "list_pop_queue",
            category = DataStructureType.LIST,
            title = "Operasi .pop() Elemen",
            difficulty = "Mudah",
            objective = "Ambil dan keluarkan elemen terakhir dari list `pesanan` menggunakan .pop(), simpan ke variabel `terlayani`, lalu cetak variabel tersebut!",
            initialCode = """
                pesanan = ["Meja 1", "Meja 2", "Meja 3"]

                # TULIS KODEMU:
                # Ambil elemen terakhir dengan .pop()
                # Simpan ke variabel: terlayani = pesanan.pop()
                # Cetak: print(terlayani)
            """.trimIndent(),
            solutionCode = """
                pesanan = ["Meja 1", "Meja 2", "Meja 3"]
                terlayani = pesanan.pop()
                print("Terlayani:", terlayani)
                print("Sisa Pesanan:", pesanan)
            """.trimIndent(),
            hint = "Gunakan terlayani = pesanan.pop() lalu cetak hasilnya.",
            expectedExplanation = ".pop() tanpa argumen menghapus dan mengembalikan elemen paling ujung kanan (O(1)).",
            quickOps = listOf(
                "pop()" to "pesanan.pop()",
                "print()" to "print(terlayani)"
            ),
            validator = { result ->
                val terlayani = result.variables["terlayani"]?.toString()?.lowercase() ?: ""
                terlayani.contains("meja 3") || result.stdout.lowercase().contains("meja 3")
            }
        ),

        SnippetChallenge(
            id = "list_slicing_top3",
            category = DataStructureType.LIST,
            title = "Operasi Slicing [start:stop]",
            difficulty = "Sedang",
            objective = "Potong 3 nilai teratas dari list `skor` menggunakan slicing [0:3] atau [:3], simpan ke variabel `tiga_besar`, lalu cetak hasilnya!",
            initialCode = """
                skor = [98, 95, 91, 88, 84, 80]

                # TULIS KODEMU:
                # tiga_besar = skor[:3]
                # print(tiga_besar)
            """.trimIndent(),
            solutionCode = """
                skor = [98, 95, 91, 88, 84, 80]
                tiga_besar = skor[:3]
                print("3 Skor Tertinggi:", tiga_besar)
            """.trimIndent(),
            hint = "Slicing skor[:3] akan mengambil indeks 0, 1, dan 2 (stop bersifat eksklusif).",
            expectedExplanation = "Slicing Python menghasilkan list baru (shallow copy) tanpa merusak atau mengubah list aslinya.",
            quickOps = listOf(
                "[:3]" to "skor[:3]",
                "[::-1]" to "skor[::-1]",
                "print()" to "print(tiga_besar)"
            ),
            validator = { result ->
                val tiga = result.variables["tiga_besar"] as? List<*>
                val matches = tiga?.size == 3 && tiga.firstOrNull()?.toString() == "98"
                matches || result.stdout.contains("[98, 95, 91]")
            }
        ),

        // ==================== 2. TUPLE OPERATIONS ====================
        SnippetChallenge(
            id = "tuple_unpacking",
            category = DataStructureType.TUPLE,
            title = "Unpacking Tuple Koordinat",
            difficulty = "Mudah",
            objective = "Bongkar (*unpack*) tuple `koordinat` ke dalam 3 variabel: `lokasi`, `lat`, dan `lon`. Kemudian cetak kalimat f-string dengan data tersebut!",
            initialCode = """
                koordinat = ("SMAN 1 Kepanjen", -8.1306, 112.5719)

                # TULIS KODEMU:
                # lokasi, lat, lon = koordinat
                # print(f"{lokasi} berada di {lat}, {lon}")
            """.trimIndent(),
            solutionCode = """
                koordinat = ("SMAN 1 Kepanjen", -8.1306, 112.5719)
                lokasi, lat, lon = koordinat
                print(f"{lokasi} berada di {lat}, {lon}")
            """.trimIndent(),
            hint = "Gunakan urutan variabel yang persis sama dengan jumlah elemen tuple: lokasi, lat, lon = koordinat.",
            expectedExplanation = "Tuple Unpacking membongkar setiap posisi indeks ke dalam variabel lokal secara instan dan elegan.",
            quickOps = listOf(
                "unpack" to "lokasi, lat, lon = koordinat",
                "f-string" to "print(f\"{lokasi}: {lat}, {lon}\")"
            ),
            validator = { result ->
                val lok = result.variables["lokasi"]?.toString() ?: ""
                val lat = result.variables["lat"]?.toString() ?: ""
                lok.contains("Kepanjen", ignoreCase = true) || (lat.contains("-8.13") && result.stdout.contains("Kepanjen"))
            }
        ),

        SnippetChallenge(
            id = "tuple_immutable_edit",
            category = DataStructureType.TUPLE,
            title = "Solusi Modifikasi Tuple",
            difficulty = "Tantangan",
            objective = "Tuple bersifat immutable. Konversikan tuple `nilai` menjadi list, ganti nilai pertama menjadi 100, lalu ubah kembali menjadi tuple bernama `nilai_baru`!",
            initialCode = """
                nilai = (80, 85, 90)

                # TULIS KODEMU:
                # 1. temp = list(nilai)
                # 2. temp[0] = 100
                # 3. nilai_baru = tuple(temp)
                # 4. print(nilai_baru)
            """.trimIndent(),
            solutionCode = """
                nilai = (80, 85, 90)
                temp = list(nilai)
                temp[0] = 100
                nilai_baru = tuple(temp)
                print("Tuple Baru:", nilai_baru)
            """.trimIndent(),
            hint = "Ingat kaidah Python: tuple tidak bisa diubah langsung (akan memicu TypeError). Gunakan konversi list(nilai).",
            expectedExplanation = "Karena tuple immutable, untuk mengubah isinya kita membuat objek list sementara lalu merekonstruksi tuple baru.",
            quickOps = listOf(
                "list()" to "temp = list(nilai)",
                "tuple()" to "nilai_baru = tuple(temp)",
                "print()" to "print(nilai_baru)"
            ),
            validator = { result ->
                val nb = result.variables["nilai_baru"]
                val strVal = nb?.toString() ?: ""
                strVal.contains("100") && (strVal.startsWith("(") || result.stdout.contains("(100, 85, 90)"))
            }
        ),

        // ==================== 3. SET OPERATIONS ====================
        SnippetChallenge(
            id = "set_deduplication",
            category = DataStructureType.SET,
            title = "Eliminasi Duplikat dengan Set",
            difficulty = "Mudah",
            objective = "List `nomor_peserta` memiliki nomor ganda. Konversikan ke Set bernama `peserta_unik` untuk menghapus duplikat secara otomatis, lalu cetak jumlah peserta unik!",
            initialCode = """
                nomor_peserta = [101, 102, 101, 103, 104, 102, 105]

                # TULIS KODEMU:
                # 1. peserta_unik = set(nomor_peserta)
                # 2. print("Peserta Unik:", peserta_unik)
                # 3. print("Jumlah Unik:", len(peserta_unik))
            """.trimIndent(),
            solutionCode = """
                nomor_peserta = [101, 102, 101, 103, 104, 102, 105]
                peserta_unik = set(nomor_peserta)
                print("Peserta Unik:", peserta_unik)
                print("Jumlah Unik:", len(peserta_unik))
            """.trimIndent(),
            hint = "Panggil fungsi set(nomor_peserta) dan hitung panjangnya dengan len(peserta_unik).",
            expectedExplanation = "Struktur data Set dibangun di atas Hash Table sehingga setiap nilai hanya dapat disimpan tepat satu kali.",
            quickOps = listOf(
                "set()" to "peserta_unik = set(nomor_peserta)",
                "len()" to "len(peserta_unik)",
                "print()" to "print(len(peserta_unik))"
            ),
            validator = { result ->
                val pu = result.variables["peserta_unik"] as? Set<*>
                (pu?.size == 5) || result.stdout.contains("5")
            }
        ),

        SnippetChallenge(
            id = "set_venn_intersection",
            category = DataStructureType.SET,
            title = "Operasi Irisan (Intersection &)",
            difficulty = "Sedang",
            objective = "Cari siswa yang aktif di KEDUA ekstrakurikuler (`robotik` dan `pramuka`) menggunakan operator irisan `&`, simpan ke variabel `irisan`, lalu cetak hasilnya!",
            initialCode = """
                robotik = {"Andi", "Budi", "Citra", "Dedi"}
                pramuka = {"Citra", "Dedi", "Eka", "Fani"}

                # TULIS KODEMU:
                # irisan = robotik & pramuka
                # print("Anggota Keduanya:", irisan)
            """.trimIndent(),
            solutionCode = """
                robotik = {"Andi", "Budi", "Citra", "Dedi"}
                pramuka = {"Citra", "Dedi", "Eka", "Fani"}
                irisan = robotik & pramuka
                print("Anggota Keduanya:", irisan)
            """.trimIndent(),
            hint = "Gunakan operator ampersand (&) atau metode robotik.intersection(pramuka).",
            expectedExplanation = "Operator '&' mencari elemen yang berada di kedua himpunan sekaligus (irisan Venn Diagram).",
            quickOps = listOf(
                "& (irisan)" to "irisan = robotik & pramuka",
                "| (gabungan)" to "gabungan = robotik | pramuka",
                "- (selisih)" to "selisih = robotik - pramuka"
            ),
            validator = { result ->
                val irisan = result.variables["irisan"]?.toString()?.lowercase() ?: ""
                val out = result.stdout.lowercase()
                (irisan.contains("citra") && irisan.contains("dedi")) || (out.contains("citra") && out.contains("dedi"))
            }
        ),

        // ==================== 4. DICTIONARY OPERATIONS ====================
        SnippetChallenge(
            id = "dict_key_update",
            category = DataStructureType.DICTIONARY,
            title = "Menambah & Mengubah Key-Value",
            difficulty = "Mudah",
            objective = "Tambahkan key baru 'sekolah' dengan nilai 'SMAN 1 Kepanjen' ke dictionary `profil`, lalu cetak dictionary tersebut!",
            initialCode = """
                profil = {
                    "nama": "Farhan",
                    "kelas": "XI-MIPA"
                }

                # TULIS KODEMU:
                # 1. Tambahkan profil["sekolah"] = "SMAN 1 Kepanjen"
                # 2. print(profil)
            """.trimIndent(),
            solutionCode = """
                profil = {
                    "nama": "Farhan",
                    "kelas": "XI-MIPA"
                }
                profil["sekolah"] = "SMAN 1 Kepanjen"
                print(profil)
            """.trimIndent(),
            hint = "Di Python, menambahkan key baru cukup dengan sintaks profil['sekolah'] = 'SMAN 1 Kepanjen'.",
            expectedExplanation = "Dictionary Python menggunakan hashing untuk memetakan key ke value dalam waktu instan O(1).",
            quickOps = listOf(
                "[\"key\"]" to "profil[\"sekolah\"] = \"SMAN 1 Kepanjen\"",
                ".keys()" to "profil.keys()",
                "print()" to "print(profil)"
            ),
            validator = { result ->
                val prof = result.variables["profil"] as? Map<*, *>
                val sek = prof?.get("sekolah")?.toString()?.lowercase() ?: ""
                sek.contains("kepanjen") || result.stdout.lowercase().contains("kepanjen")
            }
        ),

        SnippetChallenge(
            id = "dict_safe_get",
            category = DataStructureType.DICTIONARY,
            title = "Akses Aman dengan .get()",
            difficulty = "Sedang",
            objective = "Gunakan metode `.get()` untuk mengambil nilai 'alamat' dari dictionary `siswa`. Berikan nilai default 'Belum Tercatat' jika key tidak ditemukan!",
            initialCode = """
                siswa = {
                    "nis": 2024101,
                    "nama": "Nadia Putri"
                }

                # TULIS KODEMU:
                # alamat = siswa.get("alamat", "Belum Tercatat")
                # print("Alamat:", alamat)
            """.trimIndent(),
            solutionCode = """
                siswa = {
                    "nis": 2024101,
                    "nama": "Nadia Putri"
                }
                alamat = siswa.get("alamat", "Belum Tercatat")
                print("Alamat:", alamat)
            """.trimIndent(),
            hint = "Sintaks: dict.get(key, nilai_default). Mencegah program crash akibat KeyError.",
            expectedExplanation = ".get() mengembalikan default value tanpa memicu Exception jika key tidak ada dalam kamus.",
            quickOps = listOf(
                ".get()" to "siswa.get(\"alamat\", \"Belum Tercatat\")",
                "print()" to "print(alamat)"
            ),
            validator = { result ->
                val alamat = result.variables["alamat"]?.toString()?.lowercase() ?: ""
                alamat.contains("belum tercatat") || result.stdout.lowercase().contains("belum tercatat")
            }
        ),

        SnippetChallenge(
            id = "dict_loop_items",
            category = DataStructureType.DICTIONARY,
            title = "Iterasi Pasangan Key & Value",
            difficulty = "Sedang",
            objective = "Lakukan perulangan (loop) melalui `harga_buku.items()` untuk mencetak setiap nama buku beserta harganya!",
            initialCode = """
                harga_buku = {
                    "Python Dasar": 55000,
                    "Struktur Data": 75000,
                    "Kecerdasan Buatan": 90000
                }

                # TULIS KODEMU:
                # for judul, harga in harga_buku.items():
                #     print(f"{judul}: Rp {harga}")
            """.trimIndent(),
            solutionCode = """
                harga_buku = {
                    "Python Dasar": 55000,
                    "Struktur Data": 75000,
                    "Kecerdasan Buatan": 90000
                }
                for judul, harga in harga_buku.items():
                    print(f"{judul}: Rp {harga}")
            """.trimIndent(),
            hint = "Gunakan for judul, harga in harga_buku.items(): lalu cetak keduanya.",
            expectedExplanation = ".items() mengembalikan view objek berisi tuple pasangan (key, value) yang sangat efisien untuk iterasi.",
            quickOps = listOf(
                ".items()" to "for k, v in harga_buku.items():",
                "f-string" to "print(f\"{k}: {v}\")"
            ),
            validator = { result ->
                val out = result.stdout.lowercase()
                out.contains("python dasar") && (out.contains("55000") || out.contains("75000"))
            }
        ),

        // ==================== 5. NESTED DATA STRUCTURES ====================
        SnippetChallenge(
            id = "nested_list_in_dict",
            category = DataStructureType.NESTED,
            title = "Navigasi List di Dalam Dict",
            difficulty = "Sedang",
            objective = "Akses list 'nilai' milik siswa, tambahkan nilai 95 ke dalam list tersebut dengan .append(), lalu cetak rata-rata nilai siswa!",
            initialCode = """
                siswa = {
                    "nama": "Bayu Pratama",
                    "nilai": [80, 85, 90]
                }

                # TULIS KODEMU:
                # 1. Tambahkan 95 ke siswa["nilai"]
                # 2. rata_rata = sum(siswa["nilai"]) / len(siswa["nilai"])
                # 3. print("Rata-rata Baru:", rata_rata)
            """.trimIndent(),
            solutionCode = """
                siswa = {
                    "nama": "Bayu Pratama",
                    "nilai": [80, 85, 90]
                }
                siswa["nilai"].append(95)
                rata_rata = sum(siswa["nilai"]) / len(siswa["nilai"])
                print("Rata-rata Baru:", rata_rata)
            """.trimIndent(),
            hint = "Gunakan siswa['nilai'].append(95) lalu hitung sum(siswa['nilai']) / len(siswa['nilai']).",
            expectedExplanation = "Pada struktur bersarang, kita mengakses objek luar terlebih dahulu sebelum memanggil metode pada objek dalam.",
            quickOps = listOf(
                "[\"nilai\"].append" to "siswa[\"nilai\"].append(95)",
                "sum/len" to "sum(siswa[\"nilai\"]) / len(siswa[\"nilai\"])",
                "print()" to "print(rata_rata)"
            ),
            validator = { result ->
                val siswa = result.variables["siswa"] as? Map<*, *>
                val listNilai = siswa?.get("nilai") as? List<*>
                val has95 = listNilai?.any { it.toString() == "95" } == true
                has95 || result.stdout.contains("87.5") || result.stdout.contains("95")
            }
        ),

        SnippetChallenge(
            id = "nested_dict_in_list",
            category = DataStructureType.NESTED,
            title = "Navigasi Tabel Baris (Dict in List)",
            difficulty = "Tantangan",
            objective = "Data `inventaris` berisi list dictionary. Ambil nama barang pertama `inventaris[0]['item']` dan hitung total stok seluruh barang!",
            initialCode = """
                inventaris = [
                    {"item": "Mouse USB", "stok": 15},
                    {"item": "Keyboard Mekanikal", "stok": 8},
                    {"item": "Monitor 24 Inch", "stok": 5}
                ]

                # TULIS KODEMU:
                # 1. barang_pertama = inventaris[0]["item"]
                # 2. total_stok = inventaris[0]["stok"] + inventaris[1]["stok"] + inventaris[2]["stok"]
                # 3. print(f"Barang pertama: {barang_pertama}, Total: {total_stok}")
            """.trimIndent(),
            solutionCode = """
                inventaris = [
                    {"item": "Mouse USB", "stok": 15},
                    {"item": "Keyboard Mekanikal", "stok": 8},
                    {"item": "Monitor 24 Inch", "stok": 5}
                ]
                barang_pertama = inventaris[0]["item"]
                total_stok = sum(item["stok"] for item in inventaris)
                print(f"Barang pertama: {barang_pertama}")
                print(f"Total Stok: {total_stok}")
            """.trimIndent(),
            hint = "Gunakan inventaris[0]['item'] untuk mengakses record baris pertama pada kolom 'item'.",
            expectedExplanation = "Pola List-of-Dictionaries merepresentasikan data tabel database atau JSON response API secara native di Python.",
            quickOps = listOf(
                "baris 0" to "inventaris[0][\"item\"]",
                "total" to "total_stok = 15 + 8 + 5",
                "print()" to "print(total_stok)"
            ),
            validator = { result ->
                val out = result.stdout.lowercase()
                out.contains("mouse") && (out.contains("28") || result.variables["total_stok"]?.toString() == "28")
            }
        )
    )
}
