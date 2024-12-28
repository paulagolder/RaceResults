package org.lerot.raceresults;

import org.lerot.mywidgets.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class competition
{

    String competitionfile;
    Vector<String> racedayfilenames;
    private Vector<racedaymatrix> racedaymatrixlist = new Vector<racedaymatrix>();
    private String compyear;
    private String raceclass;
    private String competitionname;

    Vector<String> saillist = new Vector<String>();
    Vector<String> ranklist = new Vector<String>();

    public competition()
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
    }

    public String getCompyear()
    {
        return compyear;
    }

    public void setCompyear(String compyear)
    {
        this.compyear = compyear;
    }

    public String getRaceclass()
    {
        return raceclass;
    }

    public void setRaceclass(String raceclass)
    {
        this.raceclass = raceclass;
    }

    public String getCompetitionname()
    {
        return competitionname;
    }

    public void setCompetitionname(String competitionname)
    {
        this.competitionname = competitionname;
    }

    public void loadCompetition(String filename)
    {
        String path = utils.fileexists(filename);
        if (path == null) return;
        readracedaylistdXML(path);
        reloadracedays();
    }

    private void reloadracedays()
    {
        racedaymatrixlist.clear();
        for (int i = 0; i < racedayfilenames.size(); i++)
        {
            try
            {
                racedaymatrix raceday = new racedaymatrix();
                String filename = racedayfilenames.get(i);
                raceday.readXML(mainrace_gui.mysailinghome + filename);
                racedaymatrixlist.add(raceday);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        saillist = makesailnumbervector() ;
        ranklist = makerankvector();
    }


    public jswVerticalPanel displayrankcolumn(jswStyles tablestyles )
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Rank ");
        label00.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" height=40 ", label00);
        leftcolumn.add(" FILLW FILLH middle ", leftheader);

        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);

        int nrows = ranklist.size();
        for (int r = 0; r < nrows ; r++)
        {
            datagrid.addCell(new jswLabel(ranklist.get(r )), r+1, 0);
        }
        leftcolumn.add(" FILLH ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
    }


    public jswVerticalPanel displaysailcolumn(jswStyles tablestyles )
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);

        int nrows = ranklist.size();
        for (int r = 0; r < nrows ; r++)
        {
            datagrid.addCell(new jswLabel(saillist.get(r )), r+1, 0);
        }
        leftcolumn.add(" FILLW FILLH middle ", datagrid);

        return leftcolumn;
    }

    public jswPanel displayresults(jswStyles jswStyles)
    {
        return (new jswVerticalPanel("tesrt", true, true));
    }

    public void readracedaylistdXML(String fileNameWithPath)
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
            raceclass = rootele.getAttributeNode("class").getValue();
            competitionname = rootele.getAttributeNode("name").getValue();
            NodeList cells = rootele.getChildNodes();
            System.out.println(cells.getLength());
            NodeList racedaysnl = rootele.getElementsByTagName("raceday");
            for (int i = 0; i < racedaysnl.getLength(); i++)
            {
                Element acell = (Element) racedaysnl.item(i);
                String cname = acell.getAttribute("filename");
                racedayfilenames.add(cname);
                System.out.println(cname);
            }

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Component displayraces()
    {
        return null;
    }

    public Vector<racedaymatrix> getRacedaymatrixlist()
    {
        return racedaymatrixlist;
    }

    public void setRacedaymatrixlist(Vector<racedaymatrix> racedaymatrixlist)
    {
        this.racedaymatrixlist = racedaymatrixlist;
    }

    public racedaymatrix getRacedayNo(int i)
    {
        return racedaymatrixlist.get(i);
    }

    private Vector<String> makesailnumbervector()
    {
        SortedSet<String> nvector = new TreeSet<String>();
        for (int i = 0; i < racedaymatrixlist.size(); i++)
        {
            racedaymatrix araceday = racedaymatrixlist.get(i);
            Vector<String> values = araceday.getValuevector();
            nvector.addAll(values);
        }
        Vector<String> avector = new Vector<String>();
        avector.addAll(nvector);
        System.out.println(nvector);
        return avector;
    }

    private Vector<String> makerankvector()
    {
        SortedSet<String> nvector = new TreeSet<String>();
        for (int i = 0; i < racedaymatrixlist.size(); i++)
        {
            racedaymatrix araceday = racedaymatrixlist.get(i);
            Vector<String> values = araceday.getRankvector();
            nvector.addAll(values);
        }
        Vector<String> avector = new Vector<String>();
        avector.addAll(nvector);
        System.out.println(nvector);
        return avector;
    }

    public racedaymatrix getracedaymatrix(int i)
    {
        return racedaymatrixlist.get(i);
    }
}