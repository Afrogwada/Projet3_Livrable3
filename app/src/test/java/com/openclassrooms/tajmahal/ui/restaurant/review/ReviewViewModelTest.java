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

    @Test //test que le repository est appelé avec un review valide et que cet avis est bien ajouté.
    public void addReview_shouldCallRepositoryAddReviewAndShouldAddToLiveData() {
        // Création nouvel avis
        Review newReview = new Review("Toto", "photo2.jpg", "Très bon restaurant", 4);

        //simule ce que ferait le Repository (ajouter l'avis dans la liste)
        List<Review> updatedReviews = new ArrayList<>(initialReviews);
        updatedReviews.add(newReview);
        testReviewsLiveData.setValue(updatedReviews); // simulate DB update

        //Appelle la méthode
        testReviewViewModel.addReview(newReview);

        //Vérifie que le repository a bien été appelé
        verify(restaurantRepository).addReview(newReview);

        //Vérifie que l’avis est bien dans la LiveData
        List<Review> finalReviews = testReviewViewModel.getReviews().getValue();
        assertTrue(finalReviews.contains(newReview));
        assertEquals(2, finalReviews.size()); // 1 initial + 1 ajouté

    }

    @Test // test qu'un avis n'est jamais ajouté avec un auteur vide (nom vide remplacé par défaut )
    public void addReview_withEmptyAuthor_shouldReplaceEmptyBeforeCallRepository_AndShouldAddToLiveData() {
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

    @Test // test qu'un avis n'est jamais ajouté avec un auteur null (nom null remplacé par défaut )
    public void addReview_withNullAuthor_shouldReplaceEmptyBeforeCallRepository_AndShouldAddToLiveData() {
        // Crée un avis invalide : nom vide
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



    @Test // test que le repository n'est jammais appelé avec un review invalide
    public void addReview_withInvalidReview_shouldNotCallRepository() {
        // Crée un avis invalide : nom vide, commentaire vide, note invalide
        Review invalidReview = new Review("", "photo.jpg", "", 0);

        // Appelle la méthode
        testReviewViewModel.addReview(invalidReview);

        // Vérifie que rien n'est envoyé au repository
        verify(restaurantRepository, never()).addReview(any(Review.class));
    }
    @Test //test que si le repository lance une exception, la méthode la gère sans la propager.
    public void getReviews_shouldReturnLiveDataFromRepository() {
        List<Review> fakeReviews = Arrays.asList(
                new Review("Alice", "photo1", "Commentaire 1", 5),
                new Review("Bob", "photo2", "Commentaire 2", 4)
        );

        MutableLiveData<List<Review>> liveData = new MutableLiveData<>();
        liveData.setValue(fakeReviews);

        when(restaurantRepository.getReviews()).thenReturn(liveData);

        LiveData<List<Review>> result = testReviewViewModel.getReviews();

        assertEquals(fakeReviews, result.getValue());
    }

}

