package com.example.g3.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * A persistent object used to model a user's rating of an attraction
 *
 * @author Mike Wismont
 */
@Entity(tableName="rating", primaryKeys={"user_id", "attraction_id"})
public class Rating
{
    @ColumnInfo(name="user_id")
    private int userId;

    @ColumnInfo(name="attraction_id")
    private int attractionId;

    @ColumnInfo(name="rating")
    private int value;

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public int getAttractionId()
    {
        return attractionId;
    }

    public void setAttractionId(int attractionId)
    {
        this.attractionId = attractionId;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }
}
