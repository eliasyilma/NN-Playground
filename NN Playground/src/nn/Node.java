// Error reading included file Templates/Classes/Templates/Licenses/license-G:\SimpleML\gpl30.txt
package nn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class Node {

    public float in_value;
    public float activation_value;
    public Link[] input;
    public Link[] output;
    public float bias = 0.3f;
    public String activation_function;
    public String feature_function;
    float[][] activation_heatmap;
    public Point loc;
    public String id;
    public int type;
    //backpropagation derivative values
    public float d_E_d_b;
    public float d_in_d_b;
    public float d_E_d_in;
    public float d_Etotal_d_out;
    public float d_out_d_in;
    public int sizeX, sizeY;
    public DataSet data;
    Util p = new Util();

    

    /**
     * constructor for hidden and output nodes
     *
     * @param activation_function
     * @param type
     */
    public Node(String activation_function, int type) {
        this.activation_function = activation_function;
        this.type = type;
        if (type == 2) {
            sizeX = 400;
            sizeY = 400;

        } else {
            sizeX = 40;
            sizeY = 40;

        }
    }

    /**
     * constructor for input nodes.
     *
     * @param feature_function
     */
    public Node(String feature_function) {
        this.feature_function = feature_function;
        this.type = 0;
        sizeX = 40;
        sizeY = 40;
    }

    public void reset() {
        activation_value = 0;
        d_E_d_b = 0f;
        d_in_d_b = 0f;
        d_E_d_in = 0f;
        d_Etotal_d_out = 0f;
        d_out_d_in = 0f;
        bias = 0.3f;
    }
    
    public void update_derivatives(float learning_rate) {
        d_in_d_b = 1f;
        d_out_d_in = d_activation(in_value);

        if (type == 1) {
            d_Etotal_d_out = 0;
            for (Link l : output) {
                d_Etotal_d_out += l.d_E_d_out;
            }
            d_E_d_in = d_Etotal_d_out * d_out_d_in;
            d_E_d_b = d_E_d_in * d_in_d_b;
            bias += -learning_rate * d_E_d_b / output.length;
        } else {
            d_E_d_in = d_Etotal_d_out * d_out_d_in;
            d_E_d_b = d_E_d_in * d_in_d_b;
            bias += -learning_rate * d_E_d_b;
        }

        for (Link l : input) {
            l.update_derivatives(learning_rate);
        }
    }

    /**
     * compute the node output
     */
    public void update_output() {
        if (type == 1 || type == 2) {
            in_value = 0;
            for (Link input1 : input) {
                in_value += input1.weight * input1.src.activation_value;
            }
            in_value += bias;
            activation_value = activation(in_value);
        }
    }

    public void update_feature(Point loc) {
        activation_value = feature_function_map(loc.x, loc.y, feature_function);
    }

    /**
     * compute the activation value for the given input using this node's selected
     * activation function.
     * @param value the input value
     * @return 
     */
    public float d_activation(float value) {
        return activation_diff_function_map(value, activation_function);
    }

    public float activation(float value) {
        return activation_function_map(value, activation_function);
    }

    public void update_feature_map(int strt, int end, int res) {
        float sp = (end - strt) / (float) res;
        float x1s = strt;
        float x2s = strt;
        activation_heatmap = new float[res][res];
        for (int i = res - 1; i >= 0; i--) {
            for (int j = 0; j < res; j++) {
                activation_heatmap[j][i] = (float) feature_function_map(x1s, x2s, feature_function);
                x1s += sp;
            }
            x1s = strt;
            x2s += sp;
        }
    }

    /**
     * recompute the activation heatmap to be displayed in the GUI.
     * @param strt the minimum interval value
     * @param end the maximum interval value
     * @param res the resolution of the map.
     */
    public void update_activation_map(int strt, int end, int res) {
        float sp = (end - strt) / (float) res;
        activation_heatmap = new float[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                activation_heatmap[i][j] = 0;
                for (Link l : input) {
                    Node in = l.src;
                    activation_heatmap[i][j] += in.activation_heatmap[i][j] * l.weight;
                }
                activation_heatmap[i][j] += bias;
                activation_heatmap[i][j] = activation(activation_heatmap[i][j]);
            }
        }
    }

    public static float normalize(float val) {
        float norm = 0;
        if (val >= 1) {
            norm = 1;
        } else if (val <= -1) {
            norm = -1;
        } else if (val > -1 && val < 1) {
            norm = val;
        }
        return norm;
    }

    public static float discretize(float val) {
        return val < 0 ? -1f : 1f;
    }

    public float feature_function_map(float x1, float x2, String function_name) {
        float f = 0;
        switch (function_name) {
            // case statements
            // values must be of same type of expression
            case "x1":
                f = x1;
                break;
            case "x2":
                f = x2;
                break;
            case "x1x2":
                f = x1 * x2;
                break;
            case "sinx1":
                f = (float) Math.sin(x1);
                break;
            case "sinx2":
                f = (float) Math.sin(x2);
                break;
            case "x1sq":
                f = x1 * x1;
                break;
            case "x2sq":
                f = x2 * x2;
                break;

        }
        return f;
    }

    public float activation_function_map(float value, String activation_f_name) {
        float a = 0;
        switch (activation_f_name) {
            case "Linear":
                a = Linear(value);
                break;
            case "Tanh":
                a = Tanh(value);
                break;
            case "ReLU":
                a = ReLU(value);
                break;
            case "Sigmoid":
                a = Sigmoid(value);
                break;
        }
        return a;
    }

    public float activation_diff_function_map(float value, String activation_f_name) {
        float da = 0;
        switch (activation_f_name) {
            case "Linear":
                da = d_Linear(value);
                break;
            case "Tanh":
                da = d_Tanh(value);
                break;
            case "ReLU":
                da = d_ReLU(value);
                break;
            case "Sigmoid":
                da = d_Sigmoid(value);
                break;
        }
        return da;
    }

    //ACTIVATION FUNCTIONS
    //LINEAR
    public float Linear(float input) {
        return (input);
    }

    //SIGMOID
    public float Sigmoid(float input) {
        return (float) (1f / (1f + Math.exp(-input)));
    }

    public float ReLU(float input) {
        return Math.max(0f, input);
    }

    public float Tanh(float input) {
        return (float) Math.tanh(input);
    }

    public float d_Linear(float input) {
        return 1f;
    }

    public float d_Sigmoid(float input) {
        float out = Sigmoid(input);
        return (float) out * (1f - out);
    }

    public float d_ReLU(float input) {
        return input < 0 ? 0f : 1f;
    }

    public float d_Tanh(float input) {
        float out = (float) Math.tanh(input);
        return (float) (1f - out * out);
    }

    public void draw_neuron(Graphics2D g2d) {
        int x = (int) loc.x;
        int y = (int) loc.y;
        if (type == 0) {
            draw_input_feature_map(g2d, x, y);
            draw_feature_function_text(g2d, x, y);
            draw_neuron_borders(g2d);
        } else if (type == 1) {
            draw_activation_feature_map(g2d, x, y);
            draw_neuron_borders(g2d);
        } else {
            draw_activation_feature_map(g2d, x, y + 3);
            data.draw_dataset(g2d, x, y + 3, sizeX, sizeY);
        }
        if (type != 2) {
            for (Link output1 : output) {
                output1.draw_link(g2d);
            }
        }
    }

    public void draw_input_feature_map(Graphics2D g2d, int x, int y) {

        int h = sizeY;
        int w = sizeX;
        int resX = w / activation_heatmap[0].length;
        int resY = h / activation_heatmap.length;

        for (int j = 0; j < activation_heatmap.length; j++) {
            for (int i = 0; i < activation_heatmap[0].length; i++) {
                float val = discretize(normalize(activation_heatmap[i][j]));
                Color c = p.interpolate_color(p.pos_color, p.neg_color, val);
                g2d.setColor(c);
                g2d.fillRect(x + i * resX, y + j * resY, resX, resY);
            }
        }
        draw_neuron_borders(g2d);
    }

    public void draw_activation_feature_map(Graphics2D g2d, int x, int y) {

        int resX = (int) (sizeX / (float) activation_heatmap[0].length);
        int resY = (int) (sizeY / (float) activation_heatmap.length);

        for (int j = 0; j < activation_heatmap.length; j++) {
            for (int i = 0; i < activation_heatmap[0].length; i++) {
                float val = discretize(normalize(activation_heatmap[j][i]));
                Color c = p.interpolate_color(p.pos_color, p.neg_color, val);
                g2d.setColor(c);
                g2d.fillRect(x + i * resX, y + j * resY, resX, resY);
            }
        }
    }

    public void draw_neuron_borders(Graphics2D g2d) {
        int x = (int) loc.x;
        int y = (int) loc.y;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRect(x - 2, y - 2, sizeX, sizeY);
    }

    private void draw_feature_function_text(Graphics2D g2d, int x, int y) {
        g2d.setFont(p.PRIMARY_FONT);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, 14));
        g2d.drawString(feature_function.toUpperCase(), x - 45, y + 30);
    }

}
