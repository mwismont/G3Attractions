package com.example.g3.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.g3.R;
import com.example.g3.data.UserViewModel;
import com.example.g3.databinding.ActivityProfileBinding;

/**
 * An activity that provides an interface to manage the user's profile
 *
 * @author Mike Wismont
 */
public class ProfileActivity extends AppCompatActivity
{
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        ActivityProfileBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setLifecycleOwner(this);
        binding.setUserViewModel(userViewModel);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        userViewModel.persist();
    }
}
