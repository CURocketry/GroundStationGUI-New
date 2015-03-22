package edu.cornell.rocketry.util;

import javax.swing.ImageIcon;

public class ImageFactory {

	
	private static ImageIcon enabled_image;
	private static ImageIcon busy_image;
	private static ImageIcon disabled_image;
	
	public static void init () {
		enabled_image = new ImageIcon("./assets/green_icon_20_20.jpg");
		busy_image = new ImageIcon("./assets/yellow_icon_20_20.jpg");
		disabled_image = new ImageIcon("./assets/red_icon_20_20.jpg");
	}
	
	
	
	public static ImageIcon enabledImage () { 
		return enabled_image;
	}
	
	public static ImageIcon busyImage () {
		return busy_image;
	}
	
	public static ImageIcon disabledImage () {
		return disabled_image;
	}
}
