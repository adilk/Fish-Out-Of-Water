package fitnessapps.foow.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GameCompletedActivity extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_score);
    }
    
    @Override
	public void onBackPressed() {
		Intent goBack = new Intent(this, StartGameActivity.class);
		startActivity(goBack);
	}
}
