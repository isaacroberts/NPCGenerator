
package npcgen;

import java.util.*;
import static npcgen.Util.equal;
import npcgen.trait.*;

public class NPC 
{
    
    private ArrayList<Aspect> traits;
    class Aspect {
        SuperList parent;
        Trait blueprint;
        String result;
        public Aspect(SuperList setP) {
            parent=setP;
            blueprint=null;
            result=null;
        }
        public Aspect(SuperList setP,Trait setT,String setS)
        {
            parent=setP;
            blueprint=setT;
            result=setS;
        }
        public boolean hasParent() {
            return parent!=null;
        }
        public void rollTrait() {
            if (result!=null || blueprint!=null)
                return;
            if (parent!=null && parent!=OptionList.blankDummy)
            {
                blueprint=parent.getTrait();
            }
            else if (blueprint==null)
                blueprint=new Blank();
        }
        public void rollString(Environment e) {
            if (result==null)
                result=blueprint.getOption(e);
        }
        public void rollStringSafely()
        {//If result is rolled with environment, you risk loops of requests
        //TODO: change this to a safe roll that only meekly requests NPC data
            if (result==null)
                result=blueprint.getOption();
        }
    }
    Name name;
    //TODO 
    //Improve NPC displaying
    //add "reroll aspect"
    //add male/female capabilites?
    
    public NPC(String[] nameFormat) 
    {
        traits=new ArrayList<>();
        name=new Name(nameFormat);
    }
    public void setPath(String path)
    {
        parsePath(path);
    }
    public String getTraitByAttribute(String attribute)
    {
        for (int n=0;n<traits.size();n++)
        {
            if (traits.get(n).hasParent() &&
                    Util.equal(attribute,traits.get(n).parent.getAttribute()))
            {
                if (traits.get(n).result==null)
                    traits.get(n).rollStringSafely();
                return traits.get(n).result;
            }
        }
        return "None";
    }
    public void setTrait(SuperList parent)
    {
        if (name.containsType(parent.getAttribute()))
        {
            addNameTrait(parent);
        }
        else
        {
            for (int n=0;n<traits.size();n++)
            {
                if (traits.get(n).hasParent() && parent.overwrites(traits.get(n).parent))
                {
                    traits.set(n,new Aspect(parent));
                    return;
                }
            }
            traits.add(new Aspect(parent));
        }
    }
    public void setTrait(Trait set)
    {
        //if name figure it out
        traits.add(new Aspect(null, set, null));
    }
    private void addNameTrait(SuperList parent)
    {
        Trait trait=parent.getTrait();
        name.setPart(trait,parent.getAttribute());
    }
    public void setNameTrait(SuperList parent)
    {
        if (name.containsType(parent.getAttribute()))
        {
            addNameTrait(parent);
        }
    }
    public void rollTraits()
    {
        for (Aspect trait : traits) {
            trait.rollTrait();
        }
        for (int r=0;r<traits.size();r++)
        {
            if (traits.get(0).blueprint==null)
            {
                Aspect a=traits.remove(0);
                traits.add(a);
            }
            else break;
        }
        Environment env=new Environment(this);
        for (Aspect trait : traits) {
            trait.rollString(env);
        }
        name.rollName();
    }
    public void displayInWindow()
    {
        DisplayWindow.setNPC(this);
    }
    public String getDisplayLine(int ix)
    {
        if (!traits.get(ix).hasParent() || traits.get(ix).parent.getAttribute().isEmpty())
        {
            return traits.get(ix).result;
        }
        else
        {
            return traits.get(ix).parent.getAttribute()+": "+traits.get(ix).result;
        }
    }
    public String getName() {
        return name.toString();
    }
    public int traitAmt() {
        return traits.size();
    }
    public String getTrait(int ix)
    {
        return traits.get(ix).result;
    }
    public String getParentName(int ix)
    {
        if (traits.get(ix).hasParent())
            return traits.get(ix).parent.getName();
        else
            return "";
    }
    public SuperList getParent(int ix) {
        return traits.get(ix).parent;
    }
    
    protected void parsePath(String path) 
    {
        //Parses Filepath into character traits
        int slash=path.indexOf("/");
        while (!path.isEmpty())
        {
            String line;
            if (slash==-1)
            {
                line=path;
                path="";
            }
            else
            {
                line=path.substring(0,slash);
                path=path.substring(slash+1);
                slash=path.indexOf("/");
            }
            traits.add(new Aspect(OptionList.blankDummy,null,line));
        }
    }
    static class Name
    {
        public String[] nameResult;
        public Trait[] nameTrait;
        public String[] nametype;
        
        public Name(String[] nameBlueprint)
        {
            nametype=nameBlueprint;
            nameResult=new String[nameBlueprint.length];
            nameTrait=new Trait[nameBlueprint.length];
        }
        public void clearName() {
            for (int n=0;n<nameResult.length;n++)
            {
                nameTrait[n]=new Blank();
                nameResult[n]="";
            }
        }
        public boolean containsType(String type)
        {
            for (int n=0;n<nametype.length;n++)
            {
                if (equal(nametype[n],type))
                    return true;
            }
            return false;
        }
        public String toString()
        {
            String full="";
            for (String part : nameResult)
            {
                if (part!=null)
                    full+=part+" ";
            }
            return full;
        }
        public void setPart(Trait set,String attribute)
        {
            for (int n=0;n<nametype.length;n++)
            {
                if (equal(nametype[n],attribute))
                {
                    nameTrait[n]=set;
                }
            }
        }
        public void rollName()
        {
            for (int n=0;n<nameTrait.length;n++)
            {//TODO: add environment
                if (nameTrait[n]!=null)
                {
                    String value=nameTrait[n].getOption();
                    //capitalize name
                    if (!value.isEmpty())
                    {
                        value=Character.toUpperCase(value.charAt(0))+value.substring(1);
                        nameResult[n]=value;
                    }
                }
            }
        }
    }
}
