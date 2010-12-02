/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package blackwhiteimages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import ocr.OCRImage;

/**
 *
 * @author jannes
 */
public class ImageCreator {
    private Graphics2D graphics;

    public ImageCreator(){
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        graphics =  image.createGraphics();
    }

    public void createImages(String s, Font f, File dir){
        for(int i = 0; i < s.length(); i++){
            createImage(s.charAt(i),f,dir);
        }
    }

    public void createImages(String s, Font f, File dir, int width, int height){
        for(int i = 0; i < s.length(); i++){
            createImage(s.charAt(i),f,dir,width,height);
        }
    }

    public void createTestImages(String s, Font f, File dir, int width, int height,double changeProcentage){
        for(int i = 0; i < s.length(); i++){
            createTestImage(s.charAt(i),f,dir,width,height,changeProcentage);
        }
    }

    public BufferedImage createImage(char c,Font f){
        int height = graphics.getFontMetrics(f).getHeight();
        int width = graphics.getFontMetrics(f).charWidth(c);
        int baseline = graphics.getFontMetrics(f).getAscent();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(f);
        g2d.setColor(Color.BLACK);
        g2d.drawString("" + c, 0, baseline);
        return image;
    }

    public BufferedImage createImage(char c1,char c2,Font f){
        int height = graphics.getFontMetrics(f).getHeight();
        int width = graphics.getFontMetrics(f).charWidth(c1);
        width += graphics.getFontMetrics(f).charWidth(c2);
        width *= 2;
        int baseline = graphics.getFontMetrics(f).getAscent();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(f);
        g2d.setColor(Color.BLACK);
        g2d.drawString("" + c1 + c2, 0, baseline);
        return image;
    }

    public void createImage(char c, Font f, File dir){
        OCRImage bwi = new OCRImage(createImage(c, f),true);
        File file = new File(dir,"" + c + ".png");
        try{
            ImageIO.write(bwi.image, "png", file);
        }catch(Exception ex){
            System.out.println("createImage: " + ex.getMessage());
        }
    }

    public void createImage(char c, Font f, File dir,int width, int height){
        OCRImage bwi = new OCRImage(createImage(c, f),true);
        bwi.trimImage();
        bwi.resize(width, height);
        String caption = "" + c;
        if(caption.toLowerCase().equals(caption)){
            caption = "lower_";
        }else{
            caption = "upper_";
        }
        File file = new File(dir,caption + c + ".png");
        try{
            ImageIO.write(bwi.image, "png", file);
        }catch(Exception ex){
            System.out.println("createImage: " + ex.getMessage());
        }
    }

    public void createTestImage(char c, Font f, File dir,int width, int height, double changeProcentage){
        OCRImage bwi = new OCRImage(createImage(c, f),true);
        bwi.trimImage();
        bwi.resize(width, height);
        Random random = new Random();
        int changes = (int)(changeProcentage * width * height);
        for(int i = 0; i < changes; i++){
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            if(bwi.image.getRGB(x, y) == Color.BLACK.getRGB()){
                bwi.image.setRGB(x, y, Color.WHITE.getRGB());
            }else{
                bwi.image.setRGB(x, y, Color.BLACK.getRGB());
            }
        }
        String caption = "" + c;
        if(caption.toLowerCase().equals(caption)){
            caption = "lower_";
        }else{
            caption = "upper_";
        }
        File file = new File(dir,"test_" + caption + c + ".png");
        try{
            ImageIO.write(bwi.image, "png", file);
        }catch(Exception ex){
            System.out.println("createImage: " + ex.getMessage());
        }
    }

    public static void createImage(boolean[] data,int width,int height, File file){
        if(width*height == data.length){
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for(int x=0; x < width; x++){
                for(int y=0; y < height; y++){
                    if(data[x*width+y]){
                        image.setRGB(x, y, Color.BLACK.getRGB());
                    }else{
                        image.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            try{
                ImageIO.write(image, "png", file);
            }catch(Exception ex){
                System.out.println("createImage(boolean[]): " + ex.getMessage());
            }
        }else{
            System.out.println("createImage(boolean[]): width * height is not bpnd.size()" );
        }
    }



    public double getWidthHeightRatio(char c, Font f){
        BufferedImage bi = createImage(c, f);
        OCRImage bwi = new OCRImage(bi);
        bwi.trimImage();
        return (1.0*bwi.image.getWidth())/bwi.image.getHeight();
    }
}
