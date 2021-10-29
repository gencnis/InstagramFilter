/******************************************************************************
 *  Compilation:  javac instagramfilter.java
 *  Execution:    java instagramfilter [filePath (String)]
 * 
 *  PLEASE TYPE TO EXECUTE: java instagramfilter [file name] [filter name: swapRedBlue, binarize, mean, median] [an integer (treshold or the windowsize)]
 *  EVEN IF YOU RUN THE SWAPREDBLUE PLEASE TYPE AN INTEGER, OTHERWISE IT WILL CRASH, I COULD NOT FIGURE THAT ONE OUT YET
 *
 *  Displays an image using Java's Swing & AWT (Abstract Window Toolkit) packages.
 *  In Tuesday's class, we'll talk through Image objects, copying an Image pixel by pixel,
 *  and creating a swapRedBlue filter that manipulates pixels.
 *
 *  % java ImageFilter image.png swapRedBlue
 *      Displays image.png and filters it with changing red and blue colors in the picture.
 *  
 *	% java ImageFilter image.png binarize 128
 *		Creates a black & white image using intensity threshold T.
 *
 *	% java ImageFilter image.png mean 7
 *		Smooths the image using a W pixel by W pixel 2D sliding window streaming mean filter.
 *
 *	% java ImageFilter image.png median 10
 *		Smooths the image using a W pixel by W pixel 2D sliding window streaming median filter.
 *
 * @author Nisanur Genc
 *
 ******************************************************************************/

import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.util.Arrays;

public class instagramfilter{

	public static void main(String[] args){

		// If the user misses a commandline argument, show them a helpful usage statement
        String usageStatement = "USAGE: java ImageFilter filePath"
                + "\nFor example:"
                + "\n\tjava ImageFilter image.png"
                + "The image's file extension must be PNG, JPEG, or JPG.";

        // Parse commandline arguments
        String fileName = "";
        String filterName = "";
        int threshold = 0;

        if(args.length > 0){

            fileName = args[0];
            filterName = args[1];
            threshold = Integer.parseInt(args[2]);

        } else {

            System.out.println(usageStatement);
            return;
        }

        // Open the source image, filter it, and save the output image
        boolean successful = filterImage(fileName, filterName, threshold);
        if (!successful) {
            System.out.println("ERROR: Failed to filter the image.");
            System.out.println(usageStatement);
        }
	}

	public static boolean filterImage(String inFileName, String filterName, int threshold){

        // Read in the original image from the input file
        BufferedImage original = null;

        try {
            original = ImageIO.read(new File(inFileName));
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
            return false;
        }

        displayImage(original, inFileName);

        // Copy the image
        BufferedImage copied;
        copied = copy(original);
        displayImage(copied, "Copy");

        // Save the copy in a new image file
        int period = inFileName.indexOf( "." );
        String fileExtension = inFileName.substring( period+1 );
        String copyFileName = inFileName.substring( 0, period ) + "_copy." + fileExtension;

        try {
            File copiedFile = new File( copyFileName );
            ImageIO.write( copied, fileExtension,  copiedFile );
        } catch (IOException e) {
            System.err.println( String.format("%s%n", e) );
            return false;
        }

        // Creates the empty image
        BufferedImage filtered = null;

        // Checks which filter was chosen
        if (filterName.equals("swapRedBlue")){

            // calls the filter for swapRedBlue
            filtered = swapRedBlue(original);
            // displays the filtered image
            displayImage(filtered, "Swap Red Blue");
            // Save the filtered image in a new image file
            String filteredFileName = inFileName.substring(0, period) + "_swapRedBlue." + fileExtension;

        } else if (filterName.equals("binarize")){

            // calls the filter for binarize
            filtered = binarize(original, threshold);
            // displays the filtered image
            displayImage(filtered, "Binarized");
            // Save the filtered image in a new image file
            String filteredFileName = inFileName.substring(0, period) + "_binarized." + fileExtension;


        } else if(filterName.equals("mean")){

            // calls the filter for Mean
            filtered = mean(original, threshold);
            // displays the filtered image
            displayImage(filtered, "Mean");
            // Save the filtered image in a new image file
            String filteredFileName = inFileName.substring(0, period) + "_mean." + fileExtension;

        } else if(filterName.equals("median")){

            // calls the filter for Median
            filtered = median(original, threshold);
            // displays the filtered image
            displayImage(filtered, "Median");
            // Save the filtered image in a new image file
            String filteredFileName = inFileName.substring(0, period) + "_median." + fileExtension;

        } else{

            // Prints an error message for the invalid input
            System.out.println("Try again, this filter name does not work.");
        }

        return true;    // Success!
    }

