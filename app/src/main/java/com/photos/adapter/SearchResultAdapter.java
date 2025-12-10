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
import com.photos.model.DataManager.PhotoResult;

import java.io.InputStream;
import java.util.List;

/**
 * Adapter for displaying search results with album context.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private final Context context;
    private final List<PhotoResult> results;
    private final SearchResultClickListener listener;

    public interface SearchResultClickListener {
        void onResultClick(PhotoResult result, int position);
    }

    public SearchResultAdapter(Context context, List<PhotoResult> results, SearchResultClickListener listener) {
        this.context = context;
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        PhotoResult result = results.get(position);
        holder.bind(result, position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final ImageView photoThumbnail;
        private final TextView photoName;

        SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
            photoName = itemView.findViewById(R.id.photoName);
        }

        void bind(PhotoResult result, int position) {
            // Show photo name with album info
            String displayText = result.photo.getDisplayName(context) + " (" + result.album.getName() + ")";
            photoName.setText(displayText);
            loadThumbnail(photoThumbnail, result.photo.getUriString());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onResultClick(result, position);
                }
            });
        }

        private void loadThumbnail(ImageView imageView, String uriString) {
            try {
                Uri uri = Uri.parse(uriString);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
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

