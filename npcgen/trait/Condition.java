/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

import java.util.ArrayList;
import npcgen.Util;

/**
 *
 * @author isaac
 */
public class Condition {
    
    public void parseAndSet(String str)
    {
        int equal=indexOfSign(str,'=');
        String[] vals=new String[]{"",""};
        vals[0]=str.substring(1,equal);
        vals[1]=str.substring(equal+1,str.length()-1);
        for (int v=0;v<=1;v++)
        {
            if (vals[v].contains("|"))
            {
                int or=indexOfSign(str,'|');
                while (or!=-1)
                {
                    String op1=vals[v].substring(0,or);
                    (v==0?lval:rval).add(op1);
                    vals[v]=vals[v].substring(or+1);
                    or=indexOfSign(str,'|');
                }
            }
            (v==0?lval:rval).add(vals[v]);
        }
    }
    private int indexOfSign(String str,char sign)
    {
        char tomatch='(';
        int level=1;
        boolean backslash=false;
        for (int n=0;n<str.length();n++)
        {
            char charAtN=str.charAt(n);
            if (backslash)
            {
                backslash=false;
            }
            else if (charAtN==tomatch)
            {
                level++;
            }
            else if (charAtN==Util.matchBrace(tomatch))
            {
                level--;
            }
            else if (level==0 && charAtN==sign)
            {
                return n;
            }
            else if (charAtN =='\\')
                backslash=true;
        }
        return -1;
    }
    public ArrayList<String> lval,rval;
    public Condition(String str)
    {
        parseAndSet(str);
    }
    
    public boolean evaluate()  {
        for (int l=0;l<lval.size();l++)
        {
            for (int r=0;r<rval.size();r++)
            {
                if (lval.get(l).compareTo(rval.get(r))==0)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
