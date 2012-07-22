package com.example.liarpoker;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GameService extends Service 
{
	private static final String TAG = "GameService";	
	private static Timer timer = new Timer();
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override	
	public void onCreate() 
	{			
		Log.d(TAG, "onCreate");
		sendCurrentPlayerChanged();
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
		

		
	}
	
	public static String currentPlayerIs = "";
	public static Hashtable<String, Integer> playerTable = new Hashtable<String, Integer>();
	
	private static void sendCurrentPlayerChanged()
	{

		
		for(int i = 0; i < listeners.size(); i++) 
		{ 
			currentPlayerPos = (currentPlayerPos+1)%WaitRoomActivity.playerlistArrayList.size();
			currentPlayerIs = WaitRoomActivity.playerlistArrayList.get(currentPlayerPos);
		    listeners.get(i).onCurrentPlayerChanged(currentPlayerIs); 
		}
		
		notLiarButtonPressed = 0;
		handSubmitted = false;
	}
	
	static ArrayList<GameUpdateListener> listeners = new ArrayList<GameUpdateListener>();
	
	public static void setOnGameUpdateListener(GameUpdateListener listener)
	{
		listeners.add(listener);
	}
	
	static int  notLiarButtonPressed = 0;
	static boolean handSubmitted = false;
	public static int submittedNumber = 0;
	private static int submittedAmount;
	
	// TODO: check correct usage of Timer
	public static void submitHand(String player, int amount, int number)
	{
		
		if(player == currentPlayerIs)
		{
			if(handSubmitted)
			{
				// already submitted a hand
				return;
			}
			
			handSubmitted = true;
			submittedNumber = number;
			submittedAmount = amount;
			for(int i = 0; i < listeners.size(); i++) 
			{ 
			    listeners.get(i).onPlayerHandSubmitted(player, amount, number);
			}
		}
		
		// Timer starts after someone has submitted
		timer = new Timer();
		timer.schedule( new TimerTask() 
		{ 
			public void run() 
			{ 
				sendCurrentPlayerChanged();
			} 
		 
		}, 5000);
	}
	
	// BUG: Need to ignore all Liar presses and notButton presses after sendCurrentPlayerChanged(), in case the presses were for other player
	// BUG: NotLiar button should be greyed until change players... so can not send not liar multiple times
	public static void pressedNotLiarButton(String forPlayer)
	{
		if(forPlayer == currentPlayerIs && handSubmitted)
		{
			notLiarButtonPressed++;
		
			if(notLiarButtonPressed == WaitRoomActivity.playerlistArrayList.size() - 1)
			{
				// Stop timer
				timer.cancel();
				sendCurrentPlayerChanged();
			}
		}
	}
	
	private static int numPlayersFinishedSubmittingAmounts = 0;
	
	// BUG: Liar button should be greyed until change players... so can not send not liar multiple times
	public static void pressedLiarButton(String forPlayer)
	{
//		// Stop timer
//		timer.cancel();
		
		if(forPlayer == currentPlayerIs && handSubmitted)
		{
			// Tell everyone detected liar button pressed
			for(int i = 0; i < listeners.size(); i++) 
			{ 
			    listeners.get(i).onSomeoneCalledLiar(submittedNumber);
			}
			
			numPlayersFinishedSubmittingAmounts = 0;
		}
	}
	
	public static void submitAmount(String player, int amount)
	{
		numPlayersFinishedSubmittingAmounts++;
		playerTable.put(player, new Integer(amount));
		
		if(numPlayersFinishedSubmittingAmounts == WaitRoomActivity.playerlistArrayList.size())
		{
			for(int i = 0; i < listeners.size(); i++) 
			{ 
			    listeners.get(i).onGameEnded();
			}
		}
	}
	
	public static boolean wasLastPlayerLying()
	{
		int amount = (Integer)playerTable.get(currentPlayerIs);
		
		// BUG: Are they lying if they have 3 2's but say they only have 2 2's?
		return amount != submittedAmount;
	}
	

}
