package org.lerot.raceresults;

import static org.lerot.raceresults.Mainrace_gui.mframe;

public class Sail implements Comparable<Sail>
{
    private SailNumber sailnumber;
    private String boatclass;
    // private String sailorname;
    private String forename;
    private String surname;
    private String club;

    public Sail(String asailnumber, String aforename, String asurname, String aboatclass, String aclub)
    {
        sailnumber = new SailNumber(asailnumber);
        boatclass = aboatclass;
        forename = aforename;
        surname = asurname;
        club = aclub;
    }

    public Sail(String sailstring)
    {
        String[] fields = sailstring.split(":");
        if (fields.length < 3) return;
        sailnumber = new SailNumber(fields[0]);
        boatclass = fields[1].trim();
        String name = fields[2];
        if (name.contains("?"))
        {
            System.out.print("*******" + sailstring);
        }
        setSailorname(name);
        club = fields[3];
    }

    public static Sail parse(String avalue, String[] defclasses, String defclub)
    {
        if (mframe == null) return null;
        if (avalue == null || avalue.isEmpty()) return null;
        avalue = avalue.trim().toLowerCase();
        // String[] parts = avalue.split(" ");
        //  String patternsclcb = "(\\d)+\\s+[a-z]+\\s+[a-z]+";
        String patternsclcb = "(\\d)+\\s+\\w+\\s+[a-z]+";
        boolean foundMatch = avalue.matches(patternsclcb);
        if (foundMatch)
        {
            String[] parts = avalue.trim().split("\\s+");
            String asailnumber = parts[0];
            String aclubcypher = parts[2].toUpperCase();
            String aclasscypher = parts[1];
            //fix for transition of cypher codes
            if (aclasscypher.equalsIgnoreCase("ds")) aclasscypher = "65";
            if (aclasscypher.equalsIgnoreCase("dn")) aclasscypher = "95";
            if (aclasscypher.equalsIgnoreCase("v")) aclasscypher = "Va";
            Sail asail = mframe.clubSailList.find(asailnumber, aclasscypher, aclubcypher);
            return asail;
        }
        //  String patternscl = "(\\d)+\\s+[a-z]+";
        String patternscl = "(\\d)+\\s+\\w+";
        foundMatch = avalue.matches(patternscl);
        if (foundMatch)
        {
            String[] parts = avalue.trim().split("\\s+");
            String asailnumber = parts[0];
            // String aclubcypher = defclub;
            String aclubcypher = mframe.clublist.get(defclub).getCypher();
            String aclasscypher = parts[1];
            Sail asail = mframe.currentcompetition.allSails.find(asailnumber, aclasscypher, aclubcypher);
            return asail;
        }

        String patternscb = "(\\d)+";
        foundMatch = avalue.matches(patternscb);
        if (foundMatch)
        {
            String[] parts = avalue.trim().split("\\s+");
            String asailnumber = parts[0];
            String aclubcypher = mframe.clublist.get(defclub).getCypher();
            for (String aclasscypher : defclasses)
            {
                // String aclasscypher = mframe.classlist.get(defclasses.toLowerCase()).getCypher();
                Sail asail = mframe.clubSailList.find(asailnumber, aclasscypher, aclubcypher);
                if (asail != null) return asail;
            }
            return null;
        }
        return null;

    }
 /*   public static Sail  parsesailcypher(String avalue)
    {
        if(mframe==null) return null;
        avalue = avalue.trim();
        // String[] parts = avalue.split(" ");
        String patternsclcb = "(\\d)+\\s+[a-z]+\\s+[A-Z]+";
        boolean foundMatch = avalue.matches(patternsclcb);
        if(foundMatch)
        {
            String[] parts = avalue.trim().split( "\\s+");
            String asailnumber = parts[0];
            String aclubcypher = parts[2];
            String aclasscypher = parts[1];
            Sail asail = mframe.clubSailList.find(asailnumber, aclasscypher, aclubcypher);
            return asail;
        }
        else
            return null;

    }*/

    //  sn , cl , cb
    public String toString()
    {
        return sailnumber.ToString(6) + ":" + boatclass + ":" + getSailorname() + ":" + club;
    }

    public String toCypherString(String defclub, String defclass)
    {
        String classcypher = " ";
        if (boatclass == null)
        {
            System.out.println(" problemwith " + this.toString());
        }
        if (!defclass.equalsIgnoreCase(boatclass))
            classcypher = mframe.classlist.get(boatclass.toLowerCase()).getCypher();
        String clubcypher = " ";
        if (!defclub.equalsIgnoreCase(club))
            clubcypher = mframe.clublist.get(club).getCypher();
        return sailnumber.ToString(6) + " " + classcypher + " " + clubcypher;
    }

  /*  public String toCypherString(String defclub)
    {
        String classcypher = " ";
        classcypher = mframe.classlist.get(boatclass.toLowerCase()).getCypher();
        String clubcypher = " ";
        if (!defclub.equalsIgnoreCase(club))
            clubcypher = mframe.clublist.get(club).getCypher();
        return sailnumber.ToString(6) + " " + classcypher + " " + clubcypher;
    }*/

    public String toCypherString()
    {
        String classcypher = mframe.classlist.get(boatclass.toLowerCase()).getCypher();
        String clubcypher = mframe.clublist.get(club).getCypher();
        return sailnumber.ToString(6) + " " + classcypher + " " + clubcypher;
    }


    public String toFormattedCypherString(String defclub, String defclass)
    {
        String classcypher = " ";
        if (boatclass == null)
        {
            System.out.println(" problemwith " + this.toString());
        }
        if (!defclass.equalsIgnoreCase(boatclass))
            classcypher = mframe.classlist.get(boatclass.toLowerCase()).getCypher();
        String clubcypher = " ";
        if (!defclub.equalsIgnoreCase(club))
            clubcypher = mframe.clublist.get(club).getCypher();

        return sailnumber.ToString(6) + " <small><i>" + classcypher + " " + clubcypher + "</i></small>";
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
            int rsn = getSailorname().compareTo(anothersail.getSailorname());
            if (rsn != 0)
            {
                return rsn;
            }
            int rcn = getBoatclass().compareTo(anothersail.getBoatclass());
            if (rcn != 0)
            {
                return rcn;
            }
            int rcbn = getClub().compareTo(anothersail.getClub());
            if (rcbn != 0)
            {
                return rcbn;
            } else
            {
                return 0;
            }
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
        if (sailnumber.matches(selectedsail.sailnumber)
                && selectedsail.getSailorname().equalsIgnoreCase(getSailorname())
                && selectedsail.boatclass.equalsIgnoreCase(boatclass)
                && selectedsail.club.equalsIgnoreCase((club)))
            return true;
        else return false;
    }

    public boolean matches(int sailno, String sclass, String sclub)
    {
        if (sailnumber.matches(sailno)
                && this.boatclass.equalsIgnoreCase(sclass)
                && this.club.equalsIgnoreCase(sclub))
            return true;
        else return false;
    }

    public boolean hasSailnumber()
    {
        if (sailnumber == null) return false;
        if (sailnumber.isEmpty()) return false;
        return true;
    }
}


