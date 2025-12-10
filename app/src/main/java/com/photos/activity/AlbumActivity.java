package com.photos.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.photos.R;
import com.photos.adapter.PhotoAdapter;
import com.photos.model.Album;
import com.photos.model.DataManager;
import com.photos.model.Photo;

import java.util.List;

/**
 * Activity for displaying and managing photos within an album.
 */
public class AlbumActivity extends AppCompatActivity implements PhotoAdapter.PhotoClickListener {

    private RecyclerView photosRecyclerView;
    private TextView emptyText;
    private PhotoAdapter adapter;
    private DataManager dataManager;
    private Album album;
    private List<Photo> photos;

    private final ActivityResultLauncher<String[]> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    // Take persistent permission
                    getContentResolver().takePersistableUriPermission(uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    addPhotoFromUri(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        dataManager = DataManager.getInstance();

        // Get album from intent
        String albumName = getIntent().getStringExtra("album_name");
        if (albumName == null) {
            finish();
            return;
        }
        album = dataManager.getAlbumByName(albumName);
        if (album == null) {
            finish();
            return;
        }
        photos = album.getPhotos();

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(album.getName());
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView with grid layout
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        photosRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PhotoAdapter(this, photos, this);
        photosRecyclerView.setAdapter(adapter);

        // Setup FAB
        FloatingActionButton fabAddPhoto = findViewById(R.id.fabAddPhoto);
        fabAddPhoto.setOnClickListener(v -> pickImage());

        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (photos.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            photosRecyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            photosRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void pickImage() {
        pickImageLauncher.launch(new String[]{"image/*"});
    }

    private void addPhotoFromUri(Uri uri) {
        Photo photo = new Photo(uri.toString());
        if (album.addPhoto(photo)) {
            dataManager.saveData();
            adapter.notifyItemInserted(photos.size() - 1);
            updateEmptyState();
        } else {
            showError(getString(R.string.photo_exists));
        }
    }

    @Override
    public void onPhotoClick(Photo photo, int position) {
        Intent intent = new Intent(this, PhotoDisplayActivity.class);
        intent.putExtra("album_name", album.getName());
        intent.putExtra("photo_index", position);
        startActivity(intent);
    }

    @Override
    public void onPhotoLongClick(Photo photo, int position) {
        showPhotoOptions(photo, position);
    }

    private void showPhotoOptions(Photo photo, int position) {
        String[] options = {
                getString(R.string.display_photo),
                getString(R.string.move_photo),
                getString(R.string.remove_photo)
        };

        new AlertDialog.Builder(this)
                .setTitle(photo.getDisplayName(this))
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            onPhotoClick(photo, position);
                            break;
                        case 1:
                            showMovePhotoDialog(photo, position);
                            break;
                        case 2:
                            confirmRemovePhoto(photo, position);
                            break;
                    }
                })
                .show();
    }

    private void showMovePhotoDialog(Photo photo, int position) {
        List<Album> allAlbums = dataManager.getAlbums();
        
        // Filter out current album
        java.util.ArrayList<Album> otherAlbums = new java.util.ArrayList<>();
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
                    Album targetAlbum = otherAlbums.get(which);
                    if (dataManager.movePhoto(photo, album, targetAlbum)) {
                        adapter.notifyItemRemoved(position);
                        updateEmptyState();
                        showSuccess(getString(R.string.photo_moved));
                    } else {
                        showError(getString(R.string.photo_exists));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmRemovePhoto(Photo photo, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_photo)
                .setMessage(R.string.confirm_remove_photo)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    album.removePhoto(photo);
                    dataManager.saveData();
                    adapter.notifyItemRemoved(position);
                    updateEmptyState();
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

