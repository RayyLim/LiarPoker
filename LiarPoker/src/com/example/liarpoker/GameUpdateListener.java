package com.example.liarpoker;

import java.util.EventListener;

public interface GameUpdateListener extends EventListener{

	void onCurrentPlayerChanged(String currentPlayer);
	void onPlayerHandSubmitted(String currentPlayer, int amount, int number);
	void onSomeoneCalledLiar();
	void onGameEnded();
}
