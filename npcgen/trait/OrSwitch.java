/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

import npcgen.*;
import java.util.*;
/**
 *
 * @author isaac
 */
public class OrSwitch extends Trait
{
    ArrayList<Trait> options;
    
    public OrSwitch(ArrayList<Trait> setOptions)
    {
        options=setOptions;
    }
    
    public String getOption(Environment e) {
        /*
        int choiceCt=0;
        int literalCt=0;//First the amt of Strings in the or
                    //ie [Animal | "Rock" | "Herb"]
        int literalWeight=1;//the choiceCt that each string
                    //represents. ie: if(string) choiceCt-=literalCt
        for (int n=0;n<options.size();n++)
        {
            if (options.get(n) instanceof Link)
            {//TODO : Generalize to instanceof class that has link
                OptionList opl=((Link)options.get(n)).link;
                choiceCt+=opl.getOptionAmount();
            }
            else
                literalCt++;
        }
        if (literalCt>0)
        {
            if (choiceCt>0)
            {
                literalWeight=(int)Math.ceil(choiceCt/(float)(options.size()-literalCt));
                choiceCt+=literalCt*literalWeight;
            }
            else
            {
                choiceCt=literalCt;
                literalWeight=1;
            }
        }
        choiceCt=Util.random(choiceCt);
        for (int n=0;n<options.size();n++)
        {
            if (options.get(n) instanceof Link)
            {
                OptionList opl=((Link)options.get(n)).link;
                choiceCt-=opl.getOptionAmount();
            }
            else
                choiceCt-=literalWeight;
            if (choiceCt<0)
                return options.get(n).getOption(e);
        }
        Util.fail("OrSwitch.getOption reached end of function with "+choiceCt+" prob left");
        return "?";
        //*/
        int roll=Util.random(options.size());
        return options.get(roll).getOption();
        //*/
    }
    public String write() {
        String ret="{";
        for (int n=0;n<options.size();n++)
        {
            ret=ret+options.get(n).write();
            if (n<options.size()-1)
                ret=ret+"|";
        }
        ret=ret+'}';
        return ret;
    }
}
