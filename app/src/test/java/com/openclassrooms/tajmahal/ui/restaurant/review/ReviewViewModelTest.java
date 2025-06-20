package com.openclassrooms.tajmahal.ui.restaurant.review;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Test
    public void addReview_shouldCallRepositoryAddReview() {
        Review review = new Review("Alice", "", "Très bon restaurant",4);

        reviewViewModel.addReview(review);

        verify(restaurantRepository).addReview(review);
    }
    @Test
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