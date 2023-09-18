package com.example.fakebnb.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.fakebnb.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    private final List<Bitmap> mSliderItems;

    public SliderAdapter() {
        this.mSliderItems = new ArrayList<>();
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final Bitmap sliderItem = mSliderItems.get(position);
        viewHolder.imageViewBackground.setImageBitmap(sliderItem);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public void addItem(Bitmap bitmap) {
        mSliderItems.add(bitmap);
        notifyDataSetChanged();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.apartmentImage);
            this.itemView = itemView;
        }
    }
}
