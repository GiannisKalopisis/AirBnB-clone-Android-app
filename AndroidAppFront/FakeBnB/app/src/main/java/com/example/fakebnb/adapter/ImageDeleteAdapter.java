package com.example.fakebnb.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.R;

import java.util.List;

public class ImageDeleteAdapter extends RecyclerView.Adapter<ImageDeleteAdapter.ImageViewHolder> {

    private List<Bitmap> imageBitmapList;

    public ImageDeleteAdapter(List<Bitmap> imageBitmapList) {
        this.imageBitmapList = imageBitmapList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_delete, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Bitmap imageBitmap = imageBitmapList.get(position);
        holder.imageView.setImageBitmap(imageBitmap);

        holder.deleteButton.setOnClickListener(v -> {
            // Handle delete button click
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                imageBitmapList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBitmapList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageDeleteView); // Update the ID here
            deleteButton = itemView.findViewById(R.id.imageDeleteButton); // Update the ID here
        }
    }
}
