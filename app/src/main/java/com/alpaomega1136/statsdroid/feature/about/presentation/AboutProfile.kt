package com.alpaomega1136.statsdroid.feature.about.presentation

data class AboutProfile(
    val name: String,
    val studentId: String,
    val developerInformation: String,
    val motivation: String,
    val assistantExpectation: String,
    val contactInformation: String,
    val githubUrl: String,
    val repositoryUrl: String,
    val linkedInUrl: String? = null,
    val instagramUrl: String? = null,
    val githubHandle: String = "@Alpaomega1136",
    val instagramHandle: String = "@raymond_jo1136",
    val focusAreas: List<String> = listOf(
        "Rekayasa Perangkat Lunak & Aplikasi",
        "Sains Data & Kecerdasan Buatan"
    ),
    val visionText: String = "Membantu mahasiswa dalam memahami materi, mengarahkan proses pembelajaran, serta memberikan penilaian yang objektif pada mata kuliah Ilmu dan Rekayasa Komputasi. Selain itu, turut membantu dosen dalam mendukung pelaksanaan mata kuliah yang diampu agar kegiatan pembelajaran dapat berjalan dengan baik, terstruktur, dan efektif.",
    val missionPoints: List<String> = listOf(
        "Membantu mahasiswa memahami proses berpikir dalam menyelesaikan permasalahan, bukan hanya memberikan jawaban akhir.",
        "Memberikan arahan dan umpan balik yang jujur, objektif, serta membangun terhadap tugas maupun hasil pekerjaan mahasiswa.",
        "Mendorong mahasiswa untuk meningkatkan kemampuan berkolaborasi, baik dengan sesama mahasiswa maupun dengan asisten, sehingga tercipta lingkungan pembelajaran yang aktif, suportif, dan saling membantu."
    ),
)
