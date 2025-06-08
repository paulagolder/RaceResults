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
    //private jswVerticalPanel headermenu;
    jswHorizontalPanel compheader;
    jswHorizontalPanel scorespanel;
    ranksmatrix pointsmatrix = null;
    private jswTextBox classfield;
    private jswTextBox yearfield;
    private jswTextBox countfield;
    private jswTextBox namefield;
    //private jswMenuBar menubar;
    private int maxsailors;
    private jswTextBox clubslist;


    public Competition_gui(Competition acurrentcomp)
    {
        super("mainpanel", true, false);
        makeheadermenu();
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
        makeheadermenu();
        currentcomp = null;
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        //refreshcompetition_gui();
        //compheader.setVisible(true);
        //scorespanel.setVisible(true);
        revalidate();
        repaint();
    }

    public void refreshcompetition_gui()
    {
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

    private jswHorizontalPanel displayraces()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("races", false, false);
        racespanel.add("  ", currentcomp.displayrankcolumn(smalltable2styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            Raceday raceday = currentcomp.getracedaymatrix(i);
            racespanel.add("  ", raceday.displayraceresults(this, smalltable2styles(), i));
        }
        racespanel.setStyleAttribute("backgroundcolor", "pink");
        racespanel.applyStyle();
        return racespanel;
    }

    private jswHorizontalPanel displayscorepanels()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("scores", false, false);
        currentcomp.makeCompetitorsList();
        racespanel.add(" WIDTH=50 ", currentcomp.displaysailcolumn(smalltable2styles()));
        if (currentcomp.getRacedaymatrixlist().size() > 0)
        {
            for (int i = 0; i < currentcomp.getRacedaymatrixlist().size(); i++)
            {
                Raceday raceday = currentcomp.getracedaymatrix(i);
                racespanel.add("  ", raceday.displayscoreresults(this, smalltable1styles(), i));
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

    Vector<String> getRankVector()
    {
        return currentcomp.getRanklist();
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
            JMenuItem fmenuItem6 = new JMenuItem("Export Results to HTML");
            fmenuItem6.addActionListener(this);
            fmenu.add(fmenuItem6);

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
        jswLabel rc = new jswLabel(currentcomp.getRacecount());
        rc.applyStyle(defaultStyles().getStyle("largetext"));
        racedayheader.add(" ", rc);
        racedayheader.applyStyle();
        return racedayheader;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (currentcomp.competitors.isEmpty())
        {
            currentcomp.competitors = (mframe.clubSailList.makeList(currentcomp.compclasslist.toString(),currentcomp.getClubString()));
        }
        String cmd = e.getActionCommand();
       // System.out.println(" here we are y " + cmd);
        HashMap<String, String> cmdmap = jswUtils.parsecsvstring(cmd);
        String cduc = cmdmap.get("command");
        String command = cmd;

        if (command.startsWith("editraceday:"))
        {
            mode = RACEDAY;
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
            currentcomp.setRacecount(Integer.parseInt(countfield.getText()));
            currentcomp.setRaceclasses(classfield.getText());
            currentcomp.setRaceclubs(clubslist.getText());
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
            String outfile = "raceday_results_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
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
                currentcomp.printRacedaysToHTML(selfile, currentcomp.getCompetitionname());
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
            String outfile = "competition_results_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(Mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            //  JFileChooser chooser = new JFileChooser();
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
            String outfile = "points_results_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
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
                PositionList map = pointsmatrix.makePositionMap();
                PositionList map2 = map.rankMap();
                map2.printResultsToHTML(selfile, "title");
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Export League Table to HTML"))
        {
            String outfile = "League_table_" + currentcomp.getRaceclasses() + "_" + currentcomp.getCompyear() + ".html";
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
                PositionList map = pointsmatrix.makePositionMap();
                PositionList map2 = map.rankMap();
                map2.printLeagueTableToHTML(selfile, "title");
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
            currentcomp.printfileToXML(selfile);
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
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this competition xml file: " + selfile);
                currentcomp.printfileToXML(selfile);
                Mainrace_gui.mframe.saveProperties(selfile);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("Make Scores"))
        {
            //currentcomp.makeCompetitorsList();
            int maxsailors = currentcomp.noCompetitors;
            System.out.println("Max sailors :" + maxsailors);
            System.out.println("competitors :" + currentcomp.competitors);
            pointsmatrix = new ranksmatrix(currentcomp);
            makeheadermenu();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("Load Competition"))
        {
            final File directorylock = new File(mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
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
            }

            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("New Competition"))
        {
            currentcomp.setRaceclasses("DF95");
            currentcomp.setCompyear("2099");
            currentcomp.setCompetitionname("new competition");
            currentcomp.setRacecount(4);
            currentcomp.createTestSaillist("DF95");
            currentcomp.racedayfilenames.clear();
            currentcomp.getRacedaymatrixlist().clear();
            pointsmatrix = null;
            currentcomp.generateranklist(currentcomp.noCompetitors);
            //currentcomp.addEmptyRacedayMatrix(4, 16);
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("Load Raceday"))
        {
            final File directorylock = new File(Mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getName();
                System.out.println("You chose to open this file: " + selfile);
                currentcomp.addRaceday(selfile);
                Raceday raceday = new Raceday(currentcomp);
                raceday.readXML(Mainrace_gui.mysailinghome + selfile);
                currentcomp.getRacedaymatrixlist().add(raceday);
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
            Raceday racedayresults = new Raceday(currentcomp, boatclass, racedate, nc, nr);
            racedayresults.filename = "raceday_" + boatclass + "_" + racedate + ".xml";
            currentcomp.getRacedaymatrixlist().add(racedayresults);
            currentcomp.racedayfilenames.add(racedayresults.filename);
            racedayresults.saved = false;
            currentcomp.saved = false;
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("Add Random Raceday"))
        {
            int nc = 4;
            int nsailors = 15;
            int nr = currentcomp.competitors.size();
            String boatclass = currentcomp.compclasslist.toString();
            String racedate = "12/12/" + currentcomp.getCompyear();
            Vector<String> saillist = currentcomp.competitors.getVector(boatclass, currentcomp.getClubString());
            Raceday racedayresults = new Raceday(boatclass, racedate, nc, nsailors, saillist);
            racedayresults.filename = ("raceday_" + boatclass + "_" + racedate + ".xml").replace("/", "-");
            currentcomp.getRacedaymatrixlist().add(racedayresults);
            currentcomp.racedayfilenames.add(racedayresults.filename);
            racedayresults.printfileToXML(racedayresults.filename);
            racedayresults.saved = true;
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
