package com.bansi.chefshare.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe implements Serializable {
    private String recipeId;
    private String title;
    private String description;
    private String ingredients;
    private String steps;
    private String category;
    private List<String> imagesBase64;
    private String userId;
    private String username;
    private int likes;
    private Date timestamp;

    public Recipe() {
        this.imagesBase64 = new ArrayList<>();
    }

    public Recipe(String recipeId, String title, String description, String ingredients, String steps, String category, List<String> imagesBase64, String userId, String username, int likes, Date timestamp) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
        this.category = category;
        this.imagesBase64 = imagesBase64 != null ? imagesBase64 : new ArrayList<>();
        this.userId = userId;
        this.username = username;
        this.likes = likes;
        this.timestamp = timestamp;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImagesBase64() {
        return imagesBase64;
    }

    public void setImagesBase64(List<String> imagesBase64) {
        this.imagesBase64 = imagesBase64;
    }

    public void addImage(String base64) {
        if (this.imagesBase64 == null) this.imagesBase64 = new ArrayList<>();
        this.imagesBase64.add(base64);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
