package fitnessapps.foow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import fitnessapps.foow.components.Levels;

public class LevelActivity extends Levels {

	private long levelDurationMili;
	private int obstacles;
	private int stepsToWin;
	private int currentLevel;
	private Intent levelIntent;
	private Intent introIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.level_layout);
		setLevelView(findViewById(R.id.level_layout));
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		currentLevel = getLevelNumber();
		initLevelFeatures(currentLevel);
		setNumOfObstacles(obstacles);
		setNumOfStepsToWin(stepsToWin);

		levelIntent = new Intent(this, LevelActivity.class);
		introIntent = new Intent(this, StartGameActivity.class);

		TextView timerTextView = (TextView) findViewById(R.id.timerText);
		TextView stepView = (TextView) findViewById(R.id.stepsTextView);
		TextView instrucView = (TextView) findViewById(R.id.instrucTextView);
		Button startButton = (Button) findViewById(R.id.startAnimationBtn);
		startButton.setText("Start Level " + currentLevel);

		//initSound(); // Moved this to onResume in Levels abstract class
		initTracking();

		setAnimationDrawable(R.anim.goldfish_animation);

		setTextViews(timerTextView, stepView, instrucView);

		setCurrentIntent(levelIntent);
		setIntroIntent(introIntent);
		if (currentLevel > 1) {
			setPreviousIntent(levelIntent);
		} else {
			setPreviousIntent(introIntent);
		}

		initGameTimer(levelDurationMili);

		// GAME STARTS AFTER ANIMATION
		initAnimation(startButton); // starts game after animation is done
	}

	public void initLevelFeatures(int level) {
		switch (level) {
		case 1:
			levelDurationMili = 16000;
			obstacles = 1;
			stepsToWin = 20;
			break;
		case 2:
			levelDurationMili = 31000;
			obstacles = 3;
			stepsToWin = 40;
			break;
		case 3:
			levelDurationMili = 46000;
			obstacles = 4;
			stepsToWin = 60;
			break;
		case 4:
			levelDurationMili = 61000;
			obstacles = 5;
			stepsToWin = 80;
			break;
		case 5:
			levelDurationMili = 76000;
			obstacles = 6;
			stepsToWin = 100;
			break;
		case 6:
			levelDurationMili = 91000;
			obstacles = 8;
			stepsToWin = 120;
			break;
		case 7:
			levelDurationMili = 106000;
			obstacles = 10;
			stepsToWin = 140;
			break;
		case 8:
			levelDurationMili = 121000;
			obstacles = 11;
			stepsToWin = 160;
			break;
		case 9:
			levelDurationMili = 136000;
			obstacles = 12;
			stepsToWin = 180;
			break;
		case 10:
			levelDurationMili = 151000;
			obstacles = 14;
			stepsToWin = 200;
			break;
		case 11:
			levelDurationMili = 166000;
			obstacles = 15;
			stepsToWin = 220;
			break;
		case 12:
			levelDurationMili = 176000;
			obstacles = 15;
			stepsToWin = 240;
			break;
		case 13:
			levelDurationMili = 191000;
			obstacles = 15;
			stepsToWin = 260;
			break;
		case 14:
			levelDurationMili = 206000;
			obstacles = 17;
			stepsToWin = 280;
			break;
		case 15:
			levelDurationMili = 221000;
			obstacles = 20;
			stepsToWin = 300;
			break;
		}

	}

}
