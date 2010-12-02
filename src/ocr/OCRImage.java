/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr;

import blackwhiteimages.Pair;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author installer
 */
public class OCRImage{
    private int defaultMaxWhiteValue = 130;
    private int[] distanceVector = null;
    public BufferedImage image;

    public OCRImage(BufferedImage img, boolean isBlackAndWhite){
        if(isBlackAndWhite){
            image = img;
        }else{
            createBlackAndWhiteImage(img, defaultMaxWhiteValue);
        }
    }

    public OCRImage(BufferedImage img) {
        createBlackAndWhiteImage(img, defaultMaxWhiteValue);
    }

    public OCRImage(BufferedImage img, int maxWhiteValue){
        createBlackAndWhiteImage(img, maxWhiteValue);
    }

    private void createBlackAndWhiteImage(BufferedImage img, int maxWhiteValue){
        image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < img.getWidth(); i++){
            for(int j = 0; j < img.getHeight(); j++){
                Color c = new Color(img.getRGB(i, j));
                int grayValue = (c.getRed() + c.getGreen() + c.getBlue())/3;
                if (grayValue > maxWhiteValue) {
                    c = Color.WHITE;
                }else{
                    c = Color.BLACK;
                }
                image.setRGB(i, j, c.getRGB());
            }
        }
    }   

    public boolean[] getBooleanArray(){
        boolean[] returnValue = new boolean[image.getWidth()*image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                returnValue[x*image.getWidth() + y] = image.getRGB(x, y) == Color.BLACK.getRGB();
            }
        }
        return returnValue;
    }

    public int[] getDistanceVector(){
        if(distanceVector == null){
            int[] result = new int[image.getWidth()*image.getHeight()];
            for(int x=0; x < image.getWidth(); x++){
                for(int y=0; y < image.getHeight(); y++){
                    result[x*image.getWidth() + y] = getDistanceToBlack(x, y);
                }
            }
            distanceVector = result;
        }
        return distanceVector;
    }

    private int getDistanceToBlack(int x, int y){
        int rgb = Color.BLACK.getRGB();
        if(image.getRGB(x, y) == Color.BLACK.getRGB()){
            rgb = Color.WHITE.getRGB(); //if black search nearest white;
        }
        int distance = 0;
        boolean found_color = false;
        while(!found_color && distance < image.getWidth()){
            distance++;
            found_color = hasColor(x,y,distance,rgb);
        }
        return distance;
    }

    private boolean hasColor(int x, int y, int distance, int rgb){
        boolean hasBlack = false;
        int left = x - distance;
        int right = x + distance;
        if(left < 0){
            left = 0;
        }

        if(right >= image.getWidth()){
            right = image.getWidth()-1;
        }
        int top = y - distance;
        int bottom = y + distance;
        if(top < 0){
            top = 0;
        }
        if(bottom >= image.getHeight()){
            bottom = image.getHeight()-1;
        }
        //top row and bottom row
        int i = left;
        while(!hasBlack && i <= right){
            hasBlack = hasBlack || image.getRGB(i, top) == rgb;
            hasBlack = hasBlack || image.getRGB(i, bottom) == rgb;
            i++;
        }
        //left row and right row;
        i = top;
        while(!hasBlack && i <= bottom){
            hasBlack = hasBlack || image.getRGB(left, i) == rgb;
            hasBlack = hasBlack || image.getRGB(right, i) == rgb;
            i++;
        }
        return hasBlack;
    }

    public Integer getDistance(OCRImage bwiTest){
        if(distanceVector == null){
            getDistanceVector();
        }
        BufferedImage imageTest = bwiTest.image;
        if(imageTest.getWidth()*imageTest.getHeight() == distanceVector.length && imageTest.getWidth() == image.getWidth() && imageTest.getHeight() == image.getHeight()){
            int distance = 0;
            for(int x = 0; x < imageTest.getWidth(); x++){
                for(int y = 0; y < imageTest.getHeight(); y++){
                    boolean blackTest = imageTest.getRGB(x, y) == Color.BLACK.getRGB();
                    boolean blackThis = image.getRGB(x, y) == Color.BLACK.getRGB();
                    int currentDist = distanceVector[x*imageTest.getWidth()+y];
                    if(blackTest != blackThis){ //fault detected
                        distance += currentDist;
                    }
                }
            }
            return distance;
        }else{
            return null;
        }
    }

    public Integer getDistance(OCRImage bwiTest,int maxDistance) throws Exception{
        if(distanceVector == null){
            getDistanceVector();
        }
        BufferedImage imageTest = bwiTest.image;
        if(imageTest.getWidth()*imageTest.getHeight() == distanceVector.length && imageTest.getWidth() == image.getWidth() && imageTest.getHeight() == image.getHeight()){
            int distance = 0;
            int x = 0;
            while(x < imageTest.getWidth() && distance < maxDistance){
                int y = 0;
                while(y < image.getHeight() && distance < maxDistance){
                    boolean blackTest = imageTest.getRGB(x, y) == Color.BLACK.getRGB();
                    boolean blackThis = image.getRGB(x, y) == Color.BLACK.getRGB();
                    int currentDist = distanceVector[x*imageTest.getWidth()+y];
                    if(blackTest != blackThis){ //fault detected
                        distance += currentDist;
                    }
                    y++;
                }
                x++;
            }
            if(distance < maxDistance){
                return distance;
            }else{
                return Integer.MAX_VALUE;
            }
        }else{
            throw new RuntimeException("can't calculate distance: images have different diminsions");
        }
    }

    public OCRImage getTrimmedImage(OCRImage ocrImg){
        //search for minX and maxX
        int minX = image.getWidth()-1;
        int maxX = 0;
        for(int y = 0; y < image.getHeight(); y++){
            //Search for minX
            int x = 0;
            boolean newValue = false;
            while(!newValue && x < minX){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    minX = x;
                    newValue = true;
                }
                x++;
            }
            //Search for maxX;
            newValue = false;
            x = image.getWidth()-1;
            while(!newValue && x > maxX){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    maxX = x;
                    newValue = true;
                }
                x--;
            }

        }
        //search for minY and maxY
        int minY = image.getHeight()-1;
        int maxY = 0;
        for(int x = 0; x < image.getWidth(); x++){
            //Search for minY
            int y = 0;
            boolean newValue = false;
            while(!newValue && y < minY){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    minY = y;
                    newValue = true;
                }
                y++;
            }
            //Search for maxX;
            newValue = false;
            y = image.getHeight()-1;
            while(!newValue && y > maxY){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    maxY = y;
                    newValue = true;
                }
                y--;
            }

        }
        int width = maxX - minX;
        int height = maxY - minY;

        if(width > 0 && height > 0){
            BufferedImage img = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_RGB);
            for(int i = 0; i <= width; i++){
                for (int j = 0; j <= height; j++){
                    img.setRGB(i, j, image.getRGB(minX+i, minY+j));
                }
            }
            return new OCRImage(img,true);
        }else{
            //empty picture
            return null;
        }
    }

    public void trimImage(){
        //search for minX and maxX
        int minX = image.getWidth()-1;
        int maxX = 0;
        for(int y = 0; y < image.getHeight(); y++){
            //Search for minX
            int x = 0;
            boolean newValue = false;
            while(!newValue && x < minX){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    minX = x;
                    newValue = true;
                }
                x++;
            }
            //Search for maxX;
            newValue = false;
            x = image.getWidth()-1;
            while(!newValue && x > maxX){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    maxX = x;
                    newValue = true;
                }
                x--;
            }

        }
        //search for minY and maxY
        int minY = image.getHeight()-1;
        int maxY = 0;
        for(int x = 0; x < image.getWidth(); x++){
            //Search for minY
            int y = 0;
            boolean newValue = false;
            while(!newValue && y < minY){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    minY = y;
                    newValue = true;
                }
                y++;
            }
            //Search for maxX;
            newValue = false;
            y = image.getHeight()-1;
            while(!newValue && y > maxY){
                if(image.getRGB(x, y) == Color.BLACK.getRGB()){
                    maxY = y;
                    newValue = true;
                }
                y--;
            }

        }
        int width = maxX - minX;
        int height = maxY - minY;

        if(width > 0 && height > 0){
            BufferedImage img = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_RGB);
            for(int i = 0; i <= width; i++){
                for (int j = 0; j <= height; j++){
                    img.setRGB(i, j, image.getRGB(minX+i, minY+j));
                }
            }
            this.image = img;
        }else{
            //empty picture
            this.image = null;
        }
    }

    public OCRImage getResizedImage(int newWidth, int newHeight){
        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        double xScale = 1.0*image.getWidth()/newWidth;
        double yScale = 1.0*image.getHeight()/newHeight;
        for(int x = 0; x < newWidth; x++){
            for(int y=0; y < newHeight; y++){
                img.setRGB(x, y, image.getRGB((int)(x*xScale),(int)(y*yScale)));
            }
        }
        return new OCRImage(img,true);
    }

    public void resize(int newWidth, int newHeight){
        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        double xScale = 1.0*image.getWidth()/newWidth;
        double yScale = 1.0*image.getHeight()/newHeight;
        for(int x = 0; x < newWidth; x++){
            for(int y=0; y < newHeight; y++){
                img.setRGB(x, y, image.getRGB((int)(x*xScale),(int)(y*yScale)));
            }
        }
        image = img;
    }

    public List<OCRImage> getLines(){
        int top = -1;
        int bottom = 0;
        ArrayList<OCRImage> array = new ArrayList<OCRImage>();
        while(top < image.getHeight()-1){
            bottom = top + 1;
            boolean found = false;            
            while(top < image.getHeight()-1 && !found){
                top++;
                found = hasColorOnHorizontalLine(Color.BLACK,top);
            }
            if(found){
                bottom = top;
                while(bottom < image.getHeight()-1 && found){
                    bottom++;
                    found = hasColorOnHorizontalLine(Color.BLACK,bottom);
                }
                //if(!found){
                    BufferedImage bi = new BufferedImage(image.getWidth(), bottom - top, BufferedImage.TYPE_INT_RGB);
                    for(int y = top; y < bottom; y++){
                        for(int x = 0; x < image.getWidth(); x++){
                            bi.setRGB(x, y-top, image.getRGB(x, y));
                        }
                    }
                    array.add(new OCRImage(bi,true));
                //}
            }else{
                //no line found;
            }
            top = bottom;
        }
        return array;
    }

    public Pair<OCRImage,Integer> getImageWithLines(){
        OCRImage ocrImg = new OCRImage(image);
        int numberOfLines = 0;
        int top = -1;
        int bottom = 0;
        while(top < image.getHeight()-1){
            bottom = top + 1;
            boolean found = false;
            while(top < image.getHeight()-1 && !found){
                top++;
                found = hasColorOnHorizontalLine(Color.BLACK,top);
            }
            if(found){
                bottom = top;
                while(bottom < image.getHeight()-1 && found){
                    bottom++;
                    found = hasColorOnHorizontalLine(Color.BLACK,bottom);
                }
                if(!found){
                    Graphics2D g = ocrImg.image.createGraphics();
                    g.setColor(Color.RED);
                    g.drawLine(0, top, ocrImg.image.getWidth(), top);
                    g.setColor(Color.GREEN);
                    g.drawLine(0, bottom, ocrImg.image.getWidth(), bottom);
                    numberOfLines++;
                }
            }else{
                //no line found;
            }
            top = bottom;
        }
        Pair<OCRImage,Integer> pair = new Pair<OCRImage,Integer>();
        pair.first = ocrImg;
        pair.last = numberOfLines;
        return pair;
    }

    public OCRImage getRotatedImage(double degrees){
        AffineTransform transform = new AffineTransform();        
        transform.rotate(degrees*Math.PI/180,image.getWidth()/2,image.getHeight()/2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < img.getWidth(); x++){
            for(int y = 0; y < img.getHeight(); y++){
                img.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        op.filter(image, img);
        return new OCRImage(img);
    }

    public List<OCRImage> getCharacters(int characterSize){
        return getCharacters(characterSize,new ArrayList<OCRImage>(),new ArrayList<Integer>());
    }

    public List<OCRImage> getCharacters(int characterSize, List<OCRImage> orignalImages){
        return getCharacters(characterSize, orignalImages, new ArrayList<Integer>());
    }

    public List<OCRImage> getCharacters(int characterSize, List<OCRImage> orignalImages, List<Integer> gaps){
        ArrayList<OCRImage> characters = new ArrayList<OCRImage>();
        boolean ignoreNextGap = false;
        int lastRight = 0;
        int left = -1;
        int right = 0;
        while(left < image.getWidth()-1){
            right = left+1;
            boolean found = false;
            while(left < image.getWidth()-1 && !found){
                left++;
                found = hasColorOnVerticalLine(Color.BLACK, left);
            }
            if(found){
                if(!characters.isEmpty() && !ignoreNextGap){
                    gaps.add(left-lastRight);
                }
                ignoreNextGap = false;
                right = left;
                while(right < image.getWidth()-1 && found){
                    right++;
                    found = hasColorOnVerticalLine(Color.BLACK, right);
                }
                lastRight = right;
                if(!found){
                    BufferedImage bi = new BufferedImage(right-left, image.getHeight(), BufferedImage.TYPE_INT_RGB);
                    for(int y = 0; y < image.getHeight(); y++){
                        for(int x = left; x < right; x++){                            
                            bi.setRGB(x-left, y, image.getRGB(x, y));
                        }
                    }
                    OCRImage ocrImg = new OCRImage(bi,true);
                    if(ocrImg.image != null){
                        ocrImg.trimImage();
                        if(ocrImg.image != null){
                            //ocrImg may still have 2 letters who overlap verticaly, if they do not touch we can split them
                            List<OCRImage> images = ocrImg.getOverlappingCharacters();
                            for(int i = 0; i < images.size(); i++){
                                OCRImage img = images.get(i);
                                img.trimImage();
                                if(img.image != null){
                                    orignalImages.add(new OCRImage(img.image));
                                    img.resize(characterSize, characterSize);
                                    characters.add(img);
                                    if(i > 0){
                                        gaps.add(0);
                                    }
                                }
                            }                            
                        }else{
                            ignoreNextGap = true;
                        }
                    }
                }
            }
            left = right;
        }        
        return characters;
    }



    public List<OCRImage> getOverlappingCharacters(){
        boolean foundBlack = false;
        int grayValue = 1;
        int numberOfCharacters = 0;
        ArrayList<Point> points = new ArrayList<Point>();
        for(int x = 0; x < image.getWidth(); x++){
            foundBlack = hasColorOnVerticalLine(Color.BLACK, x);
            if(foundBlack){
                numberOfCharacters++;
                int y = 0;
                while(image.getRGB(x, y) != Color.BLACK.getRGB()){
                    y++;
                }
                makeFormGray(grayValue++,x,y);
                points.add(new Point(x,y));
            }
        }
        if(grayValue > 2){  //multiple characters
            //get height of forms if height is to small it is connected to the form under like i
            int minY[] = new int[grayValue-1];
            int maxY[] = new int[grayValue-1];
            for(int i = 0; i < grayValue-1; i++){
                minY[i] = image.getHeight();
                maxY[i] = 0;
            }
            for(int y = 0; y < image.getHeight(); y++){
                for(int i = 1; i < grayValue; i++){
                    Color gray = Color.getHSBColor(0.0f,0.0f,0.01f*i);
                    if(hasColorOnHorizontalLine(gray, y)){
                        if(y < minY[i-1]){
                            minY[i-1] = y;
                        }
                        if(y > maxY[i-1]){
                            maxY[i-1] = y;
                        }
                    }
                }
            }
            //test if height is to small of a form; // if so make the same color as underlaying form;
            for(int i = 1; i < grayValue; i++){
                int height = maxY[i-1] - minY[i-1];
                if(height < 0.4 * image.getHeight()){ //propably a dot on an other form;
                    int x = points.get(i-1).x;
                    int y = points.get(i-1).y;
                    Color gray = Color.getHSBColor(0.0f,0.0f,0.01f*i);
                    while(image.getRGB(x, y) == gray.getRGB() && y < image.getHeight()-1){
                        y++;
                    }
                    while(image.getRGB(x, y) == Color.WHITE.getRGB() && y < image.getHeight()-1){
                        y++;
                    }
                    if(image.getRGB(x, y) != Color.WHITE.getRGB()){
                        makeFormGray(i, x, y);
                    }
                }
            }
            //store the forms in different files;
            HashMap<Integer,BufferedImage> images = new HashMap<Integer, BufferedImage>();
            for(int x = 0; x < image.getWidth(); x++){
                for(int y=0; y < image.getHeight(); y++){
                    int rgb = image.getRGB(x, y);
                    if(rgb != Color.WHITE.getRGB()){
                        BufferedImage character = images.get(rgb);
                        if(character == null){
                            character = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                            for(int xNieuw = 0; xNieuw < character.getWidth(); xNieuw++){
                                for(int yNieuw = 0; yNieuw < character.getHeight(); yNieuw++){
                                    character.setRGB(xNieuw, yNieuw, Color.WHITE.getRGB());
                                }
                            }
                            images.put(rgb, character);
                        }
                        character.setRGB(x, y, Color.BLACK.getRGB());
                    }
                }
            }
            ArrayList<OCRImage> characters = new ArrayList<OCRImage>();
            for(int i = 1; i < grayValue; i++){
                Color gray = Color.getHSBColor(0.0f,0.0f,0.01f*i);
                BufferedImage img = images.get(gray.getRGB());
                if(img != null){
                    characters.add(new OCRImage(img));
                }
            }
            createBlackAndWhiteImage(image, grayValue);
            return characters;
        }else{              //one character
            ArrayList<OCRImage> characters = new ArrayList<OCRImage>();
            characters.add(new OCRImage(image));
            createBlackAndWhiteImage(image, grayValue);
            return characters;
        }
    }

    private void makeFormGray(int grayValue,int startX, int startY){
        int left = startX-1;
        int right = startX+1;
        int top = startY-1;
        int bottom = startY+1;
        Color gray = Color.getHSBColor(0.0f,0.0f,0.01f*grayValue);
        if(left < 0){
            left = 0;
        }
        if(right >= image.getWidth()){
            right = image.getWidth()-1;
        }
        if(top < 0){
            top = 0;
        }
        if(bottom >= image.getHeight()){
            bottom = image.getHeight()-1;
        }
        image.setRGB(startX, startY, gray.getRGB());
        for(int x = left; x <= right; x++){
            if(image.getRGB(x, top) != Color.WHITE.getRGB() && image.getRGB(x, top) != gray.getRGB()){
                makeFormGray(grayValue, x, top);
            }
            if(image.getRGB(x, bottom) != Color.WHITE.getRGB() && image.getRGB(x, bottom) != gray.getRGB()){
                makeFormGray(grayValue, x, bottom);
            }
        }
        if(image.getRGB(left, startY) != Color.WHITE.getRGB() && image.getRGB(left, startY) != gray.getRGB()){
            makeFormGray(grayValue, left, startY);
        }
        if(image.getRGB(right, startY) != Color.WHITE.getRGB() && image.getRGB(right, startY) != gray.getRGB()){
            makeFormGray(grayValue, right, startY);
        }
    }

    

    public boolean hasColorOnVerticalLine(Color c, int x){
        int y = 0;
        boolean foundBlack = false;
        while(y < image.getHeight() && !foundBlack){
            foundBlack = foundBlack || (image.getRGB(x, y) == c.getRGB());
            y++;
        }
        return foundBlack;
    }

    public boolean hasColorOnHorizontalLine(Color c, int y){
        int x = 0;
        boolean foundColor = false;
        while(x < image.getWidth() && !foundColor){
            foundColor = foundColor || (image.getRGB(x, y) == c.getRGB());
            x++;
        }
        return foundColor;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof OCRImage){
            BufferedImage img = ((OCRImage)obj).image;
            if(img.getWidth() == image.getWidth() && img.getHeight() == image.getHeight()){
                int x = 0;
                int y = 0;
                boolean equal = true;
                while(equal && x < image.getWidth()){
                    while(equal && y < image.getHeight()){
                        equal = image.getRGB(x, y) == img.getRGB(x, y);
                        y++;
                    }
                    x++;
                }
                return equal;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.image != null ? this.image.hashCode() : 0);
        return hash;
    }

    public OCRImage reduceFormsToPoints(){
        int aantal = 0;
        Color c = Color.BLACK;
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());        
        boolean[][] tested = new boolean[image.getWidth()][image.getHeight()];
        for(int x=0; x < image.getWidth(); x++){
            for(int y=0; y < image.getHeight(); y++){
                tested[x][y] = false;
            }
        }
        int y = 0;
        while(y < image.getHeight()){
            int x = 0;
            while(x < image.getWidth()){
                while(x < image.getWidth() && !(!tested[x][y] && image.getRGB(x, y) == c.getRGB())){
                    x++;
                }
                if(x < image.getWidth()){
                    aantal++;
                    int[] values = new int[4];
                    values[0] = x;
                    values[1] = x;
                    values[2] = y;
                    values[3] = y;
                    getBoundingBox(c, x, y, values, tested);
                    img.setRGB((values[0]+values[1])/2, (values[2]+values[3])/2, Color.BLACK.getRGB());
                    image.setRGB((values[0]+values[1])/2, (values[2]+values[3])/2, Color.RED.getRGB());
                }
            }
            y++;
        }
        System.out.println("aantal: " + aantal);
        return new OCRImage(img,true);
    }

    private void getBoundingBox(Color c, int startX, int startY, int[] values, boolean[][] tested){
        tested[startX][startY] = true;
        int left = startX-1;
        int right = startX+1;
        int top = startY-1;
        int bottom = startY+1;
        if(left < 0){
            left = 0;
        }
        if(right >= image.getWidth()){
            right = image.getWidth()-1;
        }
        if(top < 0){
            top = 0;
        }
        if(bottom >= image.getHeight()){
            bottom = image.getHeight()-1;
        }
        for(int x = left; x <= right; x++){
            if(!tested[x][top] && image.getRGB(x, top) == c.getRGB()){
                tested[x][top] = true;
                if(x < values[0]){
                    values[0] = x;
                }
                if(x > values[1]){
                    values[1] = x;
                }
                if(top < values[2]){
                    values[2] = top;
                }
                getBoundingBox(c, x, top, values,tested);
            }
            if(!tested[x][bottom] && image.getRGB(x, bottom) == c.getRGB()){
                tested[x][bottom] = true;
                if(x < values[0]){
                    values[0] = x;
                }
                if(x > values[1]){
                    values[1] = x;
                }
                if(bottom > values[3]){
                    values[3] = bottom;
                }
                getBoundingBox(c, x, bottom, values,tested);
            }
        }
        if(!tested[left][startY] && image.getRGB(left, startY) == c.getRGB()){
            tested[left][startY] = true;
            if(left < values[0]){
                values[0] = left;
            }
            getBoundingBox(c, left, startY, values,tested);
        }
        if(!tested[right][startY] && image.getRGB(right, startY) != c.getRGB()){
            tested[right][startY] = true;
            if(right > values[1]){
                values[1] = right;
            }
            getBoundingBox(c, right, startY, values,tested);
        }
    }

    public void colorCharacters(){
        Color[] colors = new Color[4];
        colors[0] = Color.RED;
        colors[1] = Color.BLUE;
        colors[2] = Color.GREEN;
        colors[3] = Color.CYAN;
        int currentColor = 0;
        for(int y = 0; y < image.getHeight(); y++){
            int x = 0;
            while(hasColorOnHorizontalLine(Color.BLACK, y)){
                while(image.getRGB(x, y) != Color.BLACK.getRGB()){
                    x++;
                }
                makeFormColor(colors[currentColor], x, y);
                currentColor++;
                currentColor %= 4;
            }
        }
    }

    private void makeFormColor(Color color,int startX, int startY){
        int left = startX-1;
        int right = startX+1;
        int top = startY-1;
        int bottom = startY+1;
        if(left < 0){
            left = 0;
        }
        if(right >= image.getWidth()){
            right = image.getWidth()-1;
        }
        if(top < 0){
            top = 0;
        }
        if(bottom >= image.getHeight()){
            bottom = image.getHeight()-1;
        }
        image.setRGB(startX, startY, color.getRGB());
        for(int x = left; x <= right; x++){
            if(image.getRGB(x, top) != Color.WHITE.getRGB() && image.getRGB(x, top) != color.getRGB()){
                makeFormColor(color, x, top);
            }
            if(image.getRGB(x, bottom) != Color.WHITE.getRGB() && image.getRGB(x, bottom) != color.getRGB()){
                makeFormColor(color, x, bottom);
            }
        }
        if(image.getRGB(left, startY) != Color.WHITE.getRGB() && image.getRGB(left, startY) != color.getRGB()){
            makeFormColor(color, left, startY);
        }
        if(image.getRGB(right, startY) != Color.WHITE.getRGB() && image.getRGB(right, startY) != color.getRGB()){
            makeFormColor(color, right, startY);
        }
    }
}
