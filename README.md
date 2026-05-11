# GokulaHealth 🐄
**Digital Health Passport for Cattle**

GokulaHealth is a comprehensive Android application designed to help dairy farmers manage their cattle's health, milk production, and vaccination schedules. It leverages modern AI and machine learning to simplify livestock management.

## 🚀 Features

### 1. 🤖 AI Health Assistant
*   Powered by **Groq (Llama 3.3)**.
*   Provides expert advice on cattle diseases, nutrition, and milk yield optimization.
*   Available 24/7 to answer farming-related queries.

### 2. 🏷️ Smart Tag Scanner
*   Uses **ML Kit Text Recognition** to scan ear tag IDs.
*   Eliminates manual entry errors.
*   Features a professional scanning overlay with real-time feedback.

### 3. 🥛 Milk Diary & Analytics
*   Track daily milk production (morning and evening).
*   Visualize production trends using **MPAndroidChart**.
*   Calculate monthly averages and total yields.

### 4. 💉 Vaccination Management
*   Schedule and track vaccinations (FMD, BQ, HS, etc.).
*   Automated reminders via **Notification Channels**.
*   Maintain a complete history of health events for each animal.

### 5. 🔐 Secure & Synchronized
*   **Firebase Authentication** for secure farmer profiles.
*   **Room Database** for offline-first performance.
*   Real-time cloud synchronization.

## 🛠️ Technical Stack
*   **Language**: Kotlin
*   **UI**: Material 3 / View Binding
*   **SDK**: Android 16 (API 36) compatible
*   **Architecture**: MVVM / Clean Architecture principles
*   **Libraries**: CameraX, Room, Firebase, ML Kit, MPAndroidChart, Glide, OkHttp/Gson.

## 📦 Setup & Installation

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Dha-lab/GokulaHealth.git
    ```
2.  **API Keys**:
    *   Add your `google-services.json` to the `app/` directory.
    *   Add `GROQ_API_KEY=your_key_here` to your `local.properties` file.
3.  **Firebase Setup**:
    *   Enable **Email/Password** authentication in the Firebase Console.
    *   Enable Firestore/Storage if required.
4.  **Build**:
    *   Open in Android Studio (Ladybug or later).
    *   Sync Gradle and run on an emulator or physical device.

## 🛡️ Android 16 Compatibility
This project is optimized for the latest Android standards:
*   **16 KB Page Alignment**: Native libraries are configured for compatibility with high-performance devices.
*   **API 36 Support**: Compiled and targeted against the newest Android SDK.

---
Developed with ❤️ for the farming community.
