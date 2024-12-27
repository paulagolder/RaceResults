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

public class resultmatrix
{
    private String boatclass_str;
    private String racedate_str;
    private dataMatrix resultmatrix;

    public resultmatrix()
    {
        resultmatrix = new dataMatrix();
    }

    public resultmatrix (String boatclass, String racedate, int nraces, int nparticipants)
    {
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        resultmatrix = new dataMatrix(nraces, nparticipants);
    }

    public  String getBoatclass_str()
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

    public  String getRacedate(String ch)
    {
        return racedate_str.replace("/", ch);
    }

    public dataMatrix getResultmatrix()
    {
        return resultmatrix;
    }

    public void setResultmatrix(dataMatrix resultmatrix)
    {
        this.resultmatrix = resultmatrix;
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
            resultmatrix.printToHTML(bw, boatclass_str, racedate_str);
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
        resultmatrix = new dataMatrix();
        System.out.println( "You chose to open this file:"+fileNameWithPath);
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            racedate_str = rootele.getAttributeNode("date").getValue();
            boatclass_str = rootele.getAttributeNode("class").getValue();
            resultmatrix.loadresults(rootele);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }

    public jswVerticalPanel displaysmallresults(ActionListener al, jswStyles tablestyles)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults",false, false);
        raceresults.setStyleAttribute("borderwidth",2);
        int ncols = getNcols();
        int nrows = getNrows();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader= new jswHorizontalPanel("heading", false, false);
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
        jswButton editheader = new jswButton(al ,"EDIT", "edithraceday");
        racedayheader.add(" right ", editheader);
        raceresults.add(" FILLW FILLH middle ", racedayheader);
        jswTable datagrid = new jswTable( null,"form1", tablestyles);
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
                jswLabel alabel = new jswLabel(getResultmatrix().getValue(c, r));
                jswCell acell = datagrid.addCell(alabel, r + 1, c + 1);
                //alabel.addMouseListener(acell);
            }
        }
        raceresults.applyStyle();
        raceresults.setPadding(5,5,5,5);
        return raceresults;
    }

    public String getValue(int c, int r)
    {
        return resultmatrix.getValue(c, r);
    }

    public int getNcols()
    {
        return resultmatrix.getncols();
    }

    public int getNrows()
    {
        return resultmatrix.getnrows();
    }

    public String getColname(int c)
    {
        return resultmatrix.colname.get(c);
    }

    public String getRowname(int r)
    {
        return resultmatrix.rowname.get(r);
    }


}
