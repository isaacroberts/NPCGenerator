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
public class Switch extends Trait
{
    Condition condition;
    Trait iftrue,elif;
    
    public Switch(Condition cond,Trait ifCondTrue,Trait ifCondFalse)
    {
        condition=cond;
        iftrue=ifCondTrue;
        elif=ifCondFalse;
    }
    
    
    
    public String getOption(Environment e)
    {
        return (condition.evaluate()?iftrue.getOption(e):elif.getOption(e));
    }
    public String write()
    {
        return "{("+condition.toString()+")?("+iftrue.toString()
                +"):("+elif.toString()+")}";
    }
}
