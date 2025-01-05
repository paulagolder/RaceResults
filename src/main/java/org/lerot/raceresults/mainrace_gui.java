package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;


import static org.lerot.raceresults.utils.parseaction;

public class mainrace_gui extends JFrame implements ActionListener
{
    public static final boolean COMPETITION = true;

    public static final boolean RACEDAY = false;
    public static boolean mode;
    public  static  mainrace_gui mframe;
    public static competition_gui compgui;
    public  jswVerticalPanel mainpanel;
    private String osversion;
    private String os;
    private String userdir;
    private String userhome;
    private String user;
    private String dotmysailing = "";
    public static String mysailinghome;
    private String mysailingexport;
    private String desktop;
    private String propsfile;


    private boolean editheader = false;

    private jswPanel resultpanel;
    racedaymatrix results;
    competition competition;


    private boolean activecell;

    public static void main(String[] args)
    {
        mode = COMPETITION;
        (mframe = new mainrace_gui(800, 400)).addWindowListener(new WindowAdapter()
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

        if (os.startsWith("Linux"))
        {
            System.out.println(" Linux identified ");
            dotmysailing = "/home/" + user + "/.sailing/";
            mysailinghome = "/home/" + user + "/Documents/sailing/";
            mysailingexport = "/home/" + user + "/Documents/sailing/export";
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
        mainpanel = new jswVerticalPanel("mainpanel", true, false);
        if (mode == RACEDAY)
        {
            mainpanel.add( " FILLH FILLW ", new raceday_gui(null));
        } else
        {
            compgui = new  competition_gui("competition_2024.xml");
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
        tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";

        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "gray");

        //  jswStyle rowstyle = tablestyles.makeStyle("row");
        //  rowstyle.putAttribute("height", 100);

        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontsize", 16);

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

        jswStyle cellstyle = tablestyles.makeStyle("cellx");
        cellstyle.putAttribute("foregroundcolor", "yellow");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 16);
        return tablestyles;
    }

    public static jswStyles smalltable1styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";

        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 1);
        tablestyle.putAttribute("bordercolor", "gray");

        jswStyle rowstyle0 = tablestyles.makeStyle("row_0");
        rowstyle0.putAttribute("backgroundcolor", "gray");
        rowstyle0.putAttribute("fontsize", 12);

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

        jswStyle cellstyle = tablestyles.makeStyle("cellx");
        cellstyle.putAttribute("foregroundcolor", "yellow");
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

        jswStyle cellstyle = tablestyles.makeStyle("cellx");
        cellstyle.putAttribute("foregroundcolor", "yellow");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute("fontsize", 10);

        return tablestyles;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {

    }
}
