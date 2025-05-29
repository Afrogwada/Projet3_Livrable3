package com.openclassrooms.tajmahal.domain.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.tajmahal.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviews) {
        this.reviewList = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.username.setText(review.getUsername());
        holder.comment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRate());

        // Chargement de l'image avec Glide (si tu l’utilises)
        Glide.with(holder.imageView.getContext())
                .load(review.getPicture())
                .placeholder(R.mipmap.ic_user_placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView username, comment;
        RatingBar ratingBar;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_user);
            username = itemView.findViewById(R.id.text_user_name);
            comment = itemView.findViewById(R.id.text_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
