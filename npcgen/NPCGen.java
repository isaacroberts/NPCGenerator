
package npcgen;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.Desktop;
import javax.imageio.ImageIO;
import static npcgen.Util.equal;
import npcgen.trait.Trait;

public class NPCGen extends JFrame implements Runnable, KeyListener, 
        MouseListener, MouseWheelListener
{
    //Application Vars
    Thread t = new Thread(this);
    Container con = getContentPane();
    static ImageIcon d20Icon;
    
    //GUI Vars
    Popup popup;
    ArrayList<Menu> templateMenus;
    Menu oplistMenu;
//    SpecButton tmback;
    SpecButton opback,generate,saveButton;
    
    //Internal Vars
    boolean running=true;
    ArrayList<Choice> folderPath;
    OptionList curOplist=null;
    Item clipboard=null;
    
/*-------------Setup--------------*/
    public NPCGen()
    {
//        System.out.println("Running");
        
        con.setBackground(Color.BLUE);
        Util.init();
        folderPath=new ArrayList<>();
        popup=null;
        try {
            Image image = ImageIO.read(new File("d20Icon.png"));
            d20Icon=new ImageIcon(image.getScaledInstance(50,50,0)
                    ,"Some sort of dodecahedron");
        }
         catch (IOException e){}
        
        con.setVisible(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseWheelListener(this); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        DisplayWindow.init();
        Data.init();
        Data.load();
        
        setupButtons();
        oplistMenu.setCurChoices(AllOpLists.oplistVector);
        
        t.start();
    }
    private void setupButtons()
    {
        SpecButton.backArrow=Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("Arrow.png"));
        SpecButton.backArrow=SpecButton.backArrow.
                getScaledInstance(30, 30, 0);
        
        templateMenus=new ArrayList<Menu>();
        Button.height=40;
        addTopMenu();
        oplistMenu=new Menu(8,45,310);
//        tmback=new SpecButton(5,105,38,38,new Color(10,100,10));
        opback=new SpecButton(15,310,25,355,new Color(10,100,10));
        generate=new SpecButton(500,440,125,125,new Color(100,100,100));
        generate.text="Generate!";
        saveButton=new SpecButton(340,335,30,30,new Color(100,100,100));
    }
/*----------Application Things------*/
    public void run()
    {
        try
        {
            while(running)
            {
                t.sleep(100);
                
                repaint();
            }
        }
        catch(Exception e){}
    }
    public void update(Graphics g)
    {
        paint(g);
    } 
    public void paint(Graphics gr)
    {
        Image i=createImage(getSize().width, getSize().height);
        Graphics2D g = (Graphics2D)i.getGraphics();
        //Background
        g.setColor(new Color(200,100,100));
        g.fillRect(0,0,getSize().width,getSize().height);
        
        //Menu Buttons
        for (int n=templateMenus.size()-1;n>=0;n--)
            templateMenus.get(n).draw(g);
            
        oplistMenu.draw(g);
                
        g.setFont(new Font("Arial",Font.PLAIN,25));
        g.setColor(Color.BLACK);
        
        if (!atTopLevel())
        {//Template Menu Title
            g.drawString(enclosing().getName(),80,50);
        }
        
        DisplayWindow.draw(g);
//        tmback.backDraw(g, atTopLevel());
        opback.backDraw(g,curOplist==null);
        generate.generateDraw(g);
        saveButton.draw(g);
        if (popup!=null)
            popup.draw(g);
        
        g.dispose();
        gr.drawImage(i, 0, 0, this);
    }
    public void exit()
    {
//        running=false;
        System.exit(0);
    }
    
/*--------First Level Input----------------*/

    public void keyPressed(KeyEvent k)
    {
        int code=k.getKeyCode();
        if (code==KeyEvent.VK_ESCAPE)
        {
            exit();
        }
        else if (code==KeyEvent.VK_D)
        {
            for (int n=0;n<Data.holders.size();++n)
            {
                Data.holders.get(n).dump();
            }
        }
        else if (code==KeyEvent.VK_SPACE)
        {
//            for (int n=0;n<Util.testRollCt*Util.testRoll;n++)
//            {
//                Util.random(Util.testRoll);
//            }
//            Util.printRandStats();
            genNPC();
        }
        else if (code==KeyEvent.VK_BACK_SPACE)
        {
            backOneFolder();
        }
        else if (code==KeyEvent.VK_S)
        {
            Data.save();
        }
        else if (code==KeyEvent.VK_A)
        {
            if (atTopLevel())
            {
                addTemplate();
            }
            else if (enclosing() instanceof Template)
            {
                String[] options={"Trait","List","Aspect","Template"};
                int answer=JOptionPane.showOptionDialog(this,"Add what?"
                    ,"Query",JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,d20Icon,options,options[0]);
                if (answer==0)
                {
                    String str=showDialog("Enter Trait");
                    Trait trait=Trait.linkTrait(str);
                    enclosing().add(trait);
                    topMenu().updateButtonsAfterAdd();
                }
                else if (answer==1)
                {
                    addOplist(enclosing());
                }
                else if (answer==2)
                {
                    String name=showDialog("Enter Aspect name");
                    Addon asp=new Addon(name,"");
                    asp.setPath(enclosing().relativePathWithin());
                    enclosing().add(asp);
                    topMenu().updateButtonsAfterAdd();
                    addTopMenu();
                }
                else if (answer==3)
                {
                    addTemplate();
                }
            }
            else
            {
                addToChoice(enclosing());
                topMenu().updateButtonsAfterAdd();
                addTopMenu();
            }
        }
    }
    public void mousePressed(MouseEvent mo)
    {
        if (mo.getButton()==MouseEvent.BUTTON1)
        {
            if (popup!=null)
            {
                if (popup.contains(mo.getPoint()))
                {
                    String action=popup.click(mo.getPoint());
                    executePopup(action,topMenu().contains(popup.x,popup.y));
                }
                else popup=null;
            }
//            else if (tmback.contains(mo.getX(),mo.getY()))
//            {
//                backOneFolder(); 
//            }
            else if (opback.contains(mo.getX(),mo.getY()))
            {
                exitOplist();
            }
            else if (generate.contains(mo.getX(),mo.getY()))
            {
                genNPC();
            }
            else if (saveButton.contains(mo.getX(),mo.getY()))
            {
                DisplayWindow.write();
            }
            else
            {
                int button=oplistMenu.getMenuButton(mo.getX(),mo.getY());

                if (button!=-1)
                {
                    if (mo.getClickCount()>1)
                        selectOplistMenuChoice(button);
                    else
                        showOplistMenuChoice(button);
                }
                else
                {
                    button=topMenu().getMenuButton(mo.getX(),mo.getY());
                    if (button!=-1)
                    {
                        selectMenuChoice(button);
                    }
                    else
                    {
                        for (int n=1;n<templateMenus.size();n++)
                        {
                            if (templateMenus.get(n).contains(mo.getPoint()))
                            {
                                for (int r=0;r<n;r++)
                                {
                                    templateMenus.remove(0);
                                    folderPath.remove(0);
                                }
                                break;
                            }
                        }
                    }
                }
                
            }
        }
        else if (mo.getButton()==MouseEvent.BUTTON3)
        {
            if (popup==null)
            {
                placePopup(mo.getPoint());
            }
            else
                popup=null;
        }
    }
    public void mouseWheelMoved(MouseWheelEvent mo)
    {
        if (DisplayWindow.contains(mo.getPoint()))
        {
            DisplayWindow.scroll(mo.getPreciseWheelRotation()*3.0);
        }
        else if (topMenu().contains(mo.getPoint()))
        {
            topMenu().scroll(mo.getWheelRotation());
        }
        else if (oplistMenu.contains(mo.getPoint()))
            oplistMenu.scroll(mo.getWheelRotation());
    }

    
/*-------Second Level Input Handling------*/
    
    public void executePopup(String action,boolean template)
    {
        popup.setVisible(false);
        if (action.compareTo("Add")==0)
        {
            if (template)
            {
                if (atTopLevel())
                    addTemplate();
                else
                    addToChoice(enclosing());
                topMenu().updateButtonsAfterAdd();
                addTopMenu();
            }
            else
            {
                addToChoice(enclosingOplist());
                oplistMenu.updateButtonsAfterAdd();
            }
        }
        else if (action.compareTo("Add Template")==0)
        {
            addTemplate();
        }
        else if (action.compareTo("Add Aspect")==0)
        {
            String name=showDialog("Enter Aspect Name");
            Addon asp=new Addon(name, "");
            asp.setPath(enclosing().relativePathWithin());
            enclosing().add(asp);
            topMenu().updateButtonsAfterAdd();
            addTopMenu();
        }
        else if (action.compareTo("Add List")==0)
        {
            addOplist(enclosing());
        }
        else if (equal(action,"Add Trait"))
        {
            String trait=showDialog("Enter Trait");
            enclosing().add(Trait.linkTrait(trait));
            topMenu().updateButtonsAfterAdd();
        }
        else if (equal(action,"Allow as Complete"))
        {
            enclosing().allowAsComplete();
            topMenu().updateButtons();
        }
        else if (equal(action,"Change Name Format"))
        {
            Template tmp=(Template)enclosing();
            setNameFormat(tmp);
        }
        else if (equal(action,"Remove"))
        {
            int butIx=topMenu().getMenuButton(popup.x,popup.y);
            if (butIx==-1)
            {
                butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                if (butIx!=-1)
                {
                    removeOplistChoice(butIx);
                    oplistMenu.updateButtons();
                }
            }
            else
            {
                removeTemplateChoice(butIx);
            }
        }
        else if (equal(action,"Rename"))
        {
            if (template)
            {
                int butIx=topMenu().getMenuButton(popup.x,popup.y);
                if (butIx!=-1 && topMenu().curChoiceInRange(butIx))
                {
                    Item choice=topMenu().getCurChoice(butIx);
                    String name=showDialog("Enter a name",choice.toString());
                    if (!name.isEmpty())
                    {
                        if (choice instanceof Choice)
                        {
                            ((Choice)choice).setName(name);
                        }
                        else if (choice instanceof Trait)
                        {
                            enclosing().remove(butIx+topMenu().getScrollOffset());
                            enclosing().addAt(butIx+topMenu().getScrollOffset(),
                                    Trait.linkTrait(name));
                        }
                        else
                            Util.fail("executePopup(Rename) reached with choice : "+choice.getClass().getName());
                        topMenu().updateButtons();
                    }//if name not empty
                }//if butIx!=-1
            }//if template
            else
            {
                int butIx=oplistMenu.getMenuButton(popup.x, popup.y);
                if (butIx!=-1 && oplistMenu.curChoiceInRange(butIx))
                {
                    Item choice=oplistMenu.getCurChoice(butIx);
                    String name=showDialog("Enter a name",choice.toString());
                    if (!name.isEmpty())
                    {
                        if (choice instanceof OptionList)
                        {
                            ((OptionList)choice).setName(name);
                            oplistMenu.updateButtons();
                        }
                        else Util.fail("executePopup(Rename):oplist:choice is a"+choice.getClass().getName());
                    }//if name not empty
                }//if (oplistMenu)
            }//if oplist
        }
        else if (equal(action,"Change"))
        {
            if (template)
            {
                int butIx=topMenu().getMenuButton(popup.x,popup.y);
                if (butIx!=-1 && topMenu().curChoiceInRange(butIx))
                {
                    Object choice=topMenu().getCurChoice(butIx);
                    String name=showDialog("Enter a name",choice.toString());
                    if (!name.isEmpty())
                    {
                        if (choice instanceof Trait)
                        {
                            Trait str=(Trait)choice;
                            Choice c=enclosing();
                            int ix=c.getList_const().indexOf(str);
                            c.remove(ix);
                            c.addAt(ix,Trait.linkTrait(name));
                            topMenu().updateButtons();
                        }
                    }
                }
            }
            else
            {
                int butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                if (butIx!=-1 && oplistMenu.curChoiceInRange(butIx))
                {
                    Object choice=oplistMenu.getCurChoice(butIx);
                    String name=showDialog("Enter a name",choice.toString());
                    if (!name.isEmpty())
                    {
                        if (choice instanceof Trait)
                        {
                            Trait str=(Trait)choice;
                            Choice c=enclosingOplist();
                            if (c instanceof OptionList)
                            {
                                OptionList opl=(OptionList)c;
                                int ix=opl.getList().indexOf(str);
                                opl.remove(ix);
                                opl.addAt(ix, name);
                                oplistMenu.updateButtons();
                            }
                        }
                    }
                }
            }
        }
        else if (equal(action,"Change List Type"))
        {
            if (template)
            {
                if (enclosing() instanceof SuperList)
                {
                    SuperList list=(SuperList)enclosing();
                    String setAtt=showDialog("Enter Attribute",list.getAttribute());
                    list.setAttribute(setAtt);
                }//TODO : remove defensive programming
                else Util.fail("Change List Type used when not in Option List");
            }
            else
            {
                OptionList list=(OptionList)enclosingOplist();
                String setAtt=showDialog("Enter Attribute",list.getAttribute());
                list.setAttribute(setAtt);
            }
        }
        else if (equal(action,"Copy"))
        {
            if (template)
            {
                int butIx=topMenu().getMenuButton(popup.x,popup.y);
                if (butIx!=-1)
                    clipboard = topMenu().getCurChoice(butIx);
                else clipboard=null;
            }
            else 
            {
                int butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                if (butIx!=-1)
                    clipboard = oplistMenu.getCurChoice(butIx);
                else clipboard=null;
            }
        }
        else if (equal(action,"Cut"))
        {
            if (template)
            {
                int butIx=topMenu().getMenuButton(popup.x,popup.y);
                if (butIx!=-1)
                {
                    clipboard = topMenu().getCurChoice(butIx);
                    removeTemplateChoice(butIx);
                }
                else clipboard=null;
            }
            else 
            {
                int butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                if (butIx!=-1)
                {
                    clipboard = oplistMenu.getCurChoice(butIx);
                    removeOplistChoice(butIx);
                }
                else clipboard=null;
            }
        }
        else if (equal(action,"Paste"))
        {
            if (clipboard!=null)
            {
                if (template)
                {
                    int butIx=topMenu().getMenuButton(popup.x,popup.y);
                    if (butIx==-1)
                    {
                        enclosing().add(clipboard);
                    }
                    else 
                    {
                        int choiceIx=butIx+topMenu().getScrollOffset();
                        enclosing().addAt(choiceIx, clipboard);
                    }
                    if (clipboard instanceof Holder)
                    {
                        ((Holder)clipboard).setPath(enclosing().relativePathWithin());
                    }
                }
                else//oplist
                {
                    int butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                    if (butIx==-1)
                    {
                        enclosingOplist().add(clipboard);
                    }
                    else 
                    {
                        int choiceIx=butIx+oplistMenu.getScrollOffset();
                        enclosingOplist().addAt(choiceIx, clipboard);
                    }
                }
            }
        }
        else if (equal(action,"Open File"))
        {//me 1 yr after writing this: 'this is the most useful feature ever'
            if (template)
            {
                int butIx=topMenu().getMenuButton(popup.x,popup.y);
                if (butIx==-1)
                {//if clicked outside window
                    openDataFolder();
                }
                else if (!topMenu().curChoiceInRange(butIx))
                {//if clicked empty option
                    openFile(enclosing());
                }
                else
                {
                    Object toOpen=topMenu().getCurChoice(butIx);
                    Choice open;
                    if (toOpen instanceof Choice)
                        open=(Choice)toOpen;
                    else
                        open=enclosing();
                    openFile(open);
                }
            }
            else
            {
                //if (oplist)
                int butIx=oplistMenu.getMenuButton(popup.x,popup.y);
                if (butIx==-1)//if outside the window  clicked
                    openDataFolder();
                else if (!oplistMenu.curChoiceInRange(butIx))
                    openListFolder();
                else
                {
                    Object toOpen=oplistMenu.getCurChoice(butIx);
                    Choice open;
                    if (toOpen instanceof Choice)
                        open=(Choice)toOpen;
                    else
                        open=enclosingOplist();
                    openFile(open);
                }
            }
        }
        popup=null;
    }
    public void selectMenuChoice(int c)
    {
        if (topMenu().curChoiceInRange(c))
        {
            Object choice=topMenu().getCurChoice(c);
            if (choice instanceof Template)
            {
                Template holder=(Template)choice;
                folderPath.add(0,holder);
                addTopMenu();
            }
            else if (choice instanceof Addon)
            {
                Addon asp=(Addon)choice;
//                asp.displayInWindow();
                folderPath.add(0,asp);
                addTopMenu();
            }
            else if (choice instanceof OptionList)
            {
                OptionList oplist=(OptionList)choice;
                folderPath.add(0,oplist);
                addTopMenu();
            }
            else if (choice instanceof Trait)
            {
                showTrait((Trait)choice,enclosing());
            }
        }
    }
    public void showOplistMenuChoice(int c)
    {
        if (oplistMenu.curChoiceInRange(c))
        {
            c+=oplistMenu.getScrollOffset();
            if (curOplist==null)
            {
                genTrait(AllOpLists.oplistVector.get(c));
    //            oplistMenu.setCurChoices(curOplist.getList());
            }
            else
            {
                Trait choice=curOplist.getTrait(c);
                showTrait(choice,curOplist);
            }
        }
    }
    public void selectOplistMenuChoice(int c)
    {
        
        if (oplistMenu.curChoiceInRange(c))
        {
            c+=oplistMenu.getScrollOffset();
            if (curOplist==null)
            {
                curOplist=AllOpLists.oplistVector.get(c);
                oplistMenu.setCurChoices(curOplist.getList());
            }
            else
            {
                Trait choice=curOplist.getTrait(c);
                showTrait(choice,curOplist);
            }
        }
    }
    
/*-------Bottom Level "Doing" Functions-------*/
    
    public void placePopup(Point mo)
    {
        if (topMenu().contains(mo))
        {
            if (atTopLevel())
            {
                popup=new Popup(mo.x,mo.y,Popup.Type.Choice);
            }
            else
            {
                Choice choice=enclosing();
                if (choice instanceof Template)
                    popup=new Popup(mo.x,mo.y,Popup.Type.Template);
                else if (choice instanceof OptionList)
                    popup=new Popup(mo.x,mo.y,Popup.Type.OpList);
                else if (choice instanceof Addon)
                    popup=new Popup(mo.x,mo.y,Popup.Type.OpList);
                else
                    popup=new Popup(mo.x,mo.y,Popup.Type.Choice);
            }
        }
        else if (oplistMenu.contains(mo))
        {
            if (curOplist==null)
            {
                popup=new Popup(mo.x,mo.y,Popup.Type.Choice);
            }
            else
            {
                popup=new Popup(mo.x,mo.y,Popup.Type.OpList);
            }
        }
        else
        {
            popup=new Popup(mo.x,mo.y,Popup.Type.Window);
        }
    }
    public void showMessageDialog(String message)
    {
        JOptionPane.showMessageDialog(this, message,"Message",
                JOptionPane.PLAIN_MESSAGE, d20Icon);
    }
    public String showDialog(String question,String defaultText)
    {
        String answer= (String)JOptionPane.showInputDialog(this,question
            ,"Query",JOptionPane.QUESTION_MESSAGE,d20Icon,null,defaultText);
        if (answer==null)
            return "";
        else return answer;
    }
    public String showDialog(String question)
    {
        String answer= (String)JOptionPane.showInputDialog(this,question
            ,"Query",JOptionPane.QUESTION_MESSAGE,d20Icon,null,null);
        if (answer==null)
            return "";
        else return answer;
    }
    public OptionList showListPicker()
    {
        String question="Pick a List";
        String options[]=new String[AllOpLists.oplistVector.size()+1];
        options[0]="New";
        int n=1;
        for (OptionList list : AllOpLists.oplistVector)
        {
            options[n]=list.getName();
            n++;
        }
        String answer= (String)JOptionPane.showInputDialog(this,question
            ,"Query",JOptionPane.QUESTION_MESSAGE,d20Icon,options,null);
        
        if (answer==null)
            return null;
        
        if (equal(answer,"New"))
        {
            //if (name=="New" | name not in allOptionList
            String name=showDialog("Enter New List Name");
            if (name==null)
                return null;
            OptionList ret= new OptionList(name);
            oplistMenu.updateButtonsAfterAdd();
            return ret;
        }
        else
        {
            return AllOpLists.oplistMap.get(answer);
        }
    }
    public void addTemplate()
    {
        String name=showDialog("Enter a Template name");
        if (name!=null && !name.isEmpty())
            addTemplate(name);
    }
    public void addTemplate(String name)
    {
        Template add=new Template(name);
        setNameFormat(add);
        if (atTopLevel()) {
            Data.holders.add(add);
        }
        else
        {
            add.setPath(enclosing().relativePathWithin());
            enclosing().add(add);
        }
        topMenu().updateButtonsAfterAdd();
//        addTopMenu();
    }
    public void setNameFormat(Template tmp)
    {
        String fmt=showDialog("Enter Name Format",tmp.displayNameFormat());
        if (fmt!=null && !fmt.isEmpty())
            tmp.setNameFormat(fmt);
        else
        {
            if (atTopLevel())
                tmp.setNameFormat(new String[]{"Full Name"});
            else
                tmp.setNameFormat(((Template)enclosing()).nameFormat);
            tmp.inheritedNameFormat=true;
        }
    }
    public void addToChoice(Choice addTo)
    {
        //Caller must update buttons
        if (addTo instanceof OptionList)
        {
            String name=showDialog("Enter a Trait");
            if (name!=null && !name.isEmpty())
            {
                ((OptionList)addTo).add(name);
            }
        }
        else if (addTo instanceof Addon)
        {
            OptionList add=showListPicker();
            if (add==null)
                return;
            addTo.add(add);
        }
        else if (addTo instanceof AllOpLists)
        {
            String name=showDialog("Enter Option List Name");
            if (name!=null && !name.isEmpty())
            {
                new OptionList(name);
                //OptionList constructor automatically alerts AllOpLists
            }
        }
        else
        {
            //TODO : Test and remove
            Util.fail("addToChoice else reached - you said it shouldn't");
//            String name=showDialog("Enter a name","");
//            addToChoice(name,addTo);
        }
    }
    public void addOplist(Choice addTo)
    {
        OptionList oplist=showListPicker();
        
        if (oplist!=null)
        {
            if (addTo instanceof Addon
                || addTo instanceof Template)
            {
                addTo.add(oplist);
            }
            else //TODO: remove this line eventually
                Util.fail("Shouldnt be calling addOplist on a "
                        +addTo.getClass().getName());
        }
        topMenu().updateButtonsAfterAdd();
    }
    public void genNPC()
    {
        if (atTopLevel())
        {
            NPC npc=Data.genNPC();
            npc.displayInWindow();
        }
        else if (enclosing() instanceof SuperList)
        {
            genTrait();
        }
        else if (enclosing() instanceof Template)
        {
            NPC npc=((Template)enclosing()).genNPC();
            for (int n=0;n<folderPath.size();n++)
            {
                Template holder=(Template)folderPath.get(n);
                holder.addToNPC(npc);
            }
            npc.rollTraits();
            npc.displayInWindow();
        }
    }
    public void genTrait()
    {
        SuperList sup=(SuperList)enclosing();
        String option=sup.getOption();
        DisplayWindow.setTitle(sup.toString());
        DisplayWindow.clearText();
        DisplayWindow.addText(option);
    }
    public void genTrait(OptionList list) {
        
        String option=list.getOption();
        DisplayWindow.setTitle(option);
        DisplayWindow.clearText();
        DisplayWindow.addText(list.toString());
    }
    public void showTrait(Trait trait,Choice parent)
    {
        DisplayWindow.clearText();
        DisplayWindow.setTitle(trait.getOption());
        DisplayWindow.addText(trait.write());
        DisplayWindow.addText(parent.getName());
    }
    public void removeTemplateChoice(int butIx)
    {
        if (topMenu().curChoiceInRange(butIx))
        {//TODO : remove defensive programming
            if (atTopLevel())
                Data.holders.remove(topMenu().getCurChoice(butIx));
            else if (topMenu().curChoiceInRange(butIx))
                enclosing().remove(topMenu().getCurChoice(butIx));
            topMenu().updateButtons();
        }
    }
    public void removeOplistChoice(int butIx)
    {
        if (oplistMenu.curChoiceInRange(butIx))
        {//TODO : remove defensive programming
            if (oplistMenu.curChoiceInRange(butIx))
                enclosingOplist().remove(oplistMenu.getCurChoice(butIx));
            oplistMenu.updateButtons();
        }
    }
    
    public void openFile(Choice open)
    {
        String path=open.filePath();
        try{
            File file=new File(path);
            Desktop.getDesktop().open(file);
        }
        catch (IOException e)
        {
            showMessageDialog("File "+path+" couldn't be opened");
        }
    }
    public void openDataFolder()
    {
        try{
            Desktop.getDesktop().open(new File("Data/"));
        }
        catch (IOException e)
        {
            showMessageDialog("Data folder couldn't be opened");
        }
    }
    public void openListFolder()
    {
        try{
            Desktop.getDesktop().open(new File("Data/Lists"));
        }
        catch (IOException e)
        {
            showMessageDialog("Data/Lists folder couldn't be opened");
        }
    }
    public void openFolder(String name)
    {
        try{
            Desktop.getDesktop().open(new File(name));
        }
        catch (IOException e)
        {
            showMessageDialog(name+" folder couldn't be opened");
        }
    }
    
    
    
/*--------Utility Macros-------------*/
    
    public void addTopMenu() 
    {
        int x=20+20*templateMenus.size();
        Menu m=new Menu(5,x,65);
        if (atTopLevel())
            m.setCurChoices(Data.holders);
        else
            m.setCurChoices(folderPath.get(0).getList());
        templateMenus.add(0,m);
    }
    public void removeTopMenu()
    {
        templateMenus.remove(0);
    }
    public Menu topMenu() {
        return templateMenus.get(0);
    }
    public void backOneFolder()
    {
        if (atTopLevel())
            return;
        folderPath.remove(0);
        removeTopMenu();
    }
    public void exitOplist()
    {
        curOplist=null;
        oplistMenu.setCurChoices(AllOpLists.oplistVector);
    }
    
    public final Choice enclosing()
    {
        return folderPath.get(0);
    }
    public final boolean atTopLevel() {
        return folderPath.isEmpty();
    }
    public Choice enclosingOplist() {
        if (curOplist==null)
            return AllOpLists.all;
        else
            return curOplist;
    }
    
/*--------Interface Appeasement--------*/
    public void keyReleased(KeyEvent k) {}
    public void keyTyped(KeyEvent k)  {}
    public void mouseReleased(MouseEvent mo)  {}
    public void mouseClicked(MouseEvent mo)   {}
    public void mouseEntered(MouseEvent mo ) {}
    public void mouseExited(MouseEvent mo) {}
    
/*---------Main-------------*/
    static NPCGen main;
    public static void main(String[] args)
    {
        main= new NPCGen();

        main.setSize(1000, 700);
        main.setVisible(true);
        
    }
}
