package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.*;

public class Competition_gui extends jswVerticalPanel implements ActionListener
{
    public Competition currentcomp;
    jswHorizontalPanel editpanel = null;
    jswHorizontalPanel compheader;
    jswHorizontalPanel scorespanel;
    ranksmatrix pointsmatrix = null;
    private jswTextBox classfield;
    private jswTextBox yearfield;
    private jswTextBox countfield;
    private jswTextBox namefield;
    private int maxsailors;
    private jswTextBox clubslist;
    private jswTextBox compfield;
    private jswTextBox dirfield;

    public Competition_gui(Competition acurrentcomp)
    {
        super("mainpanel", true, false);

        currentcomp = acurrentcomp;
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        refreshcompetition_gui();
        compheader.setVisible(true);
        scorespanel.setVisible(true);
        revalidate();
        repaint();
    }

    public Competition_gui()
    {
        super("mainpanel", true, false);
        currentcomp = null;
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        revalidate();
        repaint();
    }

    public void refreshcompetition_gui()
    {
        makeheadermenu();
        removeAll();
        compheader = displayheader();
        add(" FILLW HEIGHT=200 ", compheader);
        compheader.applyStyle();
        scorespanel = displayscorepanels();
        add(" FILLH  FILLW ", scorespanel);
        editpanel = editcompetition();
        add(" FILLW HEIGHT=200 ", editpanel);
        compheader.setVisible(false);
        scorespanel.setVisible(false);
        editpanel.setVisible(false);
        revalidate();
        repaint();
    }


