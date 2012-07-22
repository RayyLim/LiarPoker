package com.example.liarpoker;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GetPlayersService extends Service 
{
	private static final String TAG = "MyService";			
	private Timer timer = new Timer();
	
	@Override	
	public IBinder onBind(Intent intent) 
	{		
		return null;	
	}
	
	@Override	
	public void onCreate() 
	{			
		Log.d(TAG, "onCreate");
	}	
	
	@Override	
	public void onDestroy() 
	{			
		Log.d(TAG, "onDestroy");		
	}
	
	int playerid = 10;
	
	static ArrayList<PlayerListListener> listeners = new ArrayList<PlayerListListener>();
	
	public static void setOnPlayerListUpdateListener(PlayerListListener listener)
	{
		listeners.add(listener);
	}
	
	@Override	
	public void onStart(Intent intent, int startid) 
	{			
		Log.d(TAG, "onStart");		
		
		timer.scheduleAtFixedRate( new TimerTask() { 

			public void run() { 

			WaitRoomActivity.playerlistArrayList.add(Integer.toString(playerid++));
			for(int i = 0; i < listeners.size(); i++) 
			{ 
			    listeners.get(i).onPlayerListUpdate(); 
			} 
			} 

			}, 0, 1000);

		
	}
	

	
}

