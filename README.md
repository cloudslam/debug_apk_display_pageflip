# debug_apk_display_pageflip

Minimal Android debug APK that fills the screen with a black background and moves a white line for display/pageflip testing.

## Behavior

- Starts in full-screen immersive mode.
- Draws a black background with a 6 px white vertical line.
- Moves the line 12 px on every display vsync callback, targeting 60 Hz frame submissions.
- Requests a 60 Hz refresh rate from the window when supported by the device.
- Tapping the screen advances the line by one frame immediately.

## Build

```sh
./gradlew assembleDebug
```

This repository intentionally does not include binary Gradle wrapper artifacts, so `./gradlew` delegates to an installed `gradle` executable. Install Gradle before building if it is not already available.

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
