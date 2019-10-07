package nn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class Layer {

    public Node[] neurons;
    public int no_neurons = 2;
    Color btn_normal_color = new Color(237, 237, 237);
    Color btn_entered_color = new Color(220, 220, 220);
    Color btn_clicked_color = new Color(200, 200, 200);
    Color current_btn_color;
    int plus_btn_status = 0;//0 for normal,1 for entered,2 for clicked
    int minus_btn_status = 0;//0 for normal,1 for entered,2 for clicked

    float node_clearance;
    int layerX = 50, layerY = 50;
    int pos_btn_x, pos_btn_y, neg_btn_x, neg_btn_y;

    public Layer(Node[] nodes, int x, int y) {
        neurons = nodes;
        layerX = x;
        layerY = y;

    }

    public void draw_layer(Graphics2D g2d) {
        if (neurons[0].type == 1) {
            draw_plus_btn(g2d);
            draw_minus_btn(g2d);
            if (neurons.length > 1) {
                g2d.drawString(neurons.length + " neurons", layerX - 10, layerY - 15);
            } else {
                g2d.drawString(neurons.length + " neuron", layerX - 10, layerY - 15);
            }
        }

        for (int i = 0; i < neurons.length; i++) {
            //draw neurons in a vertical array
            neurons[i].draw_neuron(g2d);

        }
    }

    public void draw_plus_btn(Graphics2D g2d) {
        int x = layerX - 15;
        int y = layerY - 55;
        pos_btn_x = x + 15;
        pos_btn_y = y + 15;
        g2d.setColor(btn_normal_color);
        g2d.fillOval(x, y, 30, 30);
        g2d.setColor(new Color(110, 110, 110));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(x + 9, y + 15, x + 21, y + 15);
        g2d.drawLine(x + 15, y + 9, x + 15, y + 21);
    }

    public void draw_minus_btn(Graphics2D g2d) {
        int x = layerX + 25;
        int y = layerY - 55;
        neg_btn_x = x + 15;
        neg_btn_y = y + 15;
        g2d.setColor(btn_normal_color);
        g2d.fillOval(x, y, 30, 30);
        g2d.setColor(new Color(110, 110, 110));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(x + 9, y + 15, x + 21, y + 15);
    }

    public boolean pos_btn_clicked(int mx, int my) {
        if (Math.sqrt(Math.pow(mx - pos_btn_x, 2) + Math.pow(my - pos_btn_y, 2)) < 16.0f) {
            return true;
        }
        return false;
    }

    public boolean neg_btn_clicked(int mx, int my) {
        if (Math.sqrt(Math.pow(mx - neg_btn_x, 2) + Math.pow(my - neg_btn_y, 2)) < 16.0f) {
            return true;
        }
        return false;
    }

}
