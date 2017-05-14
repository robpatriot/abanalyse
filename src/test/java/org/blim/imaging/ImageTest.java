package org.blim.imaging;

import org.apache.commons.imaging.Imaging;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Test;

import java.awt.image.BufferedImage;


/**
 * Created by Rob on 14/05/2017.
 */
public class ImageTest {

    @Test
    public void testCountRGB() throws Exception {

        final BufferedImage image = Imaging.getBufferedImage(this.getClass().getResourceAsStream("small.png"));

        Image abImage = new Image(image);

        int count = abImage.CountRGB(255,0, 0);
        assertEquals(1, count);

        count = abImage.CountRGB(0,255, 0);
        assertEquals(1, count);

        count = abImage.CountRGB(1,1, 1);
        assertEquals(0, count);
    }

}