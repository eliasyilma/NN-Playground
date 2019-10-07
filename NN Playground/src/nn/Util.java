package nn;

import java.awt.Color;
import java.awt.*;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author CASE
 */
public class Util {

    public static Color neg_color = new Color(243, 182, 112);
    public static Color pos_color = new Color(133, 183, 204);
    public Font PRIMARY_FONT;
    public Font ICON_FONT;
    
    public Util(){
        init_primary_font();
    }

    public void init_primary_font() {
        try {
            //create the font to use. Specify the size!
            PRIMARY_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("src\\Alcubierre.otf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(PRIMARY_FONT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
    }
   
    
    public Color interpolate_color(Color c1, Color c2, float val) {
        float[] hsb1 = new float[3];
        float[] hsb2 = new float[3];
        Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), hsb1);
        Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), hsb2);

        float b_inter = 0, h_inter = 0, s_inter = 0;
        if (val > 0) {
            //0    1
            //0.6  x
            //1    hsb1[2]
            b_inter = 1 + (hsb1[2] - 1) * Math.abs(val);
            h_inter = hsb1[0];
            s_inter = (hsb1[1]) * Math.abs(val);
        } else {
            b_inter = 1 + (hsb2[2] - 1) * Math.abs(val);
            h_inter = hsb2[0];
            s_inter = hsb2[1] * Math.abs(val);
        }
        int c_rgb = Color.HSBtoRGB(h_inter, s_inter, b_inter);
        return new Color((c_rgb >> 16) & 0xFF, (c_rgb >> 8) & 0xFF, c_rgb & 0xFF);
    
    }

}
