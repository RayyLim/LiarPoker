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

	private static int currentPlayerPos = 0;
	
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
	
	static String currentPlayerIs = "";
	
	private static void sendCurrentPlayerChanged()
	{
		for(int i = 0; i < listeners.size(); i++) 
		{ 
			currentPlayerPos = (currentPlayerPos+1)%WaitRoomActivity.playerlistArrayList.size();
			currentPlayerIs = WaitRoomActivity.playerlistArrayList.get(currentPlayerPos);
		    listeners.get(i).onCurrentPlayerChanged(currentPlayerIs); 
		}
		
		notLiarButtonPressed = 0;
	}
	
	static ArrayList<GameUpdateListener> listeners = new ArrayList<GameUpdateListener>();
	
	public static void setOnGameUpdateListener(GameUpdateListener listener)
	{
		listeners.add(listener);
	}
	
	static int  notLiarButtonPressed = 0;
	
	// BUG: Need to ignore all Liar presses and notButton presses after sendCurrentPlayerChanged(), in case the presses were for other player
	// BUG: NotLiar button should be greyed until change players... so can not send not liar multiple times
	public static void pressedNotLiarButton(String forPlayer)
	{
		if(forPlayer == currentPlayerIs)
		{
			notLiarButtonPressed++;
		
			if(notLiarButtonPressed == WaitRoomActivity.playerlistArrayList.size() - 1)
			{
				sendCurrentPlayerChanged();
			}
		}
	}
	
	// BUG: Liar button should be greyed until change players... so can not send not liar multiple times
	public static void pressedLiarButton(String forPlayer)
	{
		// Tell everyone detected liar button pressed
	}
	

}