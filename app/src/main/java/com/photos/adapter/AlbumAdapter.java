package com.photos.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.model.Album;
import com.photos.model.Photo;

import java.io.InputStream;
import java.util.List;

/**
 * Adapter for displaying albums in a RecyclerView.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private final Context context;
    private final List<Album> albums;
    private final AlbumClickListener listener;

    public interface AlbumClickListener {
        void onAlbumClick(Album album);
        void onAlbumRename(Album album);
        void onAlbumDelete(Album album);
    }

    public AlbumAdapter(Context context, List<Album> albums, AlbumClickListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private final ImageView albumThumbnail;
        private final TextView albumName;
        private final TextView albumCount;
        private final ImageButton menuButton;

        AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumThumbnail = itemView.findViewById(R.id.albumThumbnail);
            albumName = itemView.findViewById(R.id.albumName);
            albumCount = itemView.findViewById(R.id.albumCount);
            menuButton = itemView.findViewById(R.id.menuButton);
        }

        void bind(Album album) {
            albumName.setText(album.getName());
            albumCount.setText(context.getString(R.string.album_count, album.getPhotoCount()));

            // Load first photo as thumbnail
            if (!album.getPhotos().isEmpty()) {
                Photo firstPhoto = album.getPhotos().get(0);
                loadThumbnail(albumThumbnail, firstPhoto.getUriString());
            } else {
                albumThumbnail.setImageResource(R.drawable.album_placeholder);
            }

            // Click on card opens album
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlbumClick(album);
                }
            });

            // Menu button shows popup menu
            menuButton.setOnClickListener(v -> showPopupMenu(v, album));
        }

        private void showPopupMenu(View anchor, Album album) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenuInflater().inflate(R.menu.menu_album_item, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_open) {
                    if (listener != null) listener.onAlbumClick(album);
                    return true;
                } else if (itemId == R.id.action_rename) {
                    if (listener != null) listener.onAlbumRename(album);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    if (listener != null) listener.onAlbumDelete(album);
                    return true;
                }
                return false;
            });
            popup.show();
        }

        private void loadThumbnail(ImageView imageView, String uriString) {
            try {
                Uri uri = Uri.parse(uriString);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            imageView.setImageResource(R.drawable.album_placeholder);
        }
    }
}

