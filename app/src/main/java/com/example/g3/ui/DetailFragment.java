/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.g3.ui;

import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.g3.R;
import com.example.g3.common.Constants;
import com.example.g3.common.Utils;
import com.example.g3.data.RatingRepository;
import com.example.g3.model.Attraction;
import com.example.g3.model.Rating;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import static com.example.g3.provider.TouristAttractions.ATTRACTIONS;

/**
 * The tourist attraction detail fragment which contains the details of a
 * a single attraction (contained inside {@link com.example.g3.ui.DetailActivity}).
 */
public class DetailFragment extends Fragment implements RatingDialogFragment.RatingDialogListener
{
    private static final String EXTRA_ATTRACTION = "attraction";
    private Attraction mAttraction;
    private RatingRepository mRatingRepository;

    public static DetailFragment createInstance(String attractionName) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ATTRACTION, attractionName);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        String attractionName = getArguments().getString(EXTRA_ATTRACTION);
        mAttraction = findAttraction(attractionName);

        if (mAttraction == null) {
            getActivity().finish();
            return null;
        }

        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        TextView descTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        TextView distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        FloatingActionButton mapFab = (FloatingActionButton) view.findViewById(R.id.mapFab);
        FloatingActionButton actionShare = (FloatingActionButton) view.findViewById(R.id.action_share);


        LatLng location = Utils.getLocation(getActivity());
        String distance = Utils.formatDistanceBetween(location, mAttraction.location);
        if (TextUtils.isEmpty(distance)) {
            distanceTextView.setVisibility(View.GONE);
        }

        nameTextView.setText(attractionName);
        distanceTextView.setText(distance);
        descTextView.setText(mAttraction.longDescription);

        int imageSize = getResources().getDimensionPixelSize(R.dimen.image_size)
                * Constants.IMAGE_ANIM_MULTIPLIER;
        Glide.with(getActivity())
                .load(mAttraction.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.color.lighter_gray)
                .override(imageSize, imageSize)
                .into(imageView);
        actionShare.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                String uri = (String.valueOf(mAttraction.locationUrl));
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String ShareSub = "Check this place out!";
                intent.putExtra(intent.EXTRA_SUBJECT, ShareSub);
                intent.putExtra(intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Constants.MAPS_INTENT_URI +
                        Uri.encode(mAttraction.name + ", " + mAttraction.city)));

                try {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), R.string.action_map_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Rating logic code added by Michael Wismont
        RatingBar ratingBar = view.findViewById(R.id.attractionRatingBar);

        this.mRatingRepository = new RatingRepository(getActivity().getApplication());

        //Note: UserID would typically come from the persistent user object, hardcoded for now
        LiveData<Rating> ratingData = mRatingRepository.getRating(1, this.mAttraction.id);

        ratingData.observe(getViewLifecycleOwner(), rating -> {
            this.mAttraction.setRating(ratingData.getValue());
        });

        //The bar only exists as a child of this fragment on tablets
        if (ratingBar != null) {
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
            {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    DetailFragment.this.saveRating((int)rating);
                }
            });
        }
        else  {
            FloatingActionButton actionRate = (FloatingActionButton) view.findViewById(R.id.action_rate);
            actionRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RatingDialogFragment dialog = RatingDialogFragment.buildInstance(mAttraction.getRatingValue());
                    dialog.setTargetFragment(DetailFragment.this,1);
                    dialog.show(DetailFragment.this.getParentFragmentManager(),  "fragment_rating");
                }
            });
        }

        return view;
    }

    /**
     * Event handler fired when the rating dialog is saved.
     *
     * @param rating selected by the user for the currently displayed attraction
     * @author Mike Wismont
     */
    @Override
    public void onFinishRatingDialog(int rating) {
        this.saveRating(rating);
    }

    /**
     * Persist the rating with the currently displayed attraction
     *
     * @param ratingValue to save
     * @author Mike Wismont
     */
    private void saveRating(int ratingValue)
    {
        System.out.println("Save Rating: " + ratingValue);
        Rating rating = this.mAttraction.getRating();

        if (rating == null) {
            rating = new Rating();
            this.mAttraction.setRating(rating);

            rating.setUserId(1); //Note: This would typically come from the persistent user object, hardcoded for now
            rating.setAttractionId(mAttraction.id);
            rating.setValue(ratingValue);
            this.mRatingRepository.insert(rating);
        }
        else {
            rating.setValue(ratingValue);
            this.mRatingRepository.update(rating);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Some small additions to handle "up" navigation correctly
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Check if up activity needs to be created (usually when
                // detail screen is opened from a notification or from the
                // Wearable app
                if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)
                        || getActivity().isTaskRoot()) {

                    // Synthesize parent stack
                    TaskStackBuilder.create(getActivity())
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // On Lollipop+ we finish so to run the nice animation
                    getActivity().finishAfterTransition();
                    return true;
                }

                // Otherwise let the system handle navigating "up"
                return false;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * Really hacky loop for finding attraction in our static content provider.
     * Obviously would not be used in a production app.
     */
    private Attraction findAttraction(String attractionName) {
        for (Map.Entry<String, List<Attraction>> attractionsList : ATTRACTIONS.entrySet()) {
            List<Attraction> attractions = attractionsList.getValue();
            for (Attraction attraction : attractions) {
                if (attractionName.equals(attraction.name)) {
                    return attraction;
                }
            }
        }
        return null;
    }





}

