package nn;

/**
 *
 * @author Elias Yilma
 */
public class Point {

    public float x;
    public float y;

    public Point(float px, float py) {
        x = px;
        y = py;
    }
    
    public Point(){
        x=(float) Math.random()*100;
        y=(float) Math.random()*100;
    }
}
