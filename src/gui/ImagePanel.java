/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


/**
 *
 * @author installer
 */
public class ImagePanel extends Component{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage image;    

    public ImagePanel(){}

    public ImagePanel(BufferedImage image){
        this.image = image;
        this.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
        this.setMinimumSize(new Dimension(image.getWidth(),image.getHeight()));
    }

    public void setImage(BufferedImage image){
        this.image = image;
        this.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));        
        this.repaint();
    }

    public BufferedImage getImage(){
        return image;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }
}
