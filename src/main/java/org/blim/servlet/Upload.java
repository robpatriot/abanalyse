package org.blim.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import org.blim.imaging.abImage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Lee on 07/05/2017.
 */
public class Upload extends HttpServlet {

    /**
     * handles HTTP POST request
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int redCount = 0;

        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                // Currently no regular form fields (input type="text|radio|checkbox|etc", select, etc).
                // If there are we can process them when item.isFormField() == true with:
                //     String fieldName = item.getFieldName();
                //     String fieldValue = item.getString();
                if (!item.isFormField()) {
                    // Process form file field (input type="file").
                    final BufferedImage image = Imaging.getBufferedImage(item.getInputStream());

                    abImage abImage = new abImage(image);
                    redCount = abImage.CountRGB(255,0, 0);
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        } catch (ImageReadException e) {
            throw new ServletException("Failed to decode imaging data: ", e);
        }

        PrintWriter writer = response.getWriter();
        writer.println("abImage contains " + redCount + " red pixels.");
        writer.flush();
    }
}
