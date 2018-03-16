/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

import npcgen.Util;

/**
 *
 * @author isaac
 */
public class Placeholder extends Trait
{
    public int index;
    public Placeholder(int setIX)
    {
        index=setIX;
    }
    
    public String getOption(Environment e) {
        Util.fail("Placeholder.getOption called");
        return "===";
    }
    public String write() {
        Util.fail("Placeholder.write called");
        return "=_=_=";
    }
}
