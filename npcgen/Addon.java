
package npcgen;

import java.io.*;
import java.util.*;
import static npcgen.Util.equal;
import npcgen.trait.Blank;
import npcgen.trait.Trait;
        
/**
 *
 * @author Isaac
 */
public class Addon extends SuperList implements Holder 
{
    
    /*--------------Member Variables----------------*/
    protected ArrayList<Item> lists;
    
    protected ArrayList<Float> probs;
    String attribute;
    String path;
    float totalListChance;
    // </Variables
    
    
    public Addon(String setpath)
    {
        lists=new ArrayList<>();
        probs=new ArrayList<>();
        attribute="_";
        name="NewAspect";
        path=setpath;
        totalListChance=0;
    }
    public Addon(String filename,String setpath)
    {
        lists=new ArrayList<>();
        probs=new ArrayList<>();
        path=setpath;
        name=filename;
        attribute=name;
        totalListChance=0;
    }
    public void setAttribute(String set)
    {
        attribute=set;
    }
    public String getAttribute()
    {
        return attribute;
    }
    public void setName(String set)
    {
        name=set;
    }
    public void setPath(String set)
    {
        path=set;
    }
    public String getPath() {
        return path;
    }
    public ArrayList<Item> getList()
    {
        return lists;
    }
    public void remove(Item remove)
    {
        int ix=lists.indexOf(remove);
        if (ix==-1)
            return;
        lists.remove(ix);
        totalListChance-=probs.remove(ix);
    }
    public void add(Item add) 
    {
        lists.add(add);
        probs.add(new Float(1));
        totalListChance++;
    }
    public void addAt(int ix,Item add)
    {
        lists.add(ix,(OptionList)add);
        probs.add(ix,new Float(1));
        totalListChance++;
    }
    public void removeOptionList(String listName)
    {
        for (int n=0;n<lists.size();++n)
        {
            if (equal(lists.get(n).getName(),(listName)))
            {
                lists.remove(n);
                totalListChance-=probs.remove(n);
                return;
            }
        }
    }
    public void remove(int n) {
        lists.remove(n);
        totalListChance-=probs.remove(n);
    }
    public String getOption() 
    {
        return getTrait().getOption();
    }
    public Trait getTrait()
    {
        if (lists.isEmpty())
            return new Blank();
        float roll=totalListChance*(float)Math.random();
        for (int n=0;n<lists.size();++n)
        {
            roll-=probs.get(n);
            if (roll<=0)
            {
                return lists.get(n).getTrait();
            }
        }
        //if the code reaches here then totalOptionChance is wrong
        System.out.println("totalListChance wrong by at least "+roll);
        recalculateTotalOptionChance();
        return lists.get(lists.size()-1).getTrait();
    }
    public void recalculateTotalOptionChance()
    {
        totalListChance=0;
        for (int n=0;n<lists.size();++n)
        {
            totalListChance+=probs.get(n);
        }
    }
    public boolean overwrites(SuperList other)
    {
        return getName().contains(other.getName());
    }
    protected static Pair<OptionList,Float> extractOptionList(String format)
    {
        int colon=format.indexOf(":");
        String name=format.substring(0,colon);
        
        format=format.substring(colon+1);
        colon=format.indexOf(":");
        
        String num=format.substring(0,colon);
        float probability=Float.parseFloat(num);
        
        String filename=format.substring(colon+1);
        
        OptionList list=AllOpLists.oplistMap.get(name);
        
        if (list!=null)
        {
            return new Pair<>(list,probability);
        }
        else
        {
            list=new OptionList(filename,name);
            return new Pair<>(list,probability);
        }
    }
    public void read()
    {
        try {
            
            FileReader fileReader=new FileReader(new File(filePath()));
            BufferedReader reader=new BufferedReader(fileReader);
            
            String line;
            
            line=reader.readLine();
            if (line==null)
            {
                System.out.println("File "+filePath()+" was empty");
                return;
            }
            line=Util.removeLeadingSpaces(line);
            line=Util.removeFormattingChars(line);
            name=line;
            attribute=reader.readLine();
            attribute=attribute.trim();
            if (attribute.isEmpty())
            {
                attribute=name;
            }
            
            while ((line=reader.readLine()) !=null)
            {   //readLine returns null when out of lines
                readLine(line);
            }
            reader.close();
            recalculateTotalOptionChance();
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filePath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private void readLine(String line)
    {
        line=Util.removeLeadingSpaces(line);
                
        if (!line.isEmpty())
        {
            Pair<OptionList,Float> element=extractOptionList(line);
            lists.add(element.getFirst());
            probs.add(element.getSecond());
        }
    }
    public void write()
    {
        try {
            PrintWriter write=new PrintWriter(filePath(),"UTF-8");
            
            
            write.println(name);
            write.println(attribute);
            write.println("");
            
            for (int n=0;n<lists.size();++n)
            {
                if (lists.get(n) instanceof OptionList)
                {
                    write.print("\t"+lists.get(n).getName());
                    write.print(":"+probs.get(n));
                    write.println(":"+((OptionList)lists.get(n)).file);
                }
                else if (lists.get(n) instanceof Trait)
                {
                    write.print("\t\"\""+lists.get(n).toString());
                    write.println(":"+probs.get(n));
                }
            }
            write.close();
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        
    }
    public void dump()
    {
        System.out.println("       Aspect "+name);
        System.out.println("          Att="+attribute);
        for (int n=0;n<lists.size();++n)
        {
        System.out.println("           "+n+" : "+probs.get(n)+" chance:");
            lists.get(n).dump();
        }
    }
    public String filePath() {
        return "Data/Templates/"+path+"/"+name+".txt";
    }
    
}
