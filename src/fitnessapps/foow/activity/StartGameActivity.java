package fitnessapps.foow.activity;

import fitnessapps.foow.data.GlobalState;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StartGameActivity extends Activity {
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.start_game);
	     GlobalState.level_number = 1;
		 GlobalState.score = 0;
	     
	 }
	 
	 public void goToOnePlayerGame(View view) {
		 Intent firstLevel = new Intent(this, LevelActivity.class);
		 startActivity(firstLevel);
	 }
	 
	 @Override
	 public void onBackPressed() {
		 moveTaskToBack(true);
	 }

}
