
package npcgen;

import npcgen.trait.Trait;


public abstract class SuperList extends Choice
{
    public abstract String getOption();
    public abstract Trait getTrait();
    public abstract boolean overwrites(SuperList other);
    public abstract String getAttribute();
    public abstract void setAttribute(String set);
    
    static OptionList blankDummy;
    static {
        blankDummy=new OptionList();
        blankDummy.setAttribute("||_||");
    }
}
