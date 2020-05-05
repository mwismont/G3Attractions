package com.example.g3.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.g3.model.Rating;

/**
 * This class provides access to the {@link Rating} entity. <br>
 * Note that the methods execute async.
 *
 * @author Mike Wismont
 */
public class RatingRepository
{
    private RatingDao ratingDao;

    public RatingRepository(Application application) {
        G3RoomDatabase db = G3RoomDatabase.getInstance(application);
        this.ratingDao = db.ratingDao();
    }

    public LiveData<Rating> getRating(int userId, int attractionId) {
        //Note: Doesn't need wrapped in async task because it returns LiveData
        return ratingDao.getUserRatingForAttraction(userId, attractionId);
    }

    public void insert(Rating rating) {
        G3RoomDatabase.writeExecutor.execute(() -> {
            ratingDao.insert(rating);
        });
    }

    public void update(Rating rating) {
        G3RoomDatabase.writeExecutor.execute(() -> {
            ratingDao.update(rating);
        });
    }
}
