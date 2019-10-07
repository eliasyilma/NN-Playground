package nn;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class DataSet {

    public String type;
    public DataPoint[] train;
    public DataPoint[] test;
    public float train_to_test_ratio;
    public int batch_size;
    public float min, max;
    Util p = new Util();

    /**
     * generates 3d data that fits the specified pattern type.
     * @param pattern_type the data pattern that maps x1 and x2 values: XOR, CIRCLE, SPIRAL, H.WAVE, V.WAVE, I.WAVE
     * @param train_to_test_ratio the ratio of training data to testing data
     * @param set_size number of samples for the dataset.
     * @param min the minimum x1 and x2 value.
     * @param max the maximum x1 and x2 values
     */
    public DataSet(String pattern_type, float train_to_test_ratio, int set_size, float min, float max) {
        this.type = pattern_type;
        this.train_to_test_ratio = (train_to_test_ratio < 1 && train_to_test_ratio > 0) ? train_to_test_ratio : 1;
        this.batch_size = set_size;
        this.min = min;
        this.max = max;
        generate();
    }

    public void generate() {
        //dispatch_table
        switch (type) {
            case "Circle":
                circle();
                break;
            case "Spiral":
                spiral();
                break;
            case "XOR":
                xor();
                break;
            case "H.Wave":
                h_wave();
                break;
            case "V.Wave":
                v_wave();
                break;
            case "I.Wave":
                i_wave();
                break;
        }
    }

    private void circle() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = circle_data(train_size);
        test = circle_data(test_size);
    }

    private void xor() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = XOR_data(train_size);
        test = XOR_data(test_size);
    }

    private void spiral() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = spiral_data( train_size);
        test = spiral_data( test_size);
    }
    
    private void h_wave() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = h_data( train_size);
        test = h_data( test_size);
    }
    private void v_wave() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = v_data( train_size);
        test = v_data( test_size);
    }
    private void i_wave() {
        int test_size = (int) (batch_size / (1 + train_to_test_ratio));
        int train_size = (int) (train_to_test_ratio * test_size);
        train = i_data( train_size);
        test = i_data( test_size);
    }    

    private float getCircleLabel(float x, float y, float radius) {
        return euclidean_distance(x, y, 0, 0) < radius * 0.5 ? 1 : -1;
    }

    private float getXORLabel(float x, float y) {
        return x * y > 0 ? 1f : -1f;
    }
    
    private float getGaussianLabel(float x, float y) {
        return x * y > 0 ? 1f : -1f;
    }    

    private float euclidean_distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private DataPoint[] XOR_data(int batch_size) {
        //compute radius from min and max

        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];

        //generate data 
        for (int i = 0; i < batch_size; i++) {
            float x = random(min, max);
            float y = random(min, max);
            data[i] = new DataPoint(new Point(center + x, center + y), getXORLabel(x, y));
        }

        return data;
    }

    private DataPoint[] h_data(int batch_size){
        //compute radius from min and max
        float start_angle = 0 ;
        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];
        
        //generate data 
        for (int i = 0; i < batch_size; i++) {
            float x = random(min, max);
            float y = random(min, max);
            data[i] = new DataPoint(new Point(center + x, center + y), Math.sin(x)<0?1:-1);
        }
        return data;
    }

    private DataPoint[] v_data(int batch_size){
        //compute radius from min and max
        float start_angle = 0 ;
        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];
        
        //generate data 
        for (int i = 0; i < batch_size; i++) {
            float x = random(min, max);
            float y = random(min, max);
            data[i] = new DataPoint(new Point(center + x, center + y), Math.sin(y)<0?1:-1);
        }
        return data;
    }
    
        private DataPoint[] i_data(int batch_size){
        //compute radius from min and max
        float start_angle = 0 ;
        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];
        
        //generate data 
        for (int i = 0; i < batch_size; i++) {
            float x = random(min, max);
            float y = random(min, max);
            data[i] = new DataPoint(new Point(center + x, center + y), Math.sin(x*y)<0?1:-1);
        }
        return data;
    }
    
    private DataPoint[] spiral_data(int batch_size) {
        //compute radius from min and max
        float start_angle = 0 ;
        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];
        int half_size = batch_size / 2;
        //generate spiral data 
        for (int i = 0; i < half_size; i++) {
            float r = i / (float) half_size * 5f;
            float angle = (float) (i / (float) half_size * Math.PI * random(2.3f, 2.5f) + start_angle);
            float x = (float) (Math.sin(angle) * r);
            float y = (float) (Math.cos(angle) * r);
            data[i] = new DataPoint(new Point(center + x, center + y), 1);
        }
        start_angle =(float) Math.PI;
        for (int i = half_size; i < batch_size; i++) {
            float r = (i-half_size) / (float) half_size * 5f;
            float angle = (float) (i / (float) half_size * Math.PI * random(2.3f, 2.5f) + start_angle);
            float x = (float) (Math.sin(angle) * r);
            float y = (float) (Math.cos(angle) * r);
            data[i] = new DataPoint(new Point(center + x, center + y), -1);
        }

        return data;
    }

    private DataPoint[] circle_data(int batch_size) {
        //compute radius from min and max
        float radius = (max - min) / 2;
        float center = (max + min) / 2;
        DataPoint[] data = new DataPoint[batch_size];

        //generate data for inner circle
        for (int i = 0; i < batch_size / 2; i++) {
            float r = random(0, 0.3f) * radius;
            float angle = random(0, (float) (Math.PI * 2f));
            float x = (float) (Math.cos(angle) * r);
            float y = (float) (Math.sin(angle) * r);
            data[i] = new DataPoint(new Point(center + x, center + y), getCircleLabel(x, y, radius));
        }

        //generate data for outer ring
        for (int i = batch_size / 2; i < batch_size; i++) {
            float r = random(0.7f, 1.0f) * radius;
            float angle = random(0, (float) (Math.PI * 2f));
            float x = (float) (Math.cos(angle) * r);
            float y = (float) (Math.sin(angle) * r);
            data[i] = new DataPoint(new Point(center + x, center + y), getCircleLabel(x, y, radius));
        }

        return data;
    }

    public void draw_dataset(Graphics2D g2d, int x, int y, int sizeX, int sizeY) {
        for (int i = 0; i < train.length; i++) {
            DataPoint pt = train[i];
            g2d.setColor(Color.white);
            Point local_p = data_to_img_coordinates(x, y, sizeX, sizeY, train[i]);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawOval((int) (local_p.x - 3), (int) (local_p.y - 3), 6, 6);
            Color inter_c = p.interpolate_color(new Color(32,131,170),new Color(225,118,12) , train[i].label);
            g2d.setColor(inter_c);
            g2d.fillOval((int) (local_p.x - 2), (int) (local_p.y - 2), 5, 5);
        }
    }

    /**
     * converts the data coordinates to GUI coordinates
     * @param x x gui coordinate
     * @param y y gui coordinate
     * @param sizeX the horizontal size of the gui panel
     * @param sizeY the vertical size of the gui panel 
     * @param p the datapoint that is to be mapped from data coordinates to gui coordinates
     * @return the transformed point.
     */
    public Point data_to_img_coordinates(int x, int y, int sizeX, int sizeY, DataPoint p) {
        float half_extent = (max - min) / 2;
        float xp = (p.loc.x);
        float yp = (p.loc.y);
        Point ret = new Point(xp / (half_extent) * (sizeX / 2) + x + sizeX / 2, -yp / (half_extent) * (sizeY / 2) + y + sizeY / 2);
        return ret;
    }

   

    public float random(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }
}
