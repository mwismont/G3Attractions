package com.example.g3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.g3.model.Rating;

/**
 * The Data Access Object for the {@link Rating} entity.
 *
 * @author Mike Wismont
 */
@Dao
public interface RatingDao
{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public void insert(Rating rating);

    @Update
    public void update(Rating rating);

    @Query("SELECT * FROM rating WHERE user_id = :userId AND attraction_id = :attractionId")
    public LiveData<Rating> getUserRatingForAttraction(int userId, int attractionId);
}
