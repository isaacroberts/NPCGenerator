
package npcgen;

import java.awt.*;

public class Button 
{
    public static int height;
    int x,y;
    String text;
    
    public Button(int setX,int setY,String setText)
    {
        x=setX;
        y=setY;
        text=setText;
    }
    
    public void setText(String set)
    {
        text=set;
    }
    public String getText() {
        return text;
    }
    public static int width=250;
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getW() {
        return width;
    }
    public int getH() {
        return height;
    }
    public void draw(Graphics2D g)
    {
        g.setColor(new Color(100,100,200));
        g.fillRect(getX(), getY(), getW(), height);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1));
        g.drawRect(getX(), getY(), getW(), height);
        g.setFont(new Font("Arial",Font.PLAIN,23));
        g.drawString(text, getX()+20, getY()+height-10);
    }
    
    public boolean contains(int px,int py)
    {
        if (px<getX())
            return false;
        if (px>getX()+getW())
            return false;
        if (py<getY())
            return false;
        if (py>getY()+getH())
            return false;
        return true;
    }
    public boolean contains(Point p)
    {
        if (p.x<getX())
            return false;
        if (p.x>getX()+getW())
            return false;
        if (p.y<getY())
            return false;
        if (p.y>getY()+getH())
            return false;
        return true;
    }
}
