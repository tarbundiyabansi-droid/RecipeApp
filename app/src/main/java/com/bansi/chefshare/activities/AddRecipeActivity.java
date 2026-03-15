package com.bansi.chefshare.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipeapp.databinding.ActivityAddRecipeBinding;
import com.bansi.chefshare.models.Recipe;
import com.bansi.chefshare.utils.ImageUtils;
import com.bansi.chefshare.viewmodels.RecipeViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private ActivityAddRecipeBinding binding;
    private String[] categories = {"Veg", "Non-Veg", "Dessert"};
    private List<String> selectedImagesBase64 = new ArrayList<>();
    private ImagePreviewAdapter imageAdapter;
    private RecipeViewModel recipeViewModel;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        processAndSetImage(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    processAndSetImage(bitmap);
                }
            }
    );

    private boolean isProcessingImage = false;

    private void processAndSetImage(Bitmap bitmap) {
        if (bitmap == null) return;
        if (selectedImagesBase64.size() >= 5) {
            Toast.makeText(this, "Max 5 images allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        isProcessingImage = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // Process Base64 in background thread to keep UI responsive
        new Thread(() -> {
            String b64 = ImageUtils.bitmapToBase64(bitmap);
            runOnUiThread(() -> {
                selectedImagesBase64.add(b64);
                imageAdapter.notifyDataSetChanged();
                isProcessingImage = false;
                binding.progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    private String editingRecipeId = null;
    private int originalLikes = 0;
    private java.util.Date originalTimestamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        // Setup Dropdown
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        binding.actCategory.setAdapter(catAdapter);

        // Setup Image RecyclerView
        imageAdapter = new ImagePreviewAdapter();
        binding.rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvImages.setAdapter(imageAdapter);

        // Check for Edit Mode
        if (getIntent().hasExtra("EDIT_RECIPE")) {
            Recipe editRecipe = (Recipe) getIntent().getSerializableExtra("EDIT_RECIPE");
            if (editRecipe != null) {
                editingRecipeId = editRecipe.getRecipeId();
                originalLikes = editRecipe.getLikes();
                originalTimestamp = editRecipe.getTimestamp();
                
                binding.etTitle.setText(editRecipe.getTitle());
                binding.etDescription.setText(editRecipe.getDescription());
                binding.actCategory.setText(editRecipe.getCategory(), false);
                binding.etIngredients.setText(editRecipe.getIngredients());
                binding.etSteps.setText(editRecipe.getSteps());
                
                if (editRecipe.getImagesBase64() != null) {
                    selectedImagesBase64.addAll(editRecipe.getImagesBase64());
                    imageAdapter.notifyDataSetChanged();
                }
                
                binding.btnSave.setText("Update Recipe");
                binding.toolbar.setTitle("Edit Recipe");
            }
        }

        binding.cvAddImage.setOnClickListener(v -> {
            if (selectedImagesBase64.size() >= 5) {
                Toast.makeText(this, "Max 5 images allowed", Toast.LENGTH_SHORT).show();
            } else {
                showImagePickerDialog();
            }
        });

        binding.btnSave.setOnClickListener(v -> saveRecipe());

        recipeViewModel.getRecipeActionSuccess().observe(this, success -> {
            if (success) {
                binding.progressBar.setVisibility(View.GONE);
                String msg = (editingRecipeId == null) ? "Recipe Posted!" : "Recipe Updated!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        recipeViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    cameraLauncher.launch(null);
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void showImagePickerDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(com.example.recipeapp.R.layout.dialog_image_picker, null);
        
        dialogView.findViewById(com.example.recipeapp.R.id.llCamera).setOnClickListener(v -> {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                cameraLauncher.launch(null);
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
            }
            dialog.dismiss();
        });

        dialogView.findViewById(com.example.recipeapp.R.id.llGallery).setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.show();
    }

    private void saveRecipe() {
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String category = binding.actCategory.getText().toString().trim();
        String ingredients = binding.etIngredients.getText().toString().trim();
        String steps = binding.etSteps.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.etTitle.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            binding.etDescription.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ingredients)) {
            binding.etIngredients.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(steps)) {
            binding.etSteps.setError("Required");
            return;
        }
        if (isProcessingImage) {
            Toast.makeText(this, "Processing images...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImagesBase64.isEmpty()) {
            Toast.makeText(this, "At least one image required", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        com.google.firebase.auth.FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String email = currentUser.getEmail();
        String username = (email != null && email.contains("@")) ? email.split("@")[0] : "Chef";

        if (editingRecipeId != null) {
            // Update mode
            Recipe updated = new Recipe(editingRecipeId, title, description, ingredients, steps, category, selectedImagesBase64, userId, username, originalLikes, originalTimestamp);
            recipeViewModel.updateRecipe(updated);
        } else {
            // Create mode
            Recipe recipe = new Recipe("", title, description, ingredients, steps, category, selectedImagesBase64, userId, username, 0, new java.util.Date());
            recipeViewModel.addRecipe(recipe);
        }
    }

    private class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(com.example.recipeapp.R.layout.item_image_preview, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String b64 = selectedImagesBase64.get(position);
            holder.imageView.setImageBitmap(ImageUtils.base64ToBitmap(b64));
            holder.removeBtn.setOnClickListener(v -> {
                selectedImagesBase64.remove(position);
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return selectedImagesBase64.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageButton removeBtn;
            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(com.example.recipeapp.R.id.ivPreview);
                removeBtn = itemView.findViewById(com.example.recipeapp.R.id.btnRemove);
            }
        }
    }
}
