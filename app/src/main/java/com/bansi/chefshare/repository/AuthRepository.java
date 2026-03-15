package com.bansi.chefshare.repository;

import androidx.lifecycle.MutableLiveData;

import com.bansi.chefshare.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class AuthRepository {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> loggedOutLiveData;
    private MutableLiveData<String> authErrorLiveData;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
        this.userLiveData = new MutableLiveData<>();
        this.loggedOutLiveData = new MutableLiveData<>();
        this.authErrorLiveData = new MutableLiveData<>();
    }

    public void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userLiveData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        authErrorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    public void signup(String name, String email, String phone, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User user = new User(firebaseUser.getUid(), name, email, phone, new Date());
                        usersRef.child(firebaseUser.getUid())
                                .setValue(user)
                                .addOnSuccessListener(aVoid -> userLiveData.postValue(firebaseUser))
                                .addOnFailureListener(e -> {
                                    authErrorLiveData.postValue("Database Error: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> authErrorLiveData.postValue("Auth Error: " + e.getMessage()));
    }

    public void logout() {
        firebaseAuth.signOut();
        loggedOutLiveData.postValue(true);
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }

    public MutableLiveData<String> getAuthErrorLiveData() {
        return authErrorLiveData;
    }
}
