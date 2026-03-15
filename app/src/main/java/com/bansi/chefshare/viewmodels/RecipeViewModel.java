package com.bansi.chefshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bansi.chefshare.models.Recipe;
import com.bansi.chefshare.repository.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository recipeRepository;
    private LiveData<List<Recipe>> recipesLiveData;
    private LiveData<List<Recipe>> userRecipesLiveData;
    private LiveData<Boolean> recipeActionSuccess;
    private LiveData<String> errorLiveData;

    public RecipeViewModel() {
        recipeRepository = new RecipeRepository();
        recipesLiveData = recipeRepository.getRecipesLiveData();
        userRecipesLiveData = recipeRepository.getUserRecipesLiveData();
        recipeActionSuccess = recipeRepository.getRecipeActionSuccess();
        errorLiveData = recipeRepository.getErrorLiveData();
    }

    public void addRecipe(Recipe recipe) {
        recipeRepository.addRecipe(recipe);
    }

    public void updateRecipe(Recipe recipe) {
        recipeRepository.updateRecipe(recipe);
    }

    public void fetchAllRecipes() {
        recipeRepository.fetchAllRecipes();
    }

    public void fetchUserRecipes(String userId) {
        recipeRepository.fetchUserRecipes(userId);
    }

    public void updateLikes(String recipeId, int currentLikes) {
        recipeRepository.updateLikes(recipeId, currentLikes);
    }

    public void deleteRecipe(String recipeId) {
        recipeRepository.deleteRecipe(recipeId);
    }

    public LiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public LiveData<List<Recipe>> getUserRecipesLiveData() {
        return userRecipesLiveData;
    }

    public LiveData<Boolean> getRecipeActionSuccess() {
        return recipeActionSuccess;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
