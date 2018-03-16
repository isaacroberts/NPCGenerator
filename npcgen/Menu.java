/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class Menu 
{
    private Button[] buttons;
    private int scrollOffset;
    private ArrayList<? extends Item> curChoices;
    private int xPos;
    public Menu(int buttonAmt,int x, int y)
    {
        buttons=new Button[buttonAmt];
        xPos=x;
        for (int n=0;n<buttons.length;n++)
        {
            buttons[n]=new Button(x,y," ");
            y+=Button.height+5;
        }
        scrollOffset=0;
        curChoices=new ArrayList<>();
        updateButtons();
    }
    
    public void setCurChoices( ArrayList<? extends Item> set)
    {
        curChoices=set;
        scrollOffset=0;
        updateButtons();
    }
    public void updateButtons()
    {
        int c=scrollOffset;
        int n;
        for (n=0;c<curChoices.size() && n<buttons.length;n++)
        {
            buttons[n].setText(curChoices.get(c).toString());
            c++;
        }
        for (;n<buttons.length;n++)
        {
            buttons[n].setText(" ");
        }
    }
    public int getMenuButton(int px,int py)
    {
        for (int n=0;n<buttons.length;++n)
        {
            if (buttons[n].contains(px,py))
            {
                return n;
            }
        }
        return -1;
    }
    public Item getCurChoice(int menuButtonIx)
    {
        return curChoices.get(menuButtonIx+scrollOffset);
    }
    public void setScrollOffset(int set) {
        scrollOffset=set;
        updateButtons();
    }
    public boolean curChoiceInRange(int menuButtonIx)
    {
        return curChoices.size() > menuButtonIx+scrollOffset;
    }
    public void scroll(int amt)
    {
        scrollOffset+=amt;
        if (scrollOffset <0)
            scrollOffset=0;
        else if (scrollOffset >= curChoices.size())
            scrollOffset = curChoices.size() -1;
        updateButtons();
    }
    public void updateButtonsAfterAdd()
    {
        scrollOffset=curChoices.size()-buttons.length;
        if (scrollOffset<0)
            scrollOffset=0;
        updateButtons();
    }
    public boolean contains(Point p)
    {
        for (int n=0;n<buttons.length;++n)
        {
            if (buttons[n].contains(p.x,p.y))
            {
                return true;
            }
        }
        return false;
    }
    public boolean contains(int x,int y)
    {
        for (int n=0;n<buttons.length;++n)
        {
            if (buttons[n].contains(x,y))
            {
                return true;
            }
        }
        return false;
    }
    public void draw(Graphics2D g)
    {
        //Box around menu buttons
        int y=buttons[0].y-5;
        int height=buttons.length*(Button.height+5)+5;
        g.setColor(new Color(250,250,250));
        g.fillRect(xPos-5,y,Button.width+10,height);
        g.setColor(new Color(50,50,50));
        g.setStroke(new BasicStroke(3));
        g.drawRect(xPos-5,y,Button.width+10,height);
        
        for (int b=0;b<buttons.length;++b)
        {
            buttons[b].draw(g);
        }
    }
    public int getScrollOffset() {
        return scrollOffset;
    }
}
