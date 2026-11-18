# Remapper

<p align="center">
  <strong>An ultra-lightweight, battery-efficient Android hardware button remapper built with Kotlin and Google Material Expressive 3.</strong>
</p>

---

## ⚡ Overview

**Remapper** allows you to repurpose hardware buttons on any modern Android device (non-rooted and rooted alike) without battery drain or unnecessary background bloat. It leverages Android's high-efficiency `AccessibilityService` key event filtering and native action intents to execute quick panel toggles, launch apps, or trigger deep app actions instantly.

Remapper follows Google's **Material Expressive 3** design philosophy, adopting the system's dynamic wallpaper-based color palette (**Material You / Monet**) and seamlessly respecting device-wide Light and Dark modes.

---

## 🎛️ Supported Button Combinations

1. **Double press the Home button** (`DOUBLE_PRESS_HOME`)  
   *Interception for hardware/capacitive or physical home buttons.*
2. **Single press Volume Up + Volume Down** (`VOLUME_UP_PLUS_DOWN`)  
   *Simultaneous dual-volume key combo with automatic volume HUD suppression.*
3. **Single press Volume Up + Power button** (`VOLUME_UP_PLUS_POWER_SINGLE`)  
   *Quick simultaneous press and release.*
4. **Hold Volume Up + Power button** (`VOLUME_UP_PLUS_POWER_HOLD`)  
   *Continuous 0.5s hold.*
5. **Double press Volume Up + Power button** (`VOLUME_UP_PLUS_POWER_DOUBLE`)  
   *Rapid two-tap sequence within 380ms.*

---

## 🚀 Remap Functionalities

All functionality lists in Remapper are **strictly sorted alphabetically** for effortless discovery.

### 1. Quick Panel & System Actions (Alphabetical Order)
| Action | Execution Method | Non-Root Behavior |
| :--- | :--- | :--- |
| **Airplane Mode** | `Settings.Global` / Intent | Direct toggle via ADB or opens Airplane Mode settings |
| **Auto-Rotate Screen** | `Settings.System` | **Direct instant toggle** (requires standard Write Settings permission) |
| **Battery Saver** | `Settings.Global` / Intent | Direct toggle via ADB or opens Battery Saver settings |
| **Bluetooth** | `BluetoothAdapter` / Panel | Direct toggle (API ≤ 32) or native Bluetooth Quick Settings panel |
| **Camera Access (Mute)** | Sensor Privacy / Intent | Opens native camera privacy toggle |
| **Dark Mode** | `UiModeManager` | **Direct instant system-wide theme toggle** (Light ↔ Dark) |
| **Data Saver** | Intent / ADB | Toggle via ADB or opens Data Saver settings |
| **Do Not Disturb (DND)** | `NotificationManager` | **Direct instant toggle** (requires standard DND policy access) |
| **Extra Dim** | `Settings.Secure` / Intent | Direct toggle via ADB or opens Display settings |
| **Eye Protection (Night Light)** | `Settings.Secure` / Intent | Direct toggle via ADB or opens Night Display settings |
| **Flashlight (Torch)** | `CameraManager` | **Direct instant toggle** with real-time hardware status sync |
| **Location (GPS)** | `Settings.Secure` / Intent | Direct toggle via ADB or opens Location settings |
| **Lock Screen** | `AccessibilityService` | **Direct instant screen lock** |
| **Microphone Access (Mute)** | `AudioManager` | **Direct instant microphone mute/unmute** |
| **Mobile Data** | Intent / Slices | Opens native cellular connectivity slice |
| **NFC** | `Settings.Global` / Intent | Direct toggle via ADB or opens NFC settings |
| **Notifications Panel** | `AccessibilityService` | **Directly expands system notification shade** |
| **Personal Hotspot** | Intent | Opens Hotspot & Tethering configuration |
| **Power Menu** | `AccessibilityService` | **Directly shows system power & restart dialog** |
| **Quick Settings Panel** | `AccessibilityService` | **Directly pulls down Quick Settings** |
| **Ringer Mode: Cycle** | `AudioManager` | **Direct instant cycling** (Normal ➔ Vibrate ➔ Silent) |
| **Ringer Mode: Mute** | `AudioManager` | **Direct instant silent mode** |
| **Ringer Mode: Vibrate Only** | `AudioManager` | **Direct instant vibrate-only mode** |
| **Screenshot** | `AccessibilityService` | **Direct instant screenshot capture** |
| **Split Screen** | `AccessibilityService` | **Direct instant multi-window split screen toggle** |
| **Volume Dialog** | `AudioManager` | **Directly summons system volume sliders** |
| **Wi-Fi** | Intent / `Settings.Global` | Direct toggle via ADB or opens native floating Internet connectivity panel |

### 2. Installed Applications (Alphabetical Order)
- Lists all launchable apps on the device sorted from A to Z with real-time search.
- Displays app name, package identifier, and system icon.

### 3. Native App Actions & Deep Shortcuts (Alphabetical Order)
- **Battery Settings**: Direct shortcut to device battery stats.
- **Camera: Take Photo**: Direct camera launch in photo mode.
- **Camera: Record Video**: Direct camera launch in video recording mode.
- **Clock: Set Alarm**: Direct shortcut into alarm clock manager.
- **Display Settings**: Direct shortcut to screen brightness & resolution.
- **Google Assistant / Voice Search**: Instant voice command prompt.
- **Network & Internet Settings**: Fast jump to network connectivity.
- **Privacy & Permission Settings**: Direct shortcut to sensor permissions.
- **Sound & Vibration Settings**: Direct shortcut to audio settings.
- **Storage Settings**: Direct shortcut to storage manager.
- **WhatsApp: Direct Camera**: Instantly opens WhatsApp's quick capture camera.

---

## 🔒 Optional Non-Root ADB Setup (For Direct Background Toggles)

Certain privileged toggles (Airplane Mode, Battery Saver, GPS Location, NFC, Extra Dim, Night Light) are restricted by Android OS from being toggled silently in the background by third-party apps.

Remapper handles this gracefully:
- **Without ADB**: Remapper instantly displays the official native floating panel/slice (e.g. `Settings.Panel.ACTION_INTERNET_CONNECTIVITY`), allowing a 1-tap flip without root.
- **With ADB (Optional)**: If you run this single command once from a PC or on-device wireless debugging tool (such as Shizuku or LADB), Remapper gains permission to toggle them **silently in the background**:

```bash
adb shell pm grant com.remapper.app android.permission.WRITE_SECURE_SETTINGS
```

---

## 🎨 Design & Google Material Expressive 3

- **Material You / Dynamic Theming**: Uses `DynamicColors.applyToActivitiesIfAvailable()` to adapt to wallpaper palettes on Android 12+.
- **Expressive Shapes**: 28dp (`ExtraLarge`) hero cards, 20dp (`Large`) trigger cards, pill badges, and smooth container transforms.
- **System Dark & Light Mode**: Strictly conforms to the system theme without unnecessary custom theme toggles.
- **Haptic Feedback**: Configurable crisp haptic vibration on key combination triggers.

---

## 🛠️ Building & Developing

### Requirements
- Android SDK 34+
- Java Development Kit (JDK 17)
- Gradle 8.7+

### Build Debug APK
```bash
./gradlew assembleDebug
```
The APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

### Build Release APK
```bash
./gradlew assembleRelease
```

---

## 📄 License
Licensed under the [Apache License, Version 2.0](LICENSE).
