package com.openclassrooms.tajmahal.ui.restaurant;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.data.service.RestaurantApi;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.domain.model.ReviewAdapter;
//import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Review> reviews;
    private ReviewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.review), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// Affiche la flèche de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Récupérer le nom du restaurant et l’afficher
            String restaurantName = getIntent().getStringExtra("restaurant_name");
            getSupportActionBar().setTitle(restaurantName);
        }
        /*ImageView userPhoto = findViewById(R.id.user_photo);
        String imageUrl = "https://www.figma.com/design/qj6JYHASYVk5HIqJOpy6ld/Restaurant?node-id=62-1135&t=OJPnaTdfIfE2gh7X-4";
        Glide.with(this)        // 'this' est l'Activity
                .load(imageUrl)
                .circleCrop()      // pour rendre l'image ronde (optionnel)
                .into(userPhoto);*/

        // Référencer le RecyclerView
        recyclerView = findViewById(R.id.review_list);

        // Récupérer les données (depuis le FakeApi)
        RestaurantApi api = new RestaurantFakeApi();
        reviews = api.getReviews();

        // Créer l'adapter
        adapter = new ReviewAdapter(reviews);

        // Associer LayoutManager + Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Ferme l’activité actuelle
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
