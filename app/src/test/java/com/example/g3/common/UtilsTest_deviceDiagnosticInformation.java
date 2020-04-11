package com.example.g3.common;

import android.os.Build;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;

public class UtilsTest_deviceDiagnosticInformation
{
    @Test
    public void test_getDeviceDiagnosticInformation()
    {
        configureDevice("samsung", "S10");

        String details = Utils.getDeviceDiagnosticInformation();
        Assert.assertTrue(details.startsWith("Samsung S10"));
    }

    @Test
    public void test_modelIncludesManufacturer()
    {
        configureDevice("google", "Google Pixel 4");

        String details = Utils.getDeviceDiagnosticInformation();
        Assert.assertTrue(details.startsWith("Google Pixel 4"));
    }

    /**
     * Convenience method to mock the device values.
     *
     * @param manufacturer to mock
     * @param model to mock
     */
    private void configureDevice(String manufacturer, String model) {
        ReflectionHelpers.setStaticField(Build.class,"MANUFACTURER", manufacturer);
        ReflectionHelpers.setStaticField(Build.class,"MODEL", model);
    }
}
