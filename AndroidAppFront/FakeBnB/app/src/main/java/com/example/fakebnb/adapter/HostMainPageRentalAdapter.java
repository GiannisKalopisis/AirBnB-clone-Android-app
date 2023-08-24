package com.example.fakebnb.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.HostMainPageRecyclerViewInterface;
import com.example.fakebnb.R;
import com.example.fakebnb.model.HostRentalMainPageModel;

import java.util.ArrayList;

public class HostMainPageRentalAdapter extends RecyclerView.Adapter<HostMainPageRentalAdapter.ViewHolder>{

    private final HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface;
    private ArrayList<HostRentalMainPageModel> rentalModel;

    public HostMainPageRentalAdapter(HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface, ArrayList<HostRentalMainPageModel> rentalModel) {
        this.hostMainPageRecyclerViewInterface = hostMainPageRecyclerViewInterface;
        this.rentalModel = rentalModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_page_rental, parent, false);
        ViewHolder holder = new ViewHolder(view, hostMainPageRecyclerViewInterface);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HostMainPageRentalAdapter.ViewHolder holder, int position) {
        holder.descriptionTextView.setText(rentalModel.get(position).getDescription());
        holder.areaTextView.setText(rentalModel.get(position).getArea());
        holder.ratingStars.setRating(rentalModel.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return rentalModel.size();
    }

    public void setRentalModel(ArrayList<HostRentalMainPageModel> rentalModel) {
        this.rentalModel = rentalModel;
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView descriptionTextView, areaTextView;
        private RatingBar ratingStars;

        ViewHolder(View itemView, HostMainPageRecyclerViewInterface hostMainPageRecyclerViewInterface) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.hostRentalDescription);
            areaTextView = itemView.findViewById(R.id.hostRentalArea);
            ratingStars = itemView.findViewById(R.id.ratingBarHostRentalHomePage);

            // Set click listener for the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hostMainPageRecyclerViewInterface != null){
                        int clickedPosition = getAdapterPosition();
                        if (clickedPosition != RecyclerView.NO_POSITION) {
                            long rentalId = rentalModel.get(clickedPosition).getRentalId(); // Get the rentalId of clicked item
                            hostMainPageRecyclerViewInterface.onItemClick(rentalId);
                        }
                    }
                }
            });
        }
    }
}
