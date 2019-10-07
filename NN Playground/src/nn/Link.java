// Error reading included file Templates/Classes/Templates/Licenses/license-G:\SimpleML\gpl30.txt
package nn;


import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 *
 * @author user
 */
public class Link {

    Node src; //the source neuron
    Node dst; //the destination neuron
    float weight;
    
    float phase=0; //phase of link bezier line
    
    //backpropagation derivatives
    public float d_E_d_w;
    public float d_E_d_out;
    public float d_in_d_out;
    public float d_in_d_w;
    Util p=new Util();
    
    /**
     * computes loss derivatives in the network and updates the weight of the link.
     * @param learning_rate the learning rate of the network. 
     */
    public void update_derivatives(float learning_rate){
        d_in_d_out=weight;
        d_E_d_out= dst.d_E_d_in*d_in_d_out;
        d_in_d_w=src.activation_value;
        d_E_d_w=dst.d_E_d_in*d_in_d_w;
        weight+=-learning_rate*d_E_d_w;
        
    }
    
 
    /**
     * link constructor
     * @param source the source neuron
     * @param destination the destination neuron
     */
    public Link(Node source,Node destination){
        src=source;
        dst=destination;
        weight=(float) ((Math.random() * (2)) + -1f);
    }

    /**
     * draws a bezier curve between the src and dst nodes.
     * @param g2d 
     */
    public void draw_link(Graphics2D g2d) {
        Point s=new Point(src.loc.x+src.sizeX,src.loc.y+src.sizeY/2);
        Point d=new Point(dst.loc.x,dst.loc.y+dst.sizeY/2);
        draw_curve(g2d, s, d);
    }
    
    /**
     * resets the link's weight and error derivatives
     */
    public void reset(){
    d_E_d_w=0f;
    d_E_d_out=0f;
    d_in_d_out=0f;
    d_in_d_w=0f;
    weight=(float) ((Math.random() * (2)) + -1f);
    phase=0; //phase of link bezier line
    }
    
    
    /**
     * Computes a bezier point using the cubic bezier form: 
     *          p=(1-t)^3*p0+(1-t)^2*t*p1+(1-t)*t^2*p2+t^3*p3
     * @param t parametric curve point ranging b/n 0 and 1.
     * @param p0 start node of the bezier curve.
     * @param p1 start node curve alignment point.
     * @param p2 end node curve alignment point.
     * @param p3 end node of the bezier curve.
     * @return a cubic bezier point
     */
    Point CalculateCubicBezierPoint(float t, Point p0, Point p1, Point p2, Point p3) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;

        Point p = new Point(uuu * p0.x,uuu * p0.y);
        p=new Point(p.x+3 * uu * t * p1.x,p.y+3 * uu * t * p1.y);
        p=new Point(p.x+3 * u * tt * p2.x,p.y+3 * u * tt * p2.y);
        p=new Point(p.x+ ttt * p3.x,p.y+ ttt * p3.y);
        return p;
    }
    
    /**
     * Draws a cubic bezier curve from src to dst
     * @param g2d
     * @param src the start point
     * @param dst the end point
     */
    public void draw_curve(Graphics2D g2d,Point src,Point dst) {
        int SEGMENT_COUNT=50;
        Point p1 = src;
        Point p2 = new Point(src.x+Math.abs(src.x-dst.x)*0.4f,src.y);
        Point p3 = new Point(dst.x-Math.abs(src.x-dst.x)*0.4f,dst.y);
        Point p4 = dst;
        
        int[] x=new int[SEGMENT_COUNT];
        int[] y=new int[SEGMENT_COUNT];
        
        x[0]=(int) p1.x;
        y[0]=(int) p1.y;
        
        for (int i = 1; i < SEGMENT_COUNT-1; i++) {
            float t = i / (float) SEGMENT_COUNT;
            Point bezier_point = CalculateCubicBezierPoint(t, p1, p2, p3, p4);
            x[i]=(int) bezier_point.x;
            y[i]=(int) bezier_point.y;
        }
        x[SEGMENT_COUNT-1]=(int) p4.x;
        y[SEGMENT_COUNT-1]=(int) p4.y;
        phase+=(0.5f);
        g2d.setColor(p.interpolate_color(p.pos_color,p.neg_color,Node.normalize(weight)));
        float stroke_width= (Math.abs(weight)+1)/2*2.0f;
        g2d.setStroke(new BasicStroke(stroke_width, BasicStroke.CAP_BUTT, BasicStroke.CAP_SQUARE, 10f, new float[]{10f,3f}, (float) (phase*100)));
        g2d.drawPolyline(x, y, SEGMENT_COUNT);
        
    }
}
