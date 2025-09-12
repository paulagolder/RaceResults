package org.lerot.raceresults;

import java.io.BufferedWriter;

public class Result
{

    Integer position = null;
    Sail sail;
    String flag;

    public Result(int aposition, Sail asail, String aflag)
    {
        position = Integer.valueOf(aposition);
        sail = asail;
        flag = aflag;
    }

    public Result(int aposition, String cypherstring)
    {
        Sail asail = Sail.parse(cypherstring, "95", "R");
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
    }

    public String printToXML(BufferedWriter bw)
    {
        return "<result position=\"" + position + "\" sail=\"" + this.sail + "\" flag=\"" + this.flag + "\" /> \n";
    }


    public boolean isNull()
    {
        if (sail == null && flag.isEmpty()) return true;
        else return false;
    }
}

