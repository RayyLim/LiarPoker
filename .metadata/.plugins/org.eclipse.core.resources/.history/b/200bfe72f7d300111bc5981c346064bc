package com.example.liarpoker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GameService extends Service 
{
	private static final String TAG = "GameService";	
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
		timer.cancel();
	}

	private int currentPlayerPos = 0;
	
	@Override	
	public void onStart(Intent intent, int startid) 
	{			
		Log.d(TAG, "onStart");
		
		// End the round at a minute
		timer.scheduleAtFixedRate( new TimerTask() 
		{ 
			public void run() 
			{ 
				sendCurrentPlayerChanged();
			} 

		}, 0, 15000);
		
	}
	
	private void sendCurrentPlayerChanged()
	{
		for(int i = 0; i < listeners.size(); i++) 
		{ 
			currentPlayerPos = (currentPlayerPos+1)%WaitRoomActivity.playerlistArrayList.size();
		    listeners.get(i).onCurrentPlayerChanged(WaitRoomActivity.playerlistArrayList.get(currentPlayerPos)); 
		}
	}
	
	static ArrayList<GameUpdateListener> listeners = new ArrayList<GameUpdateListener>();
	
	public static void setOnGameUpdateListener(GameUpdateListener listener)
	{
		listeners.add(listener);
	}
	

}
