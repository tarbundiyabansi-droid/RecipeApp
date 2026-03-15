package com.bansi.chefshare.repository;

import androidx.lifecycle.MutableLiveData;

import com.bansi.chefshare.models.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {

    private DatabaseReference recipesRef;
    private MutableLiveData<List<Recipe>> recipesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Recipe>> userRecipesLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> recipeActionSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public RecipeRepository() {
        this.recipesRef = FirebaseDatabase.getInstance().getReference("recipes");
    }

    public void addRecipe(Recipe recipe) {
        String id = recipesRef.push().getKey();
        if (id != null) {
            recipe.setRecipeId(id);
            recipesRef.child(id).setValue(recipe)
                    .addOnSuccessListener(aVoid -> recipeActionSuccess.postValue(true))
                    .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
        }
    }

    public void updateRecipe(Recipe recipe) {
        if (recipe.getRecipeId() != null) {
            recipesRef.child(recipe.getRecipeId()).setValue(recipe)
                    .addOnSuccessListener(aVoid -> recipeActionSuccess.postValue(true))
                    .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
        }
    }

    public void fetchAllRecipes() {
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Recipe> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);
                    list.add(recipe);
                }
                // Reverse to show latest first
                Collections.reverse(list);
                recipesLiveData.postValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                errorLiveData.postValue(error.getMessage());
            }
        });
    }

    public void fetchUserRecipes(String userId) {
        recipesRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Recipe> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);
                    list.add(recipe);
                }
                Collections.reverse(list);
                userRecipesLiveData.postValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                errorLiveData.postValue(error.getMessage());
            }
        });
    }

    public void updateLikes(String recipeId, int currentLikes) {
        recipesRef.child(recipeId).child("likes").setValue(currentLikes + 1);
    }

    public void deleteRecipe(String recipeId) {
        recipesRef.child(recipeId).removeValue()
                .addOnSuccessListener(aVoid -> recipeActionSuccess.postValue(true))
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    public MutableLiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public MutableLiveData<List<Recipe>> getUserRecipesLiveData() {
        return userRecipesLiveData;
    }

    public MutableLiveData<Boolean> getRecipeActionSuccess() {
        return recipeActionSuccess;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
