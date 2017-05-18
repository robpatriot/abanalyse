package org.blim.imaging;

import org.apache.commons.imaging.Imaging;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.awt.image.BufferedImage;


/**
 * Created by Rob on 14/05/2017.
 */
public class abImageTest {

    @Test
    public void testCountRGB() throws Exception {

        BufferedImage image = Imaging.getBufferedImage(this.getClass().getResourceAsStream("small.png"));
        abImage abImage = new abImage(image);

        int count = abImage.CountRGB(255,0, 0);
        assertEquals("Red was not 1 as expected", 1, count);
        count = abImage.CountRGB(0,255, 0);
        assertEquals("Green was not 1 as expected", 1, count);
        count = abImage.CountRGB(255,255, 255);
        assertEquals("White was not 0 as expected", 0, count);

        image = Imaging.getBufferedImage(this.getClass().getResourceAsStream("large.png"));
        abImage = new abImage(image);

        count = abImage.CountRGB(255,0, 0);
        assertEquals("Red was not 2422 as expected", 2422, count);
        count = abImage.CountRGB(0,0, 0);
        assertEquals("Black was not 0 as expected",0, count);
    }

    @Test
    public void testOpenCV() throws Exception {
        BufferedImage image = Imaging.getBufferedImage(this.getClass().getResourceAsStream("large.png"));
        abImage abImage = new abImage(image);

        abImage.detectBlobs();
    }
}