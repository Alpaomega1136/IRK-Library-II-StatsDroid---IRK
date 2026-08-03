package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
fun AboutRoute() {
    val uriHandler = LocalUriHandler.current

    val profile = AboutProfile(
        name = "Raymond Jonathan",
        studentId = "13524059",
        developerInformation =
            "Mahasiswa Teknik Informatika Institut Teknologi Bandung (ITB) angkatan 2024. Saya berfokus untuk membangun fondasi yang kuat dalam rekayasa perangkat lunak, sains data, dan kecerdasan buatan.\n\nSaya senang mengubah ide menjadi perangkat lunak praktis, mempelajari cara kerja sistem, serta mengeksplorasi hubungan antara kode, gim, dan animasi visual.\n\nFokus:\n• Rekayasa perangkat lunak dan pengembangan aplikasi\n• Sains data dan kecerdasan buatan",
        motivation =
            "Aplikasi ini dikembangkan untuk memenuhi tugas IRK Library II: StatsDroid, sekaligus sebagai wadah eksplorasi mandiri dalam pengembangan aplikasi berbasis Android.",
        assistantExpectation =
            "Visi:\nMembantu mahasiswa dalam memahami materi, mengarahkan proses pembelajaran, serta memberikan penilaian yang objektif pada mata kuliah Ilmu dan Rekayasa Komputasi. Selain itu, turut membantu dosen dalam mendukung pelaksanaan mata kuliah yang diampu agar kegiatan pembelajaran dapat berjalan dengan baik, terstruktur, dan efektif.\n\nMisi:\n1. Membantu mahasiswa memahami proses berpikir dalam menyelesaikan permasalahan, bukan hanya memberikan jawaban akhir.\n2. Memberikan arahan dan umpan balik yang jujur, objektif, serta membangun terhadap tugas maupun hasil pekerjaan mahasiswa.\n3. Mendorong mahasiswa untuk meningkatkan kemampuan berkolaborasi, baik dengan sesama mahasiswa maupun dengan asisten, sehingga tercipta lingkungan pembelajaran yang aktif, suportif, dan saling membantu.",
        contactInformation =
            "GitHub: https://github.com/Alpaomega1136\nInstagram: https://www.instagram.com/raymond_jo1136/",
        githubUrl = "https://github.com/Alpaomega1136",
        repositoryUrl =
            "https://github.com/Alpaomega1136/IRK-Library-II-StatsDroid---IRK",
        linkedInUrl = null,
        instagramUrl = "https://www.instagram.com/raymond_jo1136/",
    )

    AboutScreen(
        profile = profile,
        onOpenGitHub = {
            openExternalUri(
                uri = profile.githubUrl,
                uriHandler = uriHandler,
            )
        },
        onOpenRepository = {
            openExternalUri(
                uri = profile.repositoryUrl,
                uriHandler = uriHandler,
            )
        },
        onOpenLinkedIn = profile.linkedInUrl?.let { linkedInUrl ->
            {
                openExternalUri(
                    uri = linkedInUrl,
                    uriHandler = uriHandler,
                )
            }
        },
        onOpenInstagram = profile.instagramUrl?.let { instagramUrl ->
            {
                openExternalUri(
                    uri = instagramUrl,
                    uriHandler = uriHandler,
                )
            }
        },
    )
}

private fun openExternalUri(
    uri: String,
    uriHandler: UriHandler,
) {
    runCatching {
        uriHandler.openUri(uri)
    }
}
