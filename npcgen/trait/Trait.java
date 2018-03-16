/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;
import java.util.ArrayList;
import npcgen.*;
/**
 *
 * @author isaac
 */
public abstract class Trait extends Item
{

    public static Trait linkTrait(String str) {
        if (str.isEmpty() || str.startsWith("`")) {
            return new Blank();
        }
        int lbracket = str.indexOf('{');
        if (lbracket == -1) {
            lbracket = str.indexOf('[');
        }
        if (lbracket == -1) {
            return new Literal(str);
        }
        int rbracket = -1;
        int nestCt = 1;
        char match = str.charAt(lbracket);
        char oppMatch = Util.matchBrace(match);
        for (int n = lbracket + 1; n < str.length(); n++) {
            if (str.charAt(n) == match) {
                nestCt++;
            } else if (str.charAt(n) == oppMatch) {
                nestCt--;
                if (nestCt <= 0) {
                    rbracket = n;
                    break;
                }
            }
        }
        if (rbracket == -1) {
            System.out.println("Odd mismatched trait:" + str);
            return new Literal(str);
        }
        if (lbracket > 0 || rbracket < str.length() - 1) {
            String beg = str.substring(0, lbracket);
            String mid = str.substring(lbracket, rbracket + 1);
            String end = str.substring(rbracket + 1);
            ArrayList<Trait> seq = new ArrayList<>();
            if (!beg.isEmpty()) {
                seq.add(linkTrait(beg));
            }
            seq.add(linkTrait(mid));
            if (!end.isEmpty()) {
                seq.add(linkTrait(end));
            }
            for (int n = 0; n < seq.size(); n++) {
                if (seq.get(n) instanceof Sequence) {
                    Sequence sub = (Sequence) seq.get(n);
                    for (int m = sub.options.length - 1; m >= 0; m--) {
                        seq.add(n + 1, sub.options[m]);
                    }
                    seq.remove(n);
                    n--;
                } else if (seq.get(n) instanceof Blank) {
                    seq.remove(n);
                    n--;
                }
            }
            return new Sequence(seq);
        }
        else if (str.charAt(lbracket)=='{')
        {
            str = str.substring(1, rbracket);
            if (str.contains("|")) {
                ArrayList<Trait> options = new ArrayList<>();
                while (!str.isEmpty()) {
                    int orMark = str.indexOf("|");
                    if (orMark == -1) {
                        options.add(linkTrait(str));
                        str = "";
                    } else {
                        String firstChoice = str.substring(0, orMark);
                        firstChoice = firstChoice.trim();
                        str = str.substring(orMark + 1);
                        str = str.trim();
                        options.add(linkTrait(firstChoice));
                    }
                }
                return new OrSwitch(options);
            }
            else if (str.contains("?"))
            {
                int qmark=str.indexOf("?");
                int colon=str.indexOf(":");
                String condition=str.substring(0,qmark);
                String choice1=str.substring(qmark+2,colon-1);
                String choice2=str.substring(colon+2);
                Condition cond=new Condition(condition);
                Trait c1=linkTrait(choice1);
                Trait c2=linkTrait(choice2);
                return new Switch(cond,c1,c2);
            }
            else
            {
                return linkTrait(str);
            }
        }
        else if (str.charAt(lbracket)=='[')
        {
            str=str.substring(1,rbracket);
            if (str.contains("|")) {
                ArrayList<Trait> options = new ArrayList<>();
                while (!str.isEmpty()) {
                    int orMark = str.indexOf("|");
                    if (orMark == -1) {
                        options.add(linkTrait('['+str+']'));
                        str = "";
                    } else {
                        String firstChoice = str.substring(0, orMark);
                        firstChoice = firstChoice.trim();
                        str = str.substring(orMark + 1);
                        str = str.trim();
                        options.add(linkTrait('['+firstChoice+']'));
                    }
                }
                return new OrSwitch(options);
            }
            else if (str.contains(".")) {
                int dot = str.indexOf(".");
                String group = str.substring(dot + 1);
                str = str.substring(0, dot);
                OptionList opl = AllOpLists.oplistMap.get(str);
                if (opl != null) {
                    return new GroupLink(opl, group);
                } else {
                    return new Literal('[' + str + ".," + group + ']');
                }
            } else {
                OptionList opl = AllOpLists.oplistMap.get(str);
                if (opl != null) {
                    return new Link(opl);
                } else {
                    return new Literal('[' + str + ']');
                }
            }   
        }
        return new Literal(str);
    }
    
    public String getOption() {
        return getOption(null);
    }
    public Trait getTrait() {
        return this;
    }
    public abstract String getOption(Environment e);
    public abstract String write();
    public String toString() {
        return write();
    }
    public String getName() {
        return write();
    }
    public void dump() {
        System.out.println("\"\":"+write());
    }
    
}
