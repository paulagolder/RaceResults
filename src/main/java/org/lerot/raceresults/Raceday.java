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
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.homeclubname;
import static org.lerot.raceresults.Mainrace_gui.mysailinghome;

public class Raceday
{
    //dataMatrix compmatrix;
    boolean saved = false;
    String filename;
    Competition competition;
    private String boatclass_str;
    private String racedate_str;
    private dataMatrix racematrix;

    public Raceday(Competition comp)
    {
        racematrix = new dataMatrix();
        //  compmatrix = new dataMatrix();
        competition = comp;
    }

    public Raceday(Competition comp, String boatclass, String racedate, int nraces, int nparticipants)
    {
        competition = comp;
        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = new dataMatrix(nraces, nparticipants);
        for (int c = 0; c < nraces; c++)
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

    public Raceday(String boatclass, String racedate, int nraces, int nsailors, Vector<String> boatlist)
    {

        setBoatclass_str(boatclass);
        setRacedate_str(racedate);
        racematrix = dataMatrix.randomMatrix(nraces, nsailors, boatlist);
        racematrix.setColname(utils.makeColnames(nraces));
        racematrix.setRowname(utils.makeRownames(nsailors));
    }

    public static Raceday demo(Competition comp)
    {
        Raceday rm = new Raceday(comp);
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
        if (racedate_str == null) return "";
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
                racematrix.printToXML(bw);
                bw.write("</raceresults>");
                bw.close();
                saved = true;
            } catch (Exception e)
            {
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
            loadfromXML(rootele);
            //makecompmatrix();
            saved = true;
            return document;
        } catch (Exception e)
        {
            System.out.println("Does not exist:" + fileNameWithPath);
            return null;
        }

    }

    public void loadfromXML(Element rootelement)
    {
        NodeList colnames = rootelement.getElementsByTagName("colnames");
        if (colnames.getLength() > 0)
        {
            Element acolname = (Element) colnames.item(0);
            NodeList cells = acolname.getElementsByTagName("cell");
            for (int c = 0; c < cells.getLength(); c++)
            {
                Element acell = (Element) cells.item(c);
                String cname = acell.getAttribute("colname");
                racematrix.getColname().add(cname);
            }
        }
        NodeList rownames = rootelement.getElementsByTagName("rownames");
        NodeList cells;
        if (rownames.getLength() > 0)
        {
            cells = ((Element) rownames.item(0)).getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String cname = acell.getAttribute("rowname");
                racematrix.getRowname().add(cname);
            }
        }
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

            if (!racematrix.getColname().contains(cname))
            {
                racematrix.getColname().add(cname);
            }
            int cnum = racematrix.getColname().indexOf(cname);
            racematrix.getColtype().add(cnum, ctype);
            racematrix.getSelected().add(isselected);
            cells = acol.getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String rname = acell.getAttribute("rowname");
                if (!racematrix.getRowname().contains(rname))
                {
                    racematrix.getRowname().add(rname);
                }
                String value = acell.getTextContent();
                SailNumber sn = new SailNumber(value);
                String snstr = sn.ToString(5);
                racematrix.putCell(cname, rname, snstr);
                // System.out.println("hello "+cname+":"+rname+":"+value+":"+sn.ToString(5));
            }
        }
    }


    public jswVerticalPanel displayraceresults(Competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(10, 10, 10, 10);
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel addb = new jswLabel(getBoatclass_str());
        addb.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" ", addb);
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
        raceresults.add("  ", racematrix.makedatapanel(parent.getRankVector(), tablestyles));
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.setPadding(10, 10, 10, 10);
        return raceresults;
    }

    public jswVerticalPanel displayscoreresults(Competition_gui parent, jswStyles tablestyles, int index)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2, 2, 2, 4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = GetNoRaces();
        int nrows = getNoSailors();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel(getRacedate("/"));
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
        dataMatrix compmatrix = racematrix.getValueMatrix(racematrix.getColname(), parent.currentcomp.competitors.getVector());
        raceresults.add("    ", compmatrix.makesmalldatapanel(parent.currentcomp.competitors, tablestyles));
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
        if (r >= racematrix.getRowname().size()) return "Rank- " + (r + 1);
        else return racematrix.getRowname().get(r);
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
        int nr = racematrix.getRowname().size();
        String colname = "Race " + ((char) (65 + nc));
        racematrix.addColumn(colname, nr);
    }

    public void setCell(int nc, int nr, String cd)
    {
        System.out.println("  set cell :"+cd);

        String[] sailid = SailNumber.parse(cd);
        if(sailid[1].isEmpty())
        {
            Sail gsail = competition.allSails.getSail(cd, boatclass_str, homeclubname);
            getRacematrix().setCell(nc, nr, gsail.getSailnumberStr());
        }
        else
        {

            getRacematrix().setCell(nc, nr, sailid[0] +" "+sailid[1].trim());
        }





    }
}
