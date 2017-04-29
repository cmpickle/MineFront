package minefront;

import java.applet.Applet;
import java.awt.BorderLayout;

public class MinefrontApplet extends Applet {
	private static final long serialVersionUID = -8473794988768627089L;
	
	private Display display = new Display();
	
	public void init() {
		setLayout(new BorderLayout());
		add(display);
	}
	
	public void start() {
		display.start();
	}
	
	public void stop() {
		display.stop();
	}
}
