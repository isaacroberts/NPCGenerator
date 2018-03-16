
package npcgen;

import npcgen.trait.*;
import java.io.*;
import java.util.*;

public class OptionList extends SuperList
{
    
    private ArrayList<Trait> traits;
    String file;
    String attribute;
    boolean modified=true;
    private class Group {
        public String name;
        private ArrayList<Trait> members;
        public Group(String setname)
        {
            name=setname;
            members=new ArrayList<>();
        }
        public void addMember(Trait add) {
            members.add(add);
        }
        public Trait getMember(int n) {
            return members.get(n);
        }
        public Trait getRandMember() {
            return members.get(Util.random(members.size()));
        }
    }
    private ArrayList<Group> groups;
    
    public OptionList(String setname)
    {
        traits=new ArrayList<>();
        name=setname;
        file=Util.filefy(name);
        attribute="_";
        groups=new ArrayList<>();
        AllOpLists.recordExistenceOf(this,name);
    }
    public OptionList(String filename,String setname)
    {
        traits=new ArrayList<>();
        file=filename.replace(".txt","");
        name=setname;
        attribute=name;
        groups=new ArrayList<>();
        AllOpLists.recordExistenceOf(this,name);
    }
    public OptionList() {
        //Blank Dummy Optionlist
        traits=new ArrayList<>();
        name="    ";
        file="|_|";
        attribute="_|_";
        groups=new ArrayList<>();
        modified=false;
    }
    
    
    public void link() {
        for (int n=0;n<traits.size();n++)
        {
//            level=0;
            traits.set(n,Trait.linkTrait(traits.get(n).write()));
        }
        for (int n=0;n<groups.size();n++)
        {
            for (int g=0;g<groups.get(n).members.size();g++)
            {
                Trait t=groups.get(n).members.get(0);
                if (!(t instanceof Placeholder))
                {
                    Util.fail("Upon OptionList.Link, group member was non-Placeholder");
                    return;
                }
                int ix=((Placeholder)t).index;
                groups.get(n).members.add(traits.get(ix));
                groups.get(n).members.remove(0);
            }
        }
    }
    
    public void setAttribute(String set) {
        attribute=set;
        modified=true;
    }
    public String getAttribute() {
        return attribute;
    }
    public void setName(String set)
    {
        if (!Util.equal(name, set))
        {
            AllOpLists.updateRecordsFor(this, set, name);
            name=set;
            file=Util.filefy(name);
        }
        modified=true;
    }
    public void read(String fileName)
    {
        file=fileName;
        read();
    }
    
