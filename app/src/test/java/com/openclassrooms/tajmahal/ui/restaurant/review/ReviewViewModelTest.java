package com.openclassrooms.tajmahal.ui.restaurant.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    RestaurantRepository restaurantRepository;

    @InjectMocks
    ReviewViewModel testReviewViewModel;
    private List<Review> initialReviews;
    private MutableLiveData<List<Review>> testReviewsLiveData;

    @Before
    public void setUp() {

        // Liste initiale fictive
        initialReviews = Arrays.asList(
                new Review("Alice", "photo1.jpg", "Très bon", 5)
        );
        testReviewsLiveData = new MutableLiveData<>();
        testReviewsLiveData.setValue(initialReviews);



        //config du mock
        when(restaurantRepository.getReviews()).thenReturn(testReviewsLiveData);
        //création du ViewModel
        testReviewViewModel = new ReviewViewModel(restaurantRepository);

        // Force l'initialisation de la LiveData (important pour getValue())
        testReviewViewModel.getReviews().observeForever(reviews -> {});

    }

    @Test //1. test qu'un avis valide est bien ajouté en tête de liste
    public void addReview1_shouldCallRepositoryAddReviewAndShouldAddToLiveData() {
        // Création nouvel avis
        Review newReview = new Review("Toto", "photo2.jpg", "Très bon restaurant", 4);

        //simule ce que ferait le Repository (ajouter l'avis dans la liste)
        List<Review> updatedReviews = new ArrayList<>(initialReviews);
        updatedReviews.add(0,newReview);// ajout en tête de liste
        testReviewsLiveData.setValue(updatedReviews); // simulate DB update

        //Appelle la méthode
        testReviewViewModel.addReview(newReview);

        //Vérifie que le repository a bien été appelé
        verify(restaurantRepository).addReview(newReview);

        // Vérifie que l’avis est bien en tête de la LiveData
        List<Review> finalReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(finalReviews.contains(newReview)); // vérifie qu'il y a bien un nouveau commentaire
        assertEquals(newReview, finalReviews.get(0));  // vérifie qu'il est en tête
        assertEquals(2, finalReviews.size()); // 1 initial + 1 ajouté

    }

    @Test //2. test qu'un avis n'est jamais ajouté avec un auteur vide (nom vide remplacé par défaut )
    public void addReview2_withEmptyAuthor_shouldReplaceEmptyBeforeCallRepository_AndShouldAddToLiveData() {
        // Crée un avis invalide : nom vide
        Review invalidReview = new Review("", "photo.jpg", "Bon", 4);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Capture l'avis envoyé au repository
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(restaurantRepository).addReview(captor.capture());

        Review sentReview = captor.getValue();

        // Vérifie que l'auteur a été remplacé et n'est plus vide
        assertNotNull(sentReview.getUsername());
        assertFalse(sentReview.getUsername().trim().isEmpty());

        // Vérifie qu'il a bien été ajouté dans la LiveData
        List<Review> reviews = testReviewViewModel.getReviews().getValue();
        assertNotNull(reviews);

    }

    @Test //3. test qu'un avis n'est jamais ajouté avec un auteur null (nom null remplacé par défaut )
    public void addReview3_withNullAuthor_shouldReplaceEmptyBeforeCallRepository_AndShouldAddToLiveData() {
        // Crée un avis invalide : nom null
        Review invalidReview = new Review(null, "photo.jpg", "Bon", 4);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Capture l'avis envoyé au repository
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(restaurantRepository).addReview(captor.capture());

        Review sentReview = captor.getValue();

        // Vérifie que l'auteur a été remplacé et n'est plus vide
        assertNotNull(sentReview.getUsername());
        assertFalse(sentReview.getUsername().trim().isEmpty());

        // Vérifie qu'il a bien été ajouté dans la LiveData
        List<Review> reviews = testReviewViewModel.getReviews().getValue();
        assertNotNull(reviews);

    }

    @Test //4. test qu'un avis n'est jamais ajouté avec un commentaire null
    public void addReview4_withNullComment_shouldNotCallRepository_AndShouldNotAddToLiveData() {
        // Crée un avis invalide : commentaire null
        Review invalidReview = new Review("Michu", "photo.jpg", null, 4);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));

        // Vérifie que la LiveData ne contient pas cet avis
        List<Review> currentReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(currentReviews == null || !currentReviews.contains(invalidReview));

    }

    @Test //5. test qu'un avis n'est jamais ajouté avec un commentaire vide
    public void addReview5_withEmptyComment_shouldNotCallRepository_AndShouldNotAddToLiveData() {
        // Crée un avis invalide : nom vide
        Review invalidReview = new Review("Michu", "photo.jpg", "", 4);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));

        // Vérifie que la LiveData ne contient pas cet avis
        List<Review> currentReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(currentReviews == null || !currentReviews.contains(invalidReview));

    }

    @Test //6. test qu'un avis n'est jamais ajouté avec une note à 0
    public void addReview6_withLowRate_shouldNotCallRepository_AndShouldNotAddToLiveData() {
        // Crée un avis invalide : note trop basse
        Review invalidReview = new Review("Michu", "photo.jpg", "pas bon", 0);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));

        // Vérifie que la LiveData ne contient pas cet avis
        List<Review> currentReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(currentReviews == null || !currentReviews.contains(invalidReview));

    }

    @Test //7. test qu'un avis n'est jamais ajouté avec une note supérieure à 5
    public void addReview7_withTowHighRate_shouldNotCallRepository_AndShouldNotAddToLiveData() {
        // Crée un avis invalide : note trop haute
        Review invalidReview = new Review("Michu", "photo.jpg", "pas bon", 6);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));

        // Vérifie que la LiveData ne contient pas cet avis
        List<Review> currentReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(currentReviews == null || !currentReviews.contains(invalidReview));

    }

    @Test //8. test qu'un avis null n'est jamais ajouté
    public void addReview8_withNullReview_shouldNotCallRepository_AndShouldNotAddToLiveData() {

        // Appelle la méthode avec un review null
        testReviewViewModel.addReview(null);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));

        // Vérifie que la LiveData ne contient pas cet avis
        List<Review> currentReviews = testReviewViewModel.getReviews().getValue();
        assertEquals(initialReviews, currentReviews);

    }

}