	/**
     * Swap the red and blue color channels in the original image's pixels.
     * @param original (BufferedImage) the original image
     * @return filtered (BufferedImage) the filtered image
     */
    public static BufferedImage swapRedBlue(BufferedImage original){
        // The output image begins as a blank image that is the same size and type as the original
        BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        // Iterate over the original, swapping the red and blue color channels of each pixel
        int rgb, red, green, blue;
        Color colorIn, colorOut;
        for (int row=0; row<original.getHeight(); row++){
            for (int col=0; col<original.getWidth(); col++){
                rgb = original.getRGB( col, row );
                colorIn = new Color( rgb );
                red = colorIn.getBlue();
                green = colorIn.getGreen();
                blue = colorIn.getRed();
                colorOut = new Color( red, green, blue );
                System.out.println(String.format("original[%d][%d] = %d, %d, %d", row, col, red, green, blue));
                filtered.setRGB(col, row, colorOut.getRGB());
            }
		}

        // Return a reference to the shiny new filtered image
        return filtered;
    }

    public static BufferedImage binarize(BufferedImage original, int threshold){
        // The output image begins as a blank image that is the same size and type as the original
        BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        // Iterate over the original, comparing the intensity of each pixel
        int rgb, red, green, blue;
        int binarized = 0;
        double intensity;

        Color colorIn, colorOut;

        for (int row=0; row<original.getHeight(); row++){
            for (int col=0; col<original.getWidth(); col++){
                rgb = original.getRGB(col, row);
                colorIn = new Color(rgb);
                red = colorIn.getRed();
                green = colorIn.getGreen();
                blue = colorIn.getBlue();
                
                intensity = Math.sqrt((red*red)+(green*green)+(blue*blue));

                if ((int)intensity < threshold){
                    binarized = 0;
                    System.out.println("black" + intensity);

                } else if ((int)intensity > threshold){
                    binarized = 255;
                    System.out.println("white" + intensity);

                } else {
                   System.out.println("Something is wrong. Check the code.");
                }
                
                colorOut = new Color(binarized, binarized, binarized);
                filtered.setRGB(col, row, colorOut.getRGB());
                System.out.println(String.format("original[%d][%d] = %d, %d, %d, %d, %d", row, col, red, green, blue, binarized, threshold ));

            }
        }

        // Return a reference to the shiny new filtered image
        return filtered;
    }

  public static BufferedImage mean(BufferedImage original, int windowsize){
        // The output image begins as a blank image that is the same size and type as the original
        BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        // Iterate over the original, comparing the intensity of each pixel
        int rgb, red, green, blue;
        int[] colors = new int[windowsize*windowsize];

        Color colorIn, colorOut;

        for (int row=0; row<original.getHeight()-windowsize; row++){
            for (int col=0; col<original.getWidth()-windowsize; col++){

                colors = new int[windowsize*windowsize];

                int numOfColors = 0;

                for(int i=0; i<windowsize; i++) {
                    for(int a =0; a<windowsize; a++){
                        System.out.println(original.getRGB(col+a, row+i));
                        colors[numOfColors] = original.getRGB(col+a, row+i);
                        numOfColors = numOfColors + 1;
                    }
                }

                int sumRed = 0;
                int sumGreen = 0;
                int sumBlue = 0;

                for(int i=0; i<colors.length; i++) {
                    colorIn = new Color(colors[i]);
                    sumRed = sumRed + colorIn.getRed();
                    sumGreen = sumGreen + colorIn.getGreen();
                    sumBlue = sumBlue + colorIn.getBlue();
                }

                int meanRed = sumRed/colors.length;
                int meanGreen = sumGreen/colors.length;
                int meanBlue = sumBlue/colors.length;


                for(int i=0; i<windowsize; i++) {
                    for(int a =0; a<windowsize; a++){
                        colorOut = new Color(meanRed,meanGreen,meanBlue);
                        filtered.setRGB(col+a, row+i, colorOut.getRGB());
                    }
                }

                // System.out.println(String.format("original[%d][%d] = %d, %d, %d, %d, %d", row, col, red, green, blue));

            }
        }

        // Return a reference to the shiny new filtered image
        return filtered;
    }

