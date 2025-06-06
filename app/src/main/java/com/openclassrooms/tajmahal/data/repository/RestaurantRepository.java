package com.openclassrooms.tajmahal.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.tajmahal.data.service.RestaurantApi;
import com.openclassrooms.tajmahal.domain.model.Restaurant;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * This is the repository class for managing restaurant data. Repositories are responsible
 * for coordinating data operations from data sources such as network APIs, databases, etc.
 *
 * Typically in an Android app built with architecture components, the repository will handle
 * the logic for deciding whether to fetch data from a network source or use data from a local cache.
 *
 *
 * @see Restaurant
 * @see RestaurantApi
 */
@Singleton
public class RestaurantRepository {

    // The API interface instance that will be used for network requests related to restaurant data.
    private final RestaurantApi restaurantApi;

    // MutableLiveData that holds the list of reviews and allows updates
    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();

    /**
     * Constructs a new instance of {@link RestaurantRepository} with the given {@link RestaurantApi}.
     *
     * @param restaurantApi The network API interface for fetching restaurant data.
     */
    @Inject
    public RestaurantRepository(RestaurantApi restaurantApi) {
        this.restaurantApi = restaurantApi;

        // Initialiser la MutableLiveData avec la liste actuelle des reviews depuis l'API
        // On crée une nouvelle ArrayList pour pouvoir modifier la liste sans affecter l'originale
        List<Review> initialReviews = new ArrayList<>(restaurantApi.getReviews());
        reviewsLiveData.setValue(initialReviews);
    }

    /**
     * Fetches the restaurant details.
     *
     * This method will make a network call using the provided {@link RestaurantApi} instance
     * to fetch restaurant data. Note that error handling and any transformations on the data
     * would need to be managed.
     *
     *
     * @return LiveData holding the restaurant details.
     */
    public LiveData<Restaurant> getRestaurant() {
        return new MutableLiveData<>(restaurantApi.getRestaurant());
    }

    /**
     * Returns a LiveData wrapping the list of reviews.
     *
     * @return LiveData containing the list of reviews.
     */
    public LiveData<List<Review>> getReviews() {
        return reviewsLiveData;
    }

    /**
     * Adds a new review to the list and updates the LiveData.
     *
     * @param review The new review to add.
     */
    public void addReview(Review review) {
        List<Review> currentReviews = reviewsLiveData.getValue();
        if (currentReviews != null) {
            currentReviews.add(0, review); // Ajout en tête de liste
            reviewsLiveData.setValue(currentReviews); // Notifie les observateurs
        }

    }
}
