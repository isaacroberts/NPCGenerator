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
public class GroupLink extends Link
{
    String group;
    public GroupLink(OptionList opl,String groupname)
    {
        super (opl);
        link=opl;
        group=groupname;
    }
    
    public String getOption(Environment e) {
        return link.getRandomOptionFromGroup(group).getOption(e);
    }
    public String write() {
        return '['+link.getName()+'.'+group+']';
    }
}
