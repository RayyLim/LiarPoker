package com.example.liarpoker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameRoomActivity extends Activity 
{
	public static String currentPlayerHas = "";
	
	private int playerPosition = 0;
	
	private String waitString = "";
	
	private String myhand = "";
	
	private String generateHand()
	{
		return Integer.toString((int)(Math.random()*100000));
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.gameroom_main);
        
        myhand = generateHand();
        
        TextView handTextView = (TextView) findViewById(R.id.mynumbersLabel);
        handTextView.setText(myhand);
        
        
        Resources res = getResources();
        waitString = res.getString(R.string.waiting);
        
        ResetCurrentPlayer();
        

        
        playerPosition = (int)((Math.random() * (WaitRoomActivity.playerlistArrayList.size()+1)) % WaitRoomActivity.playerlistArrayList.size());
        
        UpdateGameUI();
        
		EditText numberText = (EditText) findViewById(R.id.igotnumber);
		EditText amountText = (EditText) findViewById(R.id.igotamount);
		
		
		numberText.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){updateSubmitButton();}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		amountText.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){updateSubmitButton();}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		GameService.setOnGameUpdateListener(new GameUpdateListener()
		{

			public void onCurrentPlayerChanged(String currentPlayer) {
				currentPlayerIs = currentPlayer;
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						EditText numberText = (EditText) findViewById(R.id.igotnumber);
						EditText amountText = (EditText) findViewById(R.id.igotamount); 		
						
						numberText.setText("");
						amountText.setText("");
						
						ResetCurrentPlayer();
					}
				});
			}

			public void onPlayerHandSubmitted(String currentPlayer, int amount,
					int number) {
				currentPlayerHas = Integer.toString(amount) + " of " + Integer.toString(number) ;
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						UpdateGameUI();
					}
				});
			}

			public void onSomeoneCalledLiar(int submittedNumber) {
				
				// Send your information to the service...
				// TODO service will timeout and display who did not send their count on time
				// BUG service will tell who disconnected and correctly handle them
//				int amount = 0;
//				for(int i = 0; i < myhand.length(); i++)
//				{
//					if(Integer.parseInt(Character.toString(myhand.charAt(i))) == submittedNumber)
//					{
//						amount++;
//					}
//				}
				GameService.submitHand(WaitRoomActivity.myPlayerName, myhand);
				
				test_fillOutRestOfPlayers(submittedNumber);
			}

			public void onGameEnded() {
			
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						GoToReturnActivity();
					}
				});			
			}
		});
        
	}
	
	public void GoToReturnActivity()
	{
        // Do something in response to button
    	Intent intent = new Intent(this, ResultActivity.class);
    	startActivity(intent);
	}
	
	protected void test_fillOutRestOfPlayers(int submittedNumber) {
		for(int i = 1; i < WaitRoomActivity.playerlistArrayList.size(); i++)
		{
			GameService.submitHand(WaitRoomActivity.playerlistArrayList.get(i), generateHand());
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		// stopService(new Intent(this, GameService.class));
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
        startService(new Intent(this, GameService.class));
	}
	
	public void NotLiarButtonPressed(View view)
	{
		GameService.pressedNotLiarButton(currentPlayerIs);
		Button notLiarButton = (Button) findViewById(R.id.notLiarButton);
		notLiarButton.setEnabled(false);
		
		Button liarButton = (Button) findViewById(R.id.liarButton);
		liarButton.setEnabled(false);
	}
	
	public void LiarButtonPressed(View view)
	{
		GameService.pressedLiarButton(currentPlayerIs);
		Button liarButton = (Button) findViewById(R.id.liarButton);
		liarButton.setEnabled(false);

		Button notLiarButton = (Button) findViewById(R.id.notLiarButton);
		notLiarButton.setEnabled(false);
		
		GameService.pressedLiarButton(currentPlayerIs);
	}
	
	public void fakeRandomCurrentPlayerHas(View view)
	{
		int randomNumber = (int)(Math.random()*10);
		int randomAmount = (int)(Math.random()*10);
		
		GameService.submitBluff(currentPlayerIs, randomAmount, randomNumber);
	}
	
	// BUG: What happens if someone leaves the game?
	
	
	public void fakeNoOneCallsLiar(View view)
	{
		String skipPlayer = currentPlayerIs;
		for(int i = 0; i < WaitRoomActivity.playerlistArrayList.size()-1; i++)
		{
			GameService.pressedNotLiarButton(skipPlayer);
		}
	}
	
	public void fakeSomeoneCallsLiar(View view)
	{
		// TODO: End game... If I am lying, i lose... if I was not lying... I win... and everyone else loses
		// Test: I'm not lying, but someone has higher amount of same #
		GameService.pressedLiarButton(currentPlayerIs);
	}
	
	// BUG: Service should ignore submits for liar and not liar for currentplayer if currentplayer has not submittted hands yet
	
	public void fakeMyTurn(View view)
	{
		this.playerPosition = 0;
		ResetCurrentPlayer();
	}
	
	private void ResetCurrentPlayer()
	{
		// Need to handle if my turn or someone else's turn
        currentPlayerHas = waitString;
        UpdateGameUI();
	}
	
	public void updateSubmitButton()
	{
		EditText numberText = (EditText) findViewById(R.id.igotnumber);
		EditText amountText = (EditText) findViewById(R.id.igotamount); 		
		Button submitButton = (Button) findViewById(R.id.submitButton);
		
		boolean enabled = checkEditTextNotEmpty(numberText) && checkEditTextNotEmpty(amountText);
		submitButton.setEnabled(enabled);

	}
	
	private boolean checkEditTextNotEmpty(EditText edit)
	{
		return !edit.getText().toString().equals("");
	}
	
	public void submit(View view)
	{
		EditText numberText = (EditText) findViewById(R.id.igotnumber);
		EditText amountText = (EditText) findViewById(R.id.igotamount); 
		
		int number = Integer.parseInt(numberText.getText().toString());
		int amount = Integer.parseInt(amountText.getText().toString());
		
		GameService.submitBluff(WaitRoomActivity.myPlayerName, amount, number);
	}
	
	String currentPlayerIs = "Loading ..."; 
	
	private void UpdateGameUI()
	{
		LinearLayout notMyTurnLayout = (LinearLayout) findViewById(R.id.notMyTurnLayout);
		LinearLayout myTurnLayout = (LinearLayout) findViewById(R.id.myTurnLayout);
		
		if(currentPlayerIs == WaitRoomActivity.myPlayerName)
		{
			notMyTurnLayout.setVisibility(View.GONE);
			myTurnLayout.setVisibility(View.VISIBLE);
			
			Button submitButton = (Button) findViewById(R.id.submitButton);
			submitButton.setEnabled(false);
		
		}
		else
		{
			TextView currentPlayerTextView = (TextView) findViewById(R.id.currentPlayerLabel);
			TextView currentPlayerHasTextView = (TextView) findViewById(R.id.currentPlayerHasLabel);
	    	Button liarButton = (Button) findViewById(R.id.liarButton);
	    	Button notLiarButton = (Button) findViewById(R.id.notLiarButton);
	    	
	    	currentPlayerTextView.setText(currentPlayerIs);
	    	currentPlayerHasTextView.setText(currentPlayerHas);
	    	
			if(currentPlayerHas == waitString)
	        {
	        	// Disable Buttons     	
	        	liarButton.setEnabled(false);
	        	notLiarButton.setEnabled(false);
	        }
	        else
	        {	
	        	// Enable buttons
	        	liarButton.setEnabled(true);
	        	notLiarButton.setEnabled(true);
	        }
			
			notMyTurnLayout.setVisibility(View.VISIBLE);
			myTurnLayout.setVisibility(View.GONE);
		}
	}
}
