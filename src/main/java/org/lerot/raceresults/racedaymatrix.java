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
import java.util.Vector;

public class racedaymatrix
{
    private String boatclass_str;
    private String racedate_str;
    private dataMatrix racematrix;
    dataMatrix compmatrix;

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
        makecompmatrix();
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
        return racedate_str.replace("/", ch);
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
            racematrix.printToHTML(bw, boatclass_str, racedate_str);
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
        racematrix = new dataMatrix();
        System.out.println("You chose to open this file:" + fileNameWithPath);
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            racedate_str = rootele.getAttributeNode("date").getValue();
            boatclass_str = rootele.getAttributeNode("class").getValue();
            racematrix.loadresults(rootele);
            makecompmatrix();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }

    public jswVerticalPanel displayraceresults(competition_gui parent, jswStyles tablestyles, int index)
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
        jswLabel addb = new jswLabel(getBoatclass_str());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
        jswLabel label01 = new jswLabel(" Date ");
        label01.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" middle ", label01);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if(parent instanceof ActionListener)
        {
            al = (ActionListener)parent;
        }
        jswButton editheader = new jswButton(al, "EDIT", "editraceday:" + index);
        racedayheader.add(" right ", editheader);
        raceresults.add(" FILLW FILLH middle ", racedayheader);
        raceresults.add(" FILLW FILLH middle ", racematrix.makedatapanel(parent.getRankVector(),tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }


    public jswVerticalPanel displayscoreresults(competition_gui parent, jswStyles tablestyles, int index)
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
        jswLabel addb = new jswLabel(getBoatclass_str());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
        jswLabel label01 = new jswLabel(" Date ");
        label01.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" middle ", label01);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if(parent instanceof ActionListener)
        {
            al = (ActionListener)parent;
        }
        jswButton editheader = new jswButton(al, "EDIT", "editraceday:" + index);
        racedayheader.add(" right ", editheader);
        raceresults.add(" FILLW FILLH middle ", racedayheader);
        raceresults.add(" FILLW FILLH middle ", compmatrix.makedatapanel( parent.getSailVector(), tablestyles));
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

    public void makecompmatrix()
    {
        compmatrix = new dataMatrix();
        compmatrix.colname = racematrix.colname;
        compmatrix.rowname = racematrix.getValuevector();
        compmatrix.data = racematrix.getValueMatrix(compmatrix.rowname);
    }




    public Vector<String> getValuevector()
    {
        return racematrix.getValuevector();
    }

    public Vector<String> getRankvector()
    {
        return  compmatrix.rowname;
    }
}
