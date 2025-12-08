package com.photos.model;

import java.io.Serializable;

/**
 * Represents a tag with a name and value pair.
 * Tags are used to categorize and search photos.
 * Only "person" and "location" tag types are valid.
 */
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_PERSON = "person";
    public static final String TYPE_LOCATION = "location";

    private String tagName;
    private String tagValue;

    /**
     * Constructs a new Tag with the specified name and value.
     *
     * @param tagName  The name of the tag (e.g., "location", "person")
     * @param tagValue The value of the tag (e.g., "New York", "John")
     */
    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    /**
     * Gets the tag name.
     *
     * @return The tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the tag name.
     *
     * @param tagName The tag name to set
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Gets the tag value.
     *
     * @return The tag value
     */
    public String getTagValue() {
        return tagValue;
    }

    /**
     * Sets the tag value.
     *
     * @param tagValue The tag value to set
     */
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    /**
     * Checks if this tag matches the given criteria (case-insensitive prefix match).
     *
     * @param type        The tag type to match
     * @param valuePrefix The prefix of the tag value to match
     * @return true if this tag matches
     */
    public boolean matchesPrefix(String type, String valuePrefix) {
        return tagName.equalsIgnoreCase(type) &&
                tagValue.toLowerCase().startsWith(valuePrefix.toLowerCase());
    }

    /**
     * Checks if this tag exactly matches (case-insensitive).
     *
     * @param type  The tag type to match
     * @param value The tag value to match
     * @return true if this tag matches exactly
     */
    public boolean matchesExact(String type, String value) {
        return tagName.equalsIgnoreCase(type) &&
                tagValue.equalsIgnoreCase(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return tagName.equalsIgnoreCase(tag.tagName) && 
               tagValue.equalsIgnoreCase(tag.tagValue);
    }

    @Override
    public int hashCode() {
        return tagName.toLowerCase().hashCode() * 31 + tagValue.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return tagName + ": " + tagValue;
    }
}

