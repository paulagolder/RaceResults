package org.lerot.raceresults;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import static org.lerot.raceresults.Mainrace_gui.homeclub;

public class SailList extends TreeSet<Sail>
{

  public String[] clubsfilterlist;
   public  String[] classesfilterlist;

    public SailList()
    {

    }

    public static SailList load(Element rootele)
    {
        SailList competitors = new SailList();
        NodeList sails = rootele.getElementsByTagName("sail");
        int nl = sails.getLength();
        for (int i = 0; i < sails.getLength(); i++)
        {
            Element acell = (Element) sails.item(i);
            String sailnumber = acell.getAttribute("sailnumber");
            String sailorname = acell.getAttribute("sailorname");
            String boatclass = acell.getAttribute("boatclass");
            String club = acell.getAttribute("club");
            Sail asail = new Sail(sailnumber, sailorname, boatclass, club);
            competitors.add(asail);
        }
        return competitors;
    }

   /* public Sail findsail(String sailnumber, String boatclass, String club)
    {
        for (Sail asail : this)
        {
            if (asail.matches(sailnumber, boatclass, club)) return asail;
        }
        return null;
    }*/

    public Vector<String> getVector(String boatclass, String clubs)
    {
        Vector<String> sailvector = new Vector<String>();
        Vector<Sail> sails = new Vector<Sail>();
        for (Sail asail : this)
        {
            if (asail.getBoatclass().equalsIgnoreCase(boatclass))
            {
                if (clubs.contains(asail.getClub()))
                {
                    if (asail.getClub().equalsIgnoreCase(homeclub))
                    {
                        sailvector.add(asail.getSailnumberStr());
                    } else
                    {
                        Club aclub = Mainrace_gui.mframe.clublist.get(asail.getClub());
                        sailvector.add(asail.getSailnumberStr() + aclub.getCypher());
                    }
                }
            }
        }
        return sailvector;
    }

    public Vector<String> getVector()
    {
        Vector<String> sailvector = new Vector<String>();
        for (Sail asail : this)
        {
            sailvector.add(asail.getSailnumberStr()); //+ aclub.getCypher());
        }
        return sailvector;
    }

    public HashMap<String, Sail> makeList(String boatclass, String clublist)
    {
        HashMap<String, Sail> list = new HashMap<String, Sail>();
        for (Sail asail : this)
        {
            if (asail.getBoatclass().equalsIgnoreCase(boatclass))
            {
                if (clublist.contains(asail.getClub()))
                {
                    list.put(asail.getSailnumberStr(), asail);
                }
            }
        }
        return list;
    }

    public Vector<Sail> getSailVector()
    {
        Vector<Sail> sailvector = new Vector<Sail>();
        for (Sail asail : this)
        {
            sailvector.add(asail);
        }
        return sailvector;
    }

    public TreeMap<String, Sail> getMap()
    {
        TreeMap<String, Sail> sailmap = new TreeMap<String, Sail>();
        Vector<Sail> sailvector = new Vector<Sail>();
        for (Sail asail : this)
        {
            sailmap.put(asail.getSailnumberStr(), asail);
        }
        return sailmap;
    }

    public Vector<String> getTrimmedVector()
    {
        Vector<String> trimmed = new Vector<String>();
        for (Sail asail : this)
        {
            trimmed.add(asail.getSailnumberStr().trim());
        }
        return trimmed;
    }

    public Vector<Integer> getIntegerVector()
    {
        Vector<Integer> intvector = new Vector<>();
        for (Sail asail : this)
        {
            intvector.add(Integer.parseInt(asail.getSailnumberStr().trim()));
        }
        return intvector;
    }

    public Sail getSail(String avalue)
    {
        String sailstr = avalue.trim();
        int sailno = SailNumber.getInt(sailstr);
        Vector<Integer> intvector = new Vector<>();
        int nfound = 0;
        Sail sailfound = null;
        for (Sail asail : this)
        {
            String asailstr = asail.getSailnumberStr().trim();
            int asailno = SailNumber.getInt(asailstr);
            if (sailno == asailno)
            {
                sailfound = asail;
                nfound++;
            }
        }
        if (nfound == 1) return sailfound;
        else
        {
            nfound = 0;
            for (Sail asail : this)
            {
                String asailstr = asail.getSailnumberStr().trim();
                int asailno = SailNumber.getInt(asailstr);
                if (sailstr.equalsIgnoreCase(asailstr))
                {
                    sailfound = asail;
                    nfound++;
                }
            }
            if (nfound == 1) return sailfound;
            else return null;
        }
    }

