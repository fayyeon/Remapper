# Remapper

Simple button remapper for Android. No root required.

---

## Supported Button & Combinations

- Double press the Recent Apps, Home, and Recent Apps button
- Single press Volume Up + Volume Down
- Single press Volume Up + Power button
- Hold Volume Up + Power button
- Double press Volume Up + Power button

---

## Remap Functionalities

- 🔧 Quick Panel Toggles & System Actions
- 📱 Installed Applications
- 🏃🏻 App Activities

---

## Optional ADB Setup (For Direct Background Toggles)

Certain privileged toggles (Airplane Mode, Battery Saver, GPS Location, NFC, Extra Dim, Night Light, etc.) are restricted by Android from being toggled silently in the background by 3rd-party apps.

Remapper handles this gracefully:
- **Without ADB**: Remapper displays the native floating panel or settings panel (e.g. `Settings.Panel.ACTION_INTERNET_CONNECTIVITY`), allowing a 1-tap flip without.
- **With ADB (Optional)**: If you run this single command once from a PC or on-device wireless debugging tool (such as Shizuku), Remapper gains permission to toggle them silently in the background:

```bash
adb shell pm grant com.remapper.app android.permission.WRITE_SECURE_SETTINGS
```

---

## Building & Developing

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

## License
Licensed under the [GNU General Public License v3.0](LICENSE).
