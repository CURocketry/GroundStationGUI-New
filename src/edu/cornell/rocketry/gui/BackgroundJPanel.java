package edu.cornell.rocketry.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BackgroundJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND      = Color.black;
    private static final Color BACKGROUND_2    = Color.WHITE;
    
    //String path;
    
    private Image img;

    public BackgroundJPanel(String img) {
      this(new ImageIcon(img).getImage());
    }

    public BackgroundJPanel(Image img) {
      this.img = img;
      Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
      setPreferredSize(size);
      setMinimumSize(size);
      setMaximumSize(size);
      setSize(size);
      setLayout(null);
    }

    public void paintComponent(Graphics g) {
      g.drawImage(img, 0, 0, null);
    }
    
    /*public BackgroundJPanel (String path) {
    	this.path = path;
    }*/

   /*@Override
   protected void paintComponent(Graphics g) {
       Graphics2D graphics = (Graphics2D) g.create();        
       int midY = 100;
       Paint topPaint = new GradientPaint(0, 0, BACKGROUND,0, midY, BACKGROUND_2);
       graphics.setPaint(topPaint);
       graphics.fillRect(0, 0, getWidth(), midY);        
       Paint bottomPaint = new GradientPaint(0, midY + 1, BACKGROUND_2,0, getHeight(), BACKGROUND);
       graphics.setPaint(bottomPaint);
       graphics.fillRect(0, midY, getWidth(), getHeight());
       Image img = Toolkit.getDefaultToolkit().getImage(path);
       int imgX = img.getWidth(null);
       int imgY = img.getHeight(null);
       //graphics.drawImage(img, (getWidth() - imgX) / 2, (getHeight() - imgY) / 2, imgX, imgY, null);
       graphics.drawImage(img, 0, 0, null);
     //  graphics.dispose();
   }*/
   
}


