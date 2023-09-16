package com.example.fakebnb.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.HostMainPageRecyclerViewInterface;
import com.example.fakebnb.R;
import com.example.fakebnb.model.HostRentalMainPageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HostMainPageRentalAdapter extends RecyclerView.Adapter<HostMainPageRentalAdapter.ViewHolder>{

    private final HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface;
    private final ArrayList<HostRentalMainPageModel> rentalModel;
    private final Map<Long, Bitmap> rentalImages;     // <chatId, rentalImage>

    public HostMainPageRentalAdapter(HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface, ArrayList<HostRentalMainPageModel> rentalModel) {
        this.hostMainPageRecyclerViewInterface = hostMainPageRecyclerViewInterface;
        this.rentalModel = rentalModel;
        this.rentalImages = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_page_rental, parent, false);
        return new ViewHolder(view, hostMainPageRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull HostMainPageRentalAdapter.ViewHolder holder, int position) {
        holder.descriptionTextView.setText(rentalModel.get(position).getDescription());
        holder.areaTextView.setText(rentalModel.get(position).getArea());
        holder.ratingStars.setRating(rentalModel.get(position).getRating());
        holder.rentalImageView.setImageBitmap(rentalImages.get(rentalModel.get(position).getRentalId()));
    }

    @Override
    public int getItemCount() {
        return rentalModel.size();
    }

    public void addNewRental(HostRentalMainPageModel hostRentalMainPageModel){
        this.rentalModel.add(hostRentalMainPageModel);
        notifyDataSetChanged();
    }

    public void addNewRentalSingleImage(Long rentalId, Bitmap image) {
        this.rentalImages.put(rentalId, image);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView descriptionTextView;
        private final TextView areaTextView;
        private final RatingBar ratingStars;
        private final ImageView rentalImageView;

        ViewHolder(View itemView, HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.hostRentalDescription);
            areaTextView = itemView.findViewById(R.id.hostRentalArea);
            ratingStars = itemView.findViewById(R.id.ratingBarHostRentalHomePage);
            rentalImageView = itemView.findViewById(R.id.singleImageHostRentalView);

            // Set click listener for the item view
            itemView.setOnClickListener(v -> {
                if (hostMainPageRecyclerViewInterface != null){
                    int clickedPosition = getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        Long rentalId = rentalModel.get(clickedPosition).getRentalId(); // Get the rentalId of clicked item
                        hostMainPageRecyclerViewInterface.onItemClick(rentalId);
                    }
                }
            });
        }
    }
}
