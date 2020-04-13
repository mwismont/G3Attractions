package com.example.g3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.g3.model.User;

/**
 * The Data Access Object for the {@link User} entity.
 *
 * @author Mike Wismont
 */
@Dao
public interface UserDao
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insert(User user);

    @Update
    public void update(User user);

    @Query("SELECT * FROM user WHERE id = 1")
    public LiveData<User> getDefaultUserLiveData();

    @Query("SELECT * FROM user WHERE id = 1")
    public User getDefaultUser();
}
