package org.lerot.raceresults;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Race
{

    ArrayList<Result> resultlist;
    private String name;
    private boolean incompetition;

    public Race(String aname, Boolean selected)
    {
        setName(aname);
        setIncompetition(selected);
        resultlist = new ArrayList<Result>();
    }

    int size()
    {
        return resultlist.size();
    }

    public void printToXML(BufferedWriter bw)
    {
        try
        {
            bw.write("<race name=\"" + name + "\" inCompetition=\"" + incompetition + "\">\n");
            for (Result aresult : resultlist)
            {
                if (aresult != null)
                {
                    if (aresult.sail != null)
                    {
                        if (aresult.sail.hasSailnumber())
                            bw.write(aresult.printToXML());
                    }
                }
            }
            bw.write("</race>\n");
        } catch (IOException e)
        {
            //nothing
        }
    }

    public void addResult(int r, Sail insail, String aflag)
    {
        Result aresult = new Result(r, insail, aflag);
        resultlist.add(aresult);
    }


    public void addResult(int r, Sail insail, String aflag,Sailor asailor)
    {
        Result aresult = new Result(r, insail, aflag,asailor);
        resultlist.add(aresult);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isIncompetition()
    {
        return incompetition;
    }

    public void setIncompetition(boolean incompetition)
    {
        this.incompetition = incompetition;
    }

    public boolean hasResult(int r)
    {
        if (r >= resultlist.size()) return false;
        if (resultlist.get(r) == null) return false;
        if (resultlist.get(r).isNull()) return false;
        return true;
    }

    public Result getResult(int r)
    {
        if (hasResult(r)) return resultlist.get(r);
        else return null;
    }

    public boolean hasResult(String sailtag)
    {
        boolean found= false;
        for (Result aresult : resultlist)
        {

            if (aresult != null && aresult.sail != null &&  aresult.sail.toCypherString().equalsIgnoreCase(sailtag)) return true;
        }
        return false;
    }

    public void setResult(int nr, String cypherstring)
    {
        Result aresult = new Result(nr, cypherstring);
        resultlist.set(nr, aresult);
    }

    public void setResult(int nr, Result aresult)
    {
        Result nresult = new Result(nr, aresult.sail,aresult.flag);
        resultlist.set(nr, nresult);
    }

    public void setResult(int nr, Sail asail)
    {
        Result nresult = new Result(nr, asail,"");
        resultlist.set(nr, nresult);
    }

    public void extendrace(int nr)
    {
        if (resultlist.size() > nr) return;
        else
        {
            for (int r = resultlist.size(); r < nr; r++) resultlist.add(null);
        }
    }
}
