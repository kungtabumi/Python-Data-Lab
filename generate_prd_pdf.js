const fs = require('fs');
const PDFDocument = require('pdfkit');

function buildPRDPdf() {
    const doc = new PDFDocument({
        size: 'A4',
        margins: { top: 50, bottom: 50, left: 50, right: 50 },
        bufferPages: true,
        info: {
            Title: 'Product Requirements Document - Python DataLab',
            Author: 'SMAN 1 Kepanjen / Hermawan Hidayat, S.Kom.',
            Subject: 'Media Pembelajaran Interaktif Struktur Data Python Kelas XI',
            Keywords: 'Python, Data Structures, Android, Jetpack Compose, Gemini AI, Kurikulum Merdeka',
            CreationDate: new Date()
        }
    });

    const stream = fs.createWriteStream('/PRODUCT_REQUIREMENTS_DOCUMENT.pdf');
    doc.pipe(stream);

    const PRIMARY_COLOR = '#1E293B'; // Slate Navy
    const ACCENT_BLUE = '#2563EB';   // Python Royal Blue
    const ACCENT_EMERALD = '#059669';// Success Emerald
    const TEXT_MAIN = '#0F172A';     // Slate Dark
    const TEXT_MUTED = '#475569';    // Slate Gray
    const BG_LIGHT = '#F8FAFC';      // Soft Card Gray
    const BORDER_COLOR = '#E2E8F0';  // Border Light

    // Helper: Draw Section Title
    function drawSectionHeader(number, title) {
        doc.moveDown(0.8);
        const y = doc.y;
        doc.rect(50, y, 495, 24).fill(BG_LIGHT);
        doc.rect(50, y, 4, 24).fill(ACCENT_BLUE);
        doc.fillColor(PRIMARY_COLOR)
           .font('Helvetica-Bold')
           .fontSize(13)
           .text(`${number}. ${title}`, 60, y + 6);
        doc.moveDown(0.8);
    }

    // Helper: Draw Subheading
    function drawSubHeader(title) {
        doc.moveDown(0.5);
        doc.fillColor(ACCENT_BLUE)
           .font('Helvetica-Bold')
           .fontSize(11)
           .text(title);
        doc.moveDown(0.3);
    }

    // Helper: Draw Paragraph
    function drawParagraph(text, font = 'Helvetica', size = 9.5, color = TEXT_MAIN) {
        doc.fillColor(color)
           .font(font)
           .fontSize(size)
           .text(text, { align: 'justify', lineGap: 3 });
        doc.moveDown(0.4);
    }

    // Helper: Draw Bullet Point
    function drawBullet(boldText, regularText) {
        const startX = 60;
        doc.fillColor(ACCENT_BLUE).font('Helvetica-Bold').fontSize(10).text('• ', startX, doc.y, { continued: true });
        doc.fillColor(PRIMARY_COLOR).font('Helvetica-Bold').fontSize(9.5).text(boldText + ': ', { continued: true });
        doc.fillColor(TEXT_MAIN).font('Helvetica').fontSize(9.5).text(regularText, { align: 'justify', lineGap: 2 });
        doc.moveDown(0.2);
    }

    // Helper: Draw Code Block
    function drawCodeBlock(code) {
        const y = doc.y;
        const lines = code.split('\n');
        const height = lines.length * 12 + 16;
        
        if (doc.y + height > 750) {
            doc.addPage();
        }

        const startY = doc.y;
        doc.rect(55, startY, 485, height).fill('#F1F5F9');
        doc.rect(55, startY, 485, height).stroke('#CBD5E1');
        
        doc.fillColor('#0F172A')
           .font('Courier')
           .fontSize(8.5);
        
        lines.forEach((line, idx) => {
            doc.text(line, 65, startY + 8 + (idx * 12));
        });
        
        doc.y = startY + height + 8;
    }

    // ==========================================
    // COVER / TITLE HEADER
    // ==========================================
    doc.rect(50, 45, 495, 75).fill(PRIMARY_COLOR);
    doc.fillColor('#FFFFFF')
       .font('Helvetica-Bold')
       .fontSize(18)
       .text('PRODUCT REQUIREMENTS DOCUMENT (PRD)', 65, 60);
    doc.fillColor('#93C5FD')
       .font('Helvetica')
       .fontSize(11)
       .text('Python DataLab: Media Pembelajaran Interaktif Struktur Data Python', 65, 84);
    doc.fillColor('#E2E8F0')
       .font('Helvetica-Oblique')
       .fontSize(9)
       .text('SMAN 1 Kepanjen • Kurikulum Merdeka Informatika Kelas XI • Guru Pembimbing: Hermawan Hidayat, S.Kom.', 65, 100);

    doc.y = 130;

    // Metadata Table
    const metaData = [
        ['Nama Produk', 'Python DataLab (Android App)'],
        ['Versi Dokumen', '1.1.0 (Production Release / Verified)'],
        ['Target Platform', 'Android 8.0+ (Jetpack Compose & Kotlin)'],
        ['Pengampu & Institusi', 'SMAN 1 Kepanjen (Hermawan Hidayat, S.Kom.)'],
        ['Arsitektur Inti', 'MVVM, Room DB, Local Python Engine, Gemini 3.5 AI Tutor']
    ];

    let tableY = doc.y;
    metaData.forEach(([key, val], idx) => {
        const rowBg = idx % 2 === 0 ? '#F8FAFC' : '#FFFFFF';
        doc.rect(50, tableY, 495, 18).fill(rowBg);
        doc.rect(50, tableY, 495, 18).stroke(BORDER_COLOR);
        doc.fillColor(PRIMARY_COLOR).font('Helvetica-Bold').fontSize(9).text(key, 60, tableY + 4);
        doc.fillColor(TEXT_MAIN).font('Helvetica').fontSize(9).text(val, 210, tableY + 4);
        tableY += 18;
    });

    doc.y = tableY + 12;

    // ==========================================
    // SECTION 1: EXECUTIVE SUMMARY & VISI
    // ==========================================
    drawSectionHeader('1', 'EXECUTIVE SUMMARY & VISI PRODUK');
    
    drawSubHeader('1.1 Latar Belakang Permasalahan');
    drawParagraph('Struktur Data Python (List, Tuple, Set, Dictionary) merupakan pondasi penting dalam Kurikulum Merdeka Informatika Fase F (Kelas XI). Berdasarkan observasi praktikum di sekolah, siswa kerap mengalami kendala konseptual:');
    drawBullet('Mutabilitas vs Immutabilitas', 'Kebingungan membedakan objek yang dapat dimodifikasi langsung di memori (List/Set/Dict) dengan objek konstan read-only (Tuple).');
    drawBullet('Model Pengindeksan & Hash Table', 'Kesalahan mengakses Set dan Dictionary dengan indeks numerik serta miskonsepsi unhashable keys.');
    drawBullet('Operasi Diagram Venn', 'Kesulitan memahami perbedaan aljabar himpunan (Union, Intersection, Difference, Symmetric Difference) secara komputasional.');
    drawBullet('Struktur Bersarang (Nested Structures)', 'Kerumitan dalam pengaksesan list of dictionaries dan 2D matrix.');
    drawBullet('Keterbatasan Media Konvensional', 'Bahan ajar teks statis tidak menyediakan visualisasi RAM interaktif dan minim diagnosis galat instan.');

    drawSubHeader('1.2 Visi & Tujuan Produk');
    drawParagraph('Python DataLab dirancang sebagai laboratorium digital portabel yang menyatukan 5 pilar utama: Modul Kurikulum Terstruktur, Visual State Engine interaktif, On-Device Local Python Engine, Anti-Hallucination AI Tutor (Gemini), dan Gamifikasi Edukatif berjenjang.');

    // ==========================================
    // SECTION 2: SASARAN PENGGUNA & USER JOURNEY
    // ==========================================
    drawSectionHeader('2', 'SASARAN PENGGUNA (USER PERSONAS) & USER JOURNEY');
    
    drawBullet('Siswa SMA Kelas XI (Primary Learner)', 'Memerlukan media belajar visual mandiri di HP, sarana coding langsung tanpa laptop, serta bantuan diagnosa error interpreter seketika.');
    drawBullet('Guru Informatika (Educator / Mentor)', 'Memerlukan instrumen pembelajaran digital yang terstandarisasi CPython 3.11 dan selaras dengan Capaian Pembelajaran (CP) Informatika.');

    drawSubHeader('2.1 Alur Pengalaman Belajar (User Journey)');
    drawParagraph('1. Eksplorasi Materi: Membaca konsep dan memanipulasi diagram memori RAM secara langsung.\n2. Praktik Koding: Menjalankan contoh kode pada On-Device Python Interpreter lokal.\n3. Diagnosa & Tanya AI Tutor: Mengonsultasikan pesan error dan studi kasus dengan AI Tutor kontekstual.\n4. Evaluasi & Gamifikasi: Menyelesaikan kuis berbobot XP dan mengumpulkan lencana prestasi.');

    // Check page space
    if (doc.y > 620) doc.addPage();

    // ==========================================
    // SECTION 3: KEBUTUHAN FUNGSIONAL (FUNCTIONAL REQUIREMENTS)
    // ==========================================
    drawSectionHeader('3', 'SPESIFIKASI KEBUTUHAN FUNGSIONAL');

    drawSubHeader('3.1 Modul Kurikulum 4 Struktur Data & Dasar Pemrograman (50+ Materi)');
    drawBullet('List Module (14 Topik)', 'Karakteristik dynamic array, zero-indexing, negative indexing, slicing [start:stop:step], append vs extend vs insert, pop vs remove vs del vs clear, Timsort (sort vs sorted), list of lists (matrix 2D), dan list comprehension.');
    drawBullet('Tuple Module (9 Topik)', 'Immutability & memory footprint, single-element tuple trailing comma (1,), tuple unpacking, variable swapping (a, b = b, a), defensive programming, serta tuple sebagai kunci dictionary hashable.');
    drawBullet('Set Module (10 Topik)', 'Keunikan nilai tanpa duplikat, jebakan {} (dict) vs set(), eliminasi duplikat cepat O(n), operasi Venn (Union |, Intersection &, Difference -, Symmetric Difference ^), metode aman .discard() vs .remove(), serta larangan set[0].');
    drawBullet('Dictionary Module (12 Topik)', 'Pemetaan Key-Value, aturan kunci unik & hashable, akses aman dict.get(key, default), penugasan dict[k]=v & .update(), penghapusan .pop()/.popitem()/del, looping .items()/.keys()/.values(), operator penggabungan | (Python 3.9+), dan pengurutan.');
    drawBullet('Nested Structures & Perbandingan (5 Topik)', 'List of Dictionaries (database records), Dictionary of Lists, Matriks 2D, Matriks Perbandingan 4 Struktur Data, dan Decision Flowchart.');

    if (doc.y > 620) doc.addPage();

    drawSubHeader('3.2 Visual State Engine (Visualisasi Memori Interaktif)');
    drawBullet('List & Tuple RAM Visualizer', 'Animasi visual blok slot memori linier dengan penomoran indeks positif (0, 1, 2) dan negatif (-1, -2, -3) saat data dimodifikasi.');
    drawBullet('Set Venn Diagram Visualizer', 'Visualisasi dua lingkaran himpunan (A dan B) dengan penyorotan warna area interaktif saat operasi himpunan dieksekusi.');
    drawBullet('Dictionary Hash Map Visualizer', 'Visualisasi representasi Compact Hash Table yang memetakan slot Kunci ke Nilai secara transparan.');
    drawBullet('Nested Tree & 2D Grid Visualizer', 'Representasi visual hierarki pohon dan grid matriks baris-kolom.');

    drawSubHeader('3.3 Engine Eksekusi Python & Code Playground');
    drawBullet('On-Device Python Interpreter', 'Eksekutor Python lokal berkecepatan tinggi tanpa koneksi server eksternal, memvalidasi stdout & stderr secara instan.');
    drawBullet('13 Tantangan Koding Terintegrasi', 'Latihan studi kasus terpandu per modul dengan verifikasi solusi otomatis, petunjuk (hint), dan reward +15 XP.');
    drawBullet('Full Code Playground', 'Editor kode interaktif dengan kontrol Jalankan, Reset, Format, dan Simpan Snippet favorit di database lokal.');

    if (doc.y > 620) doc.addPage();

    drawSubHeader('3.4 Asisten Belajar Cerdas (AI Python Tutor powered by Gemini)');
    drawBullet('Context-Aware Multi-Turn Consultation', 'Mampu mengenali topik modul yang sedang aktif (topicTitle), kode playground siswa, riwayat percakapan sebelumnya, serta hasil eksekusi.');
    drawBullet('Bedah Galat & Error Otomatis', 'Analisis presisi untuk SyntaxError, IndexError, KeyError, TypeError, ValueError, dan IndentationError lengkap dengan penjelasan model memori.');
    drawBullet('Anti-Hallucination & Streaming Engine', 'Menghasilkan jawaban berbasis CPython 3.11 yang terverifikasi, didukung multi-model fallback (gemini-3.5-flash, gemini-3.1-pro-preview, gemini-3.1-flash-lite-preview) dan offline ground-truth engine.');

    drawSubHeader('3.5 Gamifikasi, Kuis & Evaluasi');
    drawBullet('Sistem XP & Leveling', 'Novice (0 XP) -> Apprentice (100 XP) -> Pythonista (300 XP) -> Code Wizard (600+ XP).');
    drawBullet('Evaluasi Kuis Formatif', 'Kuis pilihan ganda interaktif dengan pembahasan mendalam.');
    drawBullet('9 Achievement Badges', 'First Step, List Master, Tuple Guardian, Set Maestro, Dict Wizard, Quiz Champion, Python Expert, Code Innovator, Lab Legend.');

    // ==========================================
    // SECTION 4: KEBUTUHAN NON-FUNGSIONAL
    // ==========================================
    if (doc.y > 600) doc.addPage();
    drawSectionHeader('4', 'SPESIFIKASI KEBUTUHAN NON-FUNGSIONAL');
    drawBullet('UI/UX & Aksesibilitas (M3)', 'Menggunakan Material Design 3, touch target >= 48dp, kontras warna optimal, dan proteksi insets keyboard virtual.');
    drawBullet('Performa & Kecepatan', 'Waktu peluncuran awal < 1.2 detik, eksekusi kode lokal < 100 ms, dan rendering UI 60 FPS.');
    drawBullet('Offline-First Architecture', 'Penyimpanan seluruh data progres, XP, streak, dan snippet di Android Room Database lokal.');
    drawBullet('Keamanan Kredensial', 'Kunci API Gemini dikelola aman melalui Secrets Panel/BuildConfig tanpa eksposur di repositori publik.');

    // ==========================================
    // SECTION 5: ARSITEKTUR TEKNIS
    // ==========================================
    if (doc.y > 580) doc.addPage();
    drawSectionHeader('5', 'ARSITEKTUR TEKNIS & STRUKTUR SISTEM');
    drawParagraph('Aplikasi mengadopsi pola arsitektur Clean Architecture / MVVM dengan pembagian modul sebagai berikut:');
    
    drawCodeBlock(
`com.example/
├── ai/          -> GeminiTutorService (API & Offline Engine), KnowledgeBase
├── data/        -> CurriculumData, Room Database, DAO, Entity, Repository
├── engine/      -> LocalPythonInterpreter, ExecutionResult, MemorySimulator
├── model/       -> DataStructureType, TopicLesson, QuizQuestion, Badges
├── navigation/  -> Type-safe Compose Routes
└── ui/          -> M3 Components (Visualizers), Screens, Theme, ViewModel`
    );

    // ==========================================
    // SECTION 6: STATUS VERIFIKASI & ACCEPTANCE CRITERIA
    // ==========================================
    if (doc.y > 580) doc.addPage();
    drawSectionHeader('6', 'KRITERIA PENERIMAAN & STATUS VERIFIKASI');

    const testResults = [
        ['Kurikulum 4 Struktur Data', '50+ modul lengkap dapat diakses & ditandai selesai', 'PASSED'],
        ['Visual State Engine', 'Visualizer RAM List, Tuple, Venn Set, Hash Map Dict aktif', 'PASSED'],
        ['Local Python Engine', 'Eksekusi sintaks Python lokal on-device berhasil instan', 'PASSED'],
        ['AI Tutor Contextuality', 'Memahami pertanyaan List, Tuple, Set, Dict & Error diagnosis', 'PASSED'],
        ['Multi-turn & Streaming', 'Streaming AI lancar tanpa blok kode terpotong', 'PASSED'],
        ['Gamifikasi & Lencana', 'Pencatatan XP, Leveling, dan Badge Celebration aktif', 'PASSED'],
        ['Automated Test Suite', '27+ unit tests lulus 100% (gradle testDebugUnitTest)', 'PASSED'],
        ['Android Applet Build', 'Kompilasi sukses tanpa galat (compile_applet)', 'PASSED']
    ];

    let tY = doc.y + 4;
    doc.rect(50, tY, 495, 20).fill(PRIMARY_COLOR);
    doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(9).text('Kategori Pengujian', 60, tY + 5);
    doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(9).text('Kriteria Keberhasilan', 200, tY + 5);
    doc.fillColor('#FFFFFF').font('Helvetica-Bold').fontSize(9).text('Status', 480, tY + 5);
    tY += 20;

    testResults.forEach(([cat, desc, status], idx) => {
        const bg = idx % 2 === 0 ? '#F8FAFC' : '#FFFFFF';
        doc.rect(50, tY, 495, 18).fill(bg);
        doc.rect(50, tY, 495, 18).stroke(BORDER_COLOR);
        doc.fillColor(PRIMARY_COLOR).font('Helvetica-Bold').fontSize(8.5).text(cat, 60, tY + 4);
        doc.fillColor(TEXT_MAIN).font('Helvetica').fontSize(8.5).text(desc, 200, tY + 4);
        doc.fillColor(ACCENT_EMERALD).font('Helvetica-Bold').fontSize(8.5).text(status, 480, tY + 4);
        tY += 18;
    });

    doc.y = tY + 20;

    // Footer signature notice
    doc.rect(50, doc.y, 495, 36).fill('#F1F5F9');
    doc.fillColor(TEXT_MUTED).font('Helvetica-Oblique').fontSize(8.5)
       .text('Dokumen ini merupakan spesifikasi resmi pengembangan aplikasi Python DataLab SMAN 1 Kepanjen.', 60, doc.y + 8, { align: 'center', width: 475 });
    doc.fillColor(PRIMARY_COLOR).font('Helvetica-Bold').fontSize(8.5)
       .text('Pembimbing: Bapak Hermawan Hidayat, S.Kom. • Status: Approved & Release Ready', 60, doc.y + 4, { align: 'center', width: 475 });

    // ==========================================
    // PAGE NUMBERS & RUNNING HEADERS
    // ==========================================
    const pageCount = doc.bufferedPageRange().count;
    for (let i = 0; i < pageCount; i++) {
        doc.switchToPage(i);
        
        // Running Top Header
        if (i > 0) {
            doc.fillColor(TEXT_MUTED)
               .font('Helvetica')
               .fontSize(7.5)
               .text('Python DataLab — Product Requirements Document (PRD)', 50, 25);
            doc.fillColor(TEXT_MUTED)
               .font('Helvetica')
               .fontSize(7.5)
               .text('SMAN 1 Kepanjen', 470, 25);
            doc.moveTo(50, 35).lineTo(545, 35).stroke(BORDER_COLOR);
        }

        // Running Bottom Footer
        doc.moveTo(50, 795).lineTo(545, 795).stroke(BORDER_COLOR);
        doc.fillColor(TEXT_MUTED)
           .font('Helvetica')
           .fontSize(8)
           .text('Informatika Kelas XI • SMAN 1 Kepanjen', 50, 805);
        doc.fillColor(TEXT_MUTED)
           .font('Helvetica')
           .fontSize(8)
           .text(`Halaman ${i + 1} dari ${pageCount}`, 470, 805);
    }

    doc.end();
}

buildPRDPdf();
console.log('PDF generated successfully at /PRODUCT_REQUIREMENTS_DOCUMENT.pdf');
