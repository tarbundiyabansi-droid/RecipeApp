package com.bansi.chefshare.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bansi.chefshare.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;
    private LiveData<Boolean> loggedOutLiveData;
    private LiveData<String> authErrorLiveData;

    public AuthViewModel() {
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
        loggedOutLiveData = authRepository.getLoggedOutLiveData();
        authErrorLiveData = authRepository.getAuthErrorLiveData();
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void signup(String name, String email, String phone, String password) {
        authRepository.signup(name, email, phone, password);
    }

    public void logout() {
        authRepository.logout();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<Boolean> getLoggedOutLiveData() {
        return loggedOutLiveData;
    }

    public LiveData<String> getAuthErrorLiveData() {
        return authErrorLiveData;
    }
}
