package com.pennstudyspaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class OptionsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
	}
	
	public void backToMain(View view) {
		Intent i = new Intent();
		
		setResult(RESULT_OK,i);
		
		finish();
	}
}
