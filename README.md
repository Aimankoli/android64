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

This project was built with assistance from Claude (Anthropic AI Assistant). The development process involved iterative prompting and code generation, with manual integration and testing performed throughout.

### Development Approach

The application was developed incrementally, starting with core data models and gradually building up to the complete UI and functionality. GenAI was primarily used for:
- Generating boilerplate Android code following best practices
- Creating XML layouts with Material Design 3 components
- Implementing RecyclerView adapters and view holders
- Writing activity lifecycle management code
- Debugging and fixing issues (e.g., photo display name extraction from content URIs)

### Example Prompts Used

#### Initial Project Setup
```
"Create an Android photo management app structure. I need to port a JavaFX photos application to Android. 
The app should have albums, photos with tags (person and location), and search functionality. 
Set up the project with proper Gradle configuration, package structure, and basic activities."
```

#### Data Model Implementation
```
"Create a DataManager singleton class for Android that handles serialization of albums and photos. 
It should persist data to internal storage using Java serialization, similar to the JavaFX version. 
Include methods for CRUD operations on albums and photos, and search functionality across all albums."
```

#### UI Layout Generation
```
"Generate XML layouts for an Android photo app using Material Design 3. I need:
- A main activity with RecyclerView showing album cards with thumbnails
- An album activity with 2-column grid of photo thumbnails
- A photo display activity with full-screen image, tag chips, and navigation buttons
- A search activity with radio buttons for single/AND/OR search modes and AutoCompleteTextView for tag values"
```

#### RecyclerView Adapter Creation
```
"Create a RecyclerView adapter for displaying albums. Each item should show:
- Album name
- Photo count
- Thumbnail of the first photo (or placeholder if empty)
- Popup menu for rename/delete actions
Use Material Design components and handle image loading from content URIs."
```

#### Search Functionality
```
"Implement search functionality that:
- Searches across all albums (not just current album)
- Supports prefix matching (case-insensitive)
- Has auto-completion for tag values as user types
- Supports single tag, AND (conjunction), and OR (disjunction) search modes
- Returns PhotoResult objects with photo and album context"
```

#### Tag Management
```
"Create a tag management system where:
- Only 'person' and 'location' tag types are allowed
- Tags are displayed as Material Chips with different colors for each type
- Users can add tags via dialog with spinner for type and AutoCompleteTextView for value
- Tags can be deleted by clicking close icon on chip
- Tags are visible when viewing a photo"
```

#### Photo Display Name Fix
```
"I'm having an issue where photos added from the gallery show up as 'msf:18' instead of the actual filename 'cow'. 
When I drag and drop a file, it shows correctly as 'cow', but when I add from the library, it shows the media ID. 
The getDisplayName() method currently uses uri.getLastPathSegment() which doesn't work for MediaStore URIs. 
Fix this to properly query the ContentResolver to get the actual display name from the media database."
```

### Code Components Generated by AI

- **Model Classes**: `Album.java`, `Photo.java`, `Tag.java`, `DataManager.java` - Initial structure and core logic
- **Activity Classes**: `MainActivity.java`, `AlbumActivity.java`, `PhotoDisplayActivity.java`, `SearchActivity.java` - Complete implementations with lifecycle management
- **Adapter Classes**: `AlbumAdapter.java`, `PhotoAdapter.java`, `SearchResultAdapter.java` - RecyclerView adapters with view holders
- **Layout XML Files**: All `activity_*.xml`, `item_*.xml`, `dialog_*.xml` files - Material Design 3 layouts
- **Resource Files**: `strings.xml`, color definitions, theme configurations
- **Photo Display Name Fix**: Updated `Photo.getDisplayName()` method to use ContentResolver for content URIs

### Code Components Written Manually

- **Integration Logic**: Connecting activities via Intents, passing data between components
- **Error Handling**: Validation for empty inputs, duplicate album names, photo existence checks
- **User Experience Refinements**: Empty state messages, confirmation dialogs, success/error feedback
- **Testing and Debugging**: Manual testing of all features, fixing edge cases
- **Final Polish**: UI adjustments, ensuring all requirements are met

### UI Drawings/Diagrams

No UI drawings or diagrams were uploaded to the chatbot. Development proceeded through text-based prompts describing the desired functionality and structure.

### Notes

All AI-generated code was reviewed, tested, and integrated manually. The final implementation ensures compliance with all project requirements and follows Android development best practices. The codebase maintains consistency in style and architecture throughout.
