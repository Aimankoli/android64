package com.photos.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.photos.R;
import com.photos.model.Album;
import com.photos.model.DataManager;
import com.photos.model.Photo;
import com.photos.model.Tag;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying a single photo with slideshow and tag management.
 */
public class PhotoDisplayActivity extends AppCompatActivity {

    private ImageView photoImageView;
    private TextView photoCounter;
    private TextView noTagsText;
    private ChipGroup tagsChipGroup;
    private Button btnAddTag;
    private Button btnMovePhoto;
    private Button btnRemovePhoto;
    private ImageButton btnPrevious;
    private ImageButton btnNext;

    private DataManager dataManager;
    private Album album;
    private List<Photo> photos;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        dataManager = DataManager.getInstance();

        // Get album and photo from intent
        String albumName = getIntent().getStringExtra("album_name");
        currentIndex = getIntent().getIntExtra("photo_index", 0);

        if (albumName == null) {
            finish();
            return;
        }

        album = dataManager.getAlbumByName(albumName);
        if (album == null || album.getPhotos().isEmpty()) {
            finish();
            return;
        }

        photos = album.getPhotos();
        if (currentIndex >= photos.size()) {
            currentIndex = 0;
        }

        initViews();
        displayCurrentPhoto();
    }

    private void initViews() {
        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        photoImageView = findViewById(R.id.photoImageView);
        photoCounter = findViewById(R.id.photoCounter);
        noTagsText = findViewById(R.id.noTagsText);
        tagsChipGroup = findViewById(R.id.tagsChipGroup);
        btnAddTag = findViewById(R.id.btnAddTag);
        btnMovePhoto = findViewById(R.id.btnMovePhoto);
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // Navigation buttons
        btnPrevious.setOnClickListener(v -> showPreviousPhoto());
        btnNext.setOnClickListener(v -> showNextPhoto());

        // Tag management
        btnAddTag.setOnClickListener(v -> showAddTagDialog());

        // Photo actions
        btnMovePhoto.setOnClickListener(v -> showMovePhotoDialog());
        btnRemovePhoto.setOnClickListener(v -> confirmRemovePhoto());

        // Update navigation visibility
        updateNavigationVisibility();
    }

    private void updateNavigationVisibility() {
        boolean hasMultiplePhotos = photos.size() > 1;
        btnPrevious.setVisibility(hasMultiplePhotos ? View.VISIBLE : View.INVISIBLE);
        btnNext.setVisibility(hasMultiplePhotos ? View.VISIBLE : View.INVISIBLE);
    }

    private void displayCurrentPhoto() {
        Photo photo = photos.get(currentIndex);

        // Update toolbar title
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(photo.getDisplayName());

        // Update counter
        photoCounter.setText((currentIndex + 1) + " / " + photos.size());

        // Load image
        loadFullImage(photoImageView, photo.getUriString());

        // Update tags
        updateTagsDisplay();
    }

    private void loadFullImage(ImageView imageView, String uriString) {
        try {
            Uri uri = Uri.parse(uriString);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageResource(R.drawable.photo_placeholder);
    }

    private void showPreviousPhoto() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = photos.size() - 1;
        }
        displayCurrentPhoto();
    }

    private void showNextPhoto() {
        if (currentIndex < photos.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        displayCurrentPhoto();
    }

    private void updateTagsDisplay() {
        Photo photo = photos.get(currentIndex);
        List<Tag> tags = photo.getTags();

        tagsChipGroup.removeAllViews();

        if (tags.isEmpty()) {
            noTagsText.setVisibility(View.VISIBLE);
        } else {
            noTagsText.setVisibility(View.GONE);
            for (Tag tag : tags) {
                addTagChip(tag);
            }
        }
    }

    private void addTagChip(Tag tag) {
        Chip chip = new Chip(this);
        chip.setText(tag.toString());
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        // Set color based on tag type
        if (tag.getTagName().equalsIgnoreCase(Tag.TYPE_PERSON)) {
            chip.setChipBackgroundColorResource(R.color.tag_person);
        } else {
            chip.setChipBackgroundColorResource(R.color.tag_location);
        }
        chip.setTextColor(getResources().getColor(R.color.tag_text, null));
        chip.setCloseIconTint(getResources().getColorStateList(R.color.tag_text, null));

        chip.setOnCloseIconClickListener(v -> confirmDeleteTag(tag, chip));

        tagsChipGroup.addView(chip);
    }

    private void showAddTagDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tag, null);
        Spinner spinnerTagType = dialogView.findViewById(R.id.spinnerTagType);
        AutoCompleteTextView autoCompleteTagValue = dialogView.findViewById(R.id.autoCompleteTagValue);

        // Setup spinner with tag types
        String[] tagTypes = {Tag.TYPE_PERSON, Tag.TYPE_LOCATION};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tagTypes);
        spinnerTagType.setAdapter(spinnerAdapter);

        // Setup auto-complete based on selected tag type
        spinnerTagType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateAutoComplete(autoCompleteTagValue, tagTypes[position]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Initialize with first tag type
        updateAutoComplete(autoCompleteTagValue, tagTypes[0]);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_tag)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String tagType = spinnerTagType.getSelectedItem().toString();
                    String tagValue = autoCompleteTagValue.getText().toString().trim();

                    if (tagValue.isEmpty()) {
                        showError(getString(R.string.empty_name));
                        return;
                    }

                    Photo photo = photos.get(currentIndex);
                    Tag newTag = new Tag(tagType, tagValue);

                    if (photo.addTag(newTag)) {
                        dataManager.saveData();
                        updateTagsDisplay();
                    } else {
                        showError(getString(R.string.tag_exists));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateAutoComplete(AutoCompleteTextView autoComplete, String tagType) {
        List<String> suggestions = dataManager.getTagValuesWithPrefix(tagType, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);
        autoComplete.setAdapter(adapter);
        autoComplete.setThreshold(1); // Show suggestions after 1 character
    }

    private void confirmDeleteTag(Tag tag, Chip chip) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_tag)
                .setMessage(R.string.confirm_delete_tag)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Photo photo = photos.get(currentIndex);
                    photo.removeTag(tag);
                    dataManager.saveData();
                    tagsChipGroup.removeView(chip);
                    if (photo.getTags().isEmpty()) {
                        noTagsText.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showMovePhotoDialog() {
        List<Album> allAlbums = dataManager.getAlbums();

        // Filter out current album
        List<Album> otherAlbums = new ArrayList<>();
        for (Album a : allAlbums) {
            if (!a.getName().equals(album.getName())) {
                otherAlbums.add(a);
            }
        }

        if (otherAlbums.isEmpty()) {
            showError(getString(R.string.no_other_albums));
            return;
        }

        String[] albumNames = new String[otherAlbums.size()];
        for (int i = 0; i < otherAlbums.size(); i++) {
            albumNames[i] = otherAlbums.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_target_album)
                .setItems(albumNames, (dialog, which) -> {
                    Photo photo = photos.get(currentIndex);
                    Album targetAlbum = otherAlbums.get(which);

                    if (dataManager.movePhoto(photo, album, targetAlbum)) {
                        if (photos.isEmpty()) {
                            finish();
                        } else {
                            if (currentIndex >= photos.size()) {
                                currentIndex = photos.size() - 1;
                            }
                            displayCurrentPhoto();
                            updateNavigationVisibility();
                        }
                        showSuccess(getString(R.string.photo_moved));
                    } else {
                        showError(getString(R.string.photo_exists));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmRemovePhoto() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_photo)
                .setMessage(R.string.confirm_remove_photo)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Photo photo = photos.get(currentIndex);
                    album.removePhoto(photo);
                    dataManager.saveData();

                    if (photos.isEmpty()) {
                        finish();
                    } else {
                        if (currentIndex >= photos.size()) {
                            currentIndex = photos.size() - 1;
                        }
                        displayCurrentPhoto();
                        updateNavigationVisibility();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showSuccess(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.success)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}

