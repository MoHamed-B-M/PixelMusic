# PixelMusic 🎵

A simple, smooth, and expressive music player built with Kotlin and Jetpack Compose, following Material 3 design guidelines with dynamic color support. PixelMusic scans your device for audio files and provides a clean interface to play, pause, skip, and seek through your favourite tracks.

![App Screenshot Placeholder](screenshots/song_list.png)
*Song list with album art and now playing indicator*

![Player Bar](screenshots/player_bar.png)
*Bottom player bar with playback controls and seek slider*

---

## ✨ Features

- **Scan local audio files** – reads music from device storage (requires permission)
- **Material 3 expressive theming** – dynamic color, large typography, rounded shapes
- **Modern media playback** – powered by Media3 ExoPlayer
- **Playback controls** – play/pause, skip next/previous, seek via slider
- **Now playing indicator** – highlights the currently playing song in the list
- **Album art display** – loads embedded album art where available
- **Permission handling** – requests necessary storage permissions gracefully

---

## 🛠️ Built With

- **Kotlin** – 100% Kotlin codebase
- **Jetpack Compose** – declarative UI toolkit
- **Material 3** – expressive theming with dynamic color
- **Media3 ExoPlayer** – robust media playback
- **Coil** – image loading for album art
- **Accompanist Permissions** – simplified runtime permission handling
- **Gradle** – build automation
- **GitHub Actions** – continuous integration

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Android SDK** – with `ANDROID_HOME` environment variable set
- **Android device or emulator** running API level 24+ (Android 7.0)
- **Gradle** (optional, the project includes the Gradle wrapper)

---

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/pixelmusic.git
cd pixelmusic
