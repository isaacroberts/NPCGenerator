/*
 * Written by Isaac Roberts
 */

package npcgen;

import java.util.*;
import npcgen.trait.*;

/**
 *
 * @author Isaac
 */
public class AllOpLists extends Choice
{
    //First element is name; second is list
    public static ArrayList<OptionList> oplistVector = new ArrayList<>();
    public static Map<String, OptionList> oplistMap = new Hashtable<>();

    static void recordExistenceOf(OptionList list, String name) {
        oplistMap.put(name, list);
        oplistVector.add(list);
    }

    static void writeAllOptionLists() {
        
        for (OptionList oplist : oplistVector) {
            if (oplist.hasContent())//dont write blank lists 
                oplist.write();
        }
    }

    static void updateRecordsFor(OptionList list, String name, String oldname) {
        oplistMap.remove(oldname);
        oplistMap.put(name, list);
        if (!oplistVector.contains(list)) {
            oplistVector.add(list);
        }
    }
    public static void link() {
        for (OptionList oplist : oplistVector) {
            oplist.link();
        }
    }
    
    public String filePath() 
    {
        return "";
    }
    public String getPath() {
        return "";
    }
    

    
    public void add(Item add) 
    {
        if (add instanceof OptionList)
        {  
            OptionList opl=(OptionList)add;
            oplistMap.put(opl.getName(), opl);
            oplistVector.add(opl);
        }
        else Util.fail("Wrong element type "+add.getClass().getName()+" in AllOptionLists");
    }

    
    public void addAt(int index, Item add) 
    {
        Util.fail("I see no reason AllOpLists.addAt(int,Object) should be called");
    }
    public void remove(int index) 
    {
        Util.fail("I see no reason AllOpLists.remove(int) should be called");
    }
    public void remove(Item remove)
    {
        if (remove instanceof OptionList)
        {
            OptionList opl=(OptionList)remove;
            oplistMap.remove(opl.getName(),opl);
            oplistVector.remove(opl);
        }
    }
    public void remove(OptionList remove) 
    {
        oplistMap.remove(remove.getName(),remove);
        oplistVector.remove(remove);
    }
    
    public ArrayList getList() {
        return oplistVector;
    }

    public void write() {
    }

    public void read() {
    }

    public void dump() {
    }
    public String getOption() {
        Util.fail("AllOpLists.getOption()??");
        return "";
    }
    public Trait getTrait() {
        Util.fail("AllOpLists.getTrait()??");
        return new Blank();
    }
    public static AllOpLists all=new AllOpLists();
    //A Choice-extending object for certain usages of AllOpList
    
    private AllOpLists()
    {
        
    }
}
