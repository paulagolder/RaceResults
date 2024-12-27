package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import static org.lerot.raceresults.mainrace_gui.*;
import static org.lerot.raceresults.utils.parseaction;

public class competition_gui extends jswVerticalPanel implements ActionListener
{

    private final jswHorizontalPanel racedayspanel;
    private final jswHorizontalPanel racedayheader;
    private competition currentcomp;

    public competition_gui(String name)
    {
        super("mainpanel", true, false);
        currentcomp = new competition();
        currentcomp.loadCompetition("competition_2024.xml");
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        add(" FILLW   minheight=90 ", makeheadermenu());
        racedayheader = new jswHorizontalPanel("header", false, false);
        racedayheader.add(" FILLW ", displayheader());
        add(" FILLW ", racedayheader);
        racedayspanel = displayraces();
        add(" FILLH ", racedayspanel);
        revalidate();
        repaint();
    }

    private jswHorizontalPanel displayraces()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("races", false, false);
        racespanel.add(" FILLH ", currentcomp.displayrankcolumn( smalltable1styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            racedaymatrix raceday = currentcomp.getracedaymatrix(i);
            racespanel.add(" FILLH ", raceday.displayraceresults(this, smalltable1styles(), i));
        }
        return racespanel;
    }

    private jswHorizontalPanel displayscores()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("scores", false, false);
        racespanel.add(" FILLH ", currentcomp.displaysailcolumn( smalltable1styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            racedaymatrix raceday = currentcomp.getracedaymatrix(i);
            racespanel.add(" FILLH ", raceday.displayscoreresults(this, smalltable1styles(), i));
        }
        return racespanel;
    }

    Vector<String> getRankVector()
    {
        return currentcomp.ranklist;
    }


    Vector<String> getSailVector()
    {
        return currentcomp.saillist;
    }

    private jswVerticalPanel makeheadermenu()
    {
        jswVerticalPanel compheader = new jswVerticalPanel("compheader", true, false);
        jswHorizontalPanel panel1 = new jswHorizontalPanel("Panel1", true, false);
        jswLabel title = new jswLabel("Competition Results");
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        panel1.setStyleAttribute("verticalalignstyle", jswLayout.MIDDLE);
        title.applyStyle(defaultStyles().getStyle("h1"));
        panel1.add(" width=200 MIDDLE  ", title);
        jswLabel date = new jswLabel(" competitionname");
        panel1.add("  RIGHT ", date);
        add(" height=400  FILLW  ", panel1);
        jswHorizontalPanel menupanel = new jswHorizontalPanel("menus", false, false);
        jswButton readcompetition = new jswButton(this, "Edit Raceday", "editraceday");
        menupanel.add(" middle ", readcompetition);
        jswButton savecompetition = new jswButton(this, "Save As XML", "savecompetition");
        menupanel.add("  ", savecompetition);
        jswButton loadcompetition = new jswButton(this, "Load XML", "loadcompetition");
        menupanel.add("  ", loadcompetition);
        jswButton newcompetition = new jswButton(this, "New", "newcompetition");
        menupanel.add("  ", newcompetition);
        jswButton competitionhtml = new jswButton(this, "To HTML", "competitionhtml");
        menupanel.add("  ", competitionhtml);
        jswButton makescore = new jswButton(this, "Make Score", "makescore");
        menupanel.add("  ", makescore);
        add(" height=400  FILLW  ", menupanel);
        return compheader;
    }

    public jswHorizontalPanel displayheader()
    {
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("RaceHeader", true, false);
        jswLabel label00 = new jswLabel(" Class ");
        label00.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
        racedayheader.add(" left ", label00);
        jswLabel addb = new jswLabel(currentcomp.getRaceclass());
        addb.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", addb);
        jswLabel label01 = new jswLabel(" Year ");
        label01.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
        racedayheader.add(" middle ", label01);
        jswLabel dt = new jswLabel(currentcomp.getCompyear());
        dt.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", dt);
        jswButton editheader = new jswButton(this, "EDIT", "editheader");
        racedayheader.add(" right ", editheader);
        racedayheader.applyStyle();
        return racedayheader;
    }


    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        System.out.println(" here we are x " + cmd);
        HashMap<String, String> cmdmap = parseaction(cmd);
        String command = cmdmap.get("command").toLowerCase();
        if (command.startsWith("editraceday:"))
        {
            String seq = command.replace("editraceday:", "");
            int intseq = Integer.parseInt(seq);
            String filename = currentcomp.racedayfilenames.get(intseq);
            String path = utils.fileexists(filename);
            if (path == null) return;
            mainrace_gui.mode = mainrace_gui.RACEDAY;
            mainrace_gui.mframe.mainpanel.removeAll();
            racedaymatrix aracedaymatrix = new racedaymatrix();
            aracedaymatrix.readXML(path);
            mainrace_gui.mframe.mainpanel.add(" ", new raceday_gui(aracedaymatrix));
            mainrace_gui.mframe.mainpanel.repaint();
        }
        if (command.equalsIgnoreCase("To HTML"))
        {

        }
        if (command.equalsIgnoreCase("makescore"))
        {
            SortedSet<String> boatlist = new TreeSet<String>();
            for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
            {
                racedaymatrix aracedaymatrix = currentcomp.getRacedayNo(i);
                Vector<String> sailnumberlist = aracedaymatrix.getValuevector();
                for (String asail : sailnumberlist)
                {
                    boatlist.add(asail);
                }
            }
            int ns = boatlist.size();
            int matrix2[][] = new int[ns][ns];
            for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
            {
                racedaymatrix aracedaymatrix = currentcomp.getRacedayNo(i);
                for (int r = 0; r < aracedaymatrix.getNrows(); r++)
                {
                    for (int c = 0; c < aracedaymatrix.getNcols(); c++)
                    {
                        String value = aracedaymatrix.compmatrix.getValue(c, r);
                    }
                }
                //aracedaymatrix.getValue()
            }
        } else if (command.equalsIgnoreCase("loadcompetition"))
        {
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " + selfile);
                currentcomp = new competition();
                currentcomp.loadCompetition(selfile);
                racedayheader.removeAll();
                racedayheader.add(" FILLW ", displayheader());
                racedayspanel.removeAll();
              //  racedayspanel.add("FILLW FILLH ", displayraces());
                racedayspanel.add("FILLW FILLH ", displayscores());
                revalidate();
            }
        }
        mainrace_gui.mframe.revalidate();
        mainrace_gui.mframe.repaint();
        mainrace_gui.mframe.pack();
    }





}
