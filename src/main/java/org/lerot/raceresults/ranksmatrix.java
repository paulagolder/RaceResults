package org.lerot.raceresults;

import org.lerot.mywidgets.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class ranksmatrix
{
    private String boatclass;
    private String raceyear;
    private dataMatrix datamatrix;
    private competition currentcompetition;
    private int maxsailors;



    public ranksmatrix(competition acompetition)
    {
        setCurrentcompetition(acompetition);
        setBoatclass(acompetition.getRaceclass());
        setRaceyear(acompetition.getCompyear());

        int nsailors = currentcompetition.getSaillist().size();
        datamatrix = new dataMatrix(nsailors, nsailors);
        datamatrix.setSelected(true);
        datamatrix.setRowname(currentcompetition.getSaillist());
        datamatrix.setColname(utils.makePositionNames(nsailors));
        datamatrix.getColname().set(0,"SAIL");
    }

    public void makeMatrix()
    {
        SortedSet<String> boatlist;
        boatlist = new TreeSet<String>();
        boatlist.addAll(currentcompetition.getSaillist());
        Vector<String> saillist = currentcompetition.getSaillist();
        int ns = boatlist.size();
        int maxsails= boatlist.size();
        maxsailors=0;
        int[][] matrix2 = new int[ns + 1][ns + 1];
        for (int r = 0; r < maxsails; r++)
        {
            int totalscore = 0;
            for (int i = 0; i < currentcompetition.racedayfilenames.size(); i++)
            {
                racedaymatrix aracedaymatrix = currentcompetition.getRacedayNo(i);
                for (int c = 0; c < aracedaymatrix.GetNoRaces(); c++)
                {
                    String value = aracedaymatrix.compmatrix.getValue(c, r);
                    int iv;
                    try
                    {
                        iv = Integer.parseInt(value.trim());
                        matrix2[iv][r] = matrix2[iv][r] + 1;
                        if (iv > maxsailors) maxsailors = iv;
                    } catch (NumberFormatException ex)
                    {
                        iv = 0;
                    }
                }
            }
        }
        datamatrix.data.clear();
        Vector<String> sails = new Vector<String>();
        datamatrix.data.add(sails);

        for (int r = 0; r < saillist.size(); r++)
        {
            sails.add(saillist.get(r));
        }


        for (int c = 1; c < maxsailors+1; c++)
        {

            Vector<String> acol = new Vector<String>();
            int nrows = matrix2[c].length;
            for (int r = 0; r < nrows; r++)
            {
                int value = matrix2[c][r];
                acol.add(" " + value);
            }
            datamatrix.data.add(acol);
        }
    }


    public jswVerticalPanel displayresults(competition_gui parent, jswStyles tablestyles, int maxsailors)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2, 2, 2, 4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = getNcols();
        int nrows = getNrows();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" minheight=30 ", dt);
        raceresults.add(" FILLW minheight=30   ", racedayheader);
        raceresults.add("    ", datamatrix.makeresultspanel(parent.getSailVector(), tablestyles, maxsailors));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }

    public String getBoatclass()
    {
        return boatclass;
    }

    public void setBoatclass(String boatclass)
    {
        this.boatclass = boatclass;
    }

    public String getRaceyear()
    {
        return raceyear;
    }

    public void setRaceyear(String raceyear)
    {
        this.raceyear = raceyear;
    }

    public String getRacedate(String ch)
    {
        return raceyear.replace("/", ch);
    }

    public dataMatrix getDatamatrix()
    {
        return datamatrix;
    }

    public void setDatamatrix(dataMatrix datamatrix)
    {
        this.datamatrix = datamatrix;
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
            datamatrix.printToHTML(bw, boatclass + " " + raceyear);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public Document readXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        datamatrix = new dataMatrix();
        System.out.println("You chose to open this file:" + fileNameWithPath);
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            raceyear = rootele.getAttributeNode("date").getValue();
            boatclass = rootele.getAttributeNode("class").getValue();
            datamatrix.loadresults(rootele);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }

    public jswVerticalPanel displaysmallresults(ActionListener al, jswStyles tablestyles)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setStyleAttribute("borderwidth", 2);
        int ncols = getNcols();
        int nrows = getNrows();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Class ");
        label00.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", label00);
        jswLabel addb = new jswLabel(getBoatclass());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
        jswLabel label01 = new jswLabel(" Date ");
        label01.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" middle ", label01);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        jswButton editheader = new jswButton(al, "EDIT", "edithraceday");
        racedayheader.add(" right ", editheader);
        raceresults.add(" FILLW FILLH middle ", racedayheader);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        raceresults.add(" FILLW FILLH middle ", datagrid);
        datagrid.addCell(new jswLabel("corner"), 0, 0);
        for (int c = 1; c < ncols + 1; c++)
        {
            datagrid.addCell(new jswLabel(getColname(c - 1)), 0, c);
        }
        for (int r = 1; r < nrows + 1; r++)
        {
            datagrid.addCell(new jswLabel(getRowname(r - 1)), r, 0);
        }
        for (int c = 0; c < ncols; c++)
        {
            for (int r = 0; r < nrows; r++)
            {
                jswLabel alabel = new jswLabel(getDatamatrix().getValue(c, r));
                jswCell acell = datagrid.addCell(alabel, r + 1, c + 1);
                //alabel.addMouseListener(acell);
            }
        }
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }

    public String getValue(int c, int r)
    {
        return datamatrix.getValue(c, r);
    }

    public int getNcols()
    {
        return datamatrix.getncols();
    }

    public int getNrows()
    {
        return datamatrix.getnrows();
    }

    public String getColname(int c)
    {
        return datamatrix.getColname().get(c);
    }

    public String getRowname(int r)
    {
        return datamatrix.getRowname().get(r);
    }


    public competition getCurrentcompetition()
    {
        return currentcompetition;
    }

    public void setCurrentcompetition(competition currentcompetition)
    {
        this.currentcompetition = currentcompetition;
    }

    public void printResultsToHTML(String path, String comptitle, HashMap<String, String> boatlist, int maxsailors)
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
            printScoresToHTML(bw, comptitle + " " + boatclass + " " + raceyear, boatlist, maxsailors);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }


    public void printScoresToHTML(BufferedWriter bw, String title, HashMap<String, String> boatlist, int maxsailors) throws IOException
    {
        dataMatrix compmatrix = datamatrix;
        int ncols = compmatrix.getColname().size();
        int nrows = compmatrix.getRowname().size();
        bw.write("<table id=\"" + compmatrix.getCssid() + "\" class=\"" + compmatrix.getCssclass() + "\">\n");
        bw.write("<tr>\n<th  colspan=" + (ncols + 2) + ">" + title + "</th>\n</tr>");
        bw.write("<tr>\n<th>Sail No</th>");
        bw.write("<th>Sailor</th>");
        for (int c = 0; c < ncols; c++)
        {
            bw.write("<th>" + compmatrix.getColname().get(c) + "</th>");
        }
        bw.write("</tr>\n");
        for (int r = 0; r < nrows; r++)
        {
            bw.write("<tr><td>" + compmatrix.getRowname().get(r) + "</td>");
            bw.write("<td>" + boatlist.get(compmatrix.getRowname().get(r).trim()) + "</td>");
            for (int c = 0; c < ncols; c++)
            {
                bw.write("<td >" + compmatrix.data.get(c).get(r) + "</td>");
            }
            bw.write("</tr>\n");
        }
        bw.write("</table>\n");
    }

    public void setColnames(Vector<String> strings)
    {
        datamatrix.setColnames(strings);
    }
}
