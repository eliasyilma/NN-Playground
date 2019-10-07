package nn;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author user
 */


public class LossChart {
    public ArrayList<Point> loss_history=new ArrayList<>();
    public float max_loss_value=0f;
    public float min_loss_value=0f;
    public float spanX=300;
    public float spanY=50;
    
    public void update_loss_data(float loss_dta){
        //add point to loss_data
        Point current=new Point(loss_history.size(),loss_dta);
        loss_history.add(current);
        //update maximum and minimum loss values        
        if(min_loss_value>=loss_dta)
            min_loss_value=loss_dta;
        
        if(max_loss_value<=loss_dta)
            max_loss_value=loss_dta;
       
            System.out.println("epoch:"+loss_history.size()+" min: "+min_loss_value+" max: "+max_loss_value);
        
        if(max_loss_value<min_loss_value){
            float temp=min_loss_value;
            min_loss_value=max_loss_value;
            max_loss_value=temp;
        }
    }
    
    public Point scale_loss_point(Point p){
        //rescale graph
        //?       --- y value
        //spanY   --- max_loss
        //0       --- min_loss
        float yp=-(min_loss_value+p.y/Math.abs(max_loss_value-min_loss_value))*spanY+spanY;
        //?       ---  x value
        //spanX   --- tn
        //0       --- t0  
        
        float xp=p.x/loss_history.size()*spanX;  
        return new Point(xp,yp);
    }
    
    public void draw_loss_history(Graphics2D g2d,int x,int y){
        g2d.setColor(Color.GRAY);
        g2d.drawRect(x-2, y-2, (int)(spanX+2),(int) (spanY+2));
        g2d.setColor(Color.BLACK);
        for(int i=0;i<loss_history.size()-1;i++){
            Point le=scale_loss_point(loss_history.get(i));
            Point ls=scale_loss_point(loss_history.get(i+1));
            g2d.drawLine((int) ls.x+x, (int) ls.y+y, (int) le.x+x, (int) le.y+y);
            //
        }
        
    }
}
