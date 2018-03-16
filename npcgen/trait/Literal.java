/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen.trait;

/**
 *
 * @author isaac
 */
public class Literal extends Trait
{
    String str;
    public Literal(String set)
    {
        str=set;
    }
    public String getOption() {
        return str;
    }
    public String getOption(Environment e) {
        return str;
    }
    public String write() {
        return str;
    }
    public String toString() {
        return str;
    }
    public String getName() {
        return str;
    }
}
