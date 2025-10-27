package org.lerot.raceresults;

import static org.lerot.raceresults.Mainrace_gui.maingui;

public class Sailor implements Comparable<Sailor>
{

    private String forename;
    private String surname;
    private String club;

    public Sailor(String aforename, String asurname, String aclub)
    {
        forename = aforename;
        surname = asurname;
        club = aclub;
    }

    public Sailor(String aforename, String asurname)
    {
        forename = aforename;
        surname = asurname;
        club = "RMBC";
    }

    public Sailor(String sailstring)
    {
        parseName(sailstring);
    }



    public void parseName(String avalue)
    {

        if (avalue == null || avalue.isEmpty()) return ;

        String patternsclcb = "[a-zA-Z]+[,\\s]+[a-zA-Z]+";
        boolean foundMatch = avalue.matches(patternsclcb);
        if (foundMatch)
        {
            String[] parts = avalue.trim().split("[,\\s]+");
            forename = parts[0];
            surname = parts[1];
        }


    }

    public String toString()
    {
        return  getName() ;
    }


    @Override
    public int compareTo(Sailor anothersail)
    {
        if (anothersail == null || anothersail.getName().isEmpty())
        {
            return +1;
        }
        String s1 = this.getName();
        String s2 = anothersail.getName();
        int r = s1.compareTo(s2);
         return r;

    }



    public String getName()
    {
        return forename + ", " + surname;
    }

    public void setSailorname(String sailorname)
    {
        String[] names = sailorname.split("[ ,]+");

        this.forename = names[0].trim();
        if (names.length == 2)
            this.surname = names[1].trim();
        else
            this.surname = "?";
    }

    public String toXML()
    {
        return "<sailor  forename=\"" + forename + "\" surname=\"" + surname + "\" club=\"" + club + "\" /> \n";
    }

    public String getClub()
    {
        return club;
    }

    public void setClub(String club)
    {
        this.club = club;
    }

    public boolean isEmpty()
    {
        if(forename.isEmpty() && surname.isEmpty()) return true;
        else return false;
    }
}


