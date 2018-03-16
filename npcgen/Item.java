/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package npcgen;

import npcgen.trait.Trait;

/**
 *
 * @author isaac
 */
public abstract class Item 
{
    public abstract String toString();
    public abstract String getName();
    public abstract void dump();
    public abstract Trait getTrait();
}
