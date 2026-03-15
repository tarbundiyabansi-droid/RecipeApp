package com.bansi.chefshare.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bansi.chefshare.models.Recipe;
import com.bansi.chefshare.utils.ImageUtils;
import com.bansi.chefshare.viewmodels.RecipeViewModel;
import com.example.recipeapp.databinding.ActivityRecipeDetailBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private ActivityRecipeDetailBinding binding;
    private Recipe recipe;
    private RecipeViewModel recipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipe = (Recipe) getIntent().getSerializableExtra("RECIPE");

        if (recipe != null) {
            displayDetails();
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnLike.setOnClickListener(v -> {
            recipeViewModel.updateLikes(recipe.getRecipeId(), recipe.getLikes());
            recipe.setLikes(recipe.getLikes() + 1);
            binding.btnLike.setText("Like (" + recipe.getLikes() + ")");
        });

        binding.btnDelete.setOnClickListener(v -> {
            recipeViewModel.deleteRecipe(recipe.getRecipeId());
        });

        recipeViewModel.getRecipeActionSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayDetails() {
        binding.tvTitleDetail.setText(recipe.getTitle());
        binding.tvDescriptionDetail.setText(recipe.getDescription());
        binding.tvIngredientsDetail.setText(recipe.getIngredients());
        binding.tvStepsDetail.setText(recipe.getSteps());
        binding.tvCategoryDetail.setText(recipe.getCategory());
        binding.tvAuthorDetail.setText("By Chef " + recipe.getUsername());
        binding.btnLike.setText("Like (" + recipe.getLikes() + ")");

        // Multi-image display
        List<String> images = recipe.getImagesBase64();
        if (images == null) images = new ArrayList<>();
        
        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(images);
        binding.vpRecipeImages.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(binding.tabIndicator, binding.vpRecipeImages, (tab, position) -> {
            // Dot is handled by selector
        }).attach();

        binding.tabIndicator.setVisibility(images.size() > 1 ? View.VISIBLE : View.GONE);

        String currentUserId = FirebaseAuth.getInstance().getUid();
        binding.btnDelete.setVisibility(View.GONE);
    }

    private class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {
        private List<String> images;

        ImagePagerAdapter(List<String> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String b64 = images.get(position);
            holder.imageView.setImageBitmap(ImageUtils.base64ToBitmap(b64));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ViewHolder(ImageView itemView) {
                super(itemView);
                this.imageView = itemView;
            }
        }
    }
}
