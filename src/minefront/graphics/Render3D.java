package minefront.graphics;

import minefront.Game;
import minefront.input.Controller;

public class Render3D extends Render {

    public double[] zBuffer;
    private double renderDistance = 5000;
    private double forward, right, up, cosine, sine;

    public Render3D(int width, int height)
    {
	super(width, height);
	zBuffer = new double[width * height];
    }

    public void floor(Game game)
    {

	double floorPosition = 8; // how far down the floor is
	double ceilingPosition = 8; // height of the ceiling
	forward = game.controls.z; // get forward motion from game control z
	right = game.controls.x; // get horizontal motion from game control x
	up = game.controls.y;
	double walking = Math.sin(game.time / 6.0) * 0.5;
	if (Controller.crouchWalk)
	{
	    walking = Math.sin(game.time / 6.0) * 0.25;
	}
	if (Controller.runWalk)
	{
	    walking = Math.sin(game.time / 6.0) * 0.9;
	}

	double rotation = /* Math.sin(game.time / 40.0) * 0.5; */game.controls.rotation;
	cosine = Math.cos(rotation);
	sine = Math.sin(rotation);

	for (int y = 0; y < height; y++)
	{
	    double ceiling = (y + -height / 2.0) / height;

	    double z = (floorPosition + up) / ceiling;
	    if (Controller.walking)
	    {
		z = (floorPosition + up + walking) / ceiling;
	    }

	    if (ceiling < 0)
	    {
		z = (ceilingPosition - up) / -ceiling;
		if (Controller.walking)
		{
		    z = (ceilingPosition - up - walking) / -ceiling;
		}
	    }

	    for (int x = 0; x < width; x++)
	    {
		double depth = (x - width / 2.0) / height;
		depth *= z;
		double xx = depth * cosine + z * sine;
		double yy = z * cosine - depth * sine;
		int xPix = (int) (xx + right);
		int yPix = (int) (yy + forward);
		zBuffer[x + y * width] = z;
		pixels[x + y * width] = Texture.floor.pixels[(xPix & 7) + (yPix & 7) * 8];
		// pixels[x + y * width] = ((xPix & 15) * 16) | ((yPix & 15) * 16) << 8;
		if (z > 500)
		{ // render distance
		    pixels[x + y * width] = 0;
		}
	    }
	}
    }

    public void renderWall(double xLeft, double xRight, double zDistance, double yHeight)
    {
	double xcLeft = ((xLeft) - right) * 2;
	double zcLeft = ((zDistance) - forward) * 2;

	double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
	double yCornerTL = ((-yHeight) - up) * 2;
	double yCornerBL = ((+0.5 - yHeight - up) * 2);
	double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;

	double xcRight = ((xRight) - right) * 2;
	double zcRight = ((zDistance) - forward) * 2;

	double rotRightSideX = xcRight * cosine - zcRight * sine;
	double yCornerTR = ((-yHeight) - up) * 2;
	double yCornerBR = ((+0.5 - yHeight - up) * 2);
	double rotRightSideZ = zcRight * cosine + xcRight * sine;

	double xPixelLeft = (rotLeftSideX / rotLeftSideZ * height + width / 2);
	double xPixelRight = (rotRightSideX / rotRightSideZ * height + width / 2);

	if (xPixelLeft >= xPixelRight)
	{
	    return;
	}

	int xPixelLeftInt = (int) (xPixelLeft);
	int xPixelRightInt = (int) (xPixelRight);

	if (xPixelLeftInt < 0)
	{
	    xPixelLeftInt = 0;
	}
	if (xPixelRightInt > width)
	{
	    xPixelRightInt = width;
	}

	double yPixelLeftTop = (int) (yCornerTL / rotLeftSideZ * height + height / 2);
	double yPixelLeftBottom = (int) (yCornerBL / rotLeftSideZ * height + height / 2);
	double yPixelRightTop = (int) (yCornerTR / rotRightSideZ * height + height / 2);
	double yPixelRightBottom = (int) (yCornerBR / rotRightSideZ * height + height / 2);

	for (int x = xPixelLeftInt; x < xPixelRightInt; x++)
	{
	    double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);

	    double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
	    double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom)
		    * pixelRotation;

	    int yPixelTopInt = (int) (yPixelTop);
	    int yPixelBottomInt = (int) (yPixelBottom);

	    if (yPixelTopInt < 0)
	    {
		yPixelTopInt = 0;
	    }
	    if (yPixelBottomInt > height)
	    {
		yPixelBottomInt = height;
	    }

	    for (int y = yPixelTopInt; y < yPixelBottomInt; y++)
	    {
		try
		{
		    pixels[x + y * width] = 0x1B91E0;
		} catch (ArrayIndexOutOfBoundsException e)
		{
		    e.printStackTrace();
		    continue;
		}
		zBuffer[x + y * width] = 0;
	    }
	}
    }

    public void renderDistanceLimiter()
    {
	for (int i = 0; i < width * height; i++)
	{
	    int color = pixels[i];
	    int brightness = (int) (renderDistance / zBuffer[i]);

	    if (brightness < 0)
	    {
		brightness = 0;
	    }

	    if (brightness > 255)
	    {
		brightness = 255;
	    }

	    int r = (color >> 16) & 0xff;
	    int g = (color >> 8) & 0xff;
	    int b = (color) & 0xff;

	    r = r * brightness / 255;
	    g = g * brightness / 255;
	    b = b * brightness / 255;

	    pixels[i] = r << 16 | g << 8 | b;
	}
    }
}