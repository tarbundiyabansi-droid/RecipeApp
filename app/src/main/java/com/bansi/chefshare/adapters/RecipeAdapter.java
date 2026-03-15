package com.bansi.chefshare.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bansi.chefshare.activities.RecipeDetailActivity;
import com.example.recipeapp.databinding.ItemRecipeBinding;
import com.bansi.chefshare.models.Recipe;
import com.bansi.chefshare.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeActionListener {
        void onDeleteClick(Recipe recipe);
        void onEditClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private Context context;
    private OnRecipeActionListener actionListener;

    public RecipeAdapter(Context context, OnRecipeActionListener actionListener) {
        this.context = context;
        this.actionListener = actionListener;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecipeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        ItemRecipeBinding binding;

        public RecipeViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Recipe recipe) {
            binding.tvRecipeTitle.setText(recipe.getTitle());
            binding.tvRecipeDescription.setText(recipe.getDescription());
            binding.tvUsername.setText("Chef " + recipe.getUsername());
            
            // Format Date (Simple version for stability)
            if (recipe.getTimestamp() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
                binding.tvTimestamp.setText("Posted: " + sdf.format(recipe.getTimestamp()));
            }

            // Show options button only if current user is the owner
            String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            if (currentUserId != null && currentUserId.equals(recipe.getUserId())) {
                binding.btnOptions.setVisibility(android.view.View.VISIBLE);
                binding.btnOptions.setOnClickListener(v -> showPopupMenu(v, recipe));
            } else {
                binding.btnOptions.setVisibility(android.view.View.GONE);
            }

            // Decode and set image (Use the first one from the list)
            if (recipe.getImagesBase64() != null && !recipe.getImagesBase64().isEmpty()) {
                try {
                    String firstImage = recipe.getImagesBase64().get(0);
                    Bitmap bitmap = ImageUtils.base64ToBitmap(firstImage);
                    binding.ivRecipe.setImageBitmap(bitmap);
                } catch (Exception e) {
                    binding.ivRecipe.setImageResource(com.example.recipeapp.R.drawable.ic_chef_logo);
                }
            } else {
                binding.ivRecipe.setImageResource(com.example.recipeapp.R.drawable.ic_chef_logo);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra("RECIPE", recipe);
                context.startActivity(intent);
            });
        }

        private void showPopupMenu(android.view.View view, Recipe recipe) {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(context, view);
            popupMenu.getMenu().add("Edit");
            popupMenu.getMenu().add("Delete");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Edit")) {
                    if (actionListener != null) actionListener.onEditClick(recipe);
                    return true;
                } else if (item.getTitle().equals("Delete")) {
                    if (actionListener != null) actionListener.onDeleteClick(recipe);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        }
    }
}
