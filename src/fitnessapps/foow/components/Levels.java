package fitnessapps.foow.components;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fitnessapps.acceltest.activity.IAccelRemoteService;
import fitnessapps.foow.activity.R;
import fitnessapps.foow.activity.StartGameActivity;
import fitnessapps.foow.data.AccelerometerData;

public abstract class Levels extends Activity implements SensorEventListener {

	private SoundPool pool;
	private MyTimer countDownTimer;
	private TextView timerTextView;
	private TextView instrucTextView;
	private Button startButton;
	private int drawableID = R.anim.goldfish_animation;
	private int beginSplash;
	private int endSplash;
	private int crowdCheer;
	private int loseBuzz;
	private int turnRight;
	private int turnLeft;
	private int turnAround;

	private int pointsForLevel;
	private int level_number;
	private int numOfObstacles;
	private int score;
	private int numOfSteps;
	private int winningSteps;
	private int[] obstacleAtSteps;

	private SensorManager sensorManager;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;

	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;
	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	private ArrayList<AccelerometerData> accelerationSamples;
	private AccelerometerData prev;
	private double azimuth;
	private TextView stepView;

	private boolean isObstacleCleared;
	private String preObstacleDir = "";
	private int obstacleSelected = 0;

	private String currDirection = "";

	private static final long COUNTDOWN_INTERVAL = 1000;
	private static final String NORTH = "North";
	private static final String SOUTH = "South";
	private static final String EAST = "East";
	private static final String WEST = "West";
	private static final String GAME_NAME = "Fish Out of Water";
	private static final String INSTRUCTIONS = "Walk to catch up to Goldie!";
	private static final int OB_TURN_LEFT = 1;
	private static final int OB_TURN_RIGHT = 2;
	private static final int OB_TURN_AROUND = 3;

	private RemoteServiceConnection conn;
	private IAccelRemoteService remoteService;

	public void gameStart() {
		registerListeners();
		numOfSteps = 0;
		initRunTextView();
		initObstacles();
		if (isAccelServiceRunning()) { bindService(); }
		countDownTimer.start();
	}

	/******************** Remote Service ***************************/
	private void bindService() {
		if (conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent();
			i.setClassName("fitnessapps.acceltest.activity",
					"fitnessapps.acceltest.activity.AccelerometerService");
			bindService(i, conn, Context.BIND_AUTO_CREATE);
		}
	}

