package com.photos.model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages data persistence and retrieval for the application.
 * Handles serialization of albums and photos.
 * Singleton pattern ensures one instance throughout the app.
 */
public class DataManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "photos_data.dat";

    private static DataManager instance;
    private static Context appContext;

    private List<Album> albums;

    /**
     * Private constructor for singleton pattern.
     */
    private DataManager() {
        albums = new ArrayList<>();
    }

    /**
     * Initializes the DataManager with application context.
     * Must be called once at app startup.
     *
     * @param context The application context
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Gets the singleton instance of DataManager.
     *
     * @return The DataManager instance
     */
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            instance.loadData();
        }
        return instance;
    }

    /**
     * Loads data from storage.
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        if (appContext == null) return;

        try (FileInputStream fis = appContext.openFileInput(DATA_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            albums = (List<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // File doesn't exist or error reading - start fresh
            albums = new ArrayList<>();
        }
    }

    /**
     * Saves data to storage.
     */
    public void saveData() {
        if (appContext == null) return;

        try (FileOutputStream fos = appContext.openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all albums.
     *
     * @return List of all albums
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds a new album.
     *
     * @param album The album to add
     * @return true if added, false if album with same name exists
     */
    public boolean addAlbum(Album album) {
        if (getAlbumByName(album.getName()) != null) {
            return false;
        }
        albums.add(album);
        saveData();
        return true;
    }

    /**
     * Removes an album.
     *
     * @param album The album to remove
     * @return true if removed, false if not found
     */
    public boolean removeAlbum(Album album) {
        boolean removed = albums.remove(album);
        if (removed) {
            saveData();
        }
        return removed;
    }

    /**
     * Gets an album by name (case-insensitive).
     *
     * @param name The album name
     * @return The album if found, null otherwise
     */
    public Album getAlbumByName(String name) {
        for (Album album : albums) {
            if (album.getName().equalsIgnoreCase(name)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Renames an album.
     *
     * @param album   The album to rename
     * @param newName The new name
     * @return true if renamed, false if new name already exists
     */
    public boolean renameAlbum(Album album, String newName) {
        if (getAlbumByName(newName) != null && !album.getName().equalsIgnoreCase(newName)) {
            return false;
        }
        album.setName(newName);
        saveData();
        return true;
    }

    /**
     * Gets all unique tag values for a given tag type across all albums.
     *
     * @param tagType The tag type (person or location)
     * @return Set of unique tag values
     */
    public Set<String> getAllTagValues(String tagType) {
        Set<String> values = new HashSet<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                values.addAll(photo.getTagValues(tagType));
            }
        }
        return values;
    }

    /**
     * Gets tag values that start with the given prefix.
     *
     * @param tagType The tag type
     * @param prefix  The prefix to match
     * @return List of matching tag values
     */
    public List<String> getTagValuesWithPrefix(String tagType, String prefix) {
        Set<String> allValues = getAllTagValues(tagType);
        List<String> matches = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (String value : allValues) {
            if (value.toLowerCase().startsWith(lowerPrefix)) {
                matches.add(value);
            }
        }
        return matches;
    }

    /**
     * Searches for photos matching a single tag (prefix match, case-insensitive).
     *
     * @param tagType     The tag type
     * @param valuePrefix The value prefix to match
     * @return List of matching photos with their album info
     */
    public List<PhotoResult> searchByTag(String tagType, String valuePrefix) {
        List<PhotoResult> results = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.hasTagWithPrefix(tagType, valuePrefix)) {
                    results.add(new PhotoResult(photo, album));
                }
            }
        }
        return results;
    }

    /**
     * Searches for photos matching two tags with AND (both must match).
     *
     * @param tagType1     First tag type
     * @param valuePrefix1 First value prefix
     * @param tagType2     Second tag type
     * @param valuePrefix2 Second value prefix
     * @return List of matching photos
     */
    public List<PhotoResult> searchByTagsAnd(String tagType1, String valuePrefix1,
                                              String tagType2, String valuePrefix2) {
        List<PhotoResult> results = new ArrayList<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (photo.hasTagWithPrefix(tagType1, valuePrefix1) &&
                        photo.hasTagWithPrefix(tagType2, valuePrefix2)) {
                    results.add(new PhotoResult(photo, album));
                }
            }
        }
        return results;
    }

    /**
     * Searches for photos matching two tags with OR (either must match).
     *
     * @param tagType1     First tag type
     * @param valuePrefix1 First value prefix
     * @param tagType2     Second tag type
     * @param valuePrefix2 Second value prefix
     * @return List of matching photos
     */
    public List<PhotoResult> searchByTagsOr(String tagType1, String valuePrefix1,
                                             String tagType2, String valuePrefix2) {
        List<PhotoResult> results = new ArrayList<>();
        Set<String> addedUris = new HashSet<>();
        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                if (!addedUris.contains(photo.getUriString()) &&
                        (photo.hasTagWithPrefix(tagType1, valuePrefix1) ||
                                photo.hasTagWithPrefix(tagType2, valuePrefix2))) {
                    results.add(new PhotoResult(photo, album));
                    addedUris.add(photo.getUriString());
                }
            }
        }
        return results;
    }

    /**
     * Moves a photo from one album to another.
     *
     * @param photo       The photo to move
     * @param sourceAlbum The source album
     * @param targetAlbum The target album
     * @return true if moved successfully, false if photo already exists in target
     */
    public boolean movePhoto(Photo photo, Album sourceAlbum, Album targetAlbum) {
        if (targetAlbum.containsPhoto(photo)) {
            return false;
        }
        sourceAlbum.removePhoto(photo);
        targetAlbum.addPhoto(photo);
        saveData();
        return true;
    }

    /**
     * Helper class to hold photo search results with album context.
     */
    public static class PhotoResult implements Serializable {
        private static final long serialVersionUID = 1L;
        public Photo photo;
        public Album album;

        public PhotoResult(Photo photo, Album album) {
            this.photo = photo;
            this.album = album;
        }
    }
}

