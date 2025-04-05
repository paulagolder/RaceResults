package org.lerot.raceresults;


import org.lerot.mywidgets.jswHorizontalPanel;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswTextBox;
import org.lerot.mywidgets.jswVerticalPanel;

import java.awt.event.ActionListener;

public class Club
{
    private String key;
    private String fullName;
    private String cypher;

    public Club(String akey, String alongname, String acypher)
    {
        setKey(akey);
        setFullName(alongname);
        setCypher(acypher);
    }

    public String toString()
    {
        return getKey() + ":" + getFullName() + " (" + getCypher() + ")";
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getCypher()
    {
        return cypher;
    }

    public void setCypher(String cypher)
    {
        this.cypher = cypher;
    }

    public int compareTo(Club otherclub)
    {
        if (fullName == null)
        {
            return +0;
        } else if (otherclub == null)
        {
            return +1;
        } else if (otherclub.fullName == null)
        {
            return +1;
        } else
        {
            return fullName.compareTo(otherclub.fullName);
        }
    }

    public String toXML()
    {
        return "<club key=\"" + this.key + "\" fullname=\"" + this.fullName + "\" cypher=\"" + this.cypher + "\" /> \n";
    }




}
