package org.lerot.raceresults;

import java.text.NumberFormat;
import java.text.ParseException;

public class SailNumber implements Comparable<SailNumber>
{
    String sailnumber;

    public SailNumber(String str)
    {
        sailnumber = str.toLowerCase().trim();
    }

    static int getInt(String sailnumber)
    {
        try
        {
            return NumberFormat.getInstance().parse(sailnumber).intValue();
        } catch (ParseException e)
        {
            return -1;
        }
    }

    @Override
    public int compareTo(SailNumber othersailnumber)
    {
        if (sailnumber == null)
        {
            return +0;
        } else if (othersailnumber == null)
        {
            return +1;
        } else if (othersailnumber.sailnumber == null)
        {
            return +1;
        } else
        {
            int i1 = SailNumber.getInt(sailnumber);
            int i2 = SailNumber.getInt(othersailnumber.sailnumber);
            return i1 - i2;
        }
    }

    public void set(String str)
    {
        sailnumber = str.toLowerCase().trim();
    }

    public Integer GetInt()
    {
        return Integer.parseInt(sailnumber);
    }

    public String ToString(int sz)
    {
        Integer nc = SailNumber.getInt(sailnumber);
        if (nc < 10)
        {
            String out = "     0" + nc;
            return out.substring(out.length() - sz);
        } else if (nc < 100)
        {
            String out = "    " + nc;
            return out.substring(out.length() - sz);
        }else
        {
            String out = "   " + nc;
            return out.substring(out.length() - sz);
        }
    }

    public boolean matches(SailNumber sn)
    {
        return SailNumber.getInt(this.sailnumber) == SailNumber.getInt(sn.sailnumber);
    }

    public boolean matches(String str)
    {
        return sailnumber.equalsIgnoreCase(str.toLowerCase().trim());
    }

    public boolean isEmpty()
    {
        if(sailnumber == null) return true;
        if(sailnumber.equalsIgnoreCase("") )return true;
        if(sailnumber.trim().isEmpty()) return true;
        return false;
    }
}
