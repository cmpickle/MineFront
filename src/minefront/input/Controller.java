package minefront.input;

public class Controller {
	public double x, y, z, rotation, rotationy, xa, za, rotationa, rotationya;
	public static boolean turnLeft = false;
	public static boolean turnRight = false;
	public static boolean lookUp = false;
	public static boolean lookDown = false;
	public static boolean walking = false; //used to determine view bobbing
	public static boolean crouchWalk = false;
	public static boolean runWalk = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean crouch, boolean run) {
		double rotationSpeed = 0.025;
		double walkSpeed = 0.5;
		double jumpHeight = 1;
		double crouchHeight = 0.3;
		double xMove = 0;
		double zMove = 0;

		if (forward) {
			zMove++;
			walking = true;
		}
		if (back) {
			zMove--;
			walking = true;
			run = false;
		}
		if (left) {
			xMove--;
			walking = true;
		}
		if (right) {
			xMove++;
			walking = true;
		}
		if (jump) {
			y += jumpHeight;
			run = false;
		}
		if(crouch) {
			y -= crouchHeight;
			walkSpeed = 0.2;
			run = false;
			crouchWalk = true;
		}
		if (run) {
			walkSpeed = 1;
			walking = true;
			runWalk = true;
		}
		if (!forward && !back && !left && !right) {
			walking = false;
		}
		if (!crouch) {
			crouchWalk = false;
		}
		if (!run) {
			runWalk = false;
		}
		if (turnLeft) {
			rotationa -= rotationSpeed;
		}
		if (turnRight) {
			rotationa += rotationSpeed;
		}
		if (lookUp) {
			rotationya += rotationy;
		}
		if (lookDown) {
			rotationya -= rotationy;
		}

		xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation)) * walkSpeed;
		za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation)) * walkSpeed;

		x += xa;
		y *= 0.9;
		z += za;
		xa *= 0.1;
		za *= 0.1;
		rotation += rotationa;
		rotationa *= 0.5;
		rotationy += rotationya;
		rotationya *= 0.5;
	}

}
