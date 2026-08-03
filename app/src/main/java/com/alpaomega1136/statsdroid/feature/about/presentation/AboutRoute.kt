package com.alpaomega1136.statsdroid.feature.about.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler

@Composable
fun AboutRoute() {
    val uriHandler = LocalUriHandler.current

    val profile = AboutProfile(
        name = "Alpaomega1136",
        studentId = "13524059",
        role = "Mahasiswa Informatika • Kandidat Asisten Laboratorium IRK",
        description =
            "Saya adalah mahasiswa Informatika yang tertarik pada pengembangan perangkat lunak, algoritma, matematika, dan visualisasi interaktif. Melalui StatsDroid, saya berusaha mengubah konsep probabilitas dan statistika menjadi pengalaman belajar yang lebih mudah diamati dan dipahami.",
        motivation =
            "StatsDroid dibuat karena probabilitas dan statistika sering dipelajari hanya melalui rumus dan tabel statis. Aplikasi ini menyediakan kalkulasi, visualisasi, serta simulasi interaktif agar pengguna dapat melihat hubungan antara parameter, bentuk distribusi, nilai probabilitas, dan keputusan pengujian hipotesis. Proyek ini juga menjadi sarana bagi saya untuk menerapkan pengembangan Android yang modular, dapat diuji, dan mudah dikembangkan lebih lanjut.",
        assistantVision =
            "Sebagai asisten IRK, saya berharap dapat membantu praktikan membangun pemahaman konsep sebelum sekadar menghafal prosedur. Saya ingin menciptakan sesi praktikum yang terbuka terhadap pertanyaan, memiliki penjelasan bertahap, dan menghubungkan teori matematika dengan implementasi program yang dapat diamati secara langsung.",
        assistantMissions = listOf(
            "Menjelaskan materi secara bertahap, mulai dari intuisi, visualisasi, rumus, hingga implementasi.",
            "Mendorong praktikan menghasilkan kode yang benar, mudah dibaca, modular, dan dapat dipelihara.",
            "Membangun suasana diskusi yang ramah agar praktikan tidak ragu bertanya atau meminta penjelasan ulang.",
            "Memberikan umpan balik yang membantu praktikan memahami alasan di balik kesalahan dan cara memperbaikinya.",
        ),
        contactSummary =
            "Informasi kontak dan portofolio dapat diakses melalui profil GitHub serta repository StatsDroid berikut.",
        githubUrl = "https://github.com/Alpaomega1136",
        repositoryUrl =
            "https://github.com/Alpaomega1136/IRK-Library-II-StatsDroid---IRK",
        linkedInUrl = null,
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
