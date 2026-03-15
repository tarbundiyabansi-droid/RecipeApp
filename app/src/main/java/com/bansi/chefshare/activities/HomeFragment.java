package com.bansi.chefshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.bansi.chefshare.adapters.RecipeAdapter;
import com.example.recipeapp.databinding.FragmentHomeBinding;
import com.bansi.chefshare.models.Recipe;
import com.bansi.chefshare.viewmodels.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecipeViewModel recipeViewModel;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> allRecipes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeAdapter = new RecipeAdapter(getContext(), new RecipeAdapter.OnRecipeActionListener() {
            @Override
            public void onDeleteClick(Recipe recipe) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete Recipe?")
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            binding.progressBar.setVisibility(View.VISIBLE);
                            recipeViewModel.deleteRecipe(recipe.getRecipeId());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onEditClick(Recipe recipe) {
                Intent intent = new Intent(getActivity(), AddRecipeActivity.class);
                intent.putExtra("EDIT_RECIPE", recipe);
                startActivity(intent);
            }
        });

        binding.rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecipes.setAdapter(recipeAdapter);

        binding.progressBar.setVisibility(View.VISIBLE);
        recipeViewModel.fetchAllRecipes();

        recipeViewModel.getRecipesLiveData().observe(getViewLifecycleOwner(), recipes -> {
            binding.progressBar.setVisibility(View.GONE);
            if (recipes != null) {
                allRecipes = recipes;
                recipeAdapter.setRecipes(recipes);
                binding.tvEmptyState.setVisibility(recipes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        recipeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.progressBar.setVisibility(View.GONE);
                android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_LONG).show();
            }
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterRecipes(String query) {
        List<Recipe> filteredList = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    recipe.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(recipe);
            }
        }
        recipeAdapter.setRecipes(filteredList);
    }
}