    public static BufferedImage median(BufferedImage original, int windowsize){
        BufferedImage filtered = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        // Iterate over the original, comparing the intensity of each pixel
        int rgb, red, green, blue;
        int[] colors = new int[windowsize*windowsize];

        Color colorIn, colorOut;

        for (int row=0; row<original.getHeight()-windowsize; row++){
            for (int col=0; col<original.getWidth()-windowsize; col++){

                colors = new int[windowsize*windowsize];

                int numOfColors = 0;

                for(int i=0; i<windowsize; i++) {
                    for(int a =0; a<windowsize; a++){
                        System.out.println(original.getRGB(col+a, row+i));
                        colors[numOfColors] = original.getRGB(col+a, row+i);
                        numOfColors = numOfColors + 1;
                    }
                }
                Arrays.sort(colors);

                int medianColor;
                if (colors.length %2 == 0){
                    medianColor = colors[(colors.length/2)-1];
                } else {
                    medianColor = colors[((colors.length-1)/2)];
                }

                for(int i=0; i<windowsize; i++) {
                    for(int a =0; a<windowsize; a++){
                        colorOut = new Color(medianColor);
                        filtered.setRGB(col+a, row+i, colorOut.getRGB());
                    }
                }

                // System.out.println(String.format("original[%d][%d] = %d, %d, %d, %d, %d", row, col, red, green, blue));

            }
        }

        // Return a reference to the shiny new filtered image
        return filtered;
    }


    public static JFrame displayImage( BufferedImage img, String title ){
        // Create the graphics window
        JFrame window = new JFrame();
        window.setTitle(title);
        window.setSize(img.getWidth()+20, img.getHeight()+40);

        // Center the image in the graphics window
        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        window.add(label);

        // Make the graphics window visible until the user closes it (which also ends the program)
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        // Return a reference to the display window, so that we can manipulate it in the future, if we like.
        return window;
    }

    /**
     * Copy the original image's pixels into the output image.
     * @param original (BufferedImage) the original image
     * @return duplicate (BufferedImage) a new copy of the original image
     */
    public static BufferedImage copy( BufferedImage original ){
        // The output image begins as a blank image that is the same size and type as the original
        BufferedImage duplicate = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        // Iterate over the original, copying each pixel's RGB color over to the new image (the copy)
        int rgb, red, green, blue;
        Color colorIn, colorOut;
        for (int row=0; row<original.getHeight(); row++){
            for (int col=0; col<original.getWidth(); col++){
                rgb = original.getRGB( col, row );
                // Casting the RGB integer to a Color is unnecessary in this case, but has been included here
                // as an example of how you can access the red (R), green (G), and blue (B) channels individually,
                // which will be essential for creating your own filters in the future.
                colorIn = new Color( rgb );
                red = colorIn.getRed();
                green = colorIn.getGreen();
                blue = colorIn.getBlue();
                colorOut = new Color( red, green, blue );
                // System.out.println(String.format("original[%d][%d] = %d, %d, %d", row, col, red, green, blue));
                duplicate.setRGB(col, row, colorOut.getRGB());
            }
        }

        // Return a reference to the shiny new copy of the input image
        return duplicate;
    }

}