package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Mainrace_gui extends JFrame implements ActionListener
{
    public static final int COMPETITION = 0;
    public static final int RACEDAY = 1;
    public static final int SAILLIST = 2;
    public static final int CLUBLIST = 3;
    static final int RACEDAYEDIT = 6;
    private static final int CLASSLIST = 4;
    private static final int START = 5;
    public int mode;
    public static Mainrace_gui mframe;
    public static Competition_gui compgui;
    public static jswVerticalPanel mainpanel;
    public static String mysailinghome;
    public static String currentcompetitionfile;
    public static String homeclubcypher;
    public static String homeclub;
    public static String homeclubname;
    public static String dotmysailing;
    static JMenuBar mainmenubar;
    final String clubsfile;
    public ClubList clublist;
    public String Boatclassfile;
    public BoatclassList classlist;
    public Competition currentcompetition;
    public SailList clubSailList = null;
    String saillistfile;
    int currentraceday;
    private String startimagefile = "startimage.png";
    private String osversion;
    private String os;
    private String userdir;
    private String userhome;
    private String user;
    private String mysailingexport;
    private String desktop;
    private String propsfile;
    private boolean editheader = false;
    private jswPanel resultpanel;
    private boolean activecell;
    private Club_gui clubgui;
    private Boatclass_gui boatclassgui;
    private Sail_gui sailgui;

    public Mainrace_gui(int w, int h)
    {
        osversion = System.getProperty("os.version");
        os = System.getProperty("os.name");
        userdir = System.getProperty("user.dir");
        userhome = System.getProperty("user.home");
        user = System.getProperty("user.name");
        currentcompetitionfile = null;
        if (os.startsWith("Linux"))
        {
            System.out.println(" Linux identified ");
            dotmysailing = "/home/" + user + "/.raceresults/";
            mysailinghome = "/home/" + user + "/Documents/raceresults/";
            mysailingexport = "/home/" + user + "/Documents/raceresults/export";
            desktop = "/home/" + user + "/Desktop/";
            propsfile = dotmysailing + "linux_properties.xml";
            startimagefile = dotmysailing + "startimage.png";
        } else if (os.startsWith("Windows"))
        {
            System.out.println(" Windows identified ");
            dotmysailing = userhome + "/.mysailing/";
            mysailinghome = userhome + "/Documents/mysailing/";
            mysailingexport = userhome + "/Documents/mysailing/export";
            desktop = "C:/Users/" + user + "/Desktop/";
            propsfile = dotmysailing + "windows_properties.xml";
        } else
        {
            System.out.println(" No  operating system identified  ");
            System.exit(0);
        }
        propsfile = dotmysailing + "properties.xml";
        clubsfile = dotmysailing + "clubs.xml";
        Boatclassfile = dotmysailing + "boatclasses.xml";
        Properties props = readProperties(propsfile);
        classlist = new BoatclassList();
        classlist.loadXML(Boatclassfile);
        clublist = new ClubList();
        clublist.loadXML(clubsfile);
        System.out.println("Clubs Loaded:" + clublist.toString());
        saillistfile = props.getProperty("saillist");
        currentcompetitionfile = props.getProperty("currentcompetition");
        System.out.println("Competition file:" + currentcompetitionfile);
        homeclub = props.getProperty("homeclub");
        homeclubname = props.getProperty("homeclubname");
        homeclubcypher = props.getProperty("homeclubcypher");
        clubSailList = new SailList();
        clubSailList.readSaillistXML(dotmysailing + saillistfile);
        File homefile = new File(mysailinghome);
        clubSailList.loadallsailfiles("saillist.xml", homefile);
        mainmenubar = new JMenuBar();
        mainpanel = new jswVerticalPanel("mainpanel", false, false);
        mainpanel.setBorder(jswStyle.makeLineBorder(Color.red, 4));
        //   currentcompetition = new Competition(currentcompetitionfile, clubSaiLlist);
        compgui = new Competition_gui();
        currentcompetition = null;
        currentraceday = 0;
        clubgui = new Club_gui(clublist);
        boatclassgui = new Boatclass_gui(classlist);
        sailgui = new Sail_gui(clubSailList);
        sailgui.setSelectedsail(clubSailList.first());
        mode = START;
        refreshGui();
        // add(mainpanel);
        //  mainpanel.repaint();
        revalidate();
        repaint();
        validate();
        pack();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args)
    {

        mframe = new Mainrace_gui(800, 400);
        mframe.mode = START;
        mframe.add(mainpanel);
        mframe.setJMenuBar(mainmenubar);
        mframe.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        mframe.setLocation(50, 50);
        mframe.setMinimumSize(new Dimension(800, 400));
        mframe.pack();
        mframe.setVisible(true);

    }

    public static jswStyles defaultStyles()
    {
        jswStyles defstyles = new jswStyles("default");
        jswStyle lstyle = defstyles.makeStyle("largebutton");
        lstyle.setFontsize(jswStyles.font_large);
        lstyle.setFontstyle(Font.BOLD);
        lstyle.setForegroundcolor("BLUE");
        lstyle.setBackgroundcolor("PINK");
        lstyle.setMyHeight(40);
        lstyle.setMyWidth(80);
        jswStyle ltext = defstyles.makeStyle("largetext");
        ltext.setFontsize(jswStyles.font_large);
        ltext.setFontstyle(Font.BOLD);
        ltext.putAttribute("borderwidth", "0");
        jswStyle mtext = defstyles.makeStyle("mediumtext");
        mtext.setFontsize(jswStyles.font_small);
        mtext.setFontstyle(Font.BOLD);
        mtext.putAttribute("borderwidth", "0");
        ltext.putAttribute("borderwidth", "0");
        jswStyle mstyle = defstyles.makeStyle("mediumbutton");
        mstyle.setFontsize(jswStyles.font_medium);
        mstyle.setFontstyle(Font.ITALIC);
        mstyle.setBackgroundcolor("yellow");
        mstyle.setMyHeight(30);
        mstyle.setMyWidth(60);
        jswStyle sstyle = defstyles.makeStyle("smallbutton");
        sstyle.setFontsize(jswStyles.font_small);
        sstyle.setBackgroundcolor("green");
        sstyle.setMyHeight(20);
        sstyle.setMyWidth(40);
        jswStyle h1style = defstyles.makeStyle("h1");
        h1style.putAttribute("foregroundColor", "black");
        h1style.putAttribute("fontSize", "18");
        h1style.putAttribute("borderwidth", "0");
        return defstyles;
    }

    public static jswStyles table1styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles.name = "defaulttable";
        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "gray");
        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontsize", 16);
        rowstyle0.putAttribute("fontStyle", Font.BOLD + Font.ITALIC);
        rowstyle0.putAttribute("foregroundColor", "#0e56f2");
        jswStyle colstyle0 = tablestyles.makeStyle("col_0");
        colstyle0.putAttribute("backgroundcolor", "blue");
        colstyle0.putAttribute("horizontalAlignment", "RIGHT");
        colstyle0.putAttribute("minwidth", 30);
        colstyle0.putAttribute("fontsize", "16");
        colstyle0.putAttribute("foregroundcolor", "gray");
        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("width", 30);
        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "black");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "black");
        cellstyle.putAttribute("fontsize", 16);
        return tablestyles;
    }

    public static jswStyles smalltable1styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles.name = "defaulttable";
        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "black");
        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontStyle", Font.BOLD + Font.ITALIC);
        rowstyle0.putAttribute("foregroundColor", "#0e56f2");
        rowstyle0.putAttribute("height", 40);
        jswStyle rowstyle = tablestyles.makeStyle("row");
        rowstyle.putAttribute("backgroundcolor", "gray");
        rowstyle.putAttribute("fontsize", 10);
        rowstyle.putAttribute("height", 20);
        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("width", 20);
        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "black");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 10);
        return tablestyles;
    }

    public static jswStyles smalltable2styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles.name = "defaulttable";
        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "black");
        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontStyle", Font.BOLD + Font.ITALIC);
        rowstyle0.putAttribute("foregroundColor", "#0e56f2");
        rowstyle0.putAttribute("height", 40);
        jswStyle rowstyle = tablestyles.makeStyle("row");
        rowstyle.putAttribute("backgroundcolor", "gray");
        rowstyle.putAttribute("fontsize", 10);
        rowstyle.putAttribute("height", 20);
        jswStyle colstyle0 = tablestyles.makeStyle("col_0");
        colstyle0.putAttribute("backgroundcolor", "blue");
        colstyle0.putAttribute("horizontalAlignment", "RIGHT");
        colstyle0.putAttribute("width", 40);
        colstyle0.putAttribute("fontsize", "10");
        colstyle0.putAttribute("fontstyle", Font.BOLD + Font.ITALIC);
        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("width", 20);
        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "black");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 10);
        return tablestyles;
    }

    public static jswStyles smalltable3styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";
        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "gray");
        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontsize", 10);
        jswStyle rowstyle = tablestyles.makeStyle("row");
        rowstyle.putAttribute("backgroundcolor", "gray");
        rowstyle.putAttribute("fontsize", 10);
        rowstyle.putAttribute("minheight", 40);
        jswStyle colstyle0 = tablestyles.makeStyle("col_0");
        colstyle0.putAttribute("backgroundcolor", "blue");
        colstyle0.putAttribute("horizontalAlignment", "MIDDLE");
        colstyle0.putAttribute("width", 40);
        colstyle0.putAttribute("fontsize", "12");
        colstyle0.putAttribute("foregroundcolor", "gray");
        colstyle0.putAttribute("padding", 4);
        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("minwidth", 40);
        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "black");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 10);
        return tablestyles;
    }

    private JMenuBar mainmenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("VIEW");
        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem("View Competition");
        menuItem1.addActionListener(this);
        menu.add(menuItem1);
        JMenuItem menuItem2 = new JMenuItem("View Raceday");
        menuItem2.addActionListener(this);
        menu.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem("View Sailors");
        menuItem3.addActionListener(this);
        menu.add(menuItem3);
        JMenuItem menuItem4 = new JMenuItem("View Clubs");
        menuItem4.addActionListener(this);
        menu.add(menuItem4);
        JMenuItem menuItem5 = new JMenuItem("View Boatclasses");
        menuItem5.addActionListener(this);
        menu.add(menuItem5);
        return menuBar;
    }

    private JMenuBar startmenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("OPEN");
        menuBar.add(menu);
        JMenuItem menuItem0 = new JMenuItem("Load Competition");
        menuItem0.addActionListener(compgui);
        menu.add(menuItem0);
        JMenuItem menuItem1 = new JMenuItem("New Competition");
        menuItem1.addActionListener(compgui);
        menu.add(menuItem1);
        return menuBar;
    }



    public SailList getClubSailList()
    {
        return clubSailList;
    }

    public void refreshGui()
    {
        mainmenubar.removeAll();
        mainpanel.removeAll();
        if (mode == START)
        {
            mainmenubar.add(startmenu());
            mainpanel.add(startwindow());
        } else if (mode == RACEDAY)
        {
            mainmenubar.add(mainmenu());
            Raceday araceday = currentcompetition.getracedaymatrix(currentraceday);

            araceday.competition = currentcompetition;
            Raceday_gui rd = new Raceday_gui(araceday);
            rd.editheader = false;
            mainpanel.add(" FILLH FILLW ", rd);
        } else if (mode == RACEDAYEDIT)
        {
            mainmenubar.add(mainmenu());
            Raceday araceday = currentcompetition.getracedaymatrix(currentraceday);
            araceday.competition = currentcompetition;
            Raceday_gui rd = new Raceday_gui(araceday);
            rd.editheader = true;
            mainpanel.add(" FILLH FILLW ", rd);
        } else if (mode == COMPETITION)
        {
            mainmenubar.add(mainmenu());
            mainpanel.removeAll();
            compgui = new Competition_gui(currentcompetition);
            mainpanel.add(" FILLH FILLW ", compgui);
        } else if (mode == CLASSLIST)
        {
            mainmenubar.add(mainmenu());
            mainpanel.add(" FILLH FILLW ", boatclassgui.makeBoatclassgui());
        } else if (mode == CLUBLIST)
        {
            mainmenubar.add(mainmenu());
            mainpanel.add(" FILLH FILLW ", clubgui.makeClubgui());
        } else if (mode == SAILLIST)
        {
            mainmenubar.add(mainmenu());
            mainpanel.add(" FILLH FILLW ", sailgui.makeSailgui());
        }
        mainpanel.repaint();
        revalidate();
    }

    private jswVerticalPanel startwindow()
    {
        jswVerticalPanel startpanel = new jswVerticalPanel("startpanel", false, false);
        jswImage startimage = new jswImage();
        startimage.importfile(startimagefile);
        jswLabel label = startimage.DisplayImage();
        startpanel.add("FILLW FILLH ", label);
        return startpanel;
    }

    public Properties readProperties(String propsfile)
    {
        Properties prop = new Properties();
        try
        {
            prop.loadFromXML(new FileInputStream(propsfile));
            return prop;
        } catch (InvalidPropertiesFormatException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void saveProperties(String compfile)
    {
        try
        {
            File file = new File(propsfile);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
            bw.write("<properties>\n");
            bw.write("<comment>raceresults  properties</comment>\n");
            bw.write("<entry key=\"currentcompetition\">" + compfile + "</entry>\n");
            bw.write("<entry key=\"saillist\">RMBC_saillist.xml</entry>\n");
            bw.write("<entry key=\"homeclub\">" + homeclub + "</entry>\n");
            bw.write("<entry key=\"homeclubname\">" + homeclubname + "</entry>\n");
            bw.write("<entry key=\"homeclubcypher\">" + homeclubcypher + "</entry>\n");
            bw.write("</properties>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
            refreshGui();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        if (command.equalsIgnoreCase("View Competition"))
        {
            mode = COMPETITION;
        }
        if (command.equalsIgnoreCase("View Raceday"))
        {
            mode = RACEDAY;
        }
        if (command.startsWith("View Clubs"))
        {
            mode = CLUBLIST;
        }
        if (command.startsWith("View Boatclasses"))
        {
            mode = CLASSLIST;
        }
        if (command.startsWith("View Sailors"))
        {
            File homefile = new File(mysailinghome);
            clubSailList.loadallsailfiles("saillist.xml", homefile);
            mode = SAILLIST;
        }
        refreshGui();
        Mainrace_gui.mframe.revalidate();
        Mainrace_gui.mframe.repaint();
        Mainrace_gui.mframe.pack();
    }


}
