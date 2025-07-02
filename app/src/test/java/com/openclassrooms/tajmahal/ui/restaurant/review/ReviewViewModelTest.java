package com.openclassrooms.tajmahal.ui.restaurant.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

/**
 * Test unitaire de ReviewViewModel.
 * 1. test qu'un avis valide est bien ajouté en tête de liste
 * 2. test qu'un avis n'est jamais ajouté avec un auteur vide (nom vide remplacé par défaut )
 * 3. test qu'un avis n'est jamais ajouté avec un auteur null (nom null remplacé par défaut )
 * 4. test qu'un avis n'est jamais ajouté avec un commentaire null
 * 5. test qu'un avis n'est jamais ajouté avec un commentaire vide
 * 6. test qu'un avis n'est jamais ajouté avec une note à 0
 * 7. test qu'un avis n'est jamais ajouté avec une note supérieure à 5
 * 8. test qu'un avis null n'est jamais ajouté
 */
@RunWith(MockitoJUnitRunner.class)
public class ReviewViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private RestaurantFakeApi fakeApi;
    private  Review newReview;
    private List<Review> finalReviews;

    RestaurantRepository restaurantRepository;

    ReviewViewModel testReviewViewModel;


    @Before
    public void setUp() {

        // Liste initiale fictive
        fakeApi = new RestaurantFakeApi();

        restaurantRepository= new RestaurantRepository(fakeApi);

        //création du ViewModel
        testReviewViewModel = new ReviewViewModel(restaurantRepository);

        // Force l'initialisation de la LiveData (important pour getValue())
        testReviewViewModel.getReviews().observeForever(reviews -> {});

    }

    @Test //1. test qu'un avis valide est bien ajouté en tête de liste
    public void addReview1_shouldAddReviewOnTopList() {
        // Création nouvel avis
        testAddReview(new Review("Toto", "photo2.jpg", "Très bon restaurant", 4),true);
    }

    @Test //2. test qu'un avis est ajouté sans un auteur vide (nom vide remplacé par défaut )
    public void addReview2_withEmptyAuthor_shouldReplaceEmptyBeforeAddedOnTopList() {
        // Crée un avis invalide : nom vide
        testAddReview(new Review("", "photo.jpg", "2. test qu'un avis est ajouté sans un auteur vide (nom vide remplacé par défaut )", 4),true);
        // Vérifie que l'auteur a été remplacé et n'est plus vide
        assertNotNull(newReview.getUsername());
        assertFalse(newReview.getUsername().trim().isEmpty());
    }

     @Test //3. test qu'un avis est ajouté sans auteur null (nom null remplacé par défaut )
    public void addReview3_withNullAuthor_shouldReplaceNullBeforeAddedOnTopList() {
        // Crée un avis invalide : nom null
         testAddReview(new Review(null, "photo.jpg", "3. test qu'un avis est ajouté sans auteur null (nom null remplacé par défaut )", 4),true);
         assertNotNull(newReview.getUsername());
         assertFalse(newReview.getUsername().trim().isEmpty());
    }


    @Test //4. test qu'un avis n'est jamais ajouté avec un commentaire null
    public void addReview4_withNullComment_shouldNotBeAdded() {
        // Crée un avis invalide : commentaire null
        testAddReview( new Review("Michu", "photo.jpg", null, 4),false);
    }

    @Test //5. test qu'un avis n'est jamais ajouté avec un commentaire vide
    public void addReview5_withEmptyComment_shouldNotBeAdded() {
        // Crée un avis invalide : nom vide
        testAddReview(  new Review("Michu", "photo.jpg", "", 4),false);
    }
    @Test //6. test qu'un avis n'est jamais ajouté avec une note à 0
    public void addReview6_withLowRate_shouldNotBeAdded() {
        // Crée un avis invalide : note trop basse
        testAddReview(new Review("Michu", "photo.jpg", "pas bon", 0),false);
    }

    @Test //7. test qu'un avis n'est jamais ajouté avec une note supérieure à 5
    public void addReview7_withTowHighRate_shouldNotBeAdded() {
        // Crée un avis invalide : note trop haute
        testAddReview(new Review("Michu", "photo.jpg", "pas bon", 6),false);
    }

    @Test //8. test qu'un avis null n'est jamais ajouté
    public void addReview8_withNullReview_shouldNotBeAdded() {
        // Appelle la méthode avec un review null
        testAddReview(null,false);
    }

    //Fonction pour les tests d'ajout de commentaires
    public void testAddReview(Review reviewToAdd, Boolean shouldBeAdded) {
        newReview =reviewToAdd;

        // Appelle la méthode
        testReviewViewModel.addReview(newReview);

        // Récupère la liste complète des avis
        finalReviews = testReviewViewModel.getReviews().getValue();

        if(shouldBeAdded) {
            // Vérifie qu'il a bien été ajouté
            assertTrue(finalReviews.contains(newReview)); // vérifie qu'il y a bien le nouveau commentaire
            assertEquals(newReview, finalReviews.get(0));  // vérifie qu'il est en tête
        }else {
            // Vérifie qu'il n'a pas été ajouté
            assertFalse(finalReviews.contains(newReview));
        }
    }
}


