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
    
    public void testgenerateReserveLink() {
        String expect, generated;
        expect = "http://pennstudyspaces.com/deeplink?date=1-1-1&time_from=0000&time_to=0101&room=1";
        generated = mActivity.generateReserveLink(0, 0, 1, 1, 1, 1, 1, 1);
        assertEquals(expect, generated);
        
        expect = "http://pennstudyspaces.com/deeplink?date=4-5-1991&time_from=1106&time_to=1107&room=1596";
        generated = mActivity.generateReserveLink(11, 6, 11, 7, 4, 5, 1991, 1596);
        assertEquals(expect, generated);
        
        expect = "http://pennstudyspaces.com/deeplink?date=4-20-2012&time_from=0420&time_to=0420&room=10101";
        generated = mActivity.generateReserveLink(4, 20, 4, 20, 4, 20, 2012, 10101);
        assertEquals(expect, generated);
        
        expect = "";
        generated = mActivity.generateReserveLink(-1, -1, -1, -1, -1, -1, -1, -1);
        assertEquals(expect, generated);
    }
    
}
