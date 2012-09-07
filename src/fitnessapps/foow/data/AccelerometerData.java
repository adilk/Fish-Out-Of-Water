package fitnessapps.foow.data;

public class AccelerometerData {
	private float accelerationX;
	private float accelerationY;
	private float accelerationZ;
	
	
	public AccelerometerData(float x, float y, float z) {
		accelerationX = x;
		accelerationY = y;
		accelerationZ = z;
	}
	
	public void setX(float newX){
		accelerationX = newX;
	}
	
	public float getX(){
		return accelerationX;
	}
	
	public void setY(float newY){
		accelerationY = newY;
	}
	
	public float getY(){
		return accelerationY;
	}
	
	public void setZ(float newZ){
		accelerationZ = newZ;
	}
	
	public float getZ(){
		return accelerationZ;
	}
	
}
