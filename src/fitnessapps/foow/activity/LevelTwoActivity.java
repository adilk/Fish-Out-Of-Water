package fitnessapps.foow.activity;


import fitnessapps.foow.components.Levels;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LevelTwoActivity extends Levels {
	
	private static final long LEVEL_DURATION_MILISEC = 21000;
	private static final int OBSTACLES = 1;
	private static final int STEPS_TO_WIN = 20;
	private static final int POINTS_LEVEL = 150;
	private static final int LEVEL = 2;
	
	
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_layout);
        TextView timerTextView = (TextView) findViewById(R.id.timerText);
        TextView stepView = (TextView) findViewById(R.id.stepsTextView);
        TextView instrucView = (TextView) findViewById(R.id.instrucTextView);
        Button startButton = (Button) findViewById(R.id.startAnimationBtn);
        startButton.setText("Start Level 2");
        
        setTextViews(timerTextView, stepView, instrucView);
        
        initSound();
        initTracking();
        setAnimationDrawable(R.anim.goldfish_animation);
		
        
        setLevelNumber(LEVEL);
		setNumOfObstacles(OBSTACLES); 
		setNumOfStepsToWin(STEPS_TO_WIN);
		setPointsForLevel(POINTS_LEVEL);
		
		initGameTimer(LEVEL_DURATION_MILISEC); 
		
        // GAME STARTS AFTER ANIMATION
        initAnimation(findViewById(R.id.level_layout), startButton); // starts game after animation is done
    }
    
    
    @Override
    public void levelCompleted() {
    	super.levelCompleted();
    	findViewById(R.id.level_layout).setBackgroundDrawable(getResources().getDrawable(R.drawable.wood_floor));
    	AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
    	alertBox.setMessage("Hooray! You saved Sparkie! You have " + getScore() + 
    			" points after clearing Level 2, time to move to Level 3!");
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
    	
    	alertBox.setMessage("You didnt catch Sparkie fast enough! Try level 2 again?");
		alertBox.setPositiveButton("Yes", clickListener);
		alertBox.setNegativeButton("No", clickListener);
		alertBox.show();
    }
    
    
    public void goToNextLevel() {
    	Intent loadLevelThree = new Intent(this, LevelThreeActivity.class);
		startActivity(loadLevelThree);
    }
    
    public void redoLevel() {
    	Intent loadLevelTwo = new Intent(this, LevelTwoActivity.class);
		startActivity(loadLevelTwo);
    }


	
    
}
