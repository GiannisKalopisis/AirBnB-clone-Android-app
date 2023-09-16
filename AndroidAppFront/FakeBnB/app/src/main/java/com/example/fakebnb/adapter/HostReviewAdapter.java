package com.example.fakebnb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.R;
import com.example.fakebnb.model.BookingReviewModel;

import java.util.ArrayList;

public class HostReviewAdapter extends RecyclerView.Adapter<HostReviewAdapter.ViewHolder>{

    private final ArrayList<BookingReviewModel> reviews;

    public HostReviewAdapter() {
        this.reviews = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.reviewUsernameTextView.setText(reviews.get(position).getUsername());
        holder.reviewRatingStarBar.setRating(reviews.get(position).getRating());
        holder.commentTextView.setText(reviews.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviewsListModel(ArrayList<BookingReviewModel> reviews) {
        this.reviews.addAll(reviews);
        /* In case data come from a server and they change
           you have to refresh them.
         */
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewUsernameTextView, commentTextView;
        RatingBar reviewRatingStarBar;

        ViewHolder(View itemView) {
            super(itemView);
            reviewUsernameTextView = itemView.findViewById(R.id.reviewUsernameTextView);
            reviewRatingStarBar = itemView.findViewById(R.id.reviewRatingStarBar);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
