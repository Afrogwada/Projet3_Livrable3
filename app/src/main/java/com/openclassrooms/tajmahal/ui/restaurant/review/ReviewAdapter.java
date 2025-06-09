package com.openclassrooms.tajmahal.ui.restaurant.review;

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
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

/**
 * Adapter RecyclerView pour afficher une liste d'avis (Review).
 * <p>
 * Gère l'affichage des données dans chaque item de la liste.
 * </p>
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    /**
     * Liste des avis à afficher. Initialisée vide pour éviter les erreurs null.
     */
    private List<Review> reviewList = new ArrayList<>();

    /**
     * Constructeur de l'adapter avec une liste initiale.
     *
     * @param reviews Liste initiale des avis.
     */
    public ReviewAdapter(List<Review> reviews) {
        if (reviews != null) {
            this.reviewList = new ArrayList<>(reviews); // Copie défensive
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate la vue pour un item de review
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        // Récupère l'avis à la position donnée
        Review review = reviewList.get(position);

        // Remplit les vues avec les données de l'avis
        holder.username.setText(review.getUsername());
        holder.comment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRate());

        // Charge l'image de l'utilisateur avec Glide (avec placeholder)
        String imageSource = review.getPicture();

        if (imageSource.startsWith("http")) {
            Glide.with(holder.imageView.getContext())
                    .load(imageSource)
                    .placeholder(R.mipmap.ic_user_placeholder)
                    .into(holder.imageView);
        } else if (imageSource.startsWith("content://") || imageSource.startsWith("file://")) {
            Glide.with(holder.imageView.getContext())
                    .load(Uri.parse(imageSource))
                    .placeholder(R.mipmap.ic_user_placeholder)
                    .into(holder.imageView);
        } else {
            try {
                int resId = Integer.parseInt(imageSource);
                Glide.with(holder.imageView.getContext())
                        .load(resId)
                        .placeholder(R.mipmap.ic_user_placeholder)
                        .into(holder.imageView);
            } catch (NumberFormatException e) {
                // Fallback si image invalide
                Glide.with(holder.imageView.getContext())
                        .load(R.mipmap.ic_user_placeholder)
                        .into(holder.imageView);
            }
        }
    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    /**
     * Met à jour la liste des avis affichés et rafraîchit la vue.
     *
     * @param newReviews Nouvelle liste d'avis à afficher.
     */
    public void updateReviews(List<Review> newReviews) {
        this.reviewList = newReviews != null ? new ArrayList<>(newReviews) : new ArrayList<>();
        notifyDataSetChanged(); // Notifie que les données ont changé
    }

    /**
     * ViewHolder représentant un item de review.
     */
    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView username, comment;
        RatingBar ratingBar;

        /**
         * Constructeur du ViewHolder.
         *
         * @param itemView La vue de l'item.
         */
        public ReviewViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_user);
            username = itemView.findViewById(R.id.text_user_name);
            comment = itemView.findViewById(R.id.text_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
