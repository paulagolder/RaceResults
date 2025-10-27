package org.lerot.raceresults;

import java.io.BufferedWriter;

public class Result
{

    Integer position = null;
    Sail sail;
    Sailor sailor;
    String flag;

    public Result(int aposition, Sail asail, String aflag)
    {
        position = Integer.valueOf(aposition);
        sail = asail;
        flag = aflag;
        sailor= null;
    }

    public Result(int aposition, Sail asail, String aflag, Sailor asailor)
    {
        position = Integer.valueOf(aposition);
        sail = asail;
        flag = aflag;
        sailor = asailor;
    }

    public Result(int aposition, String cypherstring)
    {
        String[] defclasses = {"95"};
        Sail asail = Sail.parse(cypherstring, defclasses, "R");
        position = aposition;
        if (asail != null)
        {

            sail = asail;
            flag = "";
        } else
        {
            sail = null;
            flag = cypherstring;
        }
        sailor= null;
    }

    public String printToXML()
    {
        String out = "<result position=\"" + position + "\" sail=\"" + this.sail + "\" flag=\"" + this.flag + "\"";
        if(sailor != null && !sailor.isEmpty()) out += " sailor=\""+sailor.toString()+"\"";
        out+= "/> \n";
        return out;
    }


    public boolean isNull()
    {
        if (sail == null && flag.isEmpty()) return true;
        else return false;
    }

    public Sailor getSailor()
    {
        return sailor;
    }

    public void setSailor(Sailor sailor)
    {
        this.sailor = sailor;
    }


    public void setSailor(String sailorname)
    {
        this.sailor = new Sailor(sailorname);
    }
}

