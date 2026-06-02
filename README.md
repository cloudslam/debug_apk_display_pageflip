# debug_apk_display_pageflip

Minimal Android debug APK that fills the screen and cycles the display through red, yellow, and blue frames.

## Behavior

- Starts in full-screen immersive mode.
- Automatically switches to the next color on every display vsync callback, targeting 60 Hz color submissions: red → yellow → blue → red.
- Requests a 60 Hz refresh rate from the window/view when supported by the device.
- Tapping the screen advances immediately to the next color.

## Build

```sh
./gradlew assembleDebug
```

This repository intentionally does not include binary Gradle wrapper artifacts, so `./gradlew` delegates to an installed `gradle` executable. Install Gradle before building if it is not already available.

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
