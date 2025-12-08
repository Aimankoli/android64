package com.photos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.photos.R;
import com.photos.adapter.AlbumAdapter;
import com.photos.model.Album;
import com.photos.model.DataManager;

import java.util.List;

/**
 * Main activity displaying list of albums.
 * This is the home screen of the application.
 */
public class MainActivity extends AppCompatActivity implements AlbumAdapter.AlbumClickListener {

    private RecyclerView albumsRecyclerView;
    private TextView emptyText;
    private AlbumAdapter adapter;
    private DataManager dataManager;
    private List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DataManager
        DataManager.init(getApplicationContext());
        dataManager = DataManager.getInstance();
        albums = dataManager.getAlbums();

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            }
            return false;
        });

        // Setup RecyclerView
        albumsRecyclerView = findViewById(R.id.albumsRecyclerView);
        emptyText = findViewById(R.id.emptyText);
        albumsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumAdapter(this, albums, this);
        albumsRecyclerView.setAdapter(adapter);

        // Setup FAB
        FloatingActionButton fabAddAlbum = findViewById(R.id.fabAddAlbum);
        fabAddAlbum.setOnClickListener(v -> showCreateAlbumDialog());

        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the album list in case data changed
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (albums.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            albumsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            albumsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showCreateAlbumDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.enter_album_name);
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(this)
                .setTitle(R.string.create_album)
                .setView(input)
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        showError(getString(R.string.empty_name));
                        return;
                    }
                    if (dataManager.getAlbumByName(name) != null) {
                        showError(getString(R.string.album_exists));
                        return;
                    }
                    Album album = new Album(name);
                    dataManager.addAlbum(album);
                    adapter.notifyItemInserted(albums.size() - 1);
                    updateEmptyState();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onAlbumClick(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("album_name", album.getName());
        startActivity(intent);
    }

    @Override
    public void onAlbumRename(Album album) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(album.getName());
        input.setSelection(album.getName().length());
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_album)
                .setView(input)
                .setPositiveButton(R.string.rename, (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        showError(getString(R.string.empty_name));
                        return;
                    }
                    if (!dataManager.renameAlbum(album, newName)) {
                        showError(getString(R.string.album_exists));
                        return;
                    }
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onAlbumDelete(Album album) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_album)
                .setMessage(getString(R.string.confirm_delete_album, album.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int position = albums.indexOf(album);
                    dataManager.removeAlbum(album);
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
}

