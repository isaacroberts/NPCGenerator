
package npcgen;

import java.io.*;
import java.util.*;
import static npcgen.Util.equal;
import npcgen.trait.*;

public class Template extends Bundle implements Holder
{
    
    protected ArrayList<Item> elements;
    protected int templateAmt=0;
    protected String[] nameFormat=null;
    boolean inheritedNameFormat=true;
    public Template(String setName,String setpath)
    {
        name=setName;
        elements=new ArrayList<>();
        nameFormat=new String[0];
        templateAmt=0;
        path=setpath;
    }
    public Template(String setName)
    {
        name=setName;
        elements=new ArrayList<>();
        nameFormat=new String[0];
        templateAmt=0;
        path="";
    }
    public Template(String setName, String[] fmt)
    {
        name=setName;
        nameFormat=fmt;
        elements=new ArrayList<>();
        nameFormat=new String[0];
        templateAmt=0;
        path="";
    }
    public Template(File file)
    {
        path="";
        nameFormat=new String[0];
        elements=new ArrayList<>();
        templateAmt=0;
        read(file);
    }
    public NPC genNPC() 
    {
        NPC npc;
        if (templateAmt>0)
        {
            int r= Util.random(templateAmt);
            
            if (elements.get(r) instanceof Template)
                npc=((Template)elements.get(r)).genNPC();
            else //if r is SelfRef
                npc=createNPC();
        }
        else
            npc=createNPC();
        addToNPC(npc);
        return npc;
    }
    private NPC createNPC() 
    {
        NPC npc=new NPC(nameFormat);
        npc.setPath(relativePathWithin());
        return npc;
    }
    public void addToNPC(NPC addTo)
    {
        for (int n=templateAmt;n<elements.size();++n)
        {
            if (elements.get(n) instanceof SuperList)
            {
                if (Util.isIn(((SuperList)elements.get(n)).getAttribute(),nameFormat))
                {//If this trait was intended to be a name
                    //Add it to NPC's name.
                    //If trait attribute is not part of NPC's name,
                    //NPC will ignore it.
                    addTo.setNameTrait((SuperList)elements.get(n));
                }
                else
                    addTo.setTrait((SuperList)elements.get(n));
            }
            else if (elements.get(n) instanceof Trait)
                addTo.setTrait((Trait)elements.get(n));
            else Util.fail("Unrecognized type in template.elements");
        }
    }
    public void fixNameFormats() {
        for (int n=0;n<templateAmt;n++)
        {
            Bundle child=(Bundle)elements.get(n);
            if (!child.hasNameFormat())
            {
                child.setNameFormat(nameFormat);
            }
            child.fixNameFormats();
        }
    }
    public String displayNameFormat() {
        if (nameFormat.length==0)
            return "";
        String fmt=nameFormat[0];
        for (int n=1;n<nameFormat.length;n++)
        {
            fmt=fmt+","+nameFormat[n];
        }
        return fmt;
    }
    public boolean hasNameFormat() {
        return nameFormat.length>0;
    }
    public void copyNameFormat(String[] copy)
    {
        nameFormat=new String[copy.length];
        for (int n=0;n<copy.length;n++)
        {
            nameFormat[n]=copy[n];
        }
    }
    public ArrayList<Item> getList()
    {
        return elements;
    }
    public void add(Item add)
    {
        if (add instanceof Template)
        {
            elements.add(templateAmt,(Choice)add);
            templateAmt++;
        }
        else//if ( add instanceof SuperList)
            elements.add(add);
    }
    public void addAt(int ix,Item add)
    {
        if (add instanceof Template)
        {
            if (ix > templateAmt)
                ix=templateAmt;
            else if (ix==0 && templateAmt>0 && elements.get(0) instanceof SelfRef)
                ix=1;
            elements.add(ix,(Choice)add);
            templateAmt++;
        }
        else//if ( add instanceof SuperList)
        {
            if (ix < templateAmt)
                ix=templateAmt;
            elements.add(ix,(Choice)add);
        }
    }
    public void allowAsComplete()
    {
        if (elements.isEmpty() ||
            !(elements.get(0) instanceof SelfRef) )
        {
            elements.add(0,new SelfRef(relativePathWithin()));
            templateAmt++;
        }
    }
    public void remove(Item remove)
    {
        if (remove instanceof Template)
            templateAmt--;
        elements.remove(remove);
    }
    public void remove(int ix)
    {
        if (ix < templateAmt)
            templateAmt--;
        elements.remove(ix);
    }
    public void dump()
    {
        System.out.println("   Template "+name+" in \".../"+path+"\"");
        
        for (int n=0;n<elements.size();++n)
        {
            elements.get(n).dump();
        }
    }
    public Trait getTrait() {
        Util.fail("Template.getTrait()??");
        return new Blank();
    }
    public String getOption() {
        Util.fail("Template.getOption()??");
        return "";
    }
    protected String path;
    public void setPath(String set) {
        path=set;
    }
    public String getPath() {
        return path;
    }
    public void read()
    {
        read(new File(filePath()));
    }
    public void read(File file) {
        try {
            FileReader fileReader=new FileReader(file);
            BufferedReader reader=new BufferedReader(fileReader);
            
            String line;
            
            name=file.getName().substring(0,file.getName().length()-4);

            line=reader.readLine();
            if (line==null)
            {
                System.out.println("File "+name+" is empty");
                return;
            }
            String nametypes=reader.readLine();
            if (nametypes!=null && !nametypes.isEmpty())
            {
                nametypes=nametypes.trim();
                if (Util.equal(nametypes.substring(0,5),"Name:"))
                {
                    nametypes=nametypes.substring(5).trim();
                    setNameFormat(nametypes);
                }
                else
                {
                    inheritedNameFormat=true;
                    readLine(line);
                }
            }
            
            while ((line=reader.readLine()) !=null)
            {   //readLine returns null when out of lines
                
                line=Util.removeLeadingSpaces(line);
                
                if (!line.isEmpty())
                {
                    readLine(line);
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filePath());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        postRead();
    }
    public void readLine(String line)
    {
        String tag="";
        if (line.length()>=2)
            tag=line.substring(0,2);
        if (equal(tag,"A_"))
        {
            line=line.substring(2);
                elements.add(new Addon(line,relativePathWithin()));
        }
        else if (equal(tag,"T_"))
        {
            line=line.substring(2);
            elements.add(templateAmt,new Template(line,relativePathWithin()));
            templateAmt++;
        }
        else if (equal(tag,"<<"))
        {
            elements.add(0,new SelfRef(relativePathWithin()));
            templateAmt++;
        }
        else if (equal(tag,"\"\""))
        {//if trait
            line=line.substring(2);
            elements.add(Trait.linkTrait(line));
        }
        else //if is option
        {
            OptionList add=AllOpLists.oplistMap.get(line);
            if (add==null)
            {
                add=new OptionList(line);
            }
            elements.add(add);
        }
    }
    public void setNameFormat(String[] fmt)
    {
        nameFormat=fmt;
    }
    public void setNameFormat(String parse)
    {
        if (parse.isEmpty())
        {
            inheritedNameFormat=true;
            return;
        }
        inheritedNameFormat=false;
        int partAmt=1;
        for (int n=0;n<parse.length();n++)
        {
            if (parse.charAt(n)==',')
                partAmt++;
        }
        nameFormat=new String[partAmt];
        int comma=parse.indexOf(",");
        int currentPart=0;
        while (comma!=-1)
        {
            String namePart=parse.substring(0,comma);
            nameFormat[currentPart]=namePart.trim();
            currentPart++;
            parse=parse.substring(comma+1);
            comma=parse.indexOf(",");
        }
        nameFormat[currentPart]=parse.trim();
    }
    public void postRead() 
    {
        for (int n=0;n< elements.size();++n)
        {
            if (!(elements.get(n) instanceof OptionList)
                    && !(elements.get(n) instanceof Trait))
                ((Choice)elements.get(n)).read();
        }
    }
    public void write()
    {
        try {
            File folder=new File(pathWithin());
            folder.mkdir();
            
            PrintWriter write=new PrintWriter(filePath(),"UTF-8");
            
            write.println(name);
            if (!inheritedNameFormat)
            {
                write.print("Name: ");
                for (int n=0;n<nameFormat.length-1;n++)
                    write.print(nameFormat[n]+", ");
                write.println(nameFormat[nameFormat.length-1]);
                write.println("");
            }
            
            
            for (int n=0;n<elements.size();++n)
            {
                write.print("\t");
                if (elements.get(n) instanceof SelfRef)
                    write.println("<<");
                else
                {
                    if (elements.get(n) instanceof Addon)
                        write.print("A_");
                    else if (elements.get(n) instanceof Template)
                        write.print("T_");
                    else if (elements.get(n) instanceof Trait)
                        write.print("\"\"");
                    write.println(elements.get(n).getName());
                }
            }
            write.close();
        }
        catch (FileNotFoundException e) {
            Util.leaveTracerFile(filePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (int n=0;n<elements.size();++n)
        {
            if (!(elements.get(n) instanceof OptionList)
                    && !(elements.get(n) instanceof Trait))
                ((Choice)elements.get(n)).write();
        }
    }
    
    public String filePath()
    {
        return "Data/Templates/"+path+"/"+name+".txt";      
    }
    public String pathWithin()
    {
        if (path.isEmpty())
            return "Data/Templates/"+name;
        else
            return "Data/Templates/"+path+"/"+name;
    }
    public String relativePathWithin()
    {
        if (path.isEmpty())
            return name;
        else
            return path+"/"+name;
    }
}
