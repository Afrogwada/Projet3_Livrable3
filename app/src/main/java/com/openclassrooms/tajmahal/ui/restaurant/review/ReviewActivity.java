package com.openclassrooms.tajmahal.ui.restaurant.review;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.databinding.ActivityReviewBinding;
import com.openclassrooms.tajmahal.domain.model.Review;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activité affichant la liste des avis et permettant d’en ajouter.
 * <p>
 * Intègre le ViewModel {@link ReviewViewModel} via {@link ReviewViewModelFactory} pour gérer les données.
 * </p>
 */
@AndroidEntryPoint
public class ReviewActivity extends AppCompatActivity {

    private ReviewViewModel reviewViewModel;
    private ActivityReviewBinding binding;
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null; // Pour stocker l'image sélectionnée

    private void setupViewModel() {
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialisation correcte du binding et du layout
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();

        // Configuration de la barre d’outils (Toolbar)
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Activation du bouton retour dans la toolbar et titre
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            String restaurantName = getIntent().getStringExtra("restaurant_name");
            getSupportActionBar().setTitle(restaurantName);
        }

        // Initialisation du RecyclerView via binding
        recyclerView = binding.reviewList;  

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observation des reviews depuis le ViewModel
        reviewViewModel.getReviews().observe(this, reviews -> {
            if (adapter == null) {
                adapter = new ReviewAdapter(reviews);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateReviews(reviews);
            }
        });

        // Clic sur la photo utilisateur pour ouvrir le sélecteur d’image
        binding.userPhoto.setOnClickListener(v -> openImageChooser());

        // Clic sur bouton pour valider un nouvel avis
        binding.validNewAvis.setOnClickListener(v -> {
            String comment = binding.userComment.getText().toString().trim();
            String userName;
            int rating = (int) binding.ratingBar.getRating();

            if (comment.isEmpty()) {
                Toast.makeText(this, "Veuillez écrire un commentaire.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.userName.getText().toString().trim().isEmpty()) {
                userName = "Anonyme";
            } else {
                userName = binding.userName.getText().toString().trim();
            }
            if (rating == 0) {
                Toast.makeText(this, "Veuillez attribuer une note.", Toast.LENGTH_SHORT).show();
                return;
            }
            String photoString = selectedImageUri != null ? selectedImageUri.toString() : String.valueOf(R.mipmap.ic_user_placeholder);

            Review newReview = new Review(userName, photoString, comment, rating);
            reviewViewModel.addReview(newReview);

            binding.userComment.setText("");
            binding.ratingBar.setRating(0);

            recyclerView.scrollToPosition(0);
        });
    }

    /**
     * Gestion du clic sur le bouton retour dans la toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Fermer l’activité et revenir en arrière
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // sélecteur d'image pour l'utilisateur
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choisir une image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.userPhoto.setImageURI(selectedImageUri);
        }
    }
}
