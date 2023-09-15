package com.example.fakebnb.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.MainPageRecyclerViewInterface;
import com.example.fakebnb.R;
import com.example.fakebnb.model.HostRentalMainPageModel;
import com.example.fakebnb.model.RentalMainPageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPageRentalAdapter extends RecyclerView.Adapter<MainPageRentalAdapter.ViewHolder>{

    private MainPageRecyclerViewInterface mainPageRecyclerViewInterface;
    private ArrayList<RentalMainPageModel> rentalModel;
    private final Map<Long, Bitmap> rentalImages;     // <chatId, rentalImage>

    public MainPageRentalAdapter(MainPageRecyclerViewInterface mainPageRecyclerViewInterface, ArrayList<RentalMainPageModel> rentalModel) {
        this.mainPageRecyclerViewInterface = mainPageRecyclerViewInterface;
        this.rentalModel = rentalModel;
        this.rentalImages = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_page_rental, parent, false);
        ViewHolder holder = new ViewHolder(view, mainPageRecyclerViewInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainPageRentalAdapter.ViewHolder holder, int position) {
        holder.descriptionTextView.setText(rentalModel.get(position).getDescription());
        holder.areaTextView.setText(rentalModel.get(position).getArea());
        holder.priceTextView.setText(rentalModel.get(position).getPrice());
        holder.ratingStars.setRating(rentalModel.get(position).getRating());
        holder.rentalImageView.setImageBitmap(rentalImages.get(rentalModel.get(position).getRentalId()));
    }

    @Override
    public int getItemCount() {
        return rentalModel.size();
    }

    public void setRentalModel(ArrayList<RentalMainPageModel> rentalModel) {
        this.rentalModel.addAll(rentalModel);
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public void addNewRental(RentalMainPageModel rentalMainPageModel){
        this.rentalModel.add(rentalMainPageModel);
        notifyDataSetChanged();
    }

    public void addNewRentalSingleImage(Long rentalId, Bitmap image) {
        this.rentalImages.put(rentalId, image);
        notifyDataSetChanged();
    }

    public void deleteRentals() {
        this.rentalModel.clear();
        this.rentalImages.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView descriptionTextView, areaTextView, priceTextView;
        private final RatingBar ratingStars;
        private final ImageView rentalImageView;

        ViewHolder(View itemView, MainPageRecyclerViewInterface mainPageRecyclerViewInterface) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.rentalDescription);
            areaTextView = itemView.findViewById(R.id.rentalArea);
            priceTextView = itemView.findViewById(R.id.price);
            ratingStars = itemView.findViewById(R.id.ratingBarRentalHomePage);
            rentalImageView = itemView.findViewById(R.id.singleImageUserRentalView);

            // Set click listener for the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainPageRecyclerViewInterface != null){
                        int clickedPosition = getAdapterPosition();
                        if (clickedPosition != RecyclerView.NO_POSITION) {
                            Long rentalId = rentalModel.get(clickedPosition).getRentalId(); // Get the rentalId of clicked item
                            mainPageRecyclerViewInterface.onItemClick(rentalId);
                        }
                    }
                }
            });
        }
    }
}
