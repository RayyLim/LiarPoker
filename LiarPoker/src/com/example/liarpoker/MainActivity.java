package com.example.liarpoker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String PLAYERNAME = "com.example.myapp.PLAYERNAME";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Called when the user selects the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, WaitRoomActivity.class);
    	
    	// Get player's name
    	// BUG: Need to check if chatroom already has a player w/ the same name
    	// BUG: Press button w/o filling player name (Set up a default player name?)
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String playername = editText.getText().toString();
    	intent.putExtra(PLAYERNAME, playername);
    	startActivity(intent);

    }
}

