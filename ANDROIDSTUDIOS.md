# Android Studio Setup and Usage Guide

This guide will walk you through downloading, installing, and using Android Studio to build and run the Photos Android application.

## Table of Contents
1. [Downloading Android Studio](#downloading-android-studio)
2. [Installing Android Studio](#installing-android-studio)
3. [First Launch Setup](#first-launch-setup)
4. [Opening the Project](#opening-the-project)
5. [Setting Up an Emulator](#setting-up-an-emulator)
6. [Building and Running the App](#building-and-running-the-app)
7. [Common Issues and Solutions](#common-issues-and-solutions)
8. [Project Structure Overview](#project-structure-overview)

---

## Downloading Android Studio

1. Go to the official Android Studio download page:
   **https://developer.android.com/studio**

2. Click the **"Download Android Studio"** button

3. Accept the Terms and Conditions

4. The download will start automatically (approximately 1-2 GB)

---

## Installing Android Studio

### Windows Installation

1. Run the downloaded `.exe` file
2. Follow the setup wizard:
   - Click **Next** on the Welcome screen
   - Select components (keep all defaults checked):
     - ✅ Android Studio
     - ✅ Android Virtual Device
   - Choose installation location (default is fine)
   - Click **Install**
3. Wait for installation to complete
4. Click **Finish** to launch Android Studio

### macOS Installation

1. Open the downloaded `.dmg` file
2. Drag **Android Studio** to the **Applications** folder
3. Open Android Studio from Applications
4. If prompted about security, go to System Preferences → Security & Privacy → click "Open Anyway"

### Linux Installation

1. Extract the downloaded `.tar.gz` file:
   ```bash
   tar -xzf android-studio-*.tar.gz
   ```
2. Move to opt directory (optional):
   ```bash
   sudo mv android-studio /opt/
   ```
3. Run the studio:
   ```bash
   /opt/android-studio/bin/studio.sh
   ```

---

## First Launch Setup

When you first launch Android Studio, it will guide you through initial setup:

### 1. Import Settings
- Select **"Do not import settings"** (if this is a fresh install)
- Click **OK**

### 2. Setup Wizard
- Click **Next** on the Welcome screen
- Choose **Standard** installation type (recommended)
- Click **Next**

### 3. Select UI Theme
- Choose **Light** or **Dark** theme (personal preference)
- Click **Next**

### 4. Verify Settings
- Review the components to be downloaded:
  - Android SDK
  - Android SDK Platform
  - Android Virtual Device
- Click **Next**

### 5. Accept Licenses
- Click on each license and select **Accept**
- Click **Finish**

### 6. Download Components
- Wait for the SDK components to download (may take 10-20 minutes)
- Click **Finish** when complete

---

## Opening the Project

1. On the Android Studio Welcome screen, click **"Open"**

2. Navigate to the `android-photos` folder:
   ```
   C:\Users\maima\cs213\android-photos
   ```

3. Click **OK** to open the project

4. **Wait for Gradle sync** - Android Studio will automatically:
   - Download required dependencies
   - Configure the build system
   - Index the project files
   
   This may take 5-10 minutes on first open. Watch the progress bar at the bottom of the window.

5. If prompted to update Gradle or plugins, click **"Update"**

### Gradle Sync Issues

If Gradle sync fails:

1. Go to **File → Invalidate Caches / Restart**
2. Select **"Invalidate and Restart"**
3. After restart, go to **File → Sync Project with Gradle Files**

---

## Setting Up an Emulator

### Creating a Virtual Device

1. Go to **Tools → Device Manager** (or click the phone icon in the toolbar)

2. Click **"Create Device"**

3. **Select Hardware:**
   - Category: **Phone**
   - Device: **Pixel 6** (or **Medium Phone**)
   - Verify the resolution is **1080 x 2400** at **420 dpi**
   - Click **Next**

4. **Select System Image:**
   - Click the **"x86 Images"** tab
   - Find **API 36** (Android VanillaIceCream or latest)
   - If not downloaded, click the **Download** link next to it
   - Wait for download to complete
   - Select the downloaded image
   - Click **Next**

5. **Verify Configuration:**
   - AVD Name: Keep default or rename
   - Startup orientation: Portrait
   - Click **Finish**

### Important Emulator Settings

For optimal performance:
- In Device Manager, click the **Edit** (pencil) icon on your device
- Click **Show Advanced Settings**
- Set **RAM** to at least **2048 MB**
- Enable **Hardware - GLES 2.0** for graphics

---

## Building and Running the App

### 1. Select the Device

In the toolbar at the top:
- Click the device dropdown (shows available devices)
- Select your created emulator (e.g., "Pixel 6 API 36")

### 2. Run the App

Choose one of these methods:
- Click the **green play button** (▶) in the toolbar
- Press **Shift + F10** (Windows/Linux) or **Control + R** (macOS)
- Go to **Run → Run 'app'**

### 3. First Run

- The emulator will start (takes 1-2 minutes on first launch)
- The app will be installed automatically
- Grant the **Photos permission** when prompted

### Build Variants

To switch between debug and release builds:
1. Go to **Build → Select Build Variant**
2. Select **debug** or **release**

---

## Common Issues and Solutions

### Issue: "SDK location not found"

**Solution:**
1. Go to **File → Project Structure**
2. Under **SDK Location**, set the Android SDK path:
   - Windows: `C:\Users\<username>\AppData\Local\Android\Sdk`
   - macOS: `/Users/<username>/Library/Android/sdk`
   - Linux: `/home/<username>/Android/Sdk`

### Issue: "Gradle sync failed"

**Solution:**
1. Check your internet connection
2. Go to **File → Invalidate Caches / Restart**
3. After restart, click **File → Sync Project with Gradle Files**

### Issue: Emulator won't start

**Solution:**
1. Check that virtualization is enabled in BIOS:
   - Intel: Intel VT-x
   - AMD: AMD-V
2. Install HAXM (Intel):
   - Go to **Tools → SDK Manager**
   - Under **SDK Tools** tab, check **Intel x86 Emulator Accelerator (HAXM)**
   - Click **Apply**

### Issue: App crashes on startup

**Solution:**
1. Check **Logcat** window at the bottom for error messages
2. Clean and rebuild:
   - Go to **Build → Clean Project**
   - Then **Build → Rebuild Project**

### Issue: Permission denied for photos

**Solution:**
1. Go to device Settings → Apps → Photos → Permissions
2. Enable **Photos and videos** or **Media** permission

### Issue: "Cannot resolve symbol R"

**Solution:**
1. Go to **Build → Clean Project**
2. Then **Build → Rebuild Project**
3. If still failing, check for XML errors in layout files

---

## Project Structure Overview

```
android-photos/
├── app/
│   ├── src/main/
│   │   ├── java/com/photos/
│   │   │   ├── activity/          # Activity classes
│   │   │   │   ├── MainActivity.java       # Home screen (album list)
│   │   │   │   ├── AlbumActivity.java      # Photo grid view
│   │   │   │   ├── PhotoDisplayActivity.java # Photo viewer + slideshow
│   │   │   │   └── SearchActivity.java     # Tag search
│   │   │   ├── adapter/           # RecyclerView adapters
│   │   │   │   ├── AlbumAdapter.java
│   │   │   │   ├── PhotoAdapter.java
│   │   │   │   └── SearchResultAdapter.java
│   │   │   └── model/             # Data models
│   │   │       ├── Album.java
│   │   │       ├── Photo.java
│   │   │       ├── Tag.java
│   │   │       └── DataManager.java
│   │   ├── res/
│   │   │   ├── layout/            # XML layouts
│   │   │   ├── values/            # Colors, strings, themes
│   │   │   ├── drawable/          # Vector icons
│   │   │   ├── menu/              # Menu definitions
│   │   │   └── mipmap-*/          # App icons
│   │   └── AndroidManifest.xml    # App configuration
│   └── build.gradle.kts           # App-level build config
├── gradle/                        # Gradle wrapper
├── build.gradle.kts               # Project-level build config
└── settings.gradle.kts            # Project settings
```

---

## Useful Shortcuts

| Action | Windows/Linux | macOS |
|--------|---------------|-------|
| Run app | Shift + F10 | Control + R |
| Stop app | Ctrl + F2 | Command + F2 |
| Rebuild project | Ctrl + F9 | Command + F9 |
| Find in files | Ctrl + Shift + F | Command + Shift + F |
| Go to file | Ctrl + Shift + N | Command + Shift + O |
| Format code | Ctrl + Alt + L | Command + Option + L |
| Show Logcat | Alt + 6 | Command + 6 |

---

## Testing the App

Once the app is running:

1. **Create an Album:**
   - Tap the orange **+** button
   - Enter a name and tap **Create**

2. **Add Photos:**
   - Tap on an album to open it
   - Tap the **+** button
   - Select photos from the device gallery

3. **View Photo:**
   - Tap on any photo to view it full-screen
   - Use **<** and **>** buttons for slideshow navigation

4. **Add Tags:**
   - In photo view, tap **Add Tag**
   - Select **person** or **location**
   - Enter the tag value

5. **Search:**
   - From home screen, tap the **search icon**
   - Select search mode (single, AND, OR)
   - Enter tag values with auto-completion
   - Tap **Search**

---

## Additional Resources

- [Android Developers Guide](https://developer.android.com/guide)
- [Android Studio User Guide](https://developer.android.com/studio/intro)
- [Material Design Guidelines](https://material.io/design)
- [Kotlin DSL for Gradle](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

---

## Need Help?

If you encounter issues not covered here:
1. Check the **Logcat** window for detailed error messages
2. Search the error message on [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
3. Consult the [Android Developers documentation](https://developer.android.com/docs)

