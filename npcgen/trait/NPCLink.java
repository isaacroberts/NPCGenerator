/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

import npcgen.NPC;

/**
 *
 * @author isaac
 */
public class NPCLink extends Trait
{
    String attribute;
    public NPCLink(String att)
    {
        attribute=att;
    }
    
    public String getOption(Environment e) {
        NPC npc=e.getNPC();
        return npc.getTraitByAttribute(attribute);
    }

    public String write() {
        return "NPC "+attribute;
    }
    
}
