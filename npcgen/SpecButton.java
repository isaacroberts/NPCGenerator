
package npcgen;

import java.awt.*;

public class SpecButton extends Button
{
    static Image backArrow;
//    static Image toggleIcon;
    int w,h;
    Color color;
    public SpecButton(int setx,int sety,int setw,int seth, Color setC)
    {
        super(setx,sety,"");
        x=setx;
        w=setw;
        h=seth;
        color=setC;
    }
    
    public void drawOutline(Graphics2D g)
    { 
        g.setColor(new Color(50,50,50));
        g.setStroke(new BasicStroke(1));
        g.drawRect(getX(), getY(), getW(), getH());
    }
    public void draw(Graphics2D g)
    {
        g.setColor(color);
        g.fillRect(getX(), getY(), getW(), getH());
        drawOutline(g);
    }
    public void backDraw(Graphics2D g,boolean emptyStack)
    {
        if (!emptyStack)
            g.setColor(color.brighter());
        else
            g.setColor(color);
            
        
        g.fillRect(getX(), getY(), getW(), getH());
        g.setColor(new Color(50,50,50));
        g.setStroke(new BasicStroke(3));
        g.drawRect(getX(), getY(), getW(), getH());
    }
    public void toggleDraw(Graphics2D g)
    {
        draw(g);
    }
    public void generateDraw(Graphics2D g)
    {
        draw(g);
        g.setColor(Color.RED);
        g.fillOval(getX(),getY(),getW(),getH());
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawOval(getX(),getY(),getW(),getH());
        
        g.setFont(new Font("Copperplate Gothic Bold",Font.BOLD,20));
        g.drawString("Generate!",getX()+4,getY()+69);
    }
    
    public int getX() {
        return x;
    }
    public int getH() {
        return h;
    }
    public int getW() {
        return w;
    }
}
