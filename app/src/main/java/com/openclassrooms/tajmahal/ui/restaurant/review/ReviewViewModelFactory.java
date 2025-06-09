package com.openclassrooms.tajmahal.ui.restaurant.review;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;

/**
 * ViewModelFactory sert à personnaliser la création de ViewModel.
 * On la crée quand le ViewModel a un constructeur avec paramètres.
 * Elle est généralement dans le même package que le ViewModel.
 * On l’utilise dans l’Activity ou le Fragment pour récupérer une instance du ViewModel correctement construite.
 */
public class ReviewViewModelFactory implements ViewModelProvider.Factory {

    private final RestaurantRepository restaurantRepository;

    /**
     * Constructeur de la factory.
     *
     * @param repository L'instance de {@link RestaurantRepository} à fournir au ViewModel.
     */
    public ReviewViewModelFactory(RestaurantRepository repository) {
        this.restaurantRepository = repository;
    }

    /**
     * Crée une instance du ViewModel demandée.
     *
     * @param modelClass La classe du ViewModel à instancier.
     * @param <T>        Le type de ViewModel.
     * @return Une instance de ReviewViewModel avec le repository injecté.
     * @throws IllegalArgumentException si la classe ViewModel n’est pas celle attendue.
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReviewViewModel.class)) {
            // Cast sûr puisque modelClass est bien de type ReviewViewModel
            return (T) new ReviewViewModel(restaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}