# Android Photos Application

A photo management Android application that allows users to organize photos into albums, tag photos with person and location information, and search photos by tags with auto-completion.

## Features

### Home Screen (15 pts)
- Displays list of all albums with names
- Loads album and photo data from previous session
- Floating action button to create new albums
- Search icon to access photo search

### Album Management (25 pts)
- **Create Album**: Tap + button, enter name
- **Open Album**: Tap on album card to view photos
- **Rename Album**: Long press or menu → Rename
- **Delete Album**: Long press or menu → Delete
- Albums display thumbnail of first photo and photo count

### Photo Management (25 pts)
- **Add Photo**: Open album, tap + button, select from gallery
- **Remove Photo**: Long press or view photo → Remove
- **Display Photo**: Tap on photo for full-screen view
- **Slideshow**: Use < and > buttons to navigate between photos
- Photos displayed as thumbnails in a 2-column grid

### Tag Management (15 pts)
- **Add Tag**: In photo view, tap "Add Tag"
- **Tag Types**: Only `person` and `location` are valid
- **Delete Tag**: Tap the X on any tag chip
- Tags displayed as colored chips (green for person, blue for location)
- Tags visible when viewing a photo

### Move Photo (10 pts)
- Move photos between albums
- Available from photo view or long-press menu
- Prevents duplicates in destination album

### Search (30 pts)
- **Single Tag Search**: Search by one tag type and value
- **Conjunction (AND)**: Photos must match both tags
- **Disjunction (OR)**: Photos matching either tag
- **Auto-completion**: Suggestions appear as you type
- **Prefix Matching**: All matches are case-insensitive and support prefix matching
- Searches across all albums

## Technical Details

- **Target API**: 36
- **Min SDK**: 26
- **Screen Size**: 1080 x 2400 @ 420 dpi (Pixel 6 / Medium Phone)
- **Language**: Java only (no Kotlin)
- **Build System**: Gradle with Kotlin DSL

## Project Structure

```
android-photos/
├── app/
│   ├── src/main/
│   │   ├── java/com/photos/
│   │   │   ├── activity/      # UI Activities
│   │   │   ├── adapter/       # RecyclerView Adapters
│   │   │   └── model/         # Data Models
│   │   ├── res/
│   │   │   ├── layout/        # XML Layouts
│   │   │   ├── values/        # Resources
│   │   │   ├── drawable/      # Vector Graphics
│   │   │   └── menu/          # Menu Definitions
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── ANDROIDSTUDIOS.md          # Setup Guide
└── README.md
```

## Data Persistence

- Uses Java serialization to save/load data
- Data stored in app's internal storage (`photos_data.dat`)
- Persists albums, photos (URIs), and tags
- Photos referenced by content URI, not copied

## Permissions

- `READ_MEDIA_IMAGES` (API 33+): Access device photos
- `READ_EXTERNAL_STORAGE` (API 32 and below): Legacy photo access

## Building the App

1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Select a device/emulator (Pixel 6 API 36 recommended)
4. Click Run (or Shift+F10)

See [ANDROIDSTUDIOS.md](ANDROIDSTUDIOS.md) for detailed setup instructions.

## UI Components

- **Material Design 3**: Modern Android UI components
- **RecyclerView**: Efficient scrolling lists
- **CardView**: Elevated content cards
- **ChipGroup**: Tag display and interaction
- **AutoCompleteTextView**: Search suggestions
- **AlertDialog**: User interactions (create, rename, delete)

## Authors

- Built for CS213 Android Project

## GenAI Usage

This project was built with assistance from Claude (Anthropic). The development was done incrementally:

1. **Project Structure**: Generated Android project scaffolding with Kotlin DSL Gradle files
2. **Model Classes**: Created Album, Photo, Tag, and DataManager classes based on existing JavaFX implementation patterns
3. **Layouts**: Generated XML layouts for all activities following Material Design 3 guidelines
4. **Activities**: Created MainActivity, AlbumActivity, PhotoDisplayActivity, and SearchActivity
5. **Adapters**: Generated RecyclerView adapters for albums, photos, and search results
6. **Resources**: Created color schemes, strings, themes, and drawable assets

Manual integration was performed to connect components and test functionality.
