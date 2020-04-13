package com.example.g3.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.g3.model.User;

/**
 * This class provides access to the {@link User} entity. <br>
 * Note that the methods execute async.
 *
 * @author Mike Wismont
 */
public class UserRepository
{
    private UserDao userDao;
    private LiveData<User> defaultUser;

    UserRepository(Application application) {
       G3RoomDatabase db = G3RoomDatabase.getInstance(application);
       this.userDao = db.userDao();
       this.defaultUser = this.userDao.getDefaultUserLiveData(); //Note: This will load async
    }

    LiveData<User> getDefaultUser() {
        return this.defaultUser;
    }

    void insert(User user) {
        G3RoomDatabase.writeExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    void update(User user) {
        G3RoomDatabase.writeExecutor.execute(() -> {
            userDao.update(user);
        });
    }
}
