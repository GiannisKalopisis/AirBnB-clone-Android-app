package com.example.fakebnb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HostReviewAdapter extends RecyclerView.Adapter<HostReviewAdapter.ViewHolder>{

    private ArrayList<HostReviewModel> reviews;

    public HostReviewAdapter(ArrayList<HostReviewModel> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_review, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.reviewUsernameTextView.setText(reviews.get(position).getUsername());
        holder.reviewRatingStarBar.setRating(reviews.get(position).getStars());
        holder.commentTextView.setText(reviews.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(ArrayList<HostReviewModel> reviews) {
        this.reviews = reviews;
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
