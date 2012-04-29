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
public class RoomDetailsActivityTest extends ActivityInstrumentationTestCase2<RoomDetailsActivity> {

    private RoomDetailsActivity mActivity;
    
    public RoomDetailsActivityTest() {
        super("com.pennstudyspaces", RoomDetailsActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
    }
    
    public void testPreconditions() {
        assertNotNull(mActivity);
    }
    
    
    public void testisRouteDisplayed() {
        assertFalse(mActivity.isRouteDisplayed());
    }
    
    public void testboolToString() {
        assertEquals("yes",mActivity.boolToString(true));
        assertEquals("no",mActivity.boolToString(false));
    }
    
}
