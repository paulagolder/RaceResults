package org.lerot.raceresults;

import org.lerot.mywidgets.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.mframe;

public class Raceday
{
    String outfilename = "";
    boolean saved = false;
    String infilename;
    Competition competition;
    private String boatclass_str;
    String[] boatclasses;
    private String racedate_str;
    private Vector<Race> racelist;
    private String cssid = "raceday";
    private String cssclass = "raceday";
    String[] boatcyphers;

    public Raceday(Competition comp)
    {
        competition = comp;
        racelist = new Vector<Race>();
    }

    public Raceday(Competition comp, String boatclass, String racedate)
    {
        competition = comp;
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        outfilename = makefilename();
        racelist = new Vector<Race>();
    }

    public Raceday(Competition comp, String boatclass, String racedate, int nraces, int nparticipants)
    {
        competition = comp;
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        outfilename = makefilename();
        racelist = new Vector<Race>();
        for (int c = 0; c < nraces; c++)
        {
            Race arace = new Race("Race " + ((char) (65 + c)), true);
            racelist.add(arace);
        }
    }

    public static Sail parse(String avalue, String defclasscypher, String defclubcypher)
    {
        if (mframe == null) return null;
        if (avalue == null || avalue.isEmpty()) return null;
        avalue = avalue.trim().toLowerCase();
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
            String aclubcypher = defclubcypher;
            String aclasscypher = parts[1];
            Sail asail = mframe.clubSailList.find(asailnumber, aclasscypher, aclubcypher);
            return asail;
        }

