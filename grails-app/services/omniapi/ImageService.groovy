package omniapi

import grails.transaction.Transactional

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.commons.codec.binary.Base64;
import org.imgscalr.Scalr;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;


@Transactional
class ImageService implements ResourceLoaderAware {

    def grailsApplication
    def springSecurityService
    def resourceLoader
    def appDaoService
    def campaignService
	
	
    def getThumbNail(def multipartFile, def width){
        BufferedImage image = ImageIO.read( multipartFile );
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        //float factor = width/imageWidth;
        //System.out.println(imageWidth);
        //System.out.println(factor);
        //System.out.println(Float.valueOf(imageHeight*factor));
        //Image scaledImage = image.getScaledInstance((imageWidth*factor).intValue(),
        //										(imageHeight*factor).intValue(),
        //									java.awt.Image.SCALE_SMOOTH);
        Image scaledImage = Scalr.resize(image, width);
		
        return scaledImage;
		
    }
       
    @Override
    public void setResourceLoader(ResourceLoader arg0) {
        this.resourceLoader = arg0;
		
    }

}


