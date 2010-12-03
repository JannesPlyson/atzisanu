/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import ocr.OCRImage;
import blackwhiteimages.Pair;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

/**
 *
 * @author jannes
 */
public class ImageOptimizationPanel extends JPanel{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanel imagePanel;
    private BufferedImage image = null;
    private JSlider sliderMaxBlackValue;
    private JSlider sliderRotate;

    public ImageOptimizationPanel(){
        this.setLayout(new BorderLayout());
        imagePanel = new ImagePanel();
        final JScrollPane scrollPane = new JScrollPane(imagePanel);
        this.add(scrollPane,BorderLayout.CENTER);
        JButton buttonOpenFile = new JButton("Open File");
        buttonOpenFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                int result = jfc.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION){
                    try{
                        BufferedImage img = ImageIO.read(jfc.getSelectedFile());
                        if(img != null){
                            image = img;
                            imagePanel.setImage(image);
                            scrollPane.revalidate();
                        }else{
                            JOptionPane.showMessageDialog(null, "could not open image: not a valid gif/jpeg/png");
                        }
                    }catch(Exception exc){
                        JOptionPane.showMessageDialog(null, "could not open image: can't open file");
                    }
                }
            }
        });
        this.add(buttonOpenFile,BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        this.add(optionsPanel,BorderLayout.SOUTH);

        sliderMaxBlackValue = new JSlider(0, 255);
        sliderMaxBlackValue.setToolTipText("maximum grayvalue that is turned to black");
        sliderMaxBlackValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateImage();
            }
        });
        sliderMaxBlackValue.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateImage();
            }
        });
        optionsPanel.add(sliderMaxBlackValue);   
        
        sliderRotate = new JSlider(-1800,1800);
        sliderRotate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateImage();
            }
        });
        sliderRotate.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateImage();
            }
        });
        optionsPanel.add(sliderRotate);
        //TODO create a button to find the optimal rotation;
        //count lines with no rotation;
        //count lines with rotation 0.1*x,-0.1*x
        //while lineCount does not descent x++;

        try{
            File f = new File("testImages");
            f = new File(f,"test5.jpg");
            BufferedImage img = ImageIO.read(f);
            if(img != null){
                image = img;
                imagePanel.setImage(image);
                scrollPane.revalidate();
            }
        }catch(Exception ex){}
    }

    private void updateImage(){
        if(imagePanel.getImage() != null){
            new Thread(new Runnable() {
                public void run() {
                    synchronizedUpdateImage();
                }
            }).start();
        }
    }

    private synchronized void synchronizedUpdateImage(){
        OCRImage bwi = new OCRImage(image, sliderMaxBlackValue.getValue());
        bwi = bwi.getRotatedImage(1.0*sliderRotate.getValue()/10);
        //bwi.colorCharacters();
        Pair<OCRImage,Integer> pair = bwi.getImageWithLines();
        bwi = pair.first;
        System.out.println(pair.last);        
        imagePanel.setImage(bwi.image);
    }

    public OCRImage getOptimizedImage(){
        OCRImage bwi = new OCRImage(image, sliderMaxBlackValue.getValue());
        bwi = bwi.getRotatedImage(1.0*sliderRotate.getValue()/10);
        return bwi;
    }

}
