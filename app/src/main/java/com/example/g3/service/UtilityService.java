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

package com.example.g3.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.g3.R;
import com.example.g3.model.Attraction;
import com.example.g3.common.Constants;
import com.example.g3.common.Utils;
import com.example.g3.provider.TouristAttractions;
import com.example.g3.ui.DetailActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.example.g3.provider.TouristAttractions.ATTRACTIONS;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * A utility IntentService, used for a variety of asynchronous background
 * operations that do not necessarily need to be tied to a UI.
 */
public class UtilityService extends IntentService
{
    private static final String TAG = UtilityService.class.getSimpleName();

    public static final String ACTION_GEOFENCE_TRIGGERED = "geofence_triggered";
    private static final String ACTION_LOCATION_UPDATED = "location_updated";
    private static final String ACTION_REQUEST_LOCATION = "request_location";
    private static final String ACTION_CLEAR_NOTIFICATION = "clear_notification";
    private static final String ACTION_CLEAR_REMOTE_NOTIFICATIONS = "clear_remote_notifications";
    private static final String ACTION_FAKE_UPDATE = "fake_update";
    private static final String EXTRA_TEST_MICROAPP = "test_microapp";

    public static IntentFilter getLocationUpdatedIntentFilter()
    {
        return new IntentFilter(UtilityService.ACTION_LOCATION_UPDATED);
    }

    public static void requestLocation(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_REQUEST_LOCATION);
        context.startService(intent);
    }

    public static void clearNotification(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_CLEAR_NOTIFICATION);
        context.startService(intent);
    }

    public static Intent getClearRemoteNotificationsIntent(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(UtilityService.ACTION_CLEAR_REMOTE_NOTIFICATIONS);
        return intent;
    }

    public UtilityService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_REQUEST_LOCATION.equals(action)) {
            requestLocationInternal();
        } else if (ACTION_LOCATION_UPDATED.equals(action)) {
            locationUpdated(intent);
        } else if (ACTION_CLEAR_NOTIFICATION.equals(action)) {
            clearNotificationInternal();
        } else if (ACTION_FAKE_UPDATE.equals(action)) {
            LatLng currentLocation = Utils.getLocation(this);

            // If location unknown use test city, otherwise use closest city
            String city = currentLocation == null ? TouristAttractions.TEST_CITY :
                    TouristAttractions.getClosestCity(currentLocation);

            showNotification(city,
                    intent.getBooleanExtra(EXTRA_TEST_MICROAPP, Constants.USE_MICRO_APP));
        }
    }

    /**
     * Called when a location update is requested
     */
    private void requestLocationInternal() {
        Log.v(TAG, ACTION_REQUEST_LOCATION);

        if (!Utils.checkFineLocationPermission(this)) {
            return;
        }

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        // It's OK to use blockingConnect() here as we are running in an
        // IntentService that executes work on a separate (background) thread.
        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                Constants.GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {

            Intent locationUpdatedIntent = new Intent(this, UtilityService.class);
            locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);

            // Send last known location out first if available
            Location location = FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                Intent lastLocationIntent = new Intent(locationUpdatedIntent);
                lastLocationIntent.putExtra(
                        FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
                startService(lastLocationIntent);
            }

            // Request new location
            LocationRequest mLocationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest,
                    PendingIntent.getService(this, 0, locationUpdatedIntent, 0));

            googleApiClient.disconnect();
        } else {
            Log.e(TAG, String.format(Constants.GOOGLE_API_CLIENT_ERROR_MSG,
                    connectionResult.getErrorCode()));
        }
    }

    /**
     * Called when the location has been updated
     */
    private void locationUpdated(Intent intent) {
        Log.v(TAG, ACTION_LOCATION_UPDATED);

        // Extra new location
        Location location =
                intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if (location != null) {
            LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());

            // Store in a local preference as well
            Utils.storeLocation(this, latLngLocation);

            // Send a local broadcast so if an Activity is open it can respond
            // to the updated location
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    /**
     * Clears the local device notification
     */
    private void clearNotificationInternal() {
        Log.v(TAG, ACTION_CLEAR_NOTIFICATION);
        NotificationManagerCompat.from(this).cancel(Constants.MOBILE_NOTIFICATION_ID);
    }


    /**
     * Show the notification. Either the regular notification with wearable features
     * added to enhance, or trigger the full micro app on the wearable.
     *
     * @param cityId The city to trigger the notification for
     * @param microApp If the micro app should be triggered or just enhanced notifications
     */
    private void showNotification(String cityId, boolean microApp)
    {

        List<Attraction> attractions = ATTRACTIONS.get(cityId);

        // The first (closest) tourist attraction
        Attraction attraction = attractions.get(0);

        // Limit attractions to send
        int count = attractions.size() > Constants.MAX_ATTRACTIONS ?
                Constants.MAX_ATTRACTIONS : attractions.size();

        // Pull down the tourist attraction images from the network and store
        HashMap<String, Bitmap> bitmaps = new HashMap<>();
        try {
            for (int i = 0; i < count; i++) {
                bitmaps.put(attractions.get(i).name,
                        Glide.with(this)
                                .load(attractions.get(i).imageUrl)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(Constants.WEAR_IMAGE_SIZE, Constants.WEAR_IMAGE_SIZE)
                                .get());
            }
        }
        catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error fetching image from network: " + e);
        }

        // The intent to trigger when the notification is tapped
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                DetailActivity.getLaunchIntent(this, attraction.name),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // The intent to trigger when the notification is dismissed, in this case
        // we want to clear remote notifications as well
        PendingIntent deletePendingIntent =
                PendingIntent.getService(this, 0, getClearRemoteNotificationsIntent(this), 0);

        // Construct the main notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmaps.get(attraction.name))
                        .setBigContentTitle(attraction.name)
                        .setSummaryText(getString(R.string.nearby_attraction))
                )
                .setLocalOnly(microApp)
                .setContentTitle(attraction.name)
                .setContentText(getString(R.string.nearby_attraction))
                .setSmallIcon(R.drawable.ic_stat_maps_pin_drop)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setAutoCancel(true);

        if (!microApp) {
            // If not a micro app, create some wearable pages for
            // the other nearby tourist attractions.
            ArrayList<Notification> pages = new ArrayList<Notification>();
            for (int i = 1; i < count; i++) {

                // Calculate the distance from current location to tourist attraction
                String distance = Utils.formatDistanceBetween(
                        Utils.getLocation(this), attractions.get(i).location);

                // Construct the notification and add it as a page
                pages.add(new NotificationCompat.Builder(this)
                        .setContentTitle(attractions.get(i).name)
                        .setContentText(distance)
                        .setSmallIcon(R.drawable.ic_stat_maps_pin_drop)
                        .extend(new NotificationCompat.WearableExtender()
                                .setBackground(bitmaps.get(attractions.get(i).name))
                        )
                        .build());
            }
            builder.extend(new NotificationCompat.WearableExtender().addPages(pages));
        }

        // Trigger the notification
        NotificationManagerCompat.from(this).notify(
                Constants.MOBILE_NOTIFICATION_ID, builder.build());
    }
}
