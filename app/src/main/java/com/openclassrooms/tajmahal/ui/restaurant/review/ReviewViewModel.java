package com.openclassrooms.tajmahal.ui.restaurant.review;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel pour gérer les données liées aux avis (reviews) d’un restaurant.
 * <p>
 * Il agit comme une couche entre le repository qui fournit les données et l’interface utilisateur.
 * Le ViewModel expose les données sous forme de LiveData afin que la UI puisse observer
 * les changements et se mettre à jour automatiquement.
 */
@HiltViewModel
public class ReviewViewModel extends ViewModel {
    public MutableLiveData<String> errorNewReview = new MutableLiveData<>();
    // Référence vers le repository pour accéder aux données de restaurant et reviews
    private final RestaurantRepository restaurantRepository;

    /**
     * Constructeur du ViewModel.
     *
     * @param repository Instance du RestaurantRepository pour récupérer et modifier les avis.
     */
    @Inject
    public ReviewViewModel(RestaurantRepository repository) {
        this.restaurantRepository = repository;
    }

    /**
     * Retourne la liste des avis sous forme de LiveData observable.
     * <p>
     * La UI peut observer cet objet LiveData pour recevoir les mises à jour automatiques
     * lorsque la liste des avis change (exemple : ajout d’un nouvel avis).
     *
     * @return LiveData contenant la liste des avis.
     */
    public LiveData<List<Review>> getReviews() {
        return restaurantRepository.getReviews();
    }

    /**
     * Ajoute un nouvel avis via le repository.
     * <p>
     * Cette méthode déclenche une modification des données qui mettra à jour automatiquement
     * la LiveData observée par la UI.
     *
     * @param review L’objet Review représentant le nouvel avis à ajouter.
     */
    public void addReview(Review review) {
        if (review.getUsername().trim().isEmpty()) {
            review.setUsername("Anonyme");
        }
        if (review.getComment().isEmpty()) {
            errorNewReview.setValue("Veuillez écrire un commentaire.");
        } else if (review.getRate() == 0) {
            errorNewReview.setValue("Veuillez attribuer une note.");
        } else {
            restaurantRepository.addReview(review);
        }
    }
}
