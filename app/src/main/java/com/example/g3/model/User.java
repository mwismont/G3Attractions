package com.example.g3.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="user")
public class User
{
    @PrimaryKey
    @ColumnInfo(name="id")
    private int id;

    @ColumnInfo(name="first_name")
    @NonNull
    private String firstName = "";

    @ColumnInfo(name="last_name")
    @NonNull
    private String lastName = "";

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    @NonNull
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName)
    {
        this.firstName = firstName;
    }

    @NonNull
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(@NonNull String lastName)
    {
        this.lastName = lastName;
    }
}