    public SailList select(String raceclass,String raceclub)
    {
        SailList list = new SailList();
        for (Sail asail : this)
        {
            if (asail.getBoatclass().equalsIgnoreCase(raceclass) && asail.getClub().equalsIgnoreCase(raceclub))
            {
                list.add(asail);
            }
        }
        return list;
    }

    public String totext()
    {
        StringBuilder out = new StringBuilder();
        for (Sail asail : this)
        {
            out.append(asail.getSailnumberStr()).append(" : ");
        }
        return out.toString();
    }

    public String toXML(String clubstring, String classesstring)
    {
        StringBuilder out = new StringBuilder("<sails>\n");
        for (Sail asail : this)
        {
            if(clubstring.contains(asail.getClub()) && classesstring.contains(asail.getBoatclass()))
                  out.append(asail.toXML());
        }
        out.append("</sails>\n");
        return out.toString();
    }

    public void readSaillistXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            NodeList sails = rootele.getElementsByTagName("sail");
            for (int i = 0; i < sails.getLength(); i++)
            {
                Element acell = (Element) sails.item(i);
                String temp = acell.getAttribute("sailnumber");
                SailNumber sn = new SailNumber(temp);
                String sailnumber = sn.ToString(5);
                String boatclass = acell.getAttribute("boatclass").toUpperCase();
                String club = acell.getAttribute("club").toUpperCase();
                if (club == null) club = homeclub;
                String sname = acell.getAttribute("sailorname");
                if (sname != null  && !sname.equalsIgnoreCase(""))
                {
                    Sail asail = new Sail(sailnumber, sname, boatclass, club);
                    add(asail);
                } else
                {
                    String forename = acell.getAttribute("forename");
                    String surname = acell.getAttribute("surname");
                    Sail asail = new Sail(sailnumber, forename, surname, boatclass, club);
                    add(asail);
                }
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void makefilters()
    {
        HashSet<String> clubs = new HashSet<String>();
        HashSet<String> classes = new HashSet<String>();
        for (Sail asail : this)
        {
            clubs.add(asail.getClub());
            classes.add(asail.getBoatclass());
        }
        clubsfilterlist = clubs.toArray(new String[clubs.size()]);
        classesfilterlist = classes.toArray(new String[classes.size()]);
    }

    public String printBySailNumberToHTML()
    {
        TreeMap<SailNumber, HashMap<String, String>> snl = new TreeMap<>();
        for (Sail asail : this)
        {
            if (asail.getClub().equalsIgnoreCase(homeclub))
            {
                SailNumber sailnumber = asail.getSailnumber();
                snl.computeIfAbsent(sailnumber, k -> new HashMap<String, String>());
                HashMap<String, String> snlmap = snl.get(sailnumber);
                String boatclass = asail.getBoatclass();
                String sailorname = asail.getSailorname();
                snlmap.put(boatclass, sailorname);
                snl.put(sailnumber, snlmap);
            }
        }
        String out = SailTableToString(snl, "Sail Numbers of members of " + homeclub);
        TreeMap<SailNumber, HashMap<String, String>> snl2 = new TreeMap<>();
        for (Sail asail : this)
        {
            if (!asail.getClub().equalsIgnoreCase(homeclub))
            {
                SailNumber sailnumber = asail.getSailnumber();
                snl2.computeIfAbsent(sailnumber, k -> new HashMap<String, String>());
                HashMap<String, String> snlmap = snl2.get(sailnumber);
                String boatclass = asail.getBoatclass();
                String sailorname = asail.getSailorname();
                snlmap.put(boatclass, sailorname);
                snl2.put(sailnumber, snlmap);
            }
        }
        out += SailTableToString(snl2, "Sail Numbers of  NON members of " + homeclub);

        return out;
    }
    private String NameTableToString(TreeMap<String, HashMap<String, String>> snl, String title)
    {
        String out = "<table>\n";
        out += " <tr><th colspan=\"4\">"+title + "</th></tr>\n";
        out += "<tr><th></th>";
        for (String aclass : classesfilterlist)
        {
            out += "<th>" + aclass + "</th>";
        }
        out += "</tr>\n";

        for (Map.Entry<String, HashMap<String, String>> anentry : snl.entrySet())
        {
           String asailorname = anentry.getKey();
            HashMap<String, String> classlist = anentry.getValue();
            out += "<tr><td>" + asailorname + "</td>";
            for (String aclass : classesfilterlist)
            {
                String sailnumber = classlist.get(aclass);
                if (sailnumber  != null)
                    out += "<td>" + sailnumber  + "</td>";
                else
                    out += "<td></td>";
            }
            out += "</tr>\n";
        }
        out += "</table>\n";
        return out;
    }
    public String SailTableToString(TreeMap<SailNumber, HashMap<String, String>> snl, String title)
    {
        String out = "<table>\n";
        out += " <tr><th colspan=\"4\">"+title + "</th></tr>\n";
        out += "<tr><th></th>";
        for (String aclass : classesfilterlist)
        {
            out += "<th>" + aclass + "</th>";
        }
        out += "</tr>\n";

        for (Map.Entry<SailNumber, HashMap<String, String>> anentry : snl.entrySet())
        {

            SailNumber asailnumber = anentry.getKey();
            out += "<tr><td text-align=\"right\">" + asailnumber.ToString(5) + "</td>";
            HashMap<String, String> classlist = anentry.getValue();
            for (String aclass : classesfilterlist)
            {
                String asailor = classlist.get(aclass);
                if (asailor != null)
                    out += "<td>" + asailor + "</td>";
                else
                    out += "<td></td>";
            }
            out += "</tr>\n";
        }
        out += "</table>\n";
        return out;
    }

    public String sailorTableToString()
    {
        TreeMap<String, HashMap<String, String>> snl = new TreeMap<>();
        for (Sail asail : this)
        {
            if (asail.getClub().equalsIgnoreCase(homeclub))
            {
                String sailorname = asail.getSailorname();
                snl.computeIfAbsent(sailorname, k -> new HashMap<String, String>());
                HashMap<String, String> snlmap = snl.get(sailorname);
                String boatclass = asail.getBoatclass();
                snlmap.put(boatclass, asail.getSailnumberStr());
                snl.put(sailorname, snlmap);
            }
        }
        String out = NameTableToString(snl, "Sail Numbers of members of " + homeclub);
   /*     TreeMap<SailNumber, HashMap<String, String>> snl2 = new TreeMap<>();
        for (Sail asail : this)
        {
            if (!asail.getClub().equalsIgnoreCase(homeclub))
            {
                SailNumber sailnumber = asail.getSailnumber();
                snl2.computeIfAbsent(sailnumber, k -> new HashMap<String, String>());
                HashMap<String, String> snlmap = snl2.get(sailnumber);
                String boatclass = asail.getBoatclass();
                String sailorname = asail.getSailorname();
                snlmap.put(boatclass, sailorname);
                snl2.put(sailnumber, snlmap);
            }
        }
        out += SailTableToString(snl2, "Sail Numbers of  NON members of " + homeclub);
*/
        return out;
    }

   public void loadallsailfiles(String fileending,File directory)
   {

           File[] list = directory.listFiles();
           if(list!=null)
               for (File fil : list)
               {
                   if (fil.isDirectory())
                   {
                       loadallsailfiles(fileending ,fil);
                   }
                   else if (fil.getName().endsWith(fileending))
                   {
                       System.out.println(fil.getParentFile());
                       this.readSaillistXML(fil.getPath());
                   }
               }
       }

    public void printSailnumberToHTML(String path)
    {
        try
        {
            File file = new File(path);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" <!DOCTYPE html>\n<html>\n");
            bw.write("<head>\n<style>");
            bw.write(" td { text-align: left; padding:5px; padding-left:10px; padding-right:10px; }\n");
            bw.write(" td:first-child {text-align: right;}\n");
            bw.write(" table, th, td { border: 1px solid; }\n");
            bw.write("</style>\n</head>\n<body>\n");

            bw.write(this.printBySailNumberToHTML());

            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void printBySailorToHTML(String path)
    {
        try
        {
            File file = new File(path);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" <!DOCTYPE html>\n<html>\n");
            bw.write("<head>\n<style>");
            bw.write(" td { text-align: right; padding:5px; padding-left:10px; padding-right:10px; }\n");
            bw.write(" td:first-child {text-align: left;}\n");
            bw.write(" table, th, td { border: 1px solid; }\n");
            bw.write("</style>\n</head>\n<body>\n");

            bw.write(this.sailorTableToString());

            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }
}

