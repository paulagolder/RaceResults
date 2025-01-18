package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Vector;

public class mainrace_gui extends JFrame implements ActionListener
{
    public static final boolean COMPETITION = true;
    public static final boolean RACEDAY = false;
    public static boolean mode;
    public  static  mainrace_gui mframe;
    public static competition_gui compgui;
    public static jswVerticalPanel mainpanel;
    public Vector<sail> getBoatlist()
    {
        return boatlist;
    }
    private final Vector<sail> boatlist;
    private String osversion;
    private String os;
    private String userdir;
    private String userhome;
    private String user;
    private String dotmysailing = "";
    public static String mysailinghome;
    public static String currentcompetitionfile;
    private String mysailingexport;
    private String desktop;
    private String propsfile;
    private boolean editheader = false;
    private jswPanel resultpanel;
   // racedaymatrix results;
   // competition competition;
    private boolean activecell;
    String  saillistfile;

    public static void main(String[] args)
    {
        mode = COMPETITION;
        mframe = new mainrace_gui(800, 400);
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


    public mainrace_gui(int w, int h)
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
        Properties props = readProperties(propsfile);
        saillistfile = props.getProperty("saillist");
        currentcompetitionfile = props.getProperty("currentcompetition");
        boatlist = sail.readSaillistXML(dotmysailing + saillistfile);

        mainpanel = new jswVerticalPanel("mainpanel", true, false);
        if (mode == RACEDAY)
        {
            mainpanel.add( " FILLH FILLW ", new raceday_gui(null));
        } else
        {
            compgui = new  competition_gui(currentcompetitionfile);
            mainpanel.add( " FILLH FILLW ", compgui);
        }
        add(mainpanel);
        mainpanel.repaint();
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


    public java.util.Properties readProperties(String propsfile)
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


    public void saveProperties()
    {
        try
        {
            File file = new File("xx"+propsfile);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write(    "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
            bw.write( "<properties>\n");
            bw.write( "    <comment>raceresults  properties</comment>\n");
            bw.write( "<entry key=\"saillist\">RMBC_sailnumberlist.xml</entry>\n");
            bw.write(     "<entry key=\"currentcompetition\">"+ currentcompetitionfile+"</entry>\n");
            bw.write( "   </properties>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
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
       // tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";

        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "gray");

        //  jswStyle rowstyle = tablestyles.makeStyle("row");
        //  rowstyle.putAttribute("height", 100);

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
        // colstyle.putAttribute("foregroundcolor", "yellow");

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
       // tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";

        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "black");

        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontStyle", Font.BOLD + Font.ITALIC);
        rowstyle0.putAttribute("foregroundColor", "#0e56f2");

        jswStyle rowstyle = tablestyles.makeStyle("row");
        rowstyle.putAttribute("backgroundcolor", "gray");
        rowstyle.putAttribute("fontsize", 10);
        rowstyle.putAttribute("minheight", 40);
        //rowstyle.putAttribute("mywidth", 100);

        jswStyle colstyle0 = tablestyles.makeStyle("col_0");
        colstyle0.putAttribute("backgroundcolor", "blue");
        colstyle0.putAttribute("horizontalAlignment", "RIGHT");
        colstyle0.putAttribute("width", 40);
        colstyle0.putAttribute("fontsize", "12");
        colstyle0.putAttribute("foregroundcolor", "gray");

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
        colstyle0.putAttribute("padding",4);

        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("minwidth", 40);
        // colstyle.putAttribute("foregroundcolor", "yellow");

        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "black");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 10);

        return tablestyles;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {

    }

    public HashMap<String, String> makeList(String boatclass)
    {
        HashMap<String, String> list = new HashMap<String, String>();

        for(int b =0;b<boatlist.size();b++)
        {
            sail asail = boatlist.get(b);
            if(asail.getBoatclass().equalsIgnoreCase(boatclass))
            {
                list.put(asail.getSailnumber(),asail.getSailorname());
            }
        }
        return list;

    }
}
