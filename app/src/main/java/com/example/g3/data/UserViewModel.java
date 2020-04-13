package com.example.g3.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.g3.model.User;

/**
 * The ViewModel for the {@link  User} entity
 *
 * @author Mike Wismont
 */
public class UserViewModel extends AndroidViewModel
{
    private UserRepository repository;

    private LiveData<User> userData;

    public UserViewModel(Application application)
    {
        super(application);
        System.out.println("Constructing new user view model");
        this.repository = new UserRepository(application);
        this.userData = repository.getDefaultUser();
    }

    public LiveData<User> getUserData() {
        return this.userData;
    }

    public void persist() {
        this.repository.update(this.userData.getValue());
    }
}
