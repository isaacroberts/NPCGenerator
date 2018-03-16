
package npcgen;

import java.util.ArrayList;

/**
 *
 * @author Isaac
 */
public abstract class Choice extends Item
{
    protected String name="";
    public String getName()
    {
        return name;
    }
    public void setName(String set){
        name=set;
    }
    public abstract String filePath();
    public String toString() {
        return getName();
    }
    public void allowAsComplete(){}
    
    public abstract void add(Item add);
    public abstract void addAt(int index,Item add);
    public abstract void remove(Item remove);
    public abstract void remove(int index);
    public abstract ArrayList getList();
    public ArrayList getList_const() {return getList();}
    abstract void write();
    abstract void read();
    
    public abstract String getPath();
    public String relativePathWithin() {
        return "";
    }
    
}
