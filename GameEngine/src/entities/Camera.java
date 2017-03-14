package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPalyer = 0;

	private Vector3f position = new Vector3f(0, 0, 0);
	/**
	 * 俯仰
	 */
	private float pitch = 20;
	
	private float roll;
	/**
	 * 偏移
	 */
	private float yaw = 0;
	
	private Player player;

	public Camera(Player player) {
		this.player = player;
		player.setCamera(this);
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		yaw = 180 - (player.getRotY() + angleAroundPalyer);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = player.getRotY() + angleAroundPalyer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	/**
	 * 计算放缩
	 */
	private void calculateZoom() {
		//鼠标滚轮
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		//加减与角色的距离
		if(distanceFromPlayer - zoomLevel < 100 && distanceFromPlayer - zoomLevel > 5) {
			distanceFromPlayer -= zoomLevel;
		}
	}
	
	/**
	 * 计算俯仰
	 */
	private void calculatePitch() {
		if(Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			if(pitch - pitchChange > 5 && pitch - pitchChange < 90) {
				pitch -= pitchChange;
			}
		}
	}
	
	/**
	 * 镜头角色夹角
	 */
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(1)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPalyer -= angleChange;
		}
	}

}
