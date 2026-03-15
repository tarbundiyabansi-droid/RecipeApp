# ChefShare — Recipe Sharing Android App

ChefShare is an Android application that allows users to share, browse, and manage recipes. Users can register, log in, post recipes with images and category tags, and view recipes submitted by others.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Firebase Setup](#firebase-setup)
- [Build & Run](#build--run)
- [Architecture](#architecture)
- [Dependencies](#dependencies)
- [Screenshots](#screenshots)

---

## Features

- **User Authentication** — Register and log in with email/password via Firebase Auth
- **Browse Recipes** — Home feed displaying all shared recipes in real time
- **Add Recipe** — Submit a new recipe with a title, description, category (Veg / Non-Veg / Dessert), and multiple images
- **Recipe Detail View** — Full-page view with an image carousel/pager and complete recipe info
- **User Profile** — View and manage your own posted recipes
- **Image Picker** — Pick images from gallery or capture via camera
- **Real-time Data** — Recipes stored and synced via Firebase Realtime Database
- **Splash Screen** — Branded launch screen with auto-navigation

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java |
| Platform | Android (minSdk 24, targetSdk 34) |
| UI | XML Layouts, ViewBinding, DataBinding |
| Navigation | Jetpack Navigation Component |
| Architecture | MVVM (ViewModel + LiveData) |
| Backend | Firebase (Auth, Realtime Database, Firestore) |
| Image Loading | Glide 4.16 |
| Build System | Gradle (AGP 8.13) |

---

## Project Structure

```
app/src/main/java/com/bansi/chefshare/
├── activities/
│   ├── SplashActivity.java          # Launch screen
│   ├── LoginActivity.java           # Email/password login
│   ├── SignupActivity.java          # New user registration
│   ├── MainActivity.java            # Host activity with bottom nav
│   ├── HomeFragment.java            # Recipe feed
│   ├── ProfileFragment.java         # User's own recipes
│   ├── AddRecipeActivity.java       # Create a new recipe
│   └── RecipeDetailActivity.java    # View full recipe details
├── adapters/
│   └── RecipeAdapter.java           # RecyclerView adapter for recipe list
├── models/
│   ├── Recipe.java                  # Recipe data model
│   └── User.java                    # User data model
├── repository/
│   ├── RecipeRepository.java        # Firebase Realtime Database operations
│   └── AuthRepository.java          # Firebase Auth operations
├── viewmodels/
│   ├── RecipeViewModel.java         # Recipe UI state & logic
│   └── AuthViewModel.java           # Auth UI state & logic
└── utils/
    ├── ImageUtils.java              # Bitmap/Base64 image helpers
    └── NetworkUtils.java            # Network connectivity helpers
```

---

## Prerequisites

- Android Studio Hedgehog or later
- JDK 11
- A Firebase project (see Firebase Setup below)
- Android device or emulator running API 24+

---

## Getting Started

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/ChefShare.git
   cd ChefShare
   ```

2. **Open in Android Studio**

   Select **File → Open** and choose the `RecipeApp` folder.

3. **Configure Firebase** (see next section)

4. **Sync Gradle** and run the app.

---

## Firebase Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.

2. Register your Android app with the package name `com.example.recipeapp`.

3. Download the `google-services.json` file and place it in the `app/` directory:

   ```
   app/google-services.json
   ```

4. Enable the following Firebase services in your project:
   - **Authentication** → Email/Password sign-in method
   - **Realtime Database** → Start in test mode (then secure with rules before production)
   - **Cloud Firestore** (optional — referenced in dependencies)

5. Set Realtime Database rules (example for development):

   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```

---

## Build & Run

**Debug build:**

```bash
./gradlew assembleDebug
```

The APK will be output to `app/build/outputs/apk/debug/app-debug.apk`.

**Install directly to a connected device:**

```bash
./gradlew installDebug
```

**Run tests:**

```bash
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

---

## Architecture

The app follows the **MVVM** (Model-View-ViewModel) pattern:

```
UI (Activities / Fragments)
        │  observe LiveData
        ▼
   ViewModel
        │  calls
        ▼
   Repository
        │  reads/writes
        ▼
 Firebase (Auth / Realtime Database)
```

- **Activities & Fragments** observe `LiveData` from ViewModels and update the UI reactively.
- **ViewModels** hold UI state and delegate data operations to Repositories.
- **Repositories** are the single source of truth, abstracting all Firebase calls.

---

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Firebase BOM | 33.10.0 | Firebase dependency management |
| Firebase Auth | — | User authentication |
| Firebase Realtime Database | — | Recipe storage & sync |
| Firebase Firestore | — | Additional data storage |
| Lifecycle ViewModel | 2.8.7 | MVVM architecture support |
| Lifecycle LiveData | 2.8.7 | Observable data holder |
| Navigation Fragment | 2.8.8 | In-app navigation |
| Navigation UI | 2.8.8 | Nav component + UI integration |
| Glide | 4.16.0 | Image loading and caching |
| AppCompat | 1.7.0 | Backward-compatible UI components |
| Material Components | 1.12.0 | Material Design UI widgets |
| ConstraintLayout | 2.2.1 | Flexible UI layout |

---

## Permissions

The app requests the following permissions:

| Permission | Reason |
|------------|--------|
| `INTERNET` | Firebase communication |
| `CAMERA` | Capture recipe images |

---

## Screenshots

> Add screenshots here after running the app.

| Splash | Login | Home Feed | Add Recipe | Recipe Detail |
|--------|-------|-----------|------------|---------------|
| _(screenshot)_ | _(screenshot)_ | _(screenshot)_ | _(screenshot)_ | _(screenshot)_ |

---

## License

This project is for educational purposes. Add your preferred license here.
