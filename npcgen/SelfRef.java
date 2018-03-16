/*
 * Written by Isaac Roberts
 */

package npcgen;

import java.util.ArrayList;
import npcgen.trait.Blank;
import npcgen.trait.Trait;


public class SelfRef extends Bundle
{
    /*
    Used so a Template can sometimes be a final template
        even when it has sub-templates
    Name is reused as the path of the template it came from
    
    */
    public SelfRef(String path)
    {
        name=path;
    }
    public String getName()
    {
        return "<";
    }
    public void addToNPC(NPC addTo)
    {}
    public void finishNPC(NPC addTo)
    {
        addTo.setPath(name);
    }
    public void add(Item add)
    {}
    public void addAt(int dont,Item bother)
    {}
    public void remove(Item remove)
    {}
    public void remove(int ix)
    {}
    public Trait getTrait() {
        Util.fail("SelfRef.getTrait()??");
        return new Blank();
    }
    public String getOption() {
        Util.fail("SelfRef.getOption()??");
        return "";
    }
    public  ArrayList getList()
    {return null;}
    public void write()
    {}
    public void read()
    {}
    public void dump()
    { System.out.println("<<"); }
    
    public String getPath()
    {return "";}
    public String filePath()
    {return "";}
    public boolean hasNameFormat() {
        return false;
    }
    public void setNameFormat(String[] copy) {}
    public void fixNameFormats() {}
}
