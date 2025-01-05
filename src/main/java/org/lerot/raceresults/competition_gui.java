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

    private final jswVerticalPanel headermenu;
    private jswHorizontalPanel editpanel = null;
    private jswHorizontalPanel scorespanel;
    private competition currentcomp;
    private racedaymatrix scorematrix = null;
    private jswTextBox classfield;
    private jswTextBox yearfield;
    private jswTextBox countfield;
    private jswTextBox namefield;
    private jswHorizontalPanel compheader;


    public competition_gui(String name)
    {
        super("mainpanel", true, false);
        this.setTrace(true);
        currentcomp = new competition();
        currentcomp.loadCompetition("competition_DF99_2025.xml");
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        headermenu = makeheadermenu();
        add(" FILLW HEIGHT=200  ", headermenu);
        compheader = displayheader();
        add(" FILLW HEIGHT=200 ", compheader);
        compheader.applyStyle();
        scorespanel = displayscores();
        add(" FILLH FILLW ", scorespanel);
        editpanel = editcompetition();
       // editpanel.getStyle().printList();
        add(" FILLW HEIGHT=200 ", editpanel);
        editpanel.setVisible(false);
        revalidate();
        repaint();
    }

    public void refreshcompetition_gui()
    {
        jswHorizontalPanel compheader = new jswHorizontalPanel("header", false, false);
        compheader.add(" FILLW ", displayheader());
        add(" FILLW ", compheader);
        scorespanel = displayscores();
        add(" FILLH ", scorespanel);
        revalidate();
        repaint();
    }

    private jswHorizontalPanel displayraces()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("races", false, false);
        racespanel.add("  ", currentcomp.displayrankcolumn(smalltable2styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            racedaymatrix raceday = currentcomp.getracedaymatrix(i);
            racespanel.add(" minwidth=250 ", raceday.displayraceresults(this, smalltable1styles(), i));
        }
        racespanel.setStyleAttribute("backgroundcolor", "pink");
        racespanel.applyStyle();
        return racespanel;
    }

    private jswHorizontalPanel displayscores()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("scores", false, false);
        racespanel.add(" FILLW ", currentcomp.displaysailcolumn(smalltable2styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            racedaymatrix raceday = currentcomp.getracedaymatrix(i);
            racespanel.add(" FILLW ", raceday.displayscoreresults(this, smalltable1styles(), i));
        }
        if (scorematrix != null)
        {
            racespanel.add(" FILLW ", scorematrix.displayscoreresults(this, smalltable1styles(), 0));
        }
        return racespanel;
    }

    Vector<String> getRankVector()
    {
        return currentcomp.getRanklist();
    }

    Vector<String> getSailVector()
    {
        return currentcomp.getSaillist();
    }

    private jswVerticalPanel makeheadermenu()
    {
        jswVerticalPanel compheader = new jswVerticalPanel("compheader", true, false);
        jswHorizontalPanel panel1 = new jswHorizontalPanel("Title Panel", true, false);
        jswLabel title = new jswLabel("Competition Results");
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        panel1.setStyleAttribute("verticalalignstyle", jswLayout.MIDDLE);
        title.applyStyle(defaultStyles().getStyle("h1"));
        panel1.add(" width=200 MIDDLE  ", title);
        jswLabel name = new jswLabel(currentcomp.getCompetitionname());
        name.applyStyle(defaultStyles().getStyle("h1"));
        panel1.add("  RIGHT ", name);
        add(" height=100  FILLW  ", panel1);
        jswHorizontalPanel menupanel = new jswHorizontalPanel("menus", false, false);
        jswButton editheader = new jswButton(this, "EDIT COMPETITION", "editcompetition");
        menupanel.add(" middle ", editheader);
        jswButton savecompetition = new jswButton(this, "Save As XML", "savecompetition");
        menupanel.add(" middle ", savecompetition);
        jswButton loadcompetition = new jswButton(this, "Load XML", "loadcompetition");
        menupanel.add("  ", loadcompetition);
        jswButton newcompetition = new jswButton(this, "New", "newcompetition");
        menupanel.add("  ", newcompetition);
        jswButton competitionhtml = new jswButton(this, "To HTML", "competitionhtml");
        menupanel.add("  ", competitionhtml);
        jswButton makescore = new jswButton(this, "Make Score", "makescore");
        menupanel.add("  ", makescore);
        menupanel.applyStyle();
        compheader.applyStyle();
        add(" height=150  FILLW  ", menupanel);
        return compheader;
    }

    public jswHorizontalPanel displayheader()
    {
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("RaceHeader", true, false);
        jswLabel label00 = new jswLabel(" Class ");
        label00.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" left ", label00);
        jswLabel addb = new jswLabel(currentcomp.getRaceclass());
        addb.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", addb);
        jswLabel label01 = new jswLabel(" Year ");
        label01.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" middle ", label01);
        jswLabel dt = new jswLabel(currentcomp.getCompyear());
        dt.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", dt);
        jswLabel label03 = new jswLabel(" Contributing Races ");
        label03.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" right ", label03);
        jswLabel rc = new jswLabel(currentcomp.getRacecount());
        rc.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", rc);
        racedayheader.applyStyle();
        return racedayheader;
    }

    public jswHorizontalPanel displaysailheader()
    {
        jswHorizontalPanel sailheader = new jswHorizontalPanel("RaceHeader", true, false);
        jswLabel label00 = new jswLabel(" Sail Number ");
        label00.applyStyle(defaultStyles().getStyle("largetext"));
        sailheader.applyStyle();
        return sailheader;
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        System.out.println(" here we are x " + cmd);
        HashMap<String, String> cmdmap = parseaction(cmd);
        String command = cmdmap.get("command").toLowerCase();
        if (command.startsWith("editraceday:"))
        {
            mode = RACEDAY;
            String seq = command.replace("editraceday:", "");
            int intseq = Integer.parseInt(seq);
            mframe.mainpanel.removeAll();
            racedaymatrix aracedaymatrix = currentcomp.getracedaymatrix(intseq);
            mframe.mainpanel.add(" ", new raceday_gui(aracedaymatrix));
            mframe.mainpanel.repaint();
        }
        if (command.startsWith("editcompetition"))
        {
            //compheader.removeAll();
            compheader.setVisible(false);
           // scorespanel.removeAll();
            scorespanel.setVisible(false);
            //editpanel.removeAll();
           // editpanel.add( " height=200 ", editcompetition());
            editpanel.setVisible(true);
           // editpanel.getStyle().printList();
            editpanel.repaint();
            editpanel.validate();
            revalidate();
        }
        if (command.equalsIgnoreCase("To HTML"))
        {

        }
        if (command.equalsIgnoreCase("saveeditcompetition"))
        {
            currentcomp.setCompetitionname(namefield.getText());
            currentcomp.setCompyear(yearfield.getText());
            currentcomp.setRacecount(Integer.parseInt(countfield.getText()));
            currentcomp.setRaceclass(classfield.getText());
            command = "savecompetition";
        }
        if (command.equalsIgnoreCase("canceleditcompetition"))
        {

            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
            scorespanel.repaint();
            scorespanel.validate();
        }
        if (command.equalsIgnoreCase("savecompetition"))
        {
            String outfile = "competition_" + currentcomp.getRaceclass() + "_" + currentcomp.getCompyear() + ".xml";
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            //  JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this file: " + selfile);
                currentcomp.printfileToXML(selfile);
            }
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
            scorespanel.repaint();
            scorespanel.validate();

        }
        if (command.equalsIgnoreCase("makescore"))
        {
            SortedSet<String> boatlist;
            boatlist = new TreeSet<String>();
            boatlist.addAll(currentcomp.getSaillist());

            int ns = boatlist.size();
            int matrix2[][] = new int[ns + 1][ns + 1];
            for (int r = 0; r < getSailVector().size(); r++)
            {
                int totalscore = 0;
                for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
                {
                    racedaymatrix aracedaymatrix = currentcomp.getRacedayNo(i);
                    for (int c = 0; c < aracedaymatrix.getNcols(); c++)
                    {
                        String value = aracedaymatrix.compmatrix.getValue(c, r);
                        int iv;
                        try
                        {
                            iv = Integer.parseInt(value.trim());
                            matrix2[iv][r] = matrix2[iv][r] + 1;
                        } catch (NumberFormatException ex)
                        {
                            iv = 0;
                        }
                        totalscore += iv;
                    }
                }
                System.out.println(" Total for " + getSailVector().get(r) + " = " + totalscore);
            }
            scorematrix = new racedaymatrix();
            scorematrix.setBoatclass_str(currentcomp.getRaceclass());
            scorematrix.setRacedate_str(currentcomp.getCompyear());
            scorematrix.setCompRowname(currentcomp.getSaillist());
            Vector<String> collabels = new Vector<String>();
            collabels.add("Points");
            collabels.add("1st");
            collabels.add("2nd");
            collabels.add("3rd");
            for (int r = 4; r < currentcomp.getSaillist().size()+1; r++)
            {
                collabels.add("" + r + "th");
            }
            scorematrix.setCompColname(collabels);
            scorematrix.loadcomp(matrix2, currentcomp.getRacecount());
            compheader.removeAll();
            compheader.add(" FILLW ", displayheader());
            scorespanel.removeAll();
            scorespanel.add(" FILLW  FILLH ", displayscores());
        } else if (command.equalsIgnoreCase("loadcompetition"))
        {
            final File directorylock = new File(mysailinghome);
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
                compheader.removeAll();
                compheader.add(" FILLW ", displayheader());
                scorespanel.removeAll();
                scorespanel.add(" FILLW  FILLH ", displayraces());
                //   scorespanel.removeAll();
                //    scorespanel.add("FILLW ", displayscores());
                revalidate();
            }
        } else if (command.equalsIgnoreCase("newcompetition"))
        {
            currentcomp = new competition("demo competition", "DF99", "2025");
            currentcomp.generaterandomsailnumbers(10);
            currentcomp.generateranklist(10);
            currentcomp.addracedaymatrices(3, 4);
            compheader.removeAll();
            compheader.add(" FILLW ", displayheader());
            scorespanel.removeAll();
            scorespanel.add(" FILLW FILLH ", displayraces());
            //   scorespanel.removeAll();
            //    scorespanel.add("FILLW ", displayscores());
            revalidate();

        }
        mainrace_gui.mframe.revalidate();
        mainrace_gui.mframe.repaint();
        mainrace_gui.mframe.pack();
    }

    private jswHorizontalPanel editcompetition()
    {
        jswHorizontalPanel editpanel = new jswHorizontalPanel("Edit Competition", true, true);

        jswVerticalPanel subpanel = new jswVerticalPanel("Edit ", false, false);

        jswHorizontalPanel namepanel = new jswHorizontalPanel("name", false, false);
        jswLabel label04 = new jswLabel(" Name ");
        label04.applyStyle(defaultStyles().getStyle("largetext"));
        namepanel.add(" left ", label04);
        namefield = new jswTextBox(this, "name of competition");
        namefield.setText(currentcomp.getCompetitionname());
        namefield.setStyleAttribute("mywidth", 300);
        namefield.setStyleAttribute("myheight", 50);
        namefield.applyStyle();
        namepanel.add(" ", namefield);
        subpanel.add(" height=100", namepanel);

        jswHorizontalPanel classpanel = new jswHorizontalPanel("class", false, false);
        jswLabel label00 = new jswLabel(" Class ");
        label00.applyStyle(defaultStyles().getStyle("largetext"));
        classpanel.add(" left ", label00);
        classfield = new jswTextBox(this, "Race Class");
        classfield.setText(currentcomp.getRaceclass());
        classfield.setStyleAttribute("mywidth", 100);
        classfield.setStyleAttribute("myheight", 50);
        classfield.applyStyle();
        classpanel.add(" ", classfield);
        subpanel.add(" height=100 ", classpanel);

        jswHorizontalPanel yearpanel = new jswHorizontalPanel("year", false, false);
        jswLabel label01 = new jswLabel(" Year ");
        label01.applyStyle(defaultStyles().getStyle("largetext"));
        yearpanel.add(" left ", label01);
        yearfield = new jswTextBox(this, "Year");
        yearfield.setText(currentcomp.getCompyear());
        yearfield.setStyleAttribute("mywidth", 100);
        yearfield.setStyleAttribute("myheight", 50);
        yearfield.applyStyle();
        yearpanel.add(" ", yearfield);
        subpanel.add(" height=100 ", yearpanel);

        jswHorizontalPanel racecountpanel = new jswHorizontalPanel("contributing", false, false);
        jswLabel label02 = new jswLabel(" Contributing Races ");
        label02.applyStyle(defaultStyles().getStyle("largetext"));
        racecountpanel.add(" left ", label02);
        countfield = new jswTextBox(this, "Number of contributing Races");
        countfield.setText(currentcomp.getRacecount());
        countfield.setStyleAttribute("mywidth", 100);
        countfield.setStyleAttribute("myheight", 50);
        countfield.applyStyle();
        racecountpanel.add(" ", countfield);
        subpanel.add(" height=100 ", racecountpanel);
        jswHorizontalPanel buttonpanel = new jswHorizontalPanel("button", false, false);
        jswButton savebutton = new jswButton(this, "save", "SAVEEDITCOMPETITION");
        buttonpanel.add(" ", savebutton);
        jswButton cancelbutton = new jswButton(this, "cancel", "CANCELEDITCOMPETITION");
        buttonpanel.add(" ", cancelbutton);
        subpanel.add(" right ", buttonpanel);
        editpanel.add(" FILLW FILLH ", subpanel);
        editpanel.applyStyle();
        return editpanel;
    }


}
