
package npcgen;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class DisplayWindow 
{
    static String title="NPC Window";
    
    static ArrayList<String> text;
    
    static NPC npc;
    
    static double scrollPix;
    
    //TODO
    //Make full use of displaywindow throughout:
    //each navigation change
    //on save
    //When adding
    //Display probabilities in Aspect
    //
    
    public static void init()
    {
        scrollPix=0;
        text=new ArrayList<>();
        npc=null;
        text.add("NPCs go home");
    }
    public static void setTitle(String set)
    {
        title=set;
    }
    public static void setNPC(NPC set)
    {
        clearText();
        npc=set;
        title=npc.getName();
        if (title.isEmpty())
            title=npc.getTrait(0);
    }
    public static void clearText() {
        text.clear();
        npc=null;
        scrollPix=0;
    }
    public static void setText(String set)
    {
        clearText();
        text.add(set);
    }
    public static void addText(String add)
    {
        text.add(add);
    }
    public static void addParagraph(String add) 
    {
        //Parses "...\n ..." into separate lines
        int endl=add.indexOf("\n");
        while (!add.isEmpty())
        {
            String line;
            if (endl==-1)
            {
                line=add;
                add="";
            }
            else
            {
                line=add.substring(0,endl);
                add=add.substring(endl+1);
                endl=add.indexOf("\n");
            }
            text.add(line);
        }
    }
    
    public static void addText(ArrayList<String> add)
    {
        text.addAll(add);
    }
    public static void scroll(double amt)
    {
        scrollPix+=amt;
        if (scrollPix<0)
            scrollPix=0;
        else
        {
            int scrollOOB=scrollOOB();
            if (scrollPix >scrollOOB) {
                scrollPix=scrollOOB;
            }
        }
    }
    public static void write()
    {
        if (title.startsWith("[S]"))
            return;
        title=Util.removeTrailingSpaces(title);
        String filepath="Data/Saved/"+title+".txt";
        try {
            File f=new File(filepath);
            int fileCt=2;
            while (f!=null && f.exists())
            {
                filepath="Data/Saved/"+title+"_"+fileCt+".txt";
                f=new File(filepath);
            }
                
            PrintWriter write=new PrintWriter(filepath,"UTF-8");
            
            write.println(title);
            for (int n=0;n<title.length();n++)
                write.print("-");
            write.println("");
            for (int n=0;n<textAmt();n++)
            {
                if (npc==null)
                    write.println(text.get(n));
                else
                {
                    String line = npc.getParentName(n);
                    line+=": "+npc.getTrait(n);
                    write.println(line);
                }
            }

            write.close();
            System.out.println("Wrote "+title+".txt");
            title="[S]"+title;
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filepath);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    final static int top=33,height=347,bottom=top+height;
    final static int left=330,width=600;
    final static int titleSize=25,textSize=15;
    
    public static boolean contains(Point p)
    {
        if (p.getX() < left)
            return false;
        if (p.getY() < top)
            return false;
        if (p.getY() > bottom)
            return false;
        if (p.getX() > left+width)
            return false;
        return true;
    }
    public static int scrollOOB() {
        return (textSize+4)*(text.size()-1);
    }
    public static int textAmt() {
        if (npc==null)
            return text.size();
        else return npc.traitAmt();
    }
    public static void draw(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.fillRect(left,top,width,height);
        g.setColor(new Color(50,50,50));
        g.setStroke(new BasicStroke(2));
        g.drawRect(left,top,width,height);
        
        int minY=top+titleSize+20;
        int y=minY-(int)scrollPix + textSize;
        int n=0;
        g.setFont(new Font("Arial",Font.PLAIN,textSize));
        while (y<= bottom && n < textAmt())
        {
            if (y>=minY)
            {
                if (npc==null)
                    g.drawString(text.get(n), left+30, y);
                else
                {
                    String line = npc.getParentName(n);
                    line+=": "+npc.getTrait(n);
                    if (line.length()>80)
                    {
                        //TODO : do this once instead of every frame
                        ArrayList<String> print=clipLongLine(line);
                        for (int m=0;m<print.size();m++)
                        {
                            g.drawString(print.get(m),left+30,y);
                            y+=textSize+4;
                        }
                        y-=textSize+4;
                    }
                    else
                        g.drawString(line,left+30,y);
                }
            }
            n++;
            y+=textSize+4;
        }
        
        g.setColor(Color.WHITE);
        g.fillRect(left+2,minY-textSize,width-4,textSize);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial",Font.BOLD,titleSize));
        g.drawString(title,left+20,top+titleSize+2);
    }
    public static ArrayList<String> clipLongLine(String line)
    {
        ArrayList<String> clipped=new ArrayList<>();
        String clip=line.substring(0,80);
        int space=clip.lastIndexOf(" ");
        if (space>50)
        {
            line=line.substring(space+1);
            clip=clip.substring(0,space);
        }
        else
            line=line.substring(80);
        clipped.add(clip);
        while (line.length()>0)
        {
            if (line.length()>=60)
            {
                clip="       "+line.substring(0,60);
                space=clip.lastIndexOf(" ");
                if (space>47)
                {
                    line=line.substring(space+1);
                    clip=clip.substring(0,space);
                }
                else
                    line=line.substring(60);
                clipped.add(clip);
            }
            else {
                clipped.add("       "+line);
                return clipped;
            }
        }
        return clipped;
    }
    public static SuperList getListByClick(int py)
    {
        if (npc==null) return null;
        
        int minY=top+titleSize+20;
        int y=minY-(int)scrollPix + textSize;
        int n=0;
        while (y<= bottom && n < text.size())
        {
            if (y>=minY){
                return npc.getParent(n);
            }
            n++;
            y+=textSize+4;
        }
        return null;
    }
    public static NPC getNPC() {
        return npc;
    }
}
