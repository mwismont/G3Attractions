package com.example.g3.common;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest_properCaseFirstWord
{
    @Test
    public void testProperCaseFirstWord_null()
    {
        Assert.assertEquals("", Utils.properCaseFirstWord(null));
    }

    @Test
    public void testProperCaseFirstWord_singleLetter()
    {
        Assert.assertEquals("T", Utils.properCaseFirstWord("t"));
    }

    @Test
    public void testProperCaseFirstWord_word()
    {
        Assert.assertEquals("Test", Utils.properCaseFirstWord("test"));
    }

    @Test
    public void testProperCaseFirstWord_multipleWords()
    {
        Assert.assertEquals("This is a test", Utils.properCaseFirstWord("this is a test"));
    }
}