	private void releaseService() {
		if (conn != null) {
			conn.serviceAppendEndGame();
			unbindService(conn);
			conn = null;
		}
	}

	
	 private boolean isAccelServiceRunning() { 
		 ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE); 
		 for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) { 
			 if ("fitnessapps.acceltest.activity.AccelerometerService".equals(service.service.getClassName())) { 
				 return true;
			 }
		 } 
		 return false; 
	 }
	 
	class RemoteServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className,
				IBinder boundService) {
			remoteService = IAccelRemoteService.Stub
					.asInterface((IBinder) boundService);
			try {
				remoteService.setGameNameFromService(GAME_NAME + " Level: "
						+ getLevelNumber());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(getClass().getSimpleName(), "onServiceConnected()");
		}

		public void onServiceDisconnected(ComponentName className) {
			
			remoteService = null;
			Log.d(getClass().getSimpleName(), "onServiceDisconnected");
		}
		
		public void serviceAppendEndGame() {
			try { 
				 remoteService.setEndGameFlagFromService(true); 
				 remoteService.setGameNameFromService(GAME_NAME + " Level: "
							+ getLevelNumber());
			 } catch (RemoteException e) { 
				 // TODO Auto-generated catch block
				 e.printStackTrace(); 
			 }
		}
	};

	/***************** End Remote Service ******************************/

	public void setLevelNumber(int levelNum) {
		level_number = levelNum;
	}

	private int getLevelNumber() {
		return level_number;
	}

	private void setDirection(String newDirection) {
		currDirection = newDirection;
	}

	private String getCurrDirection() {
		return currDirection;
	}

	private void updateDirection() {
		if (isFacingNorth()) {
			setDirection(NORTH);
		} else if (isFacingSouth()) {
			setDirection(SOUTH);
		} else if (isFacingEast()) {
			setDirection(EAST);
		} else if (isFacingWest()) {
			setDirection(WEST);
		}
	}

	// -------- ACCELERATION AND MAGENTIC FIELD CODE
	// ------------------------------------------------
	public void initTracking() {
		initAccelerationContainers();
		initSensorManagement();
		initValueArrays();
	}

	private void initAccelerationContainers() {
		accelerationSamples = new ArrayList<AccelerometerData>();
	}

	private void initSensorManagement() {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorAccelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	private void initValueArrays() {
		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
	}

	private void registerListeners() {
		sensorManager.registerListener(this, sensorAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private boolean isFacingNorth() {
		return ((azimuth > 0 && azimuth < 45) || (azimuth > -45 && azimuth <= 0));
	}

	private boolean isFacingEast() {
		return (azimuth > 45 && azimuth < 135);
	}

	private boolean isFacingSouth() {
		return ((azimuth > 135 && azimuth < 180) || (azimuth > -180 && azimuth < -135));
	}

	private boolean isFacingWest() {
		return (azimuth > -135 && azimuth < -45);
	}

	public void onStop() {
		unregisterListeners();
		releaseService();
		/*
		 * try { writeAccelerationToSD(); } catch (IOException e) {
		 * e.printStackTrace(); logFileWriteFailure(); }
		 */

		super.onStop();
	}

	private void unregisterListeners() {
		try {
			sensorManager.unregisterListener(this);
		} catch (NullPointerException e) {
			Log.e("UNREGISTER LISTENER",
					"Null pointer during unregistering listener");
		}
	}

	/*
	 * private void logFileWriteFailure() { Log.d("WRITEACCELEROMETER",
	 * "File write failed"); }
	 * 
	 * private void writeAccelerationToSD() throws IOException { File sampleFile
	 * = initAccelerationFile(); FileWriter fw = new FileWriter(sampleFile);
	 * 
	 * // fw.write("sessionStart_sessionEnd"); for (AccelerometerData data :
	 * accelerationSamples) { fw.write(data.getX() + "_" + data.getY() + "_" +
	 * data.getZ() + "\n"); } fw.close(); }
	 * 
	 * private File initAccelerationFile() throws IOException { File card =
	 * Environment.getExternalStorageDirectory(); File directory = new
	 * File(card.getAbsolutePath() + "/fishoutofwater/accelerometerdata"); File
	 * sampleFile = new File(directory, "playerID_FishOutOfWater_Date_" +
	 * getLevelNumber() + ".txt"); sampleFile.createNewFile();
	 * 
	 * return sampleFile; }
	 */

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Must be implemented, but no functionality

	}

	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accelerometerHandler(event);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magnetometerHandler(event);
			break;
		}

		updateGameState();
	}

	private void accelerometerHandler(SensorEvent event) {
		for (int i = 0; i < 3; i++) {
			valuesAccelerometer[i] = event.values[i];
			addAccelerationSamples(event);

			if (prev != null) {
				AccelerometerData curr = new AccelerometerData(event.values[0],
						event.values[1], event.values[2]);
				if (isStep(prev, curr) && isObstacleCleared()) {
					
					updatePlayerSteps();
					updateStepView(numOfSteps);
					if (getNumOfObstacles() > 0) {
						checkObstacleEvent(numOfSteps);
					}
				}
				prev = curr;
			} else {
				prev = new AccelerometerData(event.values[0], event.values[1],
						event.values[2]);
			}
		}
	}

	private void addAccelerationSamples(SensorEvent event) {
		accelerationSamples.add(new AccelerometerData(event.values[0],
				event.values[1], event.values[2]));
	}

	private void updatePlayerSteps() {
		numOfSteps++; 
		if ((countDownTimer.getSecondsRemaining() <= (countDownTimer.getSecondsStartedWith()/2)) 
				&& numOfSteps < (winningSteps/2)) {
			instrucTextView.setText("Hurry Up! You are behind the timer.");
		}
		else {
			instrucTextView.setText(INSTRUCTIONS);
		}
	}

	private boolean isStep(AccelerometerData prev, AccelerometerData curr) {
		return (curr.getZ() > 9 && prev.getZ() < 9);
	}

	private void magnetometerHandler(SensorEvent event) {
		for (int i = 0; i < 3; i++) {
			valuesMagneticField[i] = event.values[i];
		}
	}

	private void updateAzimuth(double newAzimuth) {
		azimuth = newAzimuth;
	}

	private void updateGameState() {

		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
				valuesAccelerometer, valuesMagneticField);

		if (success) {
			SensorManager.getOrientation(matrixR, matrixValues);
			updateAzimuth(Math.toDegrees(matrixValues[0]));
			updateDirection();
			obstacleListener();
		}

		if (numOfSteps >= getNumOfStepsToWin() && !countDownTimer.isFinished()) {
			levelCompleted();
		} else {
			if (countDownTimer.isFinished()) {
				levelFailed();
			}
		}
	}

	// -----------END OF ACCELEROMETER AND MAGNETIC FIELD CODE
	// --------------------------------------------

	public void resetScore() {
		score = 0;
	}

	public int getScore() {
		return score;
	}

	private void vibrate() {
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(300);
	}

	public void initGameTimer(long numberOfMiliSec) {
		countDownTimer = new MyTimer(numberOfMiliSec, COUNTDOWN_INTERVAL,
				timerTextView);
	}

	public void initRunTextView() {
		instrucTextView.setText(INSTRUCTIONS);
	}

	public void setTextViews(TextView timerView, TextView stepsView,
			TextView instrucView) {
		timerTextView = timerView;
		stepView = stepsView;
		instrucTextView = instrucView;
	}

	/**
	 * This method must be called before initAnimation in the child class.
	 * 
	 * @param newDrawableID
	 *            default value is goldfish_animation
	 */
	public void setAnimationDrawable(int newDrawableID) {
		drawableID = newDrawableID;
	}

	private int getAnimationDrawable() {
		return drawableID;
	}

	public void initSound() {
		pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		beginSplash = pool.load(this, R.raw.splash_out, 1);
		endSplash = pool.load(this, R.raw.splash_in, 1);
		crowdCheer = pool.load(this, R.raw.crowd_cheer, 1);
		loseBuzz = pool.load(this, R.raw.lose_buzz, 1);
		turnRight = pool.load(this, R.raw.turn_right, 1);
		turnLeft = pool.load(this, R.raw.turn_left, 1);
		turnAround = pool.load(this, R.raw.turn_around, 1);
	}

	public void levelCompleted() {
		pool.play(endSplash, 1, 1, 1, 0, 1);
		pool.play(crowdCheer, 1, 1, 1, 0, 1);
		score += getPointsForLevel();
		onStop();
	}

	public void levelFailed() {
		pool.play(loseBuzz, 1, 1, 1, 0, 1);
		vibrate();
		onStop();
	}

	public void initAnimation(final View view, Button startBtn) {

		startButton = startBtn;

		final CustomAnimationDrawable cad = new CustomAnimationDrawable(
				(AnimationDrawable) getResources().getDrawable(
						getAnimationDrawable())) {
			@Override
			public void onAnimationFinish() {
				// img.setVisibility(View.GONE);
				view.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.wood_floor_wet));
				gameStart(); // starts game
			}
		};

		startButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0) {
				view.setBackgroundDrawable(cad);
				cad.setOneShot(true);
				if (!cad.isRunning()) {
					// Start the animation
					cad.start();
					pool.play(beginSplash, 1, 1, 1, 0, 1);
					startButton.setVisibility(View.GONE);
				}
			}
		});

	}

	private void initObstacles() {
		int numObstacles = getNumOfObstacles();
		isObstacleCleared = true;
		if (numObstacles > 0) {
			int stepsWin = getNumOfStepsToWin();
			int obstacleStepInterval = stepsWin / (numObstacles + 1);
			obstacleAtSteps = new int[numObstacles];

			for (int i = 0; i < obstacleAtSteps.length; i++) {
				int obstacleAtStep = obstacleStepInterval * (i + 1);
				if (obstacleAtStep > stepsWin) {
					obstacleAtStep = stepsWin;
				}
				obstacleAtSteps[i] = obstacleAtStep;
			}

		}
	}

	private void obstacleCleared() {
		isObstacleCleared = true;
		preObstacleDir = "";
		obstacleSelected = 0;
		instrucTextView.setText("");
		instrucTextView.setVisibility(View.GONE);
	}

	private void obstacleListener() {
		if (!preObstacleDir.equals("") && obstacleSelected != 0) {
			switch (obstacleSelected) {
			case OB_TURN_LEFT:
				if (playerTurnLeft(preObstacleDir)) {
					obstacleCleared();
				}
				break;
			case OB_TURN_RIGHT:
				if (playerTurnRight(preObstacleDir)) {
					obstacleCleared();
				}
				break;
			case OB_TURN_AROUND:
				if (playerTurnAround(preObstacleDir)) {
					obstacleCleared();
				}
				break;
			}
		}
	}

	private boolean checkObstacleEvent(int currStep) {
		boolean obstacleFired = false;

		for (int i = 0; i < obstacleAtSteps.length; i++) {
			if (currStep == obstacleAtSteps[i]) {
				vibrate();
				preObstacleDir = getCurrDirection();
				isObstacleCleared = false;
				// POP UP STOP IMAGE AND INSTRUCTIONS
				instrucTextView.setText("STOP! YOU MUST TURN ");
				obstacleSelected = obstacleRandomizer();
				switch (obstacleSelected) {
				case OB_TURN_LEFT:
					instrucTextView.append("LEFT");
					pool.play(turnLeft, 1, 1, 1, 0, 1);
					break;
				case OB_TURN_RIGHT:
					instrucTextView.append("RIGHT");
					pool.play(turnRight, 1, 1, 1, 0, 1);
					break;
				case OB_TURN_AROUND:
					instrucTextView.append("AROUND");
					pool.play(turnAround, 1, 1, 1, 0, 1);
					break;
				}
				instrucTextView.setVisibility(View.VISIBLE);
				obstacleFired = true;
				numOfObstacles--;
				break;
			}
		}
		return obstacleFired;
	}

	private boolean playerTurnLeft(String prevDirection) {
		boolean turnedLeft = false;
		if (prevDirection.equals(NORTH) && getCurrDirection().equals(WEST)
				|| prevDirection.equals(SOUTH)
				&& getCurrDirection().equals(EAST)
				|| prevDirection.equals(EAST)
				&& getCurrDirection().equals(NORTH)
				|| prevDirection.equals(WEST)
				&& getCurrDirection().equals(SOUTH)) {
			turnedLeft = true;
		}
		return turnedLeft;
	}

	private boolean playerTurnRight(String prevDirection) {
		boolean turnedRight = false;
		if (prevDirection.equals(NORTH) && getCurrDirection().equals(EAST)
				|| prevDirection.equals(SOUTH)
				&& getCurrDirection().equals(WEST)
				|| prevDirection.equals(EAST)
				&& getCurrDirection().equals(SOUTH)
				|| prevDirection.equals(WEST)
				&& getCurrDirection().equals(NORTH)) {
			turnedRight = true;
		}
		return turnedRight;
	}

	private boolean playerTurnAround(String prevDirection) {
		boolean turnedAround = false;
		if (prevDirection.equals(NORTH) && getCurrDirection().equals(SOUTH)
				|| prevDirection.equals(SOUTH)
				&& getCurrDirection().equals(NORTH)
				|| prevDirection.equals(EAST)
				&& getCurrDirection().equals(WEST)
				|| prevDirection.equals(WEST)
				&& getCurrDirection().equals(EAST)) {
			turnedAround = true;
		}
		return turnedAround;
	}

	private int obstacleRandomizer() {
		Random gen = new Random();
		int pickedNumber = gen.nextInt(3) + 1;
		return pickedNumber;
	}

	public void updateStepView(int steps) {
		stepView.setText("Steps: " + steps);
	}

	private boolean isObstacleCleared() {
		return isObstacleCleared;
	}

	/**
	 * 
	 * @param newNumOfObstacles
	 */
	public void setNumOfObstacles(int newNumOfObstacles) {
		numOfObstacles = newNumOfObstacles;
	}

	public int getNumOfObstacles() {
		return numOfObstacles;
	}

	public void setNumOfStepsToWin(int stepsToWin) {
		winningSteps = stepsToWin;
	}

	private int getNumOfStepsToWin() {
		return winningSteps;
	}

	public void setPointsForLevel(int points) {
		pointsForLevel = points;
	}

	private int getPointsForLevel() {
		return pointsForLevel;
	}

	@Override
	public void onBackPressed() {
		unregisterListeners();
		Intent goBack = new Intent(this, StartGameActivity.class);
		startActivity(goBack);
	}

}
