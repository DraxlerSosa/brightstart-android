# BrightStart (Android App)

Native Android client for **BrightStart**, an education-initiative management platform. The app gives administrators and volunteers a mobile interface to manage students, attendance, programs, sessions, donations, volunteers, and performance records, backed by the [BrightStart PHP API](https://github.com/<your-username>/brightstart-web).

## Features

- **Authentication** — login and registration, with account approval handled server-side
- **Dashboard** — at-a-glance overview of key program metrics
- **Students** — view and manage student records
- **Enrolments** — track which students are enrolled in which programs
- **Attendance** — record and review attendance per session
- **Programs & Sessions** — manage program offerings and scheduled sessions
- **Volunteers** — manage volunteer records
- **Donations** — log and track donations
- **Performance** — record student/program performance data
- **Reports** — generated summaries across the above
- **Admin Users** — manage administrator accounts

## Tech Stack

- **Language:** Kotlin
- **UI:** Android Views with ViewBinding, Navigation Component, Fragments
- **Networking:** Retrofit2 + OkHttp (with logging interceptor)
- **Async:** Kotlin Coroutines
- **Images:** Glide
- **Charts:** MPAndroidChart
- **Local storage:** Jetpack DataStore (Preferences)
- **Min SDK:** 26 · **Target/Compile SDK:** 34

## Project Structure
app/src/main/java/com/brightstart/app/

├── data/

│   ├── api/          # Retrofit service + client setup

│   ├── models/        # Data models

│   └── repository/    # Repository layer

├── ui/

│   ├── auth/           # Login / Register

│   ├── home/           # Home + hero slider

│   ├── dashboard/       # Dashboard

│   ├── students/        # Student list + adapter

│   ├── enrolments/

│   ├── attendance/

│   ├── programs/

│   ├── sessions/

│   ├── volunteers/

│   ├── donations/

│   ├── performance/

│   ├── reports/

│   └── adminuser/

├── utils/               # SessionManager, extensions

├── MainActivity.kt

└── BrightStartApp.kt


## Setup

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- A running instance of the [BrightStart backend API](https://github.com/<your-username>/brightstart-web)

### Configuration

The app's base URL is defined in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://your-api-host/brightstart/api/\"")
```

Update this to point at wherever your backend is hosted (a local PHP server, an ngrok tunnel, or a deployed host), then rebuild.

### Build & Run

```bash
git clone https://github.com/<your-username>/brightstart-android.git
cd brightstart-android
```

Open the project in Android Studio, let Gradle sync, then run on an emulator or device (`minSdk` 26+).

Alternatively, from the command line:

```bash
./gradlew assembleDebug
```

## Notes

- This repo intentionally excludes build output (`app/build/`), Gradle caches (`.gradle/`), and `local.properties` — these are environment-specific and regenerate automatically when the project is opened/built.
- The app expects the backend API to be reachable at the configured `BASE_URL`; without it running, network-dependent screens won't load data.

## License

No license specified yet — add one (e.g. MIT) if you intend to make this publicly reusable.
