package com.openclassrooms.tajmahal.ui.restaurant.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    ReviewViewModel reviewViewModel;
    private List<Review> initialReviews;
    private MutableLiveData<List<Review>> reviewsLiveData;

    @Before
    public void setUp() {

        // Liste initiale fictive
        initialReviews = Arrays.asList(
                new Review("Alice", "photo1.jpg", "Très bon", 5)
        );
        reviewsLiveData = new MutableLiveData<>();
        reviewsLiveData.setValue(initialReviews);



        //config du mock
        when(restaurantRepository.getReviews()).thenReturn(reviewsLiveData);
        //création du ViewModel
        reviewViewModel = new ReviewViewModel(restaurantRepository);

    }

    @Test //test que le repository est appelé avec un review valide et que cet avis est bien ajouté.
    public void addReview_shouldCallRepositoryAddReviewAndShouldAddToLiveData() {
        // Création nouvel avis
        Review newReview = new Review("Toto", "photo2.jpg", "Très bon restaurant", 4);

        //simule ce que ferait le Repository (ajouter l'avis dans la liste)
        List<Review> updatedReviews = new ArrayList<>(initialReviews);
        updatedReviews.add(newReview);
        reviewsLiveData.setValue(updatedReviews); // simulate DB update

        //Appelle la méthode
        reviewViewModel.addReview(newReview);

        //Vérifie que le repository a bien été appelé
        verify(restaurantRepository).addReview(newReview);

        //Vérifie que l’avis est bien dans la LiveData
        List<Review> finalReviews = reviewViewModel.getReviews().getValue();
        assertTrue(finalReviews.contains(newReview));
        assertEquals(2, finalReviews.size()); // 1 initial + 1 ajouté

    }





    @Test // test que le repository n'est jammais appelé avec un review invalide
    public void addReview_withInvalidReview_shouldNotCallRepository() {
        // Crée un avis invalide : nom vide, commentaire vide, note invalide
        Review invalidReview = new Review("", "photo.jpg", "", 0);

        // Appelle la méthode
        reviewViewModel.addReview(invalidReview);

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

        LiveData<List<Review>> result = reviewViewModel.getReviews();

        assertEquals(fakeReviews, result.getValue());
    }

}

