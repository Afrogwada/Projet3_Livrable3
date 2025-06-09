package com.openclassrooms.tajmahal.ui.restaurant.review;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.ui.restaurant.details.DetailsViewModel;

import java.util.List;

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
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null; // Pour stocker l'image sélectionnée
    private ShapeableImageView userPhoto;


    private void setupViewModel() {
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        setupViewModel();
        // Configuration de la barre d’outils (Toolbar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Activation du bouton retour dans la toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Récupération du nom du restaurant passé via intent
            String restaurantName = getIntent().getStringExtra("restaurant_name");
            getSupportActionBar().setTitle(restaurantName);
        }

        // Initialisation des vues
        recyclerView = findViewById(R.id.review_list);
        EditText commentInput = findViewById(R.id.user_comment);
        EditText userNameInput = findViewById(R.id.user_name);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        Chip validateButton = findViewById(R.id.valid_new_avis);
        userPhoto = findViewById(R.id.user_photo);




        // Configuration du RecyclerView avec un LayoutManager vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observation des données LiveData du ViewModel pour mettre à jour l’UI automatiquement
        reviewViewModel.getReviews().observe(this, reviews -> {
            if (adapter == null) {
                // Création de l’adapter à partir des reviews reçues
                adapter = new ReviewAdapter(reviews);
                recyclerView.setAdapter(adapter);
            } else {
                // Mise à jour de la liste dans l’adapter
                adapter.updateReviews(reviews);
            }
        });



        // Gestion du clic sur l'image utilisateur
        userPhoto.setOnClickListener(v -> openImageChooser());

//        userPhoto.setOnClickListener(v -> {
//            // Alterne entre deux images locales pour simuler un choix
//            if (selectedPhotoResId == R.drawable.currentuser_picture) {
//                selectedPhotoResId = R.drawable.profile_alternative; // ajoute une autre image dans res/drawable
//            } else {
//                selectedPhotoResId = R.drawable.currentuser_picture;
//            }
//            userPhoto.setImageResource(selectedPhotoResId);
//        });

        // Gestion du clic sur le bouton pour ajouter un nouvel avis
        validateButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            String userName;
            int rating = (int) ratingBar.getRating();



            // Vérifications simples pour éviter avis vide ou note nulle
            if (comment.isEmpty()) {
                Toast.makeText(this, "Veuillez écrire un commentaire.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userNameInput.getText().toString().trim().isEmpty()) {
                userName="Anonyme";
            } else {
                 userName= userNameInput.getText().toString().trim();
            }
            if (rating == 0) {
                Toast.makeText(this, "Veuillez attribuer une note.", Toast.LENGTH_SHORT).show();
                return;
            }
            String photoString = selectedImageUri != null ? selectedImageUri.toString() : String.valueOf(R.drawable.currentuser_picture);

            // Création d’un nouvel avis avec données fictives pour l’auteur et photo
            Review newReview = new Review(
                    userName,
                    photoString,
                    comment,
                    rating
            );

            // Ajout de l’avis via le ViewModel, qui mettra à jour automatiquement la liste observée
            reviewViewModel.addReview(newReview);

            // Réinitialisation des champs pour le prochain avis
            commentInput.setText("");
            ratingBar.setRating(0);

            // Scroll automatique vers le haut pour voir le nouvel avis ajouté
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
            userPhoto.setImageURI(selectedImageUri);
        }


    }
}
