/*
 * Written by Isaac Roberts
 */

package npcgen;


/**
 *
 * @author Isaac
 */
public abstract class Bundle extends Choice
{
    public abstract void addToNPC(NPC addTo);
//    public abstract void finishNPC(NPC addTo);
    
    public abstract boolean hasNameFormat();
    public abstract void setNameFormat(String[] set);
    public abstract void fixNameFormats();
}
