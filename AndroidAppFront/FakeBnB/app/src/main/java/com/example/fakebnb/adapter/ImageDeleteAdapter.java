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

import java.util.ArrayList;
import java.util.List;

public class ImageDeleteAdapter extends RecyclerView.Adapter<ImageDeleteAdapter.ImageViewHolder> {

    private List<Bitmap> imageBitmapList;
    private List<Long> imageIds;
    private List<Long> deletedImageIds;

    public ImageDeleteAdapter(List<Bitmap> imageBitmapList) {
        this.imageBitmapList = imageBitmapList;
        this.imageIds = new ArrayList<>();
        this.deletedImageIds = new ArrayList<>();
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
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                imageBitmapList.remove(adapterPosition);
                if (imageIds.get(adapterPosition) != null && !imageIds.isEmpty() && imageIds.get(adapterPosition) != -1L){
                    deletedImageIds.add(imageIds.get(adapterPosition));
                }
                imageIds.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBitmapList.size();
    }

    public void addStoredItem(Bitmap bitmap, Long imageId) {
        imageBitmapList.add(bitmap);
        imageIds.add(imageId);
        notifyDataSetChanged();
    }

    public void addNewImage(Bitmap bitmap) {
        imageBitmapList.add(bitmap);
        imageIds.add(-1L);
        notifyDataSetChanged();
    }

    public List<Bitmap> getNewImages() {
        List<Bitmap> newImages = new ArrayList<>();
        for (int i = 0; i < imageIds.size(); i++) {
            if (imageIds.get(i) == -1) {
                newImages.add(imageBitmapList.get(i));
            }
        }
        return newImages;
    }

    public List<Long> getDeletedImageIds() {
        return deletedImageIds;
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