    public void remove(Item remove)
    {
        traits.remove(remove);
        for (int g=0;g<groups.size();g++)
        {
            if (groups.get(g).members.isEmpty())
            {
                groups.remove(g);
                break;
            }
        }
        modified=true;
    }
    public void remove(int ix)
    {
        traits.remove(ix);
        for (int g=0;g<groups.size();g++)
        {
            if (groups.get(g).members.isEmpty())
            {
                groups.remove(g);
                break;
            }
        }
        modified=true;
    }
    public String getOption(int ix)
    {
        return traits.get(ix).getOption();
    }
    public Trait getTrait(int ix)
    {
        return traits.get(ix);
    }
    public void add(String add)
    {
        traits.add(Trait.linkTrait(add));
        modified=true;
    }
    public void addAt(int ix,String add)
    {
        traits.add(ix,Trait.linkTrait(add));
        modified=true;
    }
    public void add(Item add)
    {
        if ( add instanceof Trait)
            traits.add((Trait)add);
        else
            Util.fail("Wrong element type");
        modified=true;
    }
    public void addAt(int ix,Item add)
    {
        if ( add instanceof Trait)
            traits.add(ix,(Trait)add);
        else
            Util.fail("Wrong element type");
        modified=true;
    }
    public void addOption(Trait add) {
        traits.add(add);
        modified=true;
    }
    public String getOption()
    {
        if (traits.isEmpty())
            return "";
        return traits.get(Util.random(traits.size())).getOption();
    }
    public Trait getTrait()
    {
        if (traits.isEmpty())
            return new Blank();
        return traits.get(Util.random(traits.size()));
    }
    public Trait getRandomOptionFromGroup(String groupname)
    {
        for (int n=0;n<groups.size();n++)
        {
            if (Util.equal(groupname, groups.get(n).name))
            {
                return groups.get(n).getRandMember();
            }
        }
        return new Literal(name+"."+groupname);
    }
    public int getOptionAmount() {
        return traits.size();
    }
    public ArrayList<Trait> getList_const()
    {
        return traits;
    }
    public ArrayList<Trait> getList() {
        modified=true;
        return traits;
    }
    public String getPath() {
        return "";
    }
    public boolean overwrites(SuperList other)
    {
        return Util.equal(attribute,other.getAttribute());
    }
    public Group getGroup(String groupname)
    {
        for (int n=0;n<groups.size();n++)
        {
            if (Util.equal(groupname, groups.get(n).name))
            {
                return groups.get(n);
            }
        }
        return null;
    }
    public void read()
    {
        /*
        This function should ONLY be called by Menu
        Otherwise some OptionLists will be doubled each time the programs run
        
        */
        if (!traits.isEmpty())
            Util.fail("Called OptionList.read() a second time");
        try {
            
            FileReader fileReader=new FileReader(new File(filePath(file)));
            BufferedReader reader=new BufferedReader(fileReader);
            
            String line;
            
            line=reader.readLine();
            if (line==null)
            {
                System.out.println("File "+file+" was empty");
                return;
            }
            line=Util.removeFormattingChars(line);
            
            setName(line);
            
            
            attribute=reader.readLine();
            attribute=attribute.trim();
            if (attribute.isEmpty())
            {
                System.out.println("Attribute not found in "+name);
                attribute=name;
            }
            
            ArrayList<Group> openGroups=new ArrayList<>();
            while ((line=reader.readLine()) !=null)
            {   //readLine returns null when out of lines
                line=Util.removeLeadingSpaces(line);
                if (!line.isEmpty())
                {
                    if (line.startsWith("==") || line.startsWith("#<") || line.startsWith("<#"))
                    {
                        line=line.substring(2).trim();
                        Group g=new Group(line);
                        groups.add(g);
                        if (line.startsWith("=="))
                            openGroups.clear();
                        openGroups.add(g);
                    }
                    else if (line.startsWith(">#") || line.startsWith("#>"))
                    {
                        line=line.substring(2).trim();
                        if (line.isEmpty())
                        {
                            openGroups.clear();
                        }
                        else
                        {
                            Group g=getGroup(line);
                            if (g!=null)
                                openGroups.remove(g);
                            else Util.fail("Invalid Group Name on ># Ender:"+line);
                        }
                    }
                    else
                    {
                        int ix=traits.size();
                        if (line.contains("#"))
                        {
                            int hash=line.lastIndexOf("#");
                            String tags=line.substring(hash+1);
                            line=line.substring(0,hash);
                            int comma;
                            while ((comma=tags.indexOf(","))!=-1)
                            {
                                String frontTag=tags.substring(0,comma).trim();
                                tags=tags.substring(comma+1).trim();
                                Group g=getGroup(frontTag);
                                if (g==null)
                                {
                                    g=new Group(frontTag);
                                    groups.add(g);
                                }
                                g.addMember(new Placeholder(ix));
                            }
                            
                            Group g=getGroup(tags);
                            if (g==null)
                            {
                                g=new Group(tags);
                                groups.add(g);
                            }
                            g.addMember(new Placeholder(ix));
                            line=line.trim();
                        }
                        //add new trait to all open groups
                        traits.add(new Literal(line));
                        for (int g=0;g<openGroups.size();g++)
                        {
                            openGroups.get(g).addMember(new Placeholder(ix));
                        }
                    }
                }
                
            }
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filePath(file));
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    static class GroupWrite {
        int line;
        String group;
        enum GWType {
            Open,Close,Tag
        };
        GWType type;
        GroupWrite(int setL,String setG,GWType setT)
        {
            line=setL;
            group=setG;
            type=setT;
        }
        public String toString() {
            return "L="+line+" Name="+group+" T="+type.name().substring(0,1);
        }
    }
    private ArrayList<GroupWrite> createGroupWriteList() 
    {
        ArrayList<String>[] traitGroups=new ArrayList[traits.size()];
        
        for (int n=0;n<traitGroups.length;n++)
            traitGroups[n]=new ArrayList<>();
        
        //index groups by Trait, ie the order theyll be written in
        for (int g=0;g<groups.size();g++)
        {
            for (int m=0;m<groups.get(g).members.size();m++)
            {
                Trait t=groups.get(g).members.get(m);
                int ix=traits.indexOf(t);
                traitGroups[ix].add(groups.get(g).name);
            }
        }
        
        ArrayList<GroupWrite> writes=new ArrayList<>();
        ArrayList<GroupWrite> closes=new ArrayList<>();
        final int MAX_TAG_CHAIN_LENGTH=3;
        //go through each trait
        for (int t=0;t<traitGroups.length;t++)
        {
            //and for each group its in
            for (int n=0;n<traitGroups[t].size();n++)
            {
                String g=traitGroups[t].get(n);
                if (!g.isEmpty())
                {
                    //find how many after it are also in that group (=chainLength)
                    int chainLength=-1;
                    //iterate over the chain of following traits
                    for (int chain=t+1;chain<traitGroups.length;chain++)
                    {
                        int match=traitGroups[chain].indexOf(g);
                        //until one does not contain my group
                        if (match==-1)
                        {
                            chainLength=chain-t;
                            //remove this group so we dont repeat this
                            if (chainLength > MAX_TAG_CHAIN_LENGTH)
                            {
                                //add a group open and group close to the write list
                                writes.add(new GroupWrite(t,g,GroupWrite.GWType.Open));
                                closes.add(new GroupWrite(chain,g,GroupWrite.GWType.Close));
                            }
                            break;
                        }
                        else
                            traitGroups[chain].set(match,"");

                    }//for chain of groups
                    if (chainLength==-1)
                    {
                        chainLength=traitGroups.length-t;
                        if (chainLength > MAX_TAG_CHAIN_LENGTH)
                        {
                            //add a group open and group close to the write list
                            writes.add(new GroupWrite(t,g,GroupWrite.GWType.Open));
                            closes.add(new GroupWrite(traitGroups.length,g,GroupWrite.GWType.Close));
                        }
                    }
                    //if chain length is too short to be a set
                    if (chainLength<= MAX_TAG_CHAIN_LENGTH)
                    {
                        //go through each following group member
                        for (int chain=t;chain<t+chainLength;chain++)
                        {
                            //and write that group
                            writes.add(new GroupWrite(chain,g,GroupWrite.GWType.Tag));
                        }
                    }
                }//if g isnt empty
            }//for n : traitGroups[t]
        }//for t : traitGroups
        //sort closes into writes
        for (int n=0;n<closes.size();n++)
        {
            //simple selection sort
            int l=closes.get(n).line;
            boolean added=false;
            for (int w=0;!added && w<writes.size();w++)
            {
                if (writes.get(w).line>=l)
                {
                    writes.add(w,closes.get(n));
                    added=true;
                }
            }
            if (!added)
                writes.add(closes.get(n));
        }
        int lineOffset=0;
        int level=0;
        int prevBlankClose=-2;
        for (int n=0;n<writes.size();n++)
        {
            writes.get(n).line+=lineOffset;
            if (writes.get(n).type==GroupWrite.GWType.Open)
            {
                lineOffset++;
                level++;
            }
            else if (writes.get(n).type==GroupWrite.GWType.Close)
            {
                level--;
                
                if (level==0)
                {
                    writes.get(n).group="";
                    if (prevBlankClose>=n-1)
                    {
                        writes.remove(n);
                        n--;
                    }
                    else 
                        lineOffset++;
                    prevBlankClose=n;
                }
                else//*/
                    lineOffset++;
            }
        }
        return writes;
    }
    public void write()
    {
        if (modified==false)
            return;//Don't bother writing something that hasnt changed
        if (file.compareTo("_")==0)
        //dummy OptionList has file="_"
            return;//dont write the dummy list
        try {
            PrintWriter write=new PrintWriter(filePath(file),"UTF-8");
            
            write.println(name);
            write.println(attribute);
            write.println("");
            
            if (groups.isEmpty())
            {
                for (int n=0;n<traits.size();++n)
                {
                    write.println("\t"+traits.get(n).write());
                }
                write.close();
            }
            else
            {
                writeBodyWithGroups(write);
            }
        }
        catch (FileNotFoundException e)
        {
            Util.leaveTracerFile(filePath(file));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }
    public void writeBodyWithGroups(PrintWriter write)
    {
        ArrayList<GroupWrite> groupWrites=createGroupWriteList();
        int g=0;//groupWrite current index
        int currentLine=0;
        for (int n=0;n<traits.size();++n)
        {
            String tags="";
            while (g<groupWrites.size() && groupWrites.get(g).line<=currentLine)
            {
                if (groupWrites.get(g).type==GroupWrite.GWType.Tag)
                {
                    tags=tags+", "+groupWrites.get(g).group;
                }
                else if (groupWrites.get(g).type==GroupWrite.GWType.Open)
                {
                    write.println("#<"+groupWrites.get(g).group);
                    currentLine++;
                }
                else if (groupWrites.get(g).type==GroupWrite.GWType.Close)
                {
                    write.println(">#"+groupWrites.get(g).group);
                    currentLine++;
                }
                g++;
            }
            if (!tags.isEmpty())
            {
                tags=tags.substring(2);//remove ", "
                write.println("\t"+traits.get(n).write()+" #"+tags);
            }
            else
                write.println("\t"+traits.get(n).write());
            currentLine++;
        }
        while (g<groupWrites.size())
        {
            if (groupWrites.get(g).type==GroupWrite.GWType.Close)
            {
                write.println(">#"+groupWrites.get(g).group);
            }
            else
                System.out.println("Ending with groupWrite"+groupWrites.get(g).toString());
            g++;
        }
        write.close();
    }
    public void dump()
    {
        System.out.println("           Name= "+name);
        System.out.println("           Filename= "+file);
        
        for (int n=0;n<traits.size();++n)
        {
        System.out.println("               "+n+": "+traits.get(n));
        }
    }
    public boolean hasContent()
    {
        return !traits.isEmpty();
    }
    public String filePath()
    {
        return filePath(file);
    }
    public static String filePath(String name) {
        return "Data/Lists/"+name+".txt";
    }
}
