package org.blim.imaging;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Byte.toUnsignedInt;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by Rob on 14/05/2017.
 */
public class abImage {

    // Get the native library
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private final BufferedImage mImage;

    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mHsvLowerBound = new Scalar(0);
    private Scalar mHsvUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50);
    private List<MatOfPoint> mContours = new ArrayList<>();

    private Scalar CONTOUR_COLOR = new Scalar(0, 0, 0);

    public abImage(BufferedImage inputImage) {
        mImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        mImage.getGraphics().drawImage(inputImage, 0, 0, null);
    }

    public int CountRGB(int red, int green, int blue) {
        int matchingPixels = 0;

        for (int x = 0; x < mImage.getWidth(); x++) {
            for (int y = 0; y < mImage.getHeight(); y++) {
                int p = mImage.getRGB(x, y);
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

    public void setHsvColour(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0] - mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0] + mColorRadius.val[0] < 180) ? hsvColor.val[0]+mColorRadius.val[0] : 180;

        mHsvLowerBound.val[0] = minH;
        mHsvUpperBound.val[0] = maxH;

        mHsvLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mHsvUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mHsvLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mHsvUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];
    }

    public void detectBlobs() {
        Mat matBgr = new Mat(mImage.getHeight(), mImage.getWidth(), CV_8UC3);
        Mat matPyrDownBgr = new Mat();
        Mat matHsv = new Mat();
        Mat matMask = new Mat();
        Mat matDilMask = new Mat();
        Mat matHierarchy = new Mat();

        byte[] pixels = ((DataBufferByte) mImage.getRaster().getDataBuffer()).getData();
        matBgr.put(0,0, pixels);
        drawImage(toBufferedImage(matBgr));

        Imgproc.pyrDown(matBgr, matPyrDownBgr);

        Imgproc.cvtColor(matPyrDownBgr, matHsv, Imgproc.COLOR_BGR2HSV);

        setHsvColour(new Scalar(0,255,255));

        Core.inRange(matHsv, mHsvLowerBound, mHsvUpperBound, matMask);
        drawImage(toBufferedImage(matMask));
        Imgproc.dilate(matMask, matDilMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(matDilMask, contours, matHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(2,2), contour);
                mContours.add(contour);
            }
        }

        Imgproc.drawContours(matBgr, mContours, -1, CONTOUR_COLOR);

        drawImage(toBufferedImage(matBgr));
    }

    private void drawImage(BufferedImage showImage) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(showImage.getWidth() + 50, showImage.getHeight() + 50);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(showImage, 0, 0, null);
            }
        };
        frame.add(panel);
        frame.setVisible(true);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage toBufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage buffI = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) buffI.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return buffI;
    }
}
