package com.bansi.chefshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bansi.chefshare.adapters.RecipeAdapter;
import com.bansi.chefshare.models.Recipe;
import com.example.recipeapp.databinding.FragmentProfileBinding;
import com.bansi.chefshare.viewmodels.AuthViewModel;
import com.bansi.chefshare.viewmodels.RecipeViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private RecipeViewModel recipeViewModel;
    private RecipeAdapter myRecipesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        myRecipesAdapter = new RecipeAdapter(getContext(), new RecipeAdapter.OnRecipeActionListener() {
            @Override
            public void onDeleteClick(Recipe recipe) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete Recipe")
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

        binding.rvMyRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyRecipes.setAdapter(myRecipesAdapter);

        String currentUserId = FirebaseAuth.getInstance().getUid();
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "No Email";
        
        binding.tvUserEmail.setText(currentEmail);

        // Fetch user details from Realtime Database
        if (currentUserId != null) {
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users").child(currentUserId)
                    .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                String phone = snapshot.child("phone").getValue(String.class);
                                binding.tvUserName.setText(name);
                                binding.tvUserPhone.setText(phone);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {}
                    });
            
            binding.progressBar.setVisibility(View.VISIBLE);
            recipeViewModel.fetchUserRecipes(currentUserId);
        }
        
        recipeViewModel.getUserRecipesLiveData().observe(getViewLifecycleOwner(), recipes -> {
            binding.progressBar.setVisibility(View.GONE);
            if (recipes != null) {
                myRecipesAdapter.setRecipes(recipes);
            }
        });

        recipeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.progressBar.setVisibility(View.GONE);
                android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_LONG).show();
            }
        });

        recipeViewModel.getRecipeActionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success && currentUserId != null) {
                android.widget.Toast.makeText(getContext(), "Recipe deleted", android.widget.Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.VISIBLE);
                recipeViewModel.fetchUserRecipes(currentUserId);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        authViewModel.logout();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finishAffinity();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}
