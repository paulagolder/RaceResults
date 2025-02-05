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
import java.util.Vector;

import static org.lerot.raceresults.mainrace_gui.mysailinghome;

public class racedaymatrix
{
    dataMatrix compmatrix;
    boolean saved = false;
    String filename;
    private String boatclass_str;
    private String racedate_str;
    private dataMatrix racematrix;
    competition competition;

    public racedaymatrix(competition comp)
    {
        racematrix = new dataMatrix();
        compmatrix = new dataMatrix();
        competition = comp;
    }

    public racedaymatrix(competition comp, String boatclass, String racedate, int nraces, int nparticipants)
    {
        competition = comp;
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = new dataMatrix(nraces, nparticipants);
        for(int c=0;c<nraces;c++)
        {
            racematrix.getColname().add("Race " + ((char) (65 + c)));
            racematrix.getColtype().add("string");
            racematrix.getSelected().add(true);
        }
        for (int r = 0; r < nparticipants; r++)
        {
            racematrix.getRowname().add("Rank" + utils.pad(r + 1));
        }
    }

    public racedaymatrix(String boatclass, String racedate, int nraces, int nsailors, Vector<sail> boatlist)
    {
        Vector<String> saillist = new Vector<String>();
        for (int b = 0; b < boatlist.size(); b++)
        {
            saillist.add(boatlist.get(b).getSailnumber());
        }
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = new dataMatrix(nraces, nsailors, saillist);
        racematrix.setColname(utils.makeColnames(nraces));
        racematrix.setRowname(utils.makeRownames(nsailors));
    }

    public static racedaymatrix demo(competition comp)
    {
        racedaymatrix rm = new racedaymatrix(comp);
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
        File file;
        if (!path.startsWith("/"))
            file = new File(mysailinghome + path);
        else
        {
            file = new File(path);
        }
        if (!file.exists())
        {
            System.out.println(" creating :" + file);
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
            } else
                System.out.println(" created:" + file);
        }
        try
        {

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<raceresults class=\"" + boatclass_str + "\" date=\"" + racedate_str + "\">\n");
            racematrix.printToXML(bw);
            bw.write("</raceresults>");
            bw.close();
            saved = true;
        } catch (Exception e)
        {
            System.out.println(" problem with :" + file);
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
            racematrix.printToHTML(bw, boatclass_str + " " + racedate_str);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void printToHTML(BufferedWriter bw, String heading)
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
            saved = true;
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
        raceresults.setPadding(10, 10, 10, 10);
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel addb = new jswLabel(getBoatclass_str());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
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
        raceresults.add("  ", racematrix.makedatapanel(parent.getRankVector(), tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.setPadding(10, 10, 10, 10);
        return raceresults;
    }

    public jswVerticalPanel displayscoreresults(competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2, 2, 2, 4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", dt);
        ActionListener al = null;
        if (parent instanceof ActionListener)
        {
            al = parent;
        }
        jswButton editheaderbutton = new jswButton(al, "EDIT", "editraceday:" + index);
        racedayheader.add(" right ", editheaderbutton);
        raceresults.add(" FILLW  ", racedayheader);
        if (compmatrix != null)
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

    public int GetNoRaces()
    {
        return racematrix.getncols();
    }

    public int getNoSailors()
    {
        return racematrix.getnrows();
    }

    public String getColname(int c)
    {
        return racematrix.getColname().get(c);
    }

    public String getRank(int r)
    {
        return racematrix.getRowname().get(r);
    }



    public void makecompmatrix(Vector<String> saillist)
    {
        compmatrix = new dataMatrix();
        compmatrix.setColname(racematrix.getColname());
        compmatrix.setRowname(saillist);
        compmatrix.setSelected(racematrix.getSelected());
        compmatrix.data = racematrix.getValueMatrix(saillist);
    }


    public Vector<String> getValuevector()
    {
        return racematrix.getValuevector();
    }

    public Vector<String> getRankvector()
    {
        return racematrix.getRowname();
    }

    public void setfilename(String selfile)
    {
        filename = selfile;
    }

    public void addRace()
    {

            int nc = racematrix.getColname().size();
            int nr =racematrix.getRowname().size();
            String colname = "Race "+((char) (65 + nc));

        racematrix.addColumn(colname,nr);
    }

}
