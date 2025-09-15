package org.lerot.raceresults;

import org.lerot.mywidgets.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.lerot.raceresults.Mainrace_gui.homeclub;
import static org.lerot.raceresults.Mainrace_gui.mframe;
import static org.lerot.raceresults.utils.fileexists;

public class Competition
{
    public String outputdir = "";
    public String racedir = "";
    public int noCompetitors;
    public boolean saved;
    String competitionfile;
    Vector<String> racedayfilenames;
    SailList allSails = new SailList();
    SailList competitors = new SailList();
    private String compyear;
    String compdir;
    public BoatclassList compclasslist;
    private String competitionname;
    ClubList compclublist;
    private int targetracecount;
    int maxParticipants = 0;
    private Vector<Raceday> racedaylist;
    private String csscompetition = "competition";


    public Competition()
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaylist = new Vector<Raceday>();
        compclublist = new ClubList();
        compclasslist = new BoatclassList();
    }

    public Competition(String compfile, SailList allsailslist)
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaylist = new Vector<Raceday>();
        competitionfile = compfile;
        compclasslist = new BoatclassList();
        compclublist = new ClubList();
        compdir = "";
        allSails = allsailslist;
        System.out.println("Loading Competition:" + compfile);
        String cfile = utils.fileexists(compfile);
        if (cfile == null)
        {
            System.out.println("Not found competition file :" + compfile);
            System.out.println("exiting...");
            System.exit(0);
        }
        readCompetitionFromXML(cfile);
        racedir = Mainrace_gui.mysailinghome + "/" + compdir;
        outputdir = Mainrace_gui.mysailinghome + "/" + compdir + "/output";
        Path path = Paths.get(outputdir);
        try
        {
            Files.createDirectories(path);
            System.out.println("Directory created: " + path);
        } catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public String getCompyear()
    {
        return compyear;
    }

    public void setCompyear(String compyear)
    {
        this.compyear = compyear;
    }

    public String getCompetitionname()
    {
        return competitionname;
    }

    public void setCompetitionname(String competitionname)
    {
        this.competitionname = competitionname;
    }

    public String getCsscompetition()
    {
        return csscompetition;
    }

    public void setCsscompetition(String csscompetition)
    {
        this.csscompetition = csscompetition;
    }

    public int getTargetracecount()
    {
        return targetracecount;
    }

    public void setTargetracecount(int targetracecount)
    {
        this.targetracecount = targetracecount;
    }

    public void readCompetitionFromXML(String path)
    {
        readCompetitionXML(path);
        if (compclublist.size() == 0)
        {
            Club aclub = new Club(homeclub, Mainrace_gui.homeclubname, Mainrace_gui.homeclubcypher);
            compclublist.put(homeclub, aclub);
        }
        reloadracedays();
        saved = true;
    }

    public void reloadracedays()
    {
        racedaylist.clear();
        ;
        for (String filename : racedayfilenames)
        {
            try
            {
                Raceday raceday = new Raceday(this);
                String file = fileexists(compdir + "/" + filename);
                if (file != null)
                {
                    System.out.println(" Loading raceday " + filename);
                    Document rd = raceday.readXML(file);
                    if (rd != null)
                    {
                        racedaylist.add(raceday);
                    }
                }
            } catch (Exception e)
            {
                removeRaceday(filename);
                this.saved = false;
                System.out.println(" Not loaded " + filename);
            }

        }
        competitors = makeCompetitorsList();
        maxParticipants = (maxparticipants());
        noCompetitors = competitors.size();
    }

    void removeRaceday(String filename)
    {
        int nraceday = racedayfilenames.indexOf(filename);
        racedayfilenames.removeElementAt(nraceday);
        racedaylist.removeElementAt(nraceday);
        saved = false;
    }

 /*   public jswVerticalPanel displayrankcolumn(jswStyles tablestyles)
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Rank ");
        label00.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" minheight=30 ", label00);
        leftcolumn.add(" FILLW middle minheight=30 ", leftheader);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("Rank"), 0, 0);
        int nrows = maxParticipants;
        for (int r = 0; r < nrows; r++)
        {
            datagrid.addCell(new jswLabel("!!" + r), r + 1, 0);
        }
        leftcolumn.add(" FILLW ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
    }*/

    public jswVerticalPanel displaysailcolumn(jswStyles tablestyles)
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Sail ");
        label00.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" minheight=30 ", label00);
        leftcolumn.add(" FILLW middle minheight=30 ", leftheader);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("SAILS"), 0, 0);
        int nrows = noCompetitors;
        int r = 0;
        String defclub = "";
        String defclass = "";
        if (compclasslist.size() == 1)
        {
            defclass = compclasslist.firstKey();
        }
        if (compclublist.size() == 1)
        {
            defclub = compclublist.firstKey();
        } else
            defclub = homeclub;
        for (Sail asail : competitors)
        {
            datagrid.addCell(new jswLabel(asail.toCypherString(defclub, defclass)), r + 1, 0);
            r++;
        }
        leftcolumn.add(" FILLW middle ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
    }


    public void readCompetitionXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            compyear = rootele.getAttributeNode("year").getValue();
            compclasslist.load(rootele.getAttributeNode("classes").getValue());
            competitionname = rootele.getAttributeNode("name").getValue();
            targetracecount = Integer.parseInt(rootele.getAttributeNode("racecount").getValue());
            if (rootele.getAttributeNode("clubs") != null)
            {
                String clublist = rootele.getAttributeNode("clubs").getValue();
                compclublist.makeClubList(clublist);
            } else
            {
                compclublist.makeClubList(homeclub);
            }
            if (rootele.getAttributeNode("classes") != null)
            {
                setRaceclasses(rootele.getAttributeNode("classes").getValue());
            } else
            {
                compclasslist.clear();
                compclasslist.put("df95", mframe.classlist.get("df95"));
            }
            if (rootele.getAttributeNode("directory") != null)
            {
                compdir = (rootele.getAttributeNode("directory").getValue());
            } else
            {
                compdir = "";
            }
            NodeList cells = rootele.getChildNodes();
            System.out.println(cells.getLength());
            NodeList racedaysnl = rootele.getElementsByTagName("raceday");
            for (int i = 0; i < racedaysnl.getLength(); i++)
            {
                Element acell = (Element) racedaysnl.item(i);
                String cname = acell.getAttribute("filename");
                //    cname = cname.replace(dotmysailing, "");
                //     cname = cname.replace(mysailinghome, "");
                racedayfilenames.add(cname);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Vector<Raceday> getRacedaylist()
    {
        return racedaylist;
    }


    public Raceday getRacedayNo(int i)
    {
        return racedaylist.get(i);
    }

    private int maxparticipants()
    {
        int maxparticipants = 0;
        for (int i = 0; i < racedaylist.size(); i++)
        {
            Raceday araceday = racedaylist.get(i);
            int np = araceday.getMaxParticiants();
            if (maxparticipants < np) maxparticipants = np;
        }

        return maxparticipants;
    }

    public Raceday getracedaymatrix(int i)
    {
        return racedaylist.get(i);
    }

    public void generaterandomsailnumbers(int n)
    {
        Set sl = new HashSet<String>();
        Random rn = new Random();
        competitors.clear();
        while (sl.size() < n + 1)
        {
            int j = rn.nextInt(90) + 10;
            sl.add(utils.sailmaker(j));
        }
        competitors.addAll(sl);
    }

    public void saveCompetitionToXML(String path)
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
            bw.write("<competition  year=\"" + compyear +
                    "\"  name= \"" + competitionname + "\" racecount = \"" + targetracecount +
                    "\" clubs = \"" + compclublist.toString() +
                    "\" directory = \"" + compdir +
                    "\" classes = \"" + compclasslist.toString() + "\">\n");
            bw.write("<racedays>\n");
            for (int r = 0; r < racedayfilenames.size(); r++)
            {
                bw.write("<raceday filename=\"" + racedayfilenames.get(r) + "\" />\n");
            }
            bw.write("</racedays>\n");
            bw.write(competitors.toXML(compclublist.toString(), compclasslist.toString()));
            bw.write("</competition>\n");
            bw.close();
            saved = true;
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void addRaceday(String filename)
    {
        racedayfilenames.add(filename);
    }

    public void printRacedaysToHTML(String selfile)
    {
        try
        {
            File file = new File(selfile);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" <!DOCTYPE html>\n<html>\n");
            bw.write("<head>\n<style>\n");
            bw.write(" td { text-align: center; }\n");
            bw.write(" table, th, td { border: 1px solid; }\n");
            bw.write("</style>\n");
            bw.write("<link rel=\"stylesheet\" href=\"raceday.css\">\n");
            bw.write("</head>\n<body id=\"" + csscompetition + "\">\n");
            bw.write("<h1> Competition " + getCompetitionname() + " " + compyear + "</h1>\n");
            bw.write("<h1> Clubs :" + compclublist.toString() + " </h1>\n");
            for (Raceday raceday : racedaylist)
            {
                raceday.printToHTML(bw);
            }
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            // System.out.println(e);
        }
    }

    public void createTestSaillist(String bclass)
    {
        Vector<Sail> asaillist = Mainrace_gui.mframe.getClubSailList().getSailVector();
        Vector<String> sb = new Vector<>();
        SailList sl = new SailList();
        for (int s = 0; s < asaillist.size(); s++)
        {
            Sail asail = asaillist.get(s);
            if (bclass.contains(asail.getBoatclass()))
            {
                sl.add(asail);
            }
        }
        competitors = sl;
    }


    public void saveCompetition()
    {
        System.out.println("You chose to save this competition file: " + competitionfile);
        this.saveCompetitionToXML(competitionfile);
        if (competitionfile != null)
            Mainrace_gui.mframe.saveProperties(competitionfile);
    }

    public Vector<Club> getCompclublist()
    {
        return compclublist.getClubVector();
    }


    public String toString()
    {
        return this.competitionname + " " + this.getCompyear() + " " + this.getRaceclasses() + " " + this.getTargetracecount() + " " + this.compclublist.toString();
    }

    public String getClubString()
    {
        return compclublist.toString();
    }

    public BoatclassList getRaceclasses()
    {
        return compclasslist;
    }

    public void setRaceclasses(String text)
    {
        this.compclasslist.makeClassList(text);
    }

    public void printResultSheetToHTML(String selfile, String competitionname)
    {
        try
        {
            File file = new File(selfile);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" <!DOCTYPE html>\n<html>\n");
            bw.write("<head>\n<style>\n");
            bw.write(" td { text-align: center; }\n");
            bw.write(" table  { border: none; }\n");
            bw.write(" table th, td { border: 1px solid; }\n");
            bw.write("table th {border : 2px black solid;}\n");
            bw.write("table th:nth-child(3)  {width:  100px;}\n");
            bw.write("table td {border : 2px black solid;}\n");
            bw.write(" table td:nth-child(3) {border : none;}\n");
            bw.write(" table th:nth-child(3) {border : none;}\n");
            bw.write(" .logo  { max-width: 100px; max-height: 100px;}");
            bw.write(" table.hd td { border: none; }\n");
            bw.write(" table.hd td { border: none;  text-align:left;}\n");
            bw.write("</style>\n");
            bw.write("<link rel=\"stylesheet\" href=\"raceday.css\">\n");
            bw.write("</head>\n<body id=\"" + csscompetition + "\">\n");
            bw.write("<table class=\"hd\" width=\"100%\" >");
            bw.write("<tr><td><img src=\"RMBC-FC_tiny.ico\" alt=\"Logo\" class=\"logo\"></td>");
            bw.write("<td> " + getRaceclasses() + " Racing Results </td></tr>\n");
            bw.write("<tr><td>Clubs :" + compclublist.toString() + "</td> <td> Date : &nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;/2025 </td></tr>\n");
            bw.write("</table>");

            TreeMap<String, Sail> saillist = allSails.makeTreeList(compclasslist.toString(), compclublist.toString());
            bw.write("<table class=\"result\" >\n");
            bw.write("<tr><th>Sail</th><th>Sailor</th><th></th><th>Rank</th><th>Race A</th><th>Race B </th><th>Race C </th><th>Race D </th></tr>\n");

            int r = 1;
            for (Map.Entry<String, Sail> anentry : saillist.entrySet())
            {
                Sail asail = anentry.getValue();
                bw.write("<tr><td>" + anentry.getKey() + "</td><td>" + asail.getSailorname() + "</td><td> </td><td>Rank " + r + "</td><td> </td><td> </td><td> </td><td> </td></tr>\n");
                r++;
            }

            bw.write("</table>\n");
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            // System.out.println(e);
        }
    }

    public SailList makeCompetitorsList()
    {
        SailList newlist = new SailList();
        for (Raceday araceday : racedaylist)
        {
            SailList asaillist = araceday.getSailors();
            for (Sail asail : asaillist)
            {
                if (asail != null)
                {
                    if (!newlist.contains(asail)) newlist.add(asail);
                }
            }
        }
        return newlist;
    }

    public void setRaceclubs(String text)
    {
        compclublist.makeClubList(text);
    }
}

