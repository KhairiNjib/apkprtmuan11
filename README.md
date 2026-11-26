# Laporan Kerusakan Mesin

## Nama aplikasi

**Laporan Kerusakan Mesin**

## Deskripsi

Aplikasi Android sederhana untuk mencatat dan mengelola laporan kerusakan mesin. Data laporan berisi nama atau kode mesin (`title`) dan keterangan kerusakan (`body`).

Aplikasi terhubung ke REST API MockAPI.io menggunakan Retrofit dan mendukung:

- Melihat daftar dan detail laporan
- Menambahkan laporan
- Mengedit seluruh laporan dengan `PUT`
- Mengubah judul saja dengan `PATCH`
- Menghapus laporan

## Versi yang digunakan

| Komponen | Versi / konfigurasi |
|---|---:|
| Android Gradle Plugin | 9.3.3 |
| Gradle | 9.5.0 |
| JDK Gradle toolchain | 25 |
| Compile SDK | 37 |
| Minimum SDK | 31 |
| Target SDK | 37 |
| Java source/target compatibility | 11 |
| Retrofit | 2.11.0 |
| Gson converter | 2.11.0 |
| Gson | 2.11.0 |
| AndroidX Core KTX | 1.19.1 |
| AndroidX AppCompat | 1.8.0 |
| AndroidX Activity KTX | 1.13.0 |
| ConstraintLayout | 2.2.2 |
| Material Components | 1.14.0 |
| Versi aplikasi | 1.0 (`versionCode` 1) |
| JUnit | 4.13.2 |
| AndroidX Test JUnit | 1.3.0 |
| Espresso | 3.7.0 |

## Susunan direktori

```text
.
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/apkprtmuan11/
│       │   │   ├── data/
│       │   │   │   ├── api/
│       │   │   │   │   ├── ApiService.kt
│       │   │   │   │   └── RetrofitClient.kt
│       │   │   │   └── model/
│       │   │   │       ├── Post.kt
│       │   │   │       └── PostRequest.kt
│       │   │   ├── MainActivity.kt
│       │   │   └── ReportAdapter.kt
│       │   ├── res/
│       │   │   ├── layout/
│       │   │   │   ├── activity_main.xml
│       │   │   │   └── item_report.xml
│       │   │   └── values/
│       │   │       ├── strings.xml
│       │   │       ├── themes.xml
│       │   │       └── colors.xml
│       │   └── AndroidManifest.xml
│       └── test/java/com/example/apkprtmuan11/
│           └── data/model/PostRequestTest.kt
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

Base URL MockAPI diatur di `RetrofitClient.kt`. Resource yang dipakai adalah `posts` dengan field String `title` dan `body`.
