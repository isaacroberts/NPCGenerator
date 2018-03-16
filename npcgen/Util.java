

package npcgen;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Isaac
 */

public class Util
{
    
    
    public static final boolean equal(String str1,String str2)
    {
        return str1.compareTo(str2)==0;
    }
    public static final boolean isIn(String find,String[] array)
    {
        for (int n=0;n<array.length;n++)
        {
            if (array[n].equals(find))
                return true;
        }
        return false;
    }
    
//    static Random rand=new Random();
//    static double[] lastRolls=new double[4];
    static int curRollIx=0;
    public static int testRoll=26;
    static int[] rollCt=new int[26];
    public static void init() {
    }
    public static int random(int range)
    {
        int r=(int)(Math.random()*range);
//        int r=rand.nextInt(range);
//        double ratio=(((double)r)/((double)range));
//        System.out.printf("roll= %.2f (%4d/%4d) ",ratio, r,range);
//        printBar(40,(int)(40*ratio));
        /*
        lastRolls[curRollIx]=ratio;
        curRollIx=(curRollIx+1)%lastRolls.length;
        double floatingAvg=0;
        for (int n=0;n<lastRolls.length;n++)
        {
            floatingAvg+=lastRolls[n];
        }
        floatingAvg/=lastRolls.length;
        System.out.printf("  FltAvg= %3f  ",floatingAvg);
        printBar(20,(int)(20*floatingAvg));//*/
//        rollCt[(int)(rollCt.length*ratio)]++;
//        if (testRoll==range)
//            rollCt[r]++;
//        System.out.printf("roll= %3d/%3d\n",r,range);
        return r;
    }
    public static int testRollCt=20;
    public static void printRandStats()
    {/*
        for (int n=0;n<rollCt.length;n++)
        {
            System.out.printf("%3d|",rollCt[n]);
        }
        System.out.println();*/
//        int min=rollCt[0];
//        for (int n=1;n<rollCt.length;n++)
//        {
//            if (rollCt[n]<min)
//                min=rollCt[n];
//        }
        int h=30;
        for (int y=0;y<h;y++)
        {
            System.out.print("{");
            double bar=(h-y)*testRollCt;
            for (int x=0;x<rollCt.length;x++)
            {
//                System.out.print((y==10?"-":" "));
                System.out.print((rollCt[x]>=bar?"|\\|":"   "));
            }
            System.out.println("}");
        }
        for (int x=0;x<rollCt.length;x++)
            System.out.print("---");
        System.out.println("--");
        System.out.print(" ");
        for (int x=0;x<rollCt.length;x++)
            System.out.printf("%2d ",x);
        System.out.println();
    }
    public static void printBar(int length,int dot)
    {
        System.out.print("|");
        for (int n=0;n<length;n++)
        {
            if (n==dot)
                System.out.print("O");
            else System.out.print(" ");
        }
        System.out.print("|");
    }
    public static final void printArray(String[] array)
    {
        System.out.print("{");
        for (int n=0;n<array.length-1;n++)
        {
            System.out.println(array[n]+",");
        }
        System.out.println(array[array.length-1]+"}");
    }
    public static String removeLeadingSpaces(String line)
    {
        
        while ( !line.isEmpty() && (
                line.charAt(0)==' '
             || line.charAt(0)=='\t'   
             || line.charAt(0)=='\n')) 
        {
            line=line.substring(1);//remove first character
        }
        return line;
    }
    public static String removeTrailingSpaces(String line)
    {
        //TODO : Investigate. May be causing freezes
        while (!line.isEmpty()) 
        {
            int end=line.length()-1;
            if (line.charAt(end)==' '
             || line.charAt(end)=='\t'   
             || line.charAt(end)=='\n') 
            { 
                line=line.substring(0,end);
            }
            else  
                return line;
        }
        return line;
    }
    public static String removeFormattingChars(String str)
    {
        str=str.replaceAll(";", "");
        str=str.replaceAll(":","");
        
        return str;
    }
    public static String filefy(String str)
    {
        str=str.replace(".","");
        str=Util.removeFormattingChars(str);
        str=str.replace("\t","");
        return str;
    }
    public static ArrayList<String> breakString(String str,char sep)
    {
        ArrayList<String> broken=new ArrayList<>();
        int ix=str.indexOf(sep);
        while (!str.isEmpty())
        {
            String line;
            if (ix==-1)
            {
                line=str;
                str="";
            }
            else
            {
                line=str.substring(0,ix);
                str=str.substring(ix+1);
                ix=str.indexOf("/");
            }
            broken.add(line);
        }
        return broken;
    }
    public static char matchBrace(char other)
    {
        if (other=='{')
            return '}';
        else if (other=='}')
            return '{';
        else if (other=='[')
            return ']';
        else if (other==']')
            return '[';
        else if (other=='(')
            return ')';
        else if (other==')')
            return '(';
        else if (other=='<')
            return '>';
        else if (other=='>')
            return '<';
        else if (other=='"')
            return '"';
        else if (other=='\'')
            return '\'';
        else Util.fail("Util.matchBrace("+other+")");
        return '~';
    }
    public static void cleanFolder(File folder)
    {
        for (File f : folder.listFiles())
        {
            if (f.isDirectory())
                cleanFolder(f);
            f.delete();
        }
    }
    public static final boolean leaveTracers=false;
    public static void leaveTracerFile(String filepath)
    {//makes a tracer file to see exactly where it was looking and
            ///what it was looking for when it got a FileNotFoundException
        System.out.println("File= \""+filepath+"\" not found");
        if (!leaveTracers)
            return;
        System.out.println("    Leaving tracer file");
        
        File trace=new File(filepath);
        try {
            trace.mkdirs();
            trace.createNewFile();
        }
        catch(IOException e2) 
        {
            System.out.println("Couldnt even make the tracer file");
        }
    }
    public static void fail(String reason)
    {
        (new Exception(reason)).printStackTrace();
        System.exit(0);
    }
}
