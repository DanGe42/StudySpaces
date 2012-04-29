package com.pennstudyspaces;

import android.test.ActivityInstrumentationTestCase2;
import com.pennstudyspaces.SearchActivity;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.pennstudyspaces.SearchActivityTest \
 * com.pennstudyspaces.tests/android.test.InstrumentationTestRunner
 */
public class SearchActivityTest extends ActivityInstrumentationTestCase2<SearchActivity> {

    private SearchActivity mActivity;
    
    public SearchActivityTest() {
        super("com.pennstudyspaces", SearchActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
    }
    
    public void testPreconditions() {
        assertNotNull(mActivity);
    }
    
    public void testpad() {
        assertEquals("0-1",mActivity.pad(-1));
        assertEquals("00",mActivity.pad(0));
        assertEquals("01",mActivity.pad(1));
        assertEquals("09",mActivity.pad(9));
        assertEquals("10",mActivity.pad(10));
    }
}
