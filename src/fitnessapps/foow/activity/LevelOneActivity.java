package fitnessapps.foow.activity;

import fitnessapps.foow.components.Levels;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LevelOneActivity extends Levels {
	
	private static final long LEVEL_DURATION_MILISEC = 11000;
	private static final int OBSTACLES = 0;
	private static final int STEPS_TO_WIN = 10;
	private static final int POINTS_LEVEL = 50;
	private static final int LEVEL = 1;
	
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_layout);
        
        TextView timerTextView = (TextView) findViewById(R.id.timerText);
        TextView stepView = (TextView) findViewById(R.id.stepsTextView);
        TextView instrucView = (TextView) findViewById(R.id.instrucTextView);
        Button startButton = (Button) findViewById(R.id.startAnimationBtn);
        startButton.setText("Start Level 1");
         
        initSound();
        initTracking();
        
        setAnimationDrawable(R.anim.goldfish_animation);
	
        setTextViews(timerTextView, stepView, instrucView);
		setLevelNumber(LEVEL);
		setNumOfObstacles(OBSTACLES); 
		setNumOfStepsToWin(STEPS_TO_WIN);
		setPointsForLevel(POINTS_LEVEL);
		resetScore(); // starting a 0 in Level 1
		
		initGameTimer(LEVEL_DURATION_MILISEC);
		
        // GAME STARTS AFTER ANIMATION
        initAnimation(findViewById(R.id.level_layout), startButton); // starts game after animation is done
    }
    
    
    @Override
    public void levelCompleted() {
    	super.levelCompleted();
    	findViewById(R.id.level_layout).setBackgroundDrawable(getResources().getDrawable(R.drawable.wood_floor));
    	AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
    	alertBox.setMessage("Hooray! You saved Goldie! You have " + getScore() + 
    			" points after clearing Level 1, time to move to Level 2!");
		alertBox.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialogInterface,
							int arg1) {
						goToNextLevel();
						dialogInterface.cancel();
					}
				});
		alertBox.show();
    	
    }
    
    @Override
    public void levelFailed() {
    	super.levelFailed();
    	findViewById(R.id.level_layout).setBackgroundDrawable(getResources().getDrawable(R.drawable.wood_floor));
    	AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
    	DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE :
					redoLevel();
					break;
				case DialogInterface.BUTTON_NEGATIVE :
					resetScore();
					onBackPressed();
					break;
				}
				
			}
		};
    	
    	alertBox.setMessage("You didnt catch Goldie fast enough! Try level 1 again?");
		alertBox.setPositiveButton("Yes", clickListener);
		alertBox.setNegativeButton("No", clickListener);
		alertBox.show();
    }
    
    
    public void goToNextLevel() {
    	Intent loadLevelTwo = new Intent(this, LevelTwoActivity.class);
		startActivity(loadLevelTwo);
    }
    
    public void redoLevel() {
    	Intent loadLevelOne = new Intent(this, LevelOneActivity.class);
		startActivity(loadLevelOne);
    }


	
    
}
