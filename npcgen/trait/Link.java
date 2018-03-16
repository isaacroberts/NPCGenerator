/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;
import npcgen.*;
/**
 *
 * @author isaac
 */
public class Link extends Trait
{
    OptionList link;
    public Link (OptionList to)
    {
        link=to;
    }
    public OptionList getOptionList() {
        return link;
    }
    public String getOption() 
    {
        return link.getOption();
    }
    public String getOption(Environment e) 
    {
        return link.getOption();
    }
    public String write() {
        return '['+link.getName()+']';
    }
}
