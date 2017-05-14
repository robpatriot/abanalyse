package org.blim.imaging;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.awt.image.BufferedImage;

/**
 * Created by Rob on 14/05/2017.
 */
public class Image {

    // Get the native library
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

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

    public void useOpenCV() {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
    }
}
