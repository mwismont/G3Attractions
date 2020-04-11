package com.example.g3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.g3.model.User;

@Dao
public interface UserDao
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insert(User user);

    @Query("SELECT * FROM user WHERE id = 1")
    public LiveData<User> getDefaultUser();
}
