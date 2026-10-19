# BrightStart (Android App)

Native Android client for **BrightStart**, an education-initiative management platform. The app gives administrators and volunteers a mobile interface to manage students, attendance, programs, sessions, donations, volunteers, and performance records, backed by the [BrightStart PHP API](https://github.com/DraxlerSosa/brightstart-web).

## Features

- **Authentication** — registration, login with token-based sessions and Google single sign-on, account approval handled server-side
- **Settings** — edit profile, change password, view app/server info, log out
- **Help & Support** — in-app FAQ
- **Input validation** — every form checks its input and explains problems instead of crashing
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
- **Networking:** Retrofit2 + OkHttp (auth and error interceptors)
- **Sign-in:** Google Sign-In via Credential Manager
- **Testing:** JUnit4 + MockWebServer, GitHub Actions CI
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
- The deployed [BrightStart backend API](https://github.com/DraxlerSosa/brightstart-web) (see its `DEPLOY_AZURE.md`)

### Configuration

Two values are read from `gradle.properties` (or `-P` flags) and fall back to defaults in `app/build.gradle.kts`:

```properties
BASE_URL=https://<your-app>.azurewebsites.net/api/
GOOGLE_WEB_CLIENT_ID=<web-oauth-client-id>.apps.googleusercontent.com
```

See `docs/GOOGLE_SIGNIN_SETUP.md` for the Google steps. Demo accounts (password `BrightStart@2026`) come from the backend's `database/seed.sql`.

### Build & Run

```bash
git clone https://github.com/DraxlerSosa/brightstart-android.git
cd brightstart-android
```

Open the project in Android Studio, let Gradle sync, then run on a phone (USB debugging on) or an emulator (`minSdk` 26+).

Alternatively, from the command line:

```bash
./gradlew assembleDebug
```

### Tests

```bash
./gradlew testDebugUnitTest
```

Unit tests cover the input validators, the auth/error network interceptors (MockWebServer) and API model parsing. GitHub Actions (`.github/workflows/android.yml`) runs the tests and builds the APK on every push.

## Documentation

- `docs/USER_GUIDE.md` - how to use the app
- `docs/GOOGLE_SIGNIN_SETUP.md` - enabling Google single sign-on
- `docs/AZURE_DEVOPS.md` - Azure DevOps set-up and evidence
- `docs/DEMO_VIDEO_CHECKLIST.md` - what to show in the demo video

## Notes

- This repo intentionally excludes build output (`app/build/`), Gradle caches (`.gradle/`), and `local.properties` — these are environment-specific and regenerate automatically when the project is opened/built.
- The app expects the backend API to be reachable at the configured `BASE_URL`; without it, network-dependent screens won't load data. Only HTTPS is allowed (cleartext traffic is disabled).

## License

No license specified yet — add one (e.g. MIT) if you intend to make this publicly reusable.
