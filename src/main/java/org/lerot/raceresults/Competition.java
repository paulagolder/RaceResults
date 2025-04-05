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

import static org.lerot.raceresults.Mainrace_gui.*;
import static org.lerot.raceresults.utils.fileexists;

public class Competition
{
    public int noCompetitors;
    //public ClubList compclublist = new ClubList();;
    String competitionfile;
    Vector<String> racedayfilenames;
    SailList allSails = new SailList();
    SailList competitors = new SailList();
    private String compyear;
    private String raceclasses;
    private String competitionname;
    private int racecount;
    private Vector<Raceday> racedaymatrixlist;
    private Vector<String> ranklist = new Vector<String>();
    private Vector<String> compclublist = new Vector<String>();
    private String csscompetition = "competition";
    private String clubString;

    public Competition()
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaymatrixlist = new Vector<Raceday>();
        compclublist = new Vector<String>();
    }

    public Competition(String compfile, SailList allsailslist)
    {
        competitionname = "not loaded yet";
        racedayfilenames = new Vector<String>();
        racedaymatrixlist = new Vector<Raceday>();
        competitionfile = compfile;
        compclublist = new Vector<String>();
        allSails = allsailslist;
        System.out.println("Loading Competition:"+compfile);
        String cfile = utils.fileexists(compfile);
        if( cfile==null)
        {
            System.out.println("Not found comapetion file :" + compfile);
            System.out.println("exiting...");
            System.exit(0);
        }
        loadCompetition(cfile);
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

    public int getRacecount()
    {
        return racecount;
    }

    public void setRacecount(int racecount)
    {
        this.racecount = racecount;
    }

    public void loadCompetition(String path)
    {
        readCompetitionXML(path);
        if (compclublist.size() == 0)
        {
            System.out.println("***"+ homeclub);
            Club aclub = new Club(homeclub, Mainrace_gui.homeclubname, Mainrace_gui.homeclubcypher);
            compclublist.add(homeclub) ;
        }
        System.out.println(" **** " + this.toString());
        reloadracedays();
    }

    public void reloadracedays()
    {
        racedaymatrixlist.clear();
        for (int i = 0; i < racedayfilenames.size(); i++)
        {
            try
            {
                Raceday raceday = new Raceday(this);
                String filename = racedayfilenames.get(i);
                String file = fileexists(filename);
                if (file != null)
                {
                    raceday.readXML(file);
                    raceday.setfilename(filename);
                    racedaymatrixlist.add(raceday);
                }
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        competitors = makecompetitorslist();
        setRanklist(makerankvector());
        noCompetitors = competitors.size();
    }

    public jswVerticalPanel displayrankcolumn(jswStyles tablestyles)
    {
        jswVerticalPanel leftcolumn = new jswVerticalPanel("Rank", false, false);
        leftcolumn.setStyleAttribute("borderwidth", 2);
        leftcolumn.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel leftheader = new jswHorizontalPanel("heading", false, false);
        jswLabel label00 = new jswLabel(" Rank ");
        label00.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        leftheader.add(" minheight=30 ", label00);
        leftcolumn.add(" FILLW middle minheight=30 ", leftheader);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("Rank"), 0, 0);
        int nrows = getRanklist().size();
        for (int r = 0; r < nrows; r++)
        {
            datagrid.addCell(new jswLabel(getRanklist().get(r)), r + 1, 0);
        }
        leftcolumn.add(" FILLW ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
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
        for (Sail asail : competitors)
        {
            datagrid.addCell(new jswLabel(asail.getSailnumberStr()), r + 1, 0);
            r++;
        }
        leftcolumn.add(" FILLW middle ", datagrid);
        leftcolumn.setPadding(5, 5, 5, 5);
        return leftcolumn;
    }

    public jswPanel displayresults(jswStyles jswStyles)
    {
        return (new jswVerticalPanel("test", true, true));
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
            raceclasses = rootele.getAttributeNode("class").getValue();
            competitionname = rootele.getAttributeNode("name").getValue();
            racecount = Integer.parseInt(rootele.getAttributeNode("racecount").getValue());
            if(rootele.getAttributeNode("clubs") != null)
            {
                setClubString( rootele.getAttributeNode("clubs").getValue());
            }
            else
            clubString = homeclub;
            if(rootele.getAttributeNode("classes") != null)
            {
                setRaceclasses( rootele.getAttributeNode("classes").getValue());
            }
            else
               raceclasses= "DF95";
            NodeList cells = rootele.getChildNodes();
            System.out.println(cells.getLength());
            NodeList racedaysnl = rootele.getElementsByTagName("raceday");
            for (int i = 0; i < racedaysnl.getLength(); i++)
            {
                Element acell = (Element) racedaysnl.item(i);
                String cname = acell.getAttribute("filename");
                cname = cname.replace(dotmysailing, "");
                cname = cname.replace(mysailinghome, "");
                racedayfilenames.add(cname);
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

    public Vector<Raceday> getRacedaymatrixlist()
    {
        return racedaymatrixlist;
    }

    public void setRacedaymatrixlist(Vector<Raceday> racedaymatrixlist)
    {
        this.racedaymatrixlist = racedaymatrixlist;
    }

    public Raceday getRacedayNo(int i)
    {
        return racedaymatrixlist.get(i);
    }

    private SailList makecompetitorslist()
    {
        SortedSet<SailNumber> nvector = new TreeSet<SailNumber>();
        for (int i = 0; i < racedaymatrixlist.size(); i++)
        {
            Raceday araceday = racedaymatrixlist.get(i);
            TreeSet<String> values = araceday.getRacematrix().getValueSet();
            for (String value : values)
            {
                nvector.add(new SailNumber(value));
            }
        }
        SailList svector = new SailList();
        for (SailNumber value : nvector)
        {
            Sail asail = allSails.getSail(value.sailnumber);
            if (asail != null)
                svector.add(asail);
        }
        System.out.println(" comp vector:" + svector);
        {
           System.out.println( svector.totext());
        }
        return svector;
    }

    private Vector<String> makerankvector()
    {
        SortedSet<String> nvector = new TreeSet<String>();
        for (int i = 0; i < racedaymatrixlist.size(); i++)
        {
            Raceday araceday = racedaymatrixlist.get(i);
            Vector<String> values = araceday.getRankvector();
            nvector.addAll(values);
        }
        Vector<String> avector = new Vector<String>();
        avector.addAll(nvector);
        System.out.println(nvector);
        return avector;
    }

    public Raceday getracedaymatrix(int i)
    {
        return racedaymatrixlist.get(i);
    }

    public void generaterandomsailnumbers(int n)
    {
        Set sl = new HashSet<String>();
        Random rn = new Random();
        competitors.clear();
        while (sl.size() < n + 1)
        {
            int j = rn.nextInt(90) + 10;
            sl.add(utils.sailmaker(j));
        }
        competitors.addAll(sl);
    }

    public void addEmptyRacedayMatrix(int nraces, int nsailors)
    {
        Raceday sailday = new Raceday(this, getRaceclasses(), "01/" + (racedaymatrixlist.size() + 1) + "/" + getCompyear(), nraces, nsailors);
        racedaymatrixlist.add(sailday);
        sailday.filename = "raceday_" + getRaceclasses() + "_" + sailday.getRacedate("-") + ".xml";
        racedayfilenames.add(sailday.filename);
    }

    public void generateranklist(int nrows)
    {
        getRanklist().clear();
        for (int r = 1; r < nrows + 1; r++)
        {
            getRanklist().add(" " + r);
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
            bw.write("<competition class=\"" + raceclasses + "\" year=\"" + compyear +
                    "\"  name= \"" + competitionname + "\" racecount = \"" + racecount +
                    "\" clubs = \"" + clubString + "\"" +
                    "\" classes = \"" + raceclasses + "\">\n");
            bw.write("<racedays>\n");
            for (int r = 0; r < racedayfilenames.size(); r++)
            {
                bw.write("<raceday filename=\"" + racedayfilenames.get(r) + "\" />\n");
            }
            bw.write("</racedays>\n");
            bw.write(competitors.toXML(clubString,raceclasses));
            bw.write("</competition>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public Vector<String> getRanklist()
    {
        return ranklist;
    }

    public void setRanklist(Vector<String> ranklist)
    {
        this.ranklist = ranklist;
    }

    public void addRaceday(String filename)
    {
        racedayfilenames.add(filename);
    }

    public void printRacedaysToHTML(String selfile, String competitionname)
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
            bw.write("<h1> Clubs :" + clubString + " </h1>\n");
            for (int i = 0; i < racedaymatrixlist.size(); i++)
            {
                Raceday racematrix = racedaymatrixlist.get(i);
                racematrix.printToHTML(bw, racematrix.getRacedate_str());
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
        Vector<Sail> aboatlist = Mainrace_gui.mframe.getClubSaiLlist().getSailVector();
        Vector<String> sb = new Vector<>();
        SailList sl = new SailList();
        for (int s = 0; s < aboatlist.size(); s++)
        {
            Sail asail = aboatlist.get(s);
            if (raceclasses.contains(asail.getBoatclass()))
            {
                sl.add(asail);
            }
        }
        competitors = sl;
    }

    public void replacefilename(String oldfile, String newfile)
    {
        int fn = racedayfilenames.indexOf(oldfile);
        String nfile = newfile.replace(mysailinghome, "");
        nfile = nfile.replace(dotmysailing, "");
        racedayfilenames.set(fn, nfile);
    }

    public void saveCompetition()
    {
        System.out.println("You chose to save this competition file: " + competitionfile);
        this.printfileToXML(competitionfile);
        if(competitionfile != null)
            Mainrace_gui.mframe.saveProperties(competitionfile);
    }

    public Vector<String> getCompclublist()
    {
        return compclublist;
    }

    public void setCompclublist(Vector<String> compclublist)
    {
        this.compclublist = compclublist;
    }

    public String toString()
    {
        return this.competitionname + " " + this.getCompyear() + " " + this.getRaceclasses() + " " + this.getRacecount() + " " + clubString;
    }

    public void makeCompetitorsList()
    {

    }

    public void setClubString(String text)
    {
        String[] clubs = text.toUpperCase().split(",");
        String clubfield = utils.makeList(clubs);
        this.clubString = clubfield;
    }

    public String getClubString()
    {
        return clubString;
    }

    public String getRaceclasses()
    {
        return raceclasses;
    }

    public void setRaceclasses(String text)
    {
        String[] classes = text.toUpperCase().split(",");
        String classfield = utils.makeList(classes);
        this.clubString = classfield;
    }
}