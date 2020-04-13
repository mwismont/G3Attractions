package com.example.g3.ui;

import android.content.Intent;

import com.example.g3.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class AttractionListActivityTest
{
    private AttractionListActivity activity;

    @Before
    public void setUp() throws Exception
    {
        activity = Robolectric.buildActivity( AttractionListActivity.class )
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() throws Exception
    {
        Assert.assertNotNull( activity );
    }

    @Test
    public void shouldHaveCorrectAppName() throws Exception {
        String appName = activity.getResources().getString(R.string.app_name);
        Assert.assertEquals("G3Attractions", appName);
    }

    @Test
    public void contactSupportOpensEmail() throws Exception {
        // Get shadow
        ShadowActivity shadowActivity = shadowOf(activity);

        // Click menu
        shadowActivity.clickMenuItem(R.id.contact_support_item);

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        // Verify the intent was started with the correct action type
        Assert.assertEquals(Intent.ACTION_SENDTO.toString(), startedIntent.getAction());
    }

    @Test
    public void viewProfileIntent() throws Exception
    {
        // Get shadow
        ShadowActivity shadowActivity = shadowOf(activity);

        // Click menu
        shadowActivity.clickMenuItem(R.id.profile_item);

        // Get intent
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);

        // Verify the profile activity was started
        Assert.assertEquals(ProfileActivity.class, shadowIntent.getIntentClass());
    }
}
