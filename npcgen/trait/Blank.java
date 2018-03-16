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
public class Blank extends Trait
{
    public Blank()
    {}
    public String getOption(Environment e) {
        return "";
    }
    public String getOption() {
        return "";
    }
    public String write() {
        return "`";
    }
}
