
package npcgen;

import java.io.File;
import java.util.ArrayList;


public class Data 
{
    static ArrayList<Template> holders;
    
    public static void init()
    {
        holders=new ArrayList<>();
    }
    
    public static NPC genNPC()
    {
        int roll=Util.random(holders.size());
        NPC npc= holders.get(roll).genNPC();
        npc.rollTraits();
        return npc;
    }
    public static void load()
    {
        try {
            //TODO
            //test loading
            //add 'save a copy' capabilities
            //maybe add easy listmaking
            File lists=new File("Data/Lists");
            if (!lists.exists())
                lists.mkdirs();
            else
            {
                File[] listList = lists.listFiles();
                for (File file : listList)
                {
                    if (!file.isDirectory())
                    {
                        if (file.getName().contains(".txt"))
                        {
        //                    System.out.println("     Reading "+file.getName());
                            OptionList oplist=new OptionList(file.getName(),"_");
                            oplist.read();
                            //read puts self into AllOpLists
                        }
                    }
                }
                AllOpLists.link();
            }

            File templates=new File("Data/Templates");
            if (!templates.exists())
                templates.mkdir();
            else
            {
                File[] superCats=templates.listFiles();
                    //superCats assemble! The city is in trouble!
                for (File holder : superCats)
                {
                    if (!holder.isDirectory())
                    {//if file isnt a folder
                        if (holder.getName().contains(".txt"))
                        {//if file is a text file
                            newHolder(holder);
                        }
                    }
                }
                for (Template t : holders)
                {
                    t.fixNameFormats();
                }
            }

            File save=new File("Data/Saved");
            if (!save.exists())
                save.mkdir();
        }
        catch (Exception e)
        {
            
        }
    }
    public static void newHolder(File f)
    {
        holders.add(new Template(f));
    }
    public static void save()
    {
        //make the two folders
        File data=new File("Data");
        data.renameTo(new File("Data_Old"));
        data=new File("Data/Templates");
        data.mkdirs();
        data=new File("Data/Lists");
        data.mkdirs();
        
        for (int c=0;c<holders.size();c++)
        {
            holders.get(c).write();
        }
        
        //write all oplists
        AllOpLists.writeAllOptionLists();
        
        data=new File("Data_Old");
        Util.cleanFolder(data);
        data.delete();
        
        DisplayWindow.clearText();
        DisplayWindow.setTitle("Saved");
    }
}
