package org.lerot.raceresults;

import java.io.BufferedWriter;

public class SailorResult
{

    Integer sequence = null;
    Sailor sailor;
    String boats;

    public SailorResult(int aposition, Sailor asailor)
    {
        sequence = aposition;
        sailor = asailor;
        boats= "";
    }


    public boolean isNull()
    {
        if (sailor == null ) return true;
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

}

