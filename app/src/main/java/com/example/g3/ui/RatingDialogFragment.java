package com.example.g3.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.g3.R;

/**
 * @author Mike Wismont
 */
public class RatingDialogFragment extends DialogFragment
{
    private RatingBar mRatingBar;

    // Defines the listener interface that the parent fragment will implement
    public interface RatingDialogListener {
        void onFinishRatingDialog(int rating);
    }

    public static RatingDialogFragment buildInstance(Integer rating) {
        //Pass rating to the new dialog, using negative to indicate no selection has been made yet
        Bundle args = new Bundle();
        args.putInt("rating", rating != null ? rating : -1);

        RatingDialogFragment fragment = new RatingDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public RatingDialogFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rating, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int rating = getArguments().getInt("rating");
        mRatingBar = (RatingBar) view.findViewById(R.id.dialog_ratingbar);
        if (rating >= 0) {
            mRatingBar.setRating(rating);
        }

        //Configure the save button event listener
        view.findViewById(R.id.rating_save_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RatingDialogFragment.this.onSaveClicked();
            }
        });
    }

    /**
     * Event handler for the save button, pass results to the parent fragment and close the dialog
     */
    private void onSaveClicked() {
        RatingDialogListener listener = (RatingDialogListener) getTargetFragment();
        listener.onFinishRatingDialog((int)mRatingBar.getRating());
        dismiss();
    }
}
