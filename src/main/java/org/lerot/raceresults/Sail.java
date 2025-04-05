package org.lerot.raceresults;

public class Sail implements Comparable<Sail>
{
    private  SailNumber sailnumber;
    private String boatclass;
   // private String sailorname;
    private String forename;
    private String surname;
    private String club;

    public Sail(String asailnumber,String aforename,  String asurname, String aboatclass, String aclub)
    {
        sailnumber = new SailNumber(asailnumber);
        boatclass = aboatclass;
        forename = aforename;
        surname = asurname;
        club = aclub;
    }

    public Sail(String asailnumber, String asailorname, String aboatclass, String aclub)
    {
        sailnumber = new SailNumber(asailnumber);
        boatclass = aboatclass;
        setSailorname(asailorname);
        club = aclub;
    }

    @Override
    public int compareTo(Sail anothersail)
    {
        if (sailnumber == null || sailnumber.isEmpty())
        {
            return +0;
        } else if (anothersail == null || anothersail.sailnumber.isEmpty())
        {
            return +1;
        }
        Integer s1 = sailnumber.GetInt();
        Integer s2 = anothersail.sailnumber.GetInt();
        int r = s1.compareTo(s2);
        if (r != 0) return r;
        else
        {
            return this.club.compareTo(anothersail.getClub());
        }
    }

    public String getSailnumberStr()
    {
        return sailnumber.sailnumber;
    }


    public SailNumber getSailnumber()
    {
        return sailnumber;
    }
    public String getBoatclass()
    {
        return boatclass;
    }

    public void setBoatclass(String boatclass)
    {
        this.boatclass = boatclass;
    }

    public String getSailorname()
    {
        return forename+" "+ surname;
    }

    public void setSailorname(String sailorname)
    {
        String[] names = sailorname.split(" ");
        this.forename =names[0].trim();
        if(names.length==2)
          this.surname= names[1].trim();
        else
            this.surname= "?";
    }

    public String toXML()
    {
        return "<sail sailnumber=\"" + sailnumber.ToString(5) + "\" boatclass=\"" + boatclass + "\" forename=\"" + forename + "\" surname=\"" + surname + "\" club=\"" + club + "\" /> \n";
    }

    public String getClub()
    {
        return club;
    }

    public void setClub(String club)
    {
        this.club = club;
    }

    public boolean matches(Sail selectedsail)
    {
        if(sailnumber.matches(selectedsail.sailnumber)
                && selectedsail.getSailorname().equalsIgnoreCase(getSailorname())
                && selectedsail.boatclass.equalsIgnoreCase(boatclass)
                && selectedsail.club.equalsIgnoreCase((club)))
            return true;
        else return false;
    }
}