        String patternscb = "(\\d)+";
        foundMatch = avalue.matches(patternscb);
        if (foundMatch)
        {
            String[] parts = avalue.trim().split("\\s+");
            String asailnumber = parts[0];
            String aclubcypher = defclubcypher;
            String aclasscypher = defclasscypher;
            Sail asail = mframe.clubSailList.find(asailnumber, aclasscypher, aclubcypher);
            return asail;
        }
        return null;

    }

    public dataMatrix MakeMatrix()
    {
        int nraces = racelist.size();
        int nparticipants = 0;
        for (Race arace : racelist)
        {
            if (arace.size() > nparticipants) nparticipants = arace.size();
        }
        dataMatrix racematrix = new dataMatrix(nraces, nparticipants);
        for (int c = 0; c < nraces; c++)
        {
            racematrix.getColname().add(racelist.get(c).getName());
            racematrix.getColtype().add("string");
            racematrix.getSelected().add(racelist.get(c).isIncompetition());
        }
        for (int r = 0; r < nparticipants; r++)
        {
            racematrix.getRowname().add("Rank" + utils.pad(r + 1));
        }
        return racematrix;
    }


    public String getBoatclass_str()
    {
        return boatclass_str;
    }

    public String makefilename()
    {
        return "RACEDAY_" + getBoatclass_str_tidy() + "_" + utils.getSortDate(racedate_str) + ".rxml";
    }

    public void setBoatclass_str(String boatclass_str)
    {
        //this.boatclass_str = boatclass_str;
        boatclasses = mframe.classlist.makeKeyList(boatclass_str);
        boatcyphers = mframe.classlist.makeCypherList(boatclass_str);
        this.boatclass_str = String.join(",", boatclasses);

    }

    public String getRacedate_str()
    {
        return racedate_str;
    }

    public void setRacedate_str(String racedate_str)
    {
        this.racedate_str = racedate_str;
    }

    public String getRacedate(String ch)
    {
        if (racedate_str == null) return "";
        String date = utils.getDate(racedate_str, "dd/mm/yyyy");
        return date.replace("/", ch);
    }

    public String getBoatclass_str_tidy()
    {
        String bc = boatclass_str.toLowerCase().replace(" ", "");
        bc = bc.replace(",", "_");
        return bc;
    }
    
  /*  public void printfileToXML_old(String path)
    {
        File file;
        if (!path.startsWith("/")) file = new File(mysailinghome + path);
        else
        {
            file = new File(path);
        }
        if (!file.exists())
        {
            boolean success = false;
            try
            {
                success = file.createNewFile();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if (!success)
            {
                System.out.println(" not created:" + file);
            }
        }
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            try
            {
                bw.write("<raceresults class=\"" + boatclass_str + "\" date=\"" + racedate_str + "\">\n");
                bw.write("<colnames>\n");
                for (Race arace : racelist)
                {
                    bw.write("<cell colname=\"" + arace.getName() + "\"/>\n");
                }
                bw.write("</colnames>\n");
                bw.write("<rownames>\n");
                int nrows = this.getSailors().size();
                Vector<String> rownames = utils.makeRownames(nrows + 1);
                for (String rowname : rownames)
                {
                    bw.write("<cell rowname=\"" + rowname + "\"/>\n");
                }
                bw.write("</rownames>\n");
                for (Race arace : racelist)
                {
                    bw.write("<col name=\"" + arace.getName() + "\" type=\"string\" select=\"" + arace.isIncompetition() + "\" >\n");
                    int r = 0;
                    for (Result aresult : arace.resultlist)
                    {
                        if (aresult != null)
                        {
                            if (aresult.sail != null)
                                bw.write("<cell rowname=\"" + rownames.get(r) + "\">" + aresult.sail.toCypherString() + "</cell>\n");
                            else
                                bw.write("<cell rowname=\"" + rownames.get(r) + "\">" + aresult.flag + "</cell>\n");
                            r++;
                        }
                    }
                    bw.write("</col>\n");
                }
                //   racematrix.printToXML(bw);
                bw.write("</raceresults>\n");
                bw.close();
                saved = true;
            } catch (Exception e)
            {
                System.out.println(e);
                e.printStackTrace();
                System.out.println(" problem with :" + file.getAbsoluteFile());

            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }*/


    public boolean filenameHasChanged()
    {
        if (infilename.equalsIgnoreCase(outfilename)) return false;
        Path pathToAFile = Paths.get(infilename);
        String shortinname = pathToAFile.getFileName().toString();
        int fn = competition.racedayfilenames.indexOf(shortinname);
        if (fn < 0)
        {
            System.out.println("not found : " + infilename);
            return false;
        } else
        {
            Path pathToAFile2 = Paths.get(outfilename);
            String shortoutname = pathToAFile2.getFileName().toString();
            competition.racedayfilenames.set(fn, shortoutname);
            competition.saved = false;
            return true;
        }
    }

    public void saveToXML_new(String path)
    {
        File file;
        System.out.println(" Saving raceday to  :" + path);

        if (!path.startsWith("/")) file = new File(path);
        else
        {
            file = new File(path);
        }
        if (!file.exists())
        {
            boolean success = false;
            try
            {
                success = file.createNewFile();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            if (!success)
            {
                System.out.println(" not created:" + file);
            }
        }
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            try
            {
                bw.write("<raceday class=\"" + boatclass_str + "\" date=\"" + racedate_str + "\">\n");
                bw.write("<races>\n");
                for (Race arace : racelist)
                {
                    arace.printToXML(bw);
                }
                bw.write("</races>\n");
                bw.write("</raceday>");
                bw.close();
                saved = true;
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println(" problem with :" + file.getAbsoluteFile());

            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    public void printToHTML(String path)
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
            bw.write(" td { text-align: center; }\n");
            bw.write(" table, th, td { border: 1px solid; }\n");
            bw.write("</style>\n</head>\n<body>\n");
            printToHTML(bw, boatclass_str + " " + racedate_str);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public void printToHTML(BufferedWriter bw)
    {
        try
        {
            printToHTML(bw, boatclass_str + " " + racedate_str);
        } catch (Exception e)
        {
            System.out.println(e);
            try
            {
                bw.close();
            } catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }

    public void printToHTML(BufferedWriter bw, String title) throws IOException
    {
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        Vector<String> rownames = utils.makeRownames(nrows);
        boolean excluded = false;
        bw.write("<table id=\"" + cssid + "\" class=\"" + cssclass + "\">\n");
        bw.write("<tr>\n<th  colspan=" + (ncols + 1) + ">" + title + "</th>\n</tr>");
        bw.write("<tr>\n<th> </th>");
        for (Race arace : racelist)
        {
            bw.write("<th>" + arace.getName() + "</th>");
        }
        bw.write("</tr>\n");
        bw.write("<tr>\n<th> </th>");
        for (Race arace : racelist)
        {
            if (arace.isIncompetition())
            {
                bw.write("<th></th>");
            } else
            {
                bw.write("<th>X</th>");
                excluded = true;
            }
        }
        bw.write("</tr>\n");
        for (int r = 0; r < nrows; r++)
        {

            int filledcols = 0;
            for (Race arace : racelist)
            {
                Result outvalue = arace.getResult(r);
                if (outvalue != null)
                    filledcols++;
            }
            if (filledcols > 0)
            {
                bw.write("<tr><td>" + rownames.get(r) + "</td>");
                for (Race arace : racelist)
                {
                    ArrayList<Result> results = arace.resultlist;
                    Result aresult;
                    if (r >= results.size())
                    {
                        aresult = null;
                    } else
                        aresult = results.get(r);
                    if (aresult == null)
                    {
                        bw.write("<td > </td>");
                    } else
                    {
                        //  Sail asail = Sail.parse(aresult.trim(), competition.compclasslist.getDefaulKey(), competition.compclublist.getDefaulKey());
                        Sail asail = aresult.sail;
                        if (asail == null || !asail.hasSailnumber())
                        {
                            bw.write("<td >" + aresult.flag + "</td>");
                        } else
                        {
                            String outtext;
                            if (boatclasses.length > 1)
                                outtext = asail.toFormattedCypherString(competition.compclublist.getDefaulKey(), "**");
                            else
                            {
                                outtext = asail.toCypherString(competition.compclublist.getDefaulKey(), boatclasses[0]);
                            }
                            bw.write("<td >" + outtext + "</td>");
                        }
                    }

                }
                bw.write("</tr>\n");
            }

        }
        if (excluded == true)
        {
            bw.write("<tr>\n<th  colspan=" + (ncols + 1) + ">X not included in competition </th>\n</tr>");
        }
        bw.write("</table>\n");
    }

    public Document readXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        System.out.println("Importing:" + fileNameWithPath);
        try
        {

            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            racedate_str = rootele.getAttributeNode("date").getValue();
            setBoatclass_str(rootele.getAttributeNode("class").getValue());
            infilename = fileNameWithPath;
            outfilename = infilename;
            loadfromXML_selmodel(rootele);
            saved = true;
            return document;
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            // Prints what exception has been thrown
            System.out.println(e);
            System.out.println("Does not exist:" + fileNameWithPath);
            return null;
        }
    }

    public void loadfromXML_selmodel(Element rootelement)
    {
        NodeList colnames = rootelement.getElementsByTagName("colnames");
        if (colnames.getLength() > 0)
        {
            loadfromXML_oldmodel(rootelement);
        } else
        {
            System.out.println("Loading new model");
            loadfromXML_newmodel(rootelement);
        }

    }

    public void loadfromXML_newmodel(Element rootelement)
    {
        NodeList races = rootelement.getElementsByTagName("race");
        for (int c = 0; c < races.getLength(); c++)
        {
            Element acol = (Element) races.item(c);
            String cname = acol.getAttribute("name");
            String asel = acol.getAttribute("inCompetition");
            Boolean isselected = true;
            if (asel != null)
            {
                isselected = ("true".equalsIgnoreCase(asel));
            }
            Race arace = new Race(cname, isselected);
            NodeList results = acol.getElementsByTagName("result");
            for (int r = 0; r < results.getLength(); r++)
            {
                Element aresult = (Element) results.item(r);
                String position = aresult.getAttribute("position");
                String sailstring = aresult.getAttribute("sail");
                String flag = aresult.getAttribute("flag");

                Sail insail = new Sail(sailstring);
                if (insail != null)
                    arace.addResult(r, insail, flag);
                else
                    arace.addResult(r, null, flag);

                // System.out.println("hello "+cname+":"+rname+":"+value+":"+sn.ToString(5));
            }
            racelist.add(arace);
        }
    }

    public void loadfromXML_oldmodel(Element rootelement)
    {

        NodeList cols = rootelement.getElementsByTagName("col");
        for (int c = 0; c < cols.getLength(); c++)
        {

            Element acol = (Element) cols.item(c);
            String cname = acol.getAttribute("name");
            String ctype = acol.getAttribute("type");
            String asel = acol.getAttribute("select");
            Boolean isselected = true;
            if (asel != null)
            {
                isselected = ("true".equalsIgnoreCase(asel));
            }
            Race arace = new Race(cname, isselected);

            NodeList cells = acol.getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String rname = acell.getAttribute("rowname");

                String value = acell.getTextContent();
                Sail insail = parse(value,
                        "95", "R");
                if (insail != null)
                    arace.addResult(r, insail, value);
                else
                    arace.addResult(r, null, value);

                // System.out.println("hello "+cname+":"+rname+":"+value+":"+sn.ToString(5));
            }
            racelist.add(arace);

        }
        int a = 2;
    }

    public jswVerticalPanel displayscores(Competition_gui parent, jswStyles tablestyles, int index)
    {
        //  dataMatrix racematrix = MakeMatrix();
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(10, 10, 10, 10);
        int ncols = GetNoRaces();
        int nrows = getNoSailors() + 2;
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        //  jswLabel addb = new jswLabel(getBoatclass_str());
        //   addb.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        //   racedayheader.add(" ", addb);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if (parent instanceof ActionListener)
        {
            al = parent;
        }
        jswButton editheader = new jswButton(al, "EDIT", "editraceday:" + index);
        if (!saved)
        {
            editheader.addStyle("foregroundcolor", "red");
            editheader.applyStyle();
        }
        racedayheader.add(" right ", editheader);
        raceresults.add("  ", racedayheader);
        dataMatrix rankmatrix = getRankMatrix(parent.currentcomp.competitors);
        //  raceresults.add("  ", racematrix.makedatapanel(parent.getRankVector(), tablestyles));
        raceresults.add("  ", rankmatrix.makesmalldatapanel(parent.currentcomp.competitors, tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.setPadding(10, 10, 10, 10);
        return raceresults;
    }

   /* public jswVerticalPanel displayscoreresults(Competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2, 2, 2, 4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel("ceci " + getRacedate("/"));
        dt.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if (parent instanceof ActionListener)
        {
            al = parent;
        }
        jswButton editheaderbutton = new jswButton(al, "EDIT", "editraceday:" + index);
        racedayheader.add(" right ", editheaderbutton);
        raceresults.add(" FILLW  ", racedayheader);
        //  dataMatrix compmatrix = getValueMatrix(racematrix, racematrix.getColname(), parent.currentcomp.competitors.getVector());
        dataMatrix rankmatrix = getRankMatrix(parent.currentcomp.competitors);
        raceresults.add("    ", rankmatrix.makesmalldatapanel(parent.currentcomp.competitors, tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }*/


    /*   public dataMatrix getValueMatrix(dataMatrix data, Vector<String> colnames, Vector<String> rownames)
       {
           int ncols = colnames.size();
           int nrows = rownames.size();
           dataMatrix vm = new dataMatrix(ncols, nrows);
           vm.setRowname(rownames);
           vm.setColname(colnames);
           vm.setColtype("String", ncols);
           vm.setSelected(true, ncols);
           int c = 0;
           for (Race arace : racelist)
           {
               Boolean included = arace.isIncompetition();
               vm.getSelected().set(c, included);
               ArrayList<Result> values = arace.resultlist;
               int r = 0;
               for (Result aresult : values)
               {
                   if (aresult != null)
                   {
                       Sail asail = aresult.sail;
                       if (asail != null)
                       {
                           String token = asail.toCypherString();
                           if (!token.trim().isEmpty())
                           {
                               vm.putaCell(c, token,  (r + 1));
                           }
                       }

                   }
                   r++;
               }
               c++;
           }
           return vm;
       }
   */
    public dataMatrix getRankMatrix(SailList sails)
    {
        int ncols = GetNoRaces();
        int nrows = sails.size();
        Vector<Sail> sailsvector = sails.getSailVector();
        dataMatrix vm = new dataMatrix(ncols, nrows);
        vm.setRowname(sails.getVector());
        vm.setColname(getRacenames());
        vm.setColtype("String", ncols);
        vm.setSelected(true, ncols);
        int c = 0;
        for (Race arace : racelist)
        {
            Boolean included = arace.isIncompetition();
            vm.getSelected().set(c, included);
            int pos = 1;
            for (Result aresult : arace.resultlist)
            {

                if (aresult != null)
                {
                    if (aresult.sail != null && aresult.sail.getSailnumber() != null)
                    {
                        // int r = sailsvector.getindexOf(aresult.sail);
                        int r = sails.indexOf(aresult.sail);
                        if (r >= 0)
                        {
                            String token = aresult.sail.toCypherString();
                            if (!token.trim().isEmpty())
                            {
                                // vm.putaCell(c, token, " !!" + (r + 1));
                                vm.setCell(c + 1, r + 1, " " + pos);
                            }
                        }
                    }

                }
                pos++;

            }
            c++;
        }
        return vm;
    }


    public Result getValue(int c, int r)
    {
        return racelist.get(c).getResult(r);
    }

    public int GetNoRaces()
    {
        return racelist.size();
    }

    public int getNoSailors()
    {
        return getSailors().size();
    }

    public SailList getSailors()
    {
        SailList newlist = new SailList();
        if (racelist == null) return newlist;
        for (Race arace : racelist)
        {
            for (Result aresult : arace.resultlist)
            {
                if (aresult != null)
                {
                    Sail asail = aresult.sail;
                    if (asail != null && asail.getSailnumber() != null)
                    {
                        //System.out.println(asail);
                        newlist.addNoDuplicate(asail);
                    }
                }
            }
        }
        return newlist;
    }

    public String getRacename(int c)
    {
        return racelist.get(c).getName();
    }

    public Vector<String> getRacenames()
    {
        Vector<String> racenames = new Vector<String>();
        for (Race arace : racelist)
        {
            racenames.add(arace.getName());
        }
        return racenames;
    }

  /*  public void setfilename(String selfile)
    {
        infilename = selfile;
    }*/

    public Race getRace(int i)
    {

        return getRacelist().get(i);
    }

    public Vector<Race> getRacelist()
    {
        return racelist;
    }

    public void setRacelist(Vector<Race> racelist)
    {
        this.racelist = racelist;
    }

    public int getMaxParticiants()
    {
        int maxsailors = 0;
        for (Race arace : racelist)
        {
            if (arace.resultlist.size() > maxsailors) maxsailors = arace.resultlist.size();
        }
        return maxsailors;
    }


    public void addRace()
    {
        int n = racelist.size();
        racelist.add(new Race("Race " + ((char) (65 + n)), true));
    }
}
