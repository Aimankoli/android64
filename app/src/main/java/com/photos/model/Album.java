package com.photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an album containing photos.
 * An album has a name and a list of photos.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    /**
     * Constructs a new Album with the specified name.
     *
     * @param name The name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Gets the name of this album.
     *
     * @return The album name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this album.
     *
     * @param name The album name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of photos in this album.
     *
     * @return The list of photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Adds a photo to this album if it doesn't already exist.
     *
     * @param photo The photo to add
     * @return true if the photo was added, false if it already exists
     */
    public boolean addPhoto(Photo photo) {
        if (!photos.contains(photo)) {
            photos.add(photo);
            return true;
        }
        return false;
    }

    /**
     * Removes a photo from this album.
     *
     * @param photo The photo to remove
     * @return true if the photo was removed, false if it wasn't found
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }

    /**
     * Gets the number of photos in this album.
     *
     * @return The number of photos
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Checks if this album contains a specific photo.
     *
     * @param photo The photo to check for
     * @return true if the album contains the photo, false otherwise
     */
    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }

    /**
     * Gets a photo by its URI string.
     *
     * @param uriString The URI string to search for
     * @return The photo if found, null otherwise
     */
    public Photo getPhotoByUri(String uriString) {
        for (Photo photo : photos) {
            if (photo.getUriString().equals(uriString)) {
                return photo;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Album album = (Album) obj;
        return name.equalsIgnoreCase(album.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}

