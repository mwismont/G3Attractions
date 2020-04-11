package com.example.g3.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.g3.model.User;

public class UserViewModel extends AndroidViewModel
{
    private UserRepository repository;

    private LiveData<User> user;

    public UserViewModel(Application application)
    {
        super(application);
        this.repository = new UserRepository(application);
        this.user = repository.getDefaultUser();
    }

    public LiveData<User> getUser() {
        return this.user;
    }
}
