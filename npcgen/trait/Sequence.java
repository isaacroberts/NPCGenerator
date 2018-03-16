/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

import java.util.ArrayList;

/**
 *
 * @author isaac
 */
public class Sequence extends Trait
{
    public Trait[] options;
    public Sequence(ArrayList<Trait> setoptions)
    {
        options=new Trait[setoptions.size()];
        options=setoptions.toArray(options);   
    }
    public Sequence(Trait[] setoptions)
    {
        options=setoptions;
    }
    
    public String getOption(Environment e) {
        String ret="";
        for (int n=0;n<options.length;n++)
        {
            ret=ret+options[n].getOption(e);
        }
        return ret;
    }
    public String write() {
        String ret="";
        for (int n=0;n<options.length;n++)
        {
            ret=ret+options[n].write();
        }
        return ret;
    }
}
