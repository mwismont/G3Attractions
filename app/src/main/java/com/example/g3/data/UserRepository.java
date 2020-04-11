package com.example.g3.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.g3.model.User;

public class UserRepository
{
    private UserDao userDao;
    private LiveData<User> defaultUser;

    UserRepository(Application application) {
       G3RoomDatabase db = G3RoomDatabase.getInstance(application);
       this.userDao = db.userDao();
       this.defaultUser = this.userDao.getDefaultUser();

       System.out.println("User repository setup");
       if (defaultUser != null) {
           System.out.println("Default user exists");
       }
    }

    LiveData<User> getDefaultUser() {
        return this.defaultUser;
    }

    void insert(User user) {
        G3RoomDatabase.writeExecutor.execute(() -> {
            userDao.insert(user);
        });
    }
}
