package com.photos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.photos.R;
import com.photos.model.Photo;

import java.io.InputStream;
import java.util.List;

/**
 * Adapter for displaying photos in a grid RecyclerView.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<Photo> photos;
    private final PhotoClickListener listener;

    public interface PhotoClickListener {
        void onPhotoClick(Photo photo, int position);
        void onPhotoLongClick(Photo photo, int position);
    }

    public PhotoAdapter(Context context, List<Photo> photos, PhotoClickListener listener) {
        this.context = context;
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.bind(photo, position);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView photoThumbnail;
        private final TextView photoName;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
            photoName = itemView.findViewById(R.id.photoName);
        }

        void bind(Photo photo, int position) {
            photoName.setText(photo.getDisplayName());
            loadThumbnail(photoThumbnail, photo.getUriString());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoClick(photo, position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoLongClick(photo, position);
                    return true;
                }
                return false;
            });
        }

        private void loadThumbnail(ImageView imageView, String uriString) {
            try {
                Uri uri = Uri.parse(uriString);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    // Decode with sampling for efficiency
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // Scale down for thumbnails
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
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
    }
}

