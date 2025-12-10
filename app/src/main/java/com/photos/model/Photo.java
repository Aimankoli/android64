package com.photos.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a photo in the application.
 * A photo has a URI path and tags.
 * Caption is derived from the filename.
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uriString;
    private List<Tag> tags;

    /**
     * Constructs a new Photo from a URI string.
     *
     * @param uriString The URI string of the photo
     */
    public Photo(String uriString) {
        this.uriString = uriString;
        this.tags = new ArrayList<>();
    }

    /**
     * Gets the URI string of this photo.
     *
     * @return The URI string
     */
    public String getUriString() {
        return uriString;
    }

    /**
     * Sets the URI string of this photo.
     *
     * @param uriString The URI string to set
     */
    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    /**
     * Gets the URI of this photo.
     *
     * @return The URI
     */
    public Uri getUri() {
        return Uri.parse(uriString);
    }

    /**
     * Gets the display name (filename) of this photo.
     * Uses ContentResolver for content URIs to get the actual display name.
     *
     * @param context The context to use for ContentResolver (can be null)
     * @return The display name
     */
    public String getDisplayName(Context context) {
        Uri uri = Uri.parse(uriString);
        
        // Try to get display name from ContentResolver for content URIs
        if (context != null && "content".equals(uri.getScheme())) {
            try {
                ContentResolver resolver = context.getContentResolver();
                
                // First try OpenableColumns.DISPLAY_NAME (works for most content URIs)
                try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex >= 0) {
                            String displayName = cursor.getString(nameIndex);
                            if (displayName != null && !displayName.isEmpty()) {
                                // Remove extension if present
                                int dotIndex = displayName.lastIndexOf('.');
                                if (dotIndex > 0) {
                                    return displayName.substring(0, dotIndex);
                                }
                                return displayName;
                            }
                        }
                    }
                }
                
                // If that didn't work, try MediaStore for media URIs
                if (uri.toString().contains("media")) {
                    String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
                    try (Cursor cursor = resolver.query(uri, projection, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int nameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                            if (nameIndex >= 0) {
                                String displayName = cursor.getString(nameIndex);
                                if (displayName != null && !displayName.isEmpty()) {
                                    // Remove extension if present
                                    int dotIndex = displayName.lastIndexOf('.');
                                    if (dotIndex > 0) {
                                        return displayName.substring(0, dotIndex);
                                    }
                                    return displayName;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Fall through to path-based extraction
            }
        }
        
        // Fallback to path-based extraction
        String path = uri.getLastPathSegment();
        if (path != null) {
            // Try to get just the filename
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash >= 0) {
                path = path.substring(lastSlash + 1);
            }
            // Remove extension if present
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex > 0) {
                return path.substring(0, dotIndex);
            }
            return path;
        }
        return "Photo";
    }

    /**
     * Gets the display name (filename) of this photo.
     * This is a convenience method that doesn't require a context.
     * For better results with content URIs, use getDisplayName(Context) instead.
     *
     * @return The display name
     */
    public String getDisplayName() {
        return getDisplayName(null);
    }

    /**
     * Gets the list of tags for this photo.
     *
     * @return The list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Adds a tag to this photo if it doesn't already exist.
     *
     * @param tag The tag to add
     * @return true if the tag was added, false if it already exists
     */
    public boolean addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    /**
     * Removes a tag from this photo.
     *
     * @param tag The tag to remove
     * @return true if the tag was removed, false if it wasn't found
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }

    /**
     * Checks if this photo has a tag matching the given criteria (case-insensitive prefix match).
     *
     * @param type        The tag type to match
     * @param valuePrefix The prefix of the tag value to match
     * @return true if the photo has a matching tag
     */
    public boolean hasTagWithPrefix(String type, String valuePrefix) {
        for (Tag tag : tags) {
            if (tag.matchesPrefix(type, valuePrefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this photo has a tag matching exactly (case-insensitive).
     *
     * @param type  The tag type to match
     * @param value The tag value to match
     * @return true if the photo has a matching tag
     */
    public boolean hasTag(String type, String value) {
        for (Tag tag : tags) {
            if (tag.matchesExact(type, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all tag values of a specific type.
     *
     * @param type The tag type
     * @return List of tag values for that type
     */
    public List<String> getTagValues(String type) {
        List<String> values = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getTagName().equalsIgnoreCase(type)) {
                values.add(tag.getTagValue());
            }
        }
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo photo = (Photo) obj;
        return uriString.equals(photo.uriString);
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }
}

