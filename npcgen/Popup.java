
package npcgen;

import java.awt.*;

public class Popup 
{
    enum Type
    {
        Choice(),Template(),OpList(),Window()
        ;
        int height;
        Type() 
        {
            if (this.ordinal()==0)//ew
                init();//ew ew
            height=getPoptions().length*textSize + 3;
        }
        String[] getPoptions() {
            return poptions[ordinal()];
        }
        public static String poptions[][];
        public static void init()
        {
            poptions=new String[4][];
            poptions[0]=new String[]{"Add","Remove","Rename","Copy",
                "Cut","Paste","Open File"};
            poptions[1]=new String[]{"Add Template","Add Aspect","Add List","Add Trait",
                "Remove","Rename","Change Name Format","Allow as Complete","Open File",
                "Copy","Cut","Paste"};
            poptions[2]=new String[]{"Add","Remove","Change","Change List Type",
                "Copy","Cut","Paste","Open File"};
            poptions[3]=new String[]{"Open File"};
        }
    }
    
    int x,y;
    final static int width=120;
    final static int textSize=15;
    Type type;
    boolean visible;
    
    public Popup(int px,int py,Type setType)
    {
        x=px;
        y=py;
        type=setType;
        visible=true;
    }
    public void setVisible(boolean set) {
        visible=set;
    }
    public boolean contains(Point p)
    {
        if (p.x < x)
            return false;
        if (p.y < y)
            return false;
        if (p.x > x+width)
            return false;
        if (p.y > y+type.height)
            return false;
        return true;
    }
    public String poption(int n)
    {
        return type.getPoptions()[n];
    }
    public int poptionAmt() {
        return type.getPoptions().length;
    }
    public String click(Point p)
    {
        int py=y;
        for (int n=0;n<poptionAmt();n++)
        {
            py+=textSize;
            if (p.y < py)
                return poption(n);
        }
        return "_ERROR_";
    }
    public void draw(Graphics2D g)
    {
        if (!visible)
            return;
        g.setColor(new Color(120,120,120));
        g.fillRect(x, y, width, type.height);
        
        int opY=y+textSize-2;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.PLAIN,textSize-3));
        for (int n=0;n<poptionAmt();n++)
        {
            g.drawString(poption(n), x+5, opY);
            opY+=textSize;
        }
        
        g.setColor(new Color(50,50,50));
        g.setStroke(new BasicStroke(2));
        g.drawRect(x,y,width,type.height);
    }
}
