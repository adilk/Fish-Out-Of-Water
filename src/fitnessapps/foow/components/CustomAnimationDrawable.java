package fitnessapps.foow.components;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public abstract class CustomAnimationDrawable extends AnimationDrawable {
	/** Handles the animation callback. */
	Handler mAnimationHandler;

	public CustomAnimationDrawable(AnimationDrawable aniDrawable) {
	    /* Add each frame to our animation drawable */
	    for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
	        this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
	    }
	}

	@Override
	public void start() {
	    super.start();
	    /*
	     * Call super.start() to call the base class start animation method.
	     * Then add a handler to call onAnimationFinish() when the total
	     * duration for the animation has passed
	     */
	    mAnimationHandler = new Handler();
	    mAnimationHandler.postDelayed(new Runnable() {

	        public void run() {
	            onAnimationFinish();
	        }
	    }, getTotalDuration());

	}

	/**
	 * Gets the total duration of all frames.
	 * 
	 * @return The total duration.
	 */
	public int getTotalDuration() {

	    int iDuration = 0;

	    for (int i = 0; i < this.getNumberOfFrames(); i++) {
	        iDuration += this.getDuration(i);
	    }

	    return iDuration;
	}

	/**
	 * Called when the animation finishes.
	 */
	public abstract void onAnimationFinish();

}