    private jswHorizontalPanel displayscorepanels()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("scores", false, false);
        currentcomp.makeCompetitorsList();
        racespanel.add(" WIDTH=50 ", currentcomp.displaysailcolumn(smalltable2styles()));
        if (currentcomp.getRacedaylist().size() > 0)
        {
            for (int i = 0; i < currentcomp.getRacedaylist().size(); i++)
            {
                Raceday raceday = currentcomp.getracedaymatrix(i);
                racespanel.add("  ", raceday.displayscores(this, smalltable1styles(), i));
            }
            if (pointsmatrix != null)
            {
                PositionList scoresorder = pointsmatrix.makePositionMap();
                racespanel.add(" FILLW  ", scoresorder.displayresults());
                racespanel.add(" FILLW ", pointsmatrix.displayresults(this, smalltable1styles(), maxsailors));
                int a = 2;
            }
        }
        return racespanel;
    }

    public void makeheadermenu()
    {
        if (mainmenubar.getMenuCount() > 2)
        {
            mainmenubar.remove(2);
        }
        if (mainmenubar.getMenuCount() > 1)
        {
            mainmenubar.remove(1);
        }

        JMenu fmenu = new JMenu("File");
        mainmenubar.add(fmenu);
        JMenuItem fmenuItem1 = new JMenuItem("Load Competition");
        fmenuItem1.addActionListener(this);
        fmenu.add(fmenuItem1);
        JMenuItem fmenuItem2 = new JMenuItem("Save Competition");
        fmenuItem2.addActionListener(this);
        fmenu.add(fmenuItem2);
        JMenuItem fmenuItem2b = new JMenuItem("Save Competition As");
        fmenuItem2b.addActionListener(this);
        fmenu.add(fmenuItem2b);
        JMenuItem fmenuItem3 = new JMenuItem("Export All Racedays to HTML");
        fmenuItem3.addActionListener(this);
        fmenu.add(fmenuItem3);
        if (pointsmatrix != null)
        {
            JMenuItem fmenuItem4 = new JMenuItem("Export Competition to HTML");
            fmenuItem4.addActionListener(this);
            fmenu.add(fmenuItem4);
            JMenuItem fmenuItem5 = new JMenuItem("Export Positions to HTML");
            fmenuItem5.addActionListener(this);
            fmenu.add(fmenuItem5);
            JMenuItem fmenuItem5a = new JMenuItem("Export League Table to HTML");
            fmenuItem5a.addActionListener(this);
            fmenu.add(fmenuItem5a);
            //   JMenuItem fmenuItem6 = new JMenuItem("Export Results to HTML");
            //  fmenuItem6.addActionListener(this);
            //   fmenu.add(fmenuItem6);

        }
        JMenuItem fmenuItem7 = new JMenuItem("Create Result Sheet");
        fmenuItem7.addActionListener(this);
        fmenu.add(fmenuItem7);
        JMenu amenu = new JMenu("Action");
        mainmenubar.add(amenu);
        JMenuItem amenuItem0 = new JMenuItem("Edit Competition");
        amenuItem0.addActionListener(this);
        amenu.add(amenuItem0);
        JMenuItem amenuItem1 = new JMenuItem("Make Scores");
        amenuItem1.addActionListener(this);
        amenu.add(amenuItem1);
        JMenuItem amenuItem2 = new JMenuItem("Add New Raceday");
        amenuItem2.addActionListener(this);
        amenu.add(amenuItem2);
        JMenuItem amenuItem3 = new JMenuItem("Add Random Raceday");
        amenuItem3.addActionListener(this);
        amenu.add(amenuItem3);
        JMenuItem amenuItem4 = new JMenuItem("Load Raceday");
        amenuItem4.addActionListener(this);
        amenu.add(amenuItem4);
        JMenuItem amenuItem5 = new JMenuItem("New Competition");
        amenuItem5.addActionListener(this);
        amenu.add(amenuItem5);
    }

    public jswHorizontalPanel displayheader()
    {
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("RaceHeader", true, false);
        if(!currentcomp.saved)
            racedayheader.setStyleAttribute("backgroundcolor", "pink");
        jswLabel addcn = new jswLabel(currentcomp.getCompetitionname());
        addcn.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", addcn);
        jswLabel label00 = new jswLabel(" Class ");
        label00.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" left ", label00);
        jswLabel addb = new jswLabel(currentcomp.compclasslist.toString());
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
        jswLabel rc = new jswLabel(currentcomp.getTargetracecount());
        rc.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", rc);
        racedayheader.applyStyle();
        return racedayheader;
    }

    public void actionPerformed(ActionEvent e)
    {
        mframe.mode = COMPETITION;
        String cmd = e.getActionCommand();
        HashMap<String, String> cmdmap = jswUtils.parsecsvstring(cmd);
        String cduc = cmdmap.get("command");
        String command = cmd;

        if (command.startsWith("editraceday:"))
        {
            mframe.mode = RACEDAY;
            String seq = command.replace("editraceday:", "");
            int intseq = Integer.parseInt(seq);
            mframe.currentraceday = intseq;
            mframe.refreshGui();
        }
        if (command.startsWith("Edit Competition"))
        {
            refreshcompetition_gui();
            if (mainmenubar.getMenuCount() > 2)
            {
                mainmenubar.remove(2);
            }
            if (mainmenubar.getMenuCount() > 1)
            {
                mainmenubar.remove(1);
            }
            compheader.setVisible(false);
            scorespanel.setVisible(false);
            editpanel.setVisible(true);
        }
        if (command.equalsIgnoreCase("saveeditcompetition"))
        {
            currentcomp.setCompetitionname(namefield.getText());
            currentcomp.setCompyear(yearfield.getText());
            currentcomp.setTargetracecount(Integer.parseInt(countfield.getText()));
            currentcomp.setRaceclasses(classfield.getText());
            currentcomp.setRaceclubs(clubslist.getText());
            currentcomp.compdir = compfield.getText();
            command = "Save Competition";
        }
        if (command.equalsIgnoreCase("canceleditcompetition"))
        {
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Export All Racedays to HTML"))
        {
            String outfile = "RACEDAY_RESULTS_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(currentcomp.outputdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                currentcomp.printRacedaysToHTML(selfile);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Create Result Sheet"))
        {
            String outfile = "Result_Sheet_" + currentcomp.getRaceclasses() + ".html";
            final File directorylock = new File(Mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                currentcomp.printResultSheetToHTML(selfile, currentcomp.getCompetitionname());
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Export Competition to HTML"))
        {
            String outfile = "COMPETITION_RESULTS_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(compgui.currentcomp.outputdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                TreeMap<String, Sail> sailorlist = mframe.getClubSailList().makeTreeList(currentcomp.compclasslist.toString(), currentcomp.getClubString());
                pointsmatrix.printResultsToHTML(selfile, currentcomp.getCompetitionname(), sailorlist, maxsailors);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Export Positions to HTML"))
        {
            String outfile = "POINTS_RESULTS_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
            //  final File directorylock = new File(Mainrace_gui.mysailinghome);
            final File directorylock = new File(compgui.currentcomp.outputdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                PositionList map = pointsmatrix.makePositionMap();
                PositionList map2 = map.rankMap();
                String defclass;
                if (currentcomp.compclasslist.size() > 1) defclass = "**";
                else defclass = currentcomp.compclasslist.getDefaulKey();
                String defclub;
                if (currentcomp.compclublist.size() > 1) defclub = "**";
                else defclub = currentcomp.compclublist.firstKey();
                map2.printResultsToHTML(selfile, defclass, defclub);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Export League Table to HTML"))
        {
            String outfile = "LEAGUE_TABLE_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(compgui.currentcomp.outputdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                PositionList map = pointsmatrix.makePositionMap();
                PositionList map2 = map.rankMap();
                String defclass;
                if (currentcomp.compclasslist.size() > 1) defclass = "**";
                else defclass = currentcomp.compclasslist.getDefaulKey();
                String defclub;
                if (currentcomp.compclublist.size() > 1) defclub = "**";
                else defclub = currentcomp.compclublist.firstKey();
                //   map2.printResultsToHTML(selfile, defclass,defclub);
                map2.printLeagueTableToHTML(selfile, defclass, defclub);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Save Competition"))
        {
            String selfile = currentcomp.competitionfile;
            System.out.println("You chose to save to this competition xml file: " + selfile);
            currentcomp.saveCompetitionToXML(selfile);
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Save Competition As"))
        {
            String outfile = "competition_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".xml";
            final File directorylock = new File(Mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Competitions", "cxml");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this competition xml file: " + selfile);
                currentcomp.saveCompetitionToXML(selfile);
                Mainrace_gui.mframe.saveProperties(selfile);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Make Scores"))
        {
            int maxsailors = currentcomp.noCompetitors;
            System.out.println("Max sailors :" + maxsailors);
            pointsmatrix = new ranksmatrix(currentcomp);
            //makeheadermenu();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("Load Competition"))
        {
            mframe.mode = COMPETITION;
            final File directorylock = new File(mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Competitions", "cxml");
            chooser.setFileFilter(filter);
            String outfile = mysailinghome + currentcompetitionfile;
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("*You chose to open this file: " + selfile);
                pointsmatrix = null;
                currentcomp = new Competition(selfile, mframe.clubSailList);
                currentcompetitionfile = selfile;
                mframe.currentcompetition = currentcomp;
            }
            currentcomp.reloadracedays();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
            mframe.refreshGui();
        } else if (command.equalsIgnoreCase("New Competition"))
        {
            currentcomp = new Competition();
            currentcomp.setRaceclasses("DF95");
            currentcomp.setCompyear("2099");
            currentcomp.setCompetitionname("new competition");
            currentcomp.setTargetracecount(4);
            currentcomp.createTestSaillist("DF95");
            currentcomp.compdir = "df952025";
            currentcomp.racedayfilenames.clear();
            currentcomp.getRacedaylist().clear();
            pointsmatrix = null;
            currentcomp.maxParticipants = 0;
            //  currentcomp.generateranklist(currentcomp.noCompetitors);
            mframe.currentcompetition = currentcomp;
            if (mainmenubar.getMenuCount() > 2)
            {
                mainmenubar.remove(2);
            }
            if (mainmenubar.getMenuCount() > 1)
            {
                mainmenubar.remove(1);
            }
            refreshcompetition_gui();
            compheader.setVisible(false);
            scorespanel.setVisible(false);
            editpanel.setVisible(true);
            mframe.refreshGui();

        } else if (command.equalsIgnoreCase("Load Raceday"))
        {
            final File directorylock = new File(Mainrace_gui.mysailinghome + currentcomp.compdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("racedays", "rxml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getName();
                System.out.println("You chose to open this file: " + selfile);
                Raceday raceday = new Raceday(currentcomp);
                raceday.readXML(Mainrace_gui.mysailinghome + currentcomp.compdir + "/" + selfile);
                raceday.infilename = selfile;
                raceday.outfilename = selfile;
                currentcomp.addRaceday(selfile);
                currentcomp.getRacedaylist().add(raceday);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("Add New Raceday"))
        {
            int nc = 4;
            int nr = currentcomp.competitors.size();
            if(nr==0) nr=10;
            String boatclass = currentcomp.compclasslist.toString();
            String racedate = "31/12/" + currentcomp.getCompyear();
            Raceday araceday = new Raceday(currentcomp, boatclass, racedate, nc, nr);
            currentcomp.getRacedaylist().add(araceday);
            currentcomp.racedayfilenames.add(araceday.infilename);
            araceday.saved = false;
            currentcomp.saved = false;
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
            mframe.mode = RACEDAYEDIT;
            int intseq = currentcomp.racedayfilenames.size() - 1;
            mframe.currentraceday = intseq;
            mframe.refreshGui();
        } else if (command.equalsIgnoreCase("Add Random Raceday"))
        {
            int nc = 4;
            String boatclass = currentcomp.compclasslist.toString();
            String racedate = "31/12/" + currentcomp.getCompyear();
            Vector<String> saillist = currentcomp.competitors.getVector(boatclass, currentcomp.getClubString());
            Raceday rndraceday = new Raceday(currentcomp, boatclass, racedate);
            currentcomp.getRacedaylist().add(rndraceday);
            currentcomp.racedayfilenames.add(rndraceday.infilename);
            rndraceday.saved = false;
            currentcomp.reloadracedays();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }

        revalidate();
        Mainrace_gui.mframe.revalidate();
        Mainrace_gui.mframe.repaint();
        Mainrace_gui.mframe.pack();
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
        classfield.setText(currentcomp.compclasslist.toString());
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

        jswHorizontalPanel directorypanel = new jswHorizontalPanel("directory", false, false);
        jswLabel label02a = new jswLabel(" Raceday directory.. ");
        label02a.applyStyle(defaultStyles().getStyle("largetext"));
        directorypanel.add(" left ", label02a);
        dirfield = new jswTextBox(this, "raceday directory");
        dirfield.setText(currentcomp.compdir);
        dirfield.setStyleAttribute("mywidth", 100);
        dirfield.setStyleAttribute("myheight", 50);
        dirfield.applyStyle();
        directorypanel.add("  ", dirfield);
        subpanel.add(" height=100 ", directorypanel);
        jswHorizontalPanel racecountpanel = new jswHorizontalPanel("contributing", false, false);
        jswLabel label02 = new jswLabel(" Contributing Races ");
        label02.applyStyle(defaultStyles().getStyle("largetext"));
        racecountpanel.add(" left ", label02);
        countfield = new jswTextBox(this, "Number of contributing Races");
        countfield.setText(currentcomp.getTargetracecount());
        countfield.setStyleAttribute("mywidth", 100);
        countfield.setStyleAttribute("myheight", 50);
        countfield.applyStyle();
        racecountpanel.add(" ", countfield);
        subpanel.add(" height=100 ", racecountpanel);
        jswHorizontalPanel clubpanel = new jswHorizontalPanel("clubs", false, false);
        jswLabel label03 = new jswLabel(" Clubs Racing - separated by commas");
        label03.applyStyle(defaultStyles().getStyle("largetext"));
        clubpanel.add(" left ", label03);
        clubslist = new jswTextBox(this, "Clubs racing, separated by commas");
        clubslist.setText(currentcomp.getClubString());
        clubslist.setStyleAttribute("mywidth", 200);
        clubslist.setStyleAttribute("myheight", 50);
        clubslist.applyStyle();
        clubpanel.add(" ", clubslist);
        compfield = new jswTextBox(this, " raceday directory");
        compfield.setText(currentcomp.compdir);
        compfield.setStyleAttribute("mywidth", 200);
        compfield.setStyleAttribute("myheight", 50);
        compfield.applyStyle();
        clubpanel.add(" ", clubslist);
        subpanel.add(" height=100 ", clubpanel);
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
