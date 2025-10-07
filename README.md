# Secure Alarm Clock

Secure Alarm Clock is an Android application tailored to the specification in `ALARM_APP_SPECIFICATION.md`. It lets users create highly secure alarms that can only be dismissed by their creator after successful authentication.

## Features
- Alarm scheduling backed by `AlarmManager` and a foreground `AlarmService` to keep alarms alive even when the app is backgrounded.
- Multiple authentication methods per alarm: PIN, password, pattern, or fingerprint.
- Encrypted credential storage via Android Keystore with AES-GCM.
- Secure dismissal workflow that launches a biometric or passcode prompt before stopping the alarm sound.
- Persistent notification and full-screen overlay that survive task switching or attempted swipes.
- Automatic guard worker that restarts the alarm service if the system kills it while an alarm is still active.
- Security alert receiver that raises high-priority notifications after repeated failed dismissal attempts.
- Accessibility-aware Compose UI with descriptive semantics for time selection, dismissal, and snooze controls.
- Snooze support with configurable snooze duration per alarm (default 10 minutes).
- Room-powered local database and repository layer for alarm persistence.

## Project Structure
```
app/
  src/main/java/com/example/securealarm/
    data/                  // Room entities, DAO, repository
    security/              // Keystore encryption & credential hashing
    service/               // Foreground alarm service and scheduling helpers
    receiver/              // Alarm and boot receivers
    ui/                    // Compose-based UI layers (main, setup, overlay, dismissal)
  src/main/res/            // Resources (themes, icons, strings)
```

## Getting Started
1. **Install Android SDK** – ensure that the Android SDK (API level 34 or higher) is available on your machine.
2. **Configure `local.properties`** – point `sdk.dir` to your SDK installation, for example:
   ```
   sdk.dir=/path/to/Android/Sdk
   ```
3. **Build the project**
   ```bash
   ./gradlew :app:assembleDebug
   ```
4. **Run on device/emulator** – install the generated APK or launch from Android Studio.

> **Note:** The Codespaces container used to author this project does not include the Android SDK by default. The build command above was executed, but it fails in the container until the SDK is installed and `local.properties` is configured.

## Usage Tips
- To enable overlay behaviour on Android 10+, ensure the `SYSTEM_ALERT_WINDOW` permission is granted.
- Fingerprint authentication requires enrolled biometrics and a device that supports `BiometricPrompt`.
- PINs must be 4–6 digits; passwords require at least six characters; patterns should be brief textual descriptions (e.g., "ULDR").

## Testing
- **Unit tests:** `AlarmTimeCalculatorTest` validates repeat scheduling logic. Run with `./gradlew test` once the Android SDK is configured locally.
- **Instrumentation tests:** `AuthenticationManagerInstrumentedTest` exercises encrypted credential creation on device/emulator hardware. Execute with `./gradlew connectedAndroidTest` after connecting a device.

> Tests were authored but not executed in the Codespaces container because the Android SDK is unavailable there.

## License
This project is licensed under the MIT License. See `LICENSE` for details.
