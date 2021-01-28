package pac1;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class ImageConverter {

  public static void imageConverter(String input_path, String output_path) {

    try {

      File input = new File(input_path);
      File output = new File(output_path);

      BufferedImage input_image = ImageIO.read(input);
      BufferedImage output_image = new BufferedImage(input_image.getWidth(),input_image.getHeight(),BufferedImage.TYPE_INT_RGB);

      output_image.createGraphics().drawImage(input_image, 0, 0, Color.WHITE, null);
      ImageIO.write(output_image, "jpg", output);

    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}
