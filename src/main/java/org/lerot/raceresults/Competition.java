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
import static org.lerot.raceresults.Mainrace_gui.maingui;
import static org.lerot.raceresults.utils.fileexists;

public class Competition
{
    public String outputdir = "";
    public String racedir = "";
    public int noCompetitors;
    public boolean saved;
    public BoatclassList compclasslist;
    Vector<String> viewlist;
    String competitionfile;
    Vector<String> racedayfilenames;
    SailList allSails = new SailList();
    SailList competitors = new SailList();
    String compdir;
    ClubList compclublist;
    int maxParticipants = 0;
    private String compyear;
    private String competitionname;
    private int targetracecount;
    private final Vector<Raceday> racedaylist;
    private String csscompetition = "competition";

    public Competition()
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaylist = new Vector<Raceday>();
        compclublist = new ClubList();
        compclasslist = new BoatclassList();
        viewlist=new Vector<String>();
    }

    public Competition(String compfile, SailList allsailslist)
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaylist = new Vector<Raceday>();
        viewlist= new Vector<String>();
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
        viewlist.clear();
        String viewtag= "LONG";
        if(racedayfilenames.size()>6) viewtag="SHORT";
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
                        viewlist.add(viewtag);
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
        Path path = Paths.get(filename);
        Path lastSegment = path.getName(path.getNameCount() - 1);
        int nraceday = racedayfilenames.indexOf(lastSegment);
        racedayfilenames.removeElementAt(nraceday);
        racedaylist.removeElementAt(nraceday);
        saved = false;
    }

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
        boolean cclub = compclublist.size() > 1;
        boolean cclass = compclasslist.size()>1 ;

        for (Sail asail : competitors)
        {
            datagrid.addCell(new jswLabel(asail.toCypherString(cclub, cclass)), r + 1, 0);
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
                compclasslist.put("df95", maingui.classlist.get("df95"));
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
                if(!racedayfilenames.contains(cname))
                {
                    racedayfilenames.add(cname);
                }

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
            bw.write("<competition  year=\"" + compyear + "\"  name= \"" + competitionname + "\" racecount = \"" + targetracecount + "\" clubs = \"" + compclublist.toString() + "\" directory = \"" + compdir + "\" classes = \"" + compclasslist.toString() + "\">\n");
            bw.write("<racedays>\n");
            for (int r = 0; r < racedayfilenames.size(); r++)
            {
                File rdfile = new File(Mainrace_gui.mysailinghome +compdir + "/" +racedayfilenames.get(r));
                if (rdfile.exists())
                {
                    bw.write("<raceday filename=\"" + racedayfilenames.get(r) + "\" />\n");
                }
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
        Vector<Sail> asaillist = Mainrace_gui.maingui.getClubSailList().getSailVector();
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
        if (competitionfile != null) Mainrace_gui.maingui.saveProperties(competitionfile);
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


    public String getClassString()
    {
        return compclasslist.toString();
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

    public PositionList makePositionList()
    {
        int ns = this.noCompetitors;
        PositionList positions = new PositionList(this);
        for (Raceday araceday : this.getRacedaylist())
        {
            for (Race arace : araceday.getRacelist())
            {
                if (arace.isIncompetition() || targetracecount <1)
                {
                    int iv;
                    for (Result aresult : arace.resultlist)
                    {
                        if (aresult != null)
                        {
                            int rank = aresult.position;
                            String sailorname = aresult.sail.getSailorname();
                            if (aresult.getSailor() != null)
                            {
                                sailorname = aresult.getSailor().getName();
                            }
                            if (!positions.list.containsKey(sailorname))
                            {
                                Position aposition = new Position(new Sailor(sailorname));
                                aposition.setRanks(ns);
                                positions.add(aposition);
                            }
                            Position aposition = positions.list.get(sailorname);
                            Integer r = aposition.getRanks().get(rank);
                            aposition.getRanks().set(rank, r + 1);
                            aposition.addSail(aresult.sail.getSailnumber().ToString(4));
                            aposition.incrementTotalraces();
                            if (positions.maxrank < rank + 1) positions.maxrank = rank + 1;
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Position> anentry : positions.list.entrySet())
        {
            Position position = anentry.getValue();
            ArrayList<Integer> ranks = position.getRanks();
            int c = 0;
            int points = 0;
            int count = 0;
            if (this.targetracecount > 0)
            {

                c = 0;
                while (count + ranks.get(c) <= targetracecount && c < ranks.size() - 1)
                {
                    points += ranks.get(c) * (c+1);
                    count += ranks.get(c);
                    c++;
                }
                if (count < targetracecount && c < ranks.size() - 1 )
                {
                    points += (targetracecount - count) * (c + 1);
                    count=targetracecount;
                }

            } else
            {
                int rank = 1;
                for (int rankcount : ranks)
                {
                    points += rankcount * rank;
                    rank++;
                }
            }
            position.setRace_points(points);
            position.setScoredraces(count);
        }
        return positions;
    }
}

