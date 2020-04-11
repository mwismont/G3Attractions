package com.example.g3.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.g3.R;
import com.example.g3.data.UserViewModel;
import com.example.g3.model.User;

public class ProfileActivity extends AppCompatActivity
{
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUser().observe(this, new Observer<User>()
        {
            @Override
            public void onChanged(User user)
            {
                System.out.println("User changed!");
            }
        });
    }
}
