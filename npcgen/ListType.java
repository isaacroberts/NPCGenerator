/*
 * Written by Isaac Roberts
 */

package npcgen;


public enum ListType 
{
    Basic(false),Hidden(false),NoTag(false),
    FirstName(true),MiddleName(true),LastName(true),
    Title(true),Epithet(true),
    FullName(true),
    Conjugator(false),
    Error(false)
    ;
    boolean isName;
    ListType(boolean name)
    {
        isName=name;
    }
    
    static ListType getListType(String line)
    {
        for (ListType type : ListType.values())
        {
            if (line.contains(type.name()))
                return type;
        }
        return Error;
    }
}
