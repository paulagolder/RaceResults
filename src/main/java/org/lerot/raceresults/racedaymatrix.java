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
import java.util.Vector;

public class racedaymatrix
{
    private String boatclass_str;
    private String racedate_str;
    private dataMatrix racematrix;
    dataMatrix compmatrix;
    boolean saved = false;
    String filename;

    public racedaymatrix()
    {
        racematrix = new dataMatrix();
        compmatrix = new dataMatrix();
    }

    public racedaymatrix(String boatclass, String racedate, int nraces, int nparticipants)
    {
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = new dataMatrix(nraces, nparticipants);
    }

    public racedaymatrix(String boatclass, String racedate, int nraces, Vector<String> saillist)
    {
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = new dataMatrix(nraces, saillist);
    }

    public static racedaymatrix demo()
    {
        racedaymatrix rm = new racedaymatrix();
        rm.boatclass_str = "DF99";
        rm.racedate_str = "08/07/1944";
        rm.racematrix = dataMatrix.demo(4, 15);
        return rm;
    }

    public String getBoatclass_str()
    {
        return boatclass_str;
    }

    public void setBoatclass_str(String boatclass_str)
    {
        this.boatclass_str = boatclass_str;
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
        String date = utils.getDate(racedate_str, "dd/mm/yyyy");
        return date.replace("/", ch);
    }

    public dataMatrix getRacematrix()
    {
        return racematrix;
    }

    public void setRacematrix(dataMatrix racematrix)
    {
        this.racematrix = racematrix;
    }

    public void printfileToXML(String path)
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
            bw.write("<raceresults class=\"" + boatclass_str + "\" date=\"" + racedate_str + "\">\n");
            racematrix.printToXML(bw);
            bw.write("</raceresults>");
            bw.close();
            saved=true;
        } catch (Exception e)
        {
            System.out.println(e);
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
            racematrix.printToHTML(bw, boatclass_str+" "+racedate_str);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void printToHTML( BufferedWriter bw ,String heading)
    {
        try
        {
            racematrix.printToHTML(bw, heading);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Document readXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        racematrix = new dataMatrix();
        System.out.println("Importing:" + fileNameWithPath);
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            racedate_str = rootele.getAttributeNode("date").getValue();
            boatclass_str = rootele.getAttributeNode("class").getValue();
            racematrix.loadresults(rootele);
            //makecompmatrix();
            saved=true;
        } catch (Exception e)
        {
            System.out.println("Does not exist:" + fileNameWithPath);
            return null;
        }
        return document;
    }

    public jswVerticalPanel displayraceresults(competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(10,10,10,10);
        int ncols = getNcols();
        int nrows = getNrows();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel addb = new jswLabel(getBoatclass_str());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if(parent instanceof ActionListener)
        {
            al = (ActionListener)parent;
        }
        jswButton editheader = new jswButton(al, "EDIT", "editraceday:" + index);
        if(!saved)
        {
            editheader.addStyle("foregroundcolor","red");
            editheader.applyStyle();
        }
        racedayheader.add(" right ", editheader);
        raceresults.add("  ", racedayheader);
        raceresults.add("  ", racematrix.makedatapanel(parent.getRankVector(),tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.setPadding(10,10,10,10);
        return raceresults;
    }


    public jswVerticalPanel displayscoreresults(competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2,2,2,4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = getNcols();
        int nrows = getNrows();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if(parent instanceof ActionListener)
        {
            al = (ActionListener)parent;
        }
        jswButton editheaderbutton = new jswButton(al, "EDIT", "editraceday:" + index);
        racedayheader.add(" right ", editheaderbutton);
        raceresults.add(" FILLW   ", racedayheader);
        if(compmatrix != null)
        {
            raceresults.add("    ", compmatrix.makesmalldatapanel(parent.getSailVector(), tablestyles));
        }
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }

    public String getValue(int c, int r)
    {
        return racematrix.getValue(c, r);
    }

    public int getNcols()
    {
        return racematrix.getncols();
    }

    public int getNrows()
    {
        return racematrix.getnrows();
    }

    public String getColname(int c)
    {
        return racematrix.colname.get(c);
    }

    public String getRowname(int r)
    {
        return racematrix.rowname.get(r);
    }

    public void setRowname(Vector<String> saillist)
    {
        racematrix.rowname = saillist;
    }

    public void makecompmatrix(Vector<String> saillist)
    {
        compmatrix = new dataMatrix();
        compmatrix.colname = racematrix.colname;
        compmatrix.rowname = saillist;
        compmatrix.selected = racematrix.selected;
        compmatrix.data = racematrix.getValueMatrix(saillist);
    }




    public Vector<String> getValuevector()
    {
        return racematrix.getValuevector();
    }

    public Vector<String> getRankvector()
    {
        return  racematrix.rowname;
    }

    public void setfilename(String selfile)
    {
        filename = selfile;
    }

    public void loadcomp(dataMatrix matrix2, int racecount)
    {
        //racematrix.colnames();
        compmatrix.createcompmatrix(matrix2, racecount);
    }


    public void setColname(Vector<String> collist)
    {
        racematrix.colname = collist;
    }

    public void setCompColname(Vector<String> collist)
    {
        compmatrix.colname = collist;
    }

    public void setSelect(boolean b)
    {
        Vector<Boolean> sel = new Vector<>();
        for(int c=0;c< compmatrix.getncols();c++)
        {
            sel.add(b);
        }
        compmatrix.selected = sel;
    }

    public void setCompRowname(Vector<String> saillist)
    {
        compmatrix.rowname = saillist;
    }

    public void printResultsToHTML(String path, String comptitle, HashMap<String, String> boatlist)
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
            printScoresToHTML(bw, comptitle+" "+boatclass_str+" "+ racedate_str,boatlist);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void printScoresToHTML(BufferedWriter bw, String title, HashMap<String, String> boatlist) throws IOException
    {
        int ncols = compmatrix.colname.size();
        int nrows = compmatrix.rowname.size();
        bw.write("<table id=\"" + compmatrix.getCssid() + "\" class=\"" + compmatrix.getCssclass() + "\">\n");
        bw.write("<tr>\n<th  colspan=" + (ncols + 2) + ">" + title + "</th>\n</tr>");

        bw.write("<tr>\n<th>Sail No</th>");
        bw.write("<th>Sailor</th>");
        for (int c = 0; c < ncols; c++)
        {
            bw.write("<th>" + compmatrix.colname.get(c) + "</th>");
        }
        bw.write("</tr>\n");
        for (int r = 0; r < nrows; r++)
        {
            bw.write("<tr><td>" + compmatrix.rowname.get(r) + "</td>");
            bw.write("<td>" +boatlist.get(compmatrix.rowname.get(r).trim() ) + "</td>");
            for (int c = 0; c < ncols; c++)
            {
                bw.write("<td >" + compmatrix.data.get(c).get(r) + "</td>");
            }
            bw.write("</tr>\n");
        }
        bw.write("</table>\n");
    }

    public void addRace()
    {
        racematrix.addRace();
    }

}
