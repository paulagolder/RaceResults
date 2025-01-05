package org.lerot.raceresults;

import org.lerot.mywidgets.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class competition
{

    String competitionfile;
    Vector<String> racedayfilenames;
    private Vector<racedaymatrix> racedaymatrixlist ;
    private String compyear;
    private String raceclass;
    private String competitionname;

    private Vector<String> saillist = new Vector<String>();
    private Vector<String> ranklist = new Vector<String>();

    public int getRacecount()
    {
        return racecount;
    }

    public void setRacecount(int racecount)
    {
        this.racecount = racecount;
    }

    private int racecount;

    public competition()
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaymatrixlist = new Vector<racedaymatrix>();
    }

    public competition(String name, String rclass,String year)
    {
        competitionname = name;
        compyear = year;
        raceclass = rclass;
        racedayfilenames = new Vector<String>();
        racedaymatrixlist = new Vector<racedaymatrix>();
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
        setSaillist(makesailnumbervector());
        setRanklist(makerankvector());
    }


    public jswVerticalPanel displayrankcolumn(jswStyles tablestyles )
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Rank ");
        label00.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" minheight=30 ", label00);
        leftcolumn.add(" FILLW middle minheight=30 ", leftheader);


        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);

        int nrows = getRanklist().size();
        for (int r = 0; r < nrows ; r++)
        {
            //
            datagrid.addCell(new jswLabel(getRanklist().get(r )), r+1, 0);
        }
        leftcolumn.add(" FILLW ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
    }


    public jswVerticalPanel displaysailcolumn(jswStyles tablestyles )
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Sail ");
        label00.applyStyle(mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" minheight=30 ", label00);
        leftcolumn.add(" FILLW middle minheight=30 ", leftheader);

        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);

        int nrows = getSaillist().size();
        for (int r = 0; r < nrows ; r++)
        {
            datagrid.addCell(new jswLabel(getSaillist().get(r )), r+1, 0);
        }
        leftcolumn.add(" FILLW middle ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
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
            racecount = Integer.parseInt(rootele.getAttributeNode("racecount").getValue());
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

    public void generaterandomsailnumbers(int n)
    {
        Set sl = new HashSet<String>();
        Random rn = new Random();
        getSaillist().clear();

        while(sl.size()<n+1)
        {
            int j = rn.nextInt(90) + 10;
            sl.add(utils.sailmaker(j));
        }
        getSaillist().addAll(sl);
       Collections.sort(getSaillist());

    }

    public void addracedaymatrix(int nraces)
    {
        racedaymatrix sailday = new racedaymatrix(getRaceclass(), "01/"+ (racedaymatrixlist.size()+1)+"/" + getCompyear(), nraces, getSaillist());
        racedaymatrixlist.add(sailday);
        sailday.filename = "raceday_"+getRaceclass()+"_"+sailday.getRacedate("-")+".xml";
        racedayfilenames.add( sailday.filename );
    }

    public void addracedaymatrices(int ndays, int nraces)
    {
        for (int d= 0;d<ndays;d++)
        {
            addracedaymatrix(nraces);
        }
    }

    public void generateranklist(int nrows)
    {
        getRanklist().clear();
        for(int r=1; r<nrows+1;r++)
        {
            getRanklist().add(" "+r);
        }
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
            bw.write("<competition class=\"" + raceclass + "\" year=\"" + compyear +
                    "\"  name= \""+competitionname + "\" racecount = \""+racecount + "\">\n");
            bw.write("<racedays>\n");
            for(int r=0; r<racedayfilenames.size();r++)
            {
                bw.write("<raceday filename=\""+racedayfilenames.get(r)+"\" />\n");
            }
            bw.write("</racedays>\n");
            bw.write("</competition>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public Vector<String> getSaillist()
    {
        return saillist;
    }

    public void setSaillist(Vector<String> saillist)
    {
        this.saillist = saillist;
    }

    public Vector<String> getRanklist()
    {
        return ranklist;
    }

    public void setRanklist(Vector<String> ranklist)
    {
        this.ranklist = ranklist;
    }
}