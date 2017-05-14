package org.blim.imaging;

import java.awt.image.BufferedImage;

/**
 * Created by Rob on 14/05/2017.
 */
public class Image {

    private final BufferedImage image;

    public Image(BufferedImage image) {
        this.image = image;
    }

    public int CountRGB (int red, int green, int blue) {
        int matchingPixels = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int p = image.getRGB(x, y);
                int blueCount = (p & 0x000000FF);
                int greenCount = (p >> 8) & 0x000000FF;
                int redCount = (p >> 16) & 0x000000FF;
                if (redCount == red && greenCount == green && blueCount == blue) {
                    matchingPixels++;
                }
            }
        }

        return matchingPixels;
    }
}
