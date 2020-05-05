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

package com.example.g3.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * A simple shared tourist attraction class to easily pass data around.
 */
public class Attraction
{
    public int id;
    public String name;
    public String description;
    public String longDescription;
    public Uri imageUrl;
    public Uri secondaryImageUrl;
    public Uri locationUrl;
    public LatLng location;
    public String city;

    private Rating rating;

    public Bitmap image;
    public Bitmap secondaryImage;
    public String distance;

    public Attraction() {}

    public Attraction(int id, String name, String description, String longDescription, Uri imageUrl,
                      Uri secondaryImageUrl, Uri locationUrl, LatLng location, String city) {
        this.id =  id;
        this.name = name;
        this.description = description;
        this.longDescription = longDescription;
        this.imageUrl = imageUrl;
        this.secondaryImageUrl = secondaryImageUrl;
        this.locationUrl = locationUrl;
        this.location = location;
        this.city = city;
    }

    public Integer getRatingValue() {
        return rating != null ? rating.getValue() : null;
    }

    public Rating getRating()
    {
        return rating;
    }

    public void setRating(Rating rating)
    {
        this.rating = rating;
    }
}