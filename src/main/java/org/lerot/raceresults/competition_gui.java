package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

import static org.lerot.raceresults.mainrace_gui.*;


public class competition_gui extends jswVerticalPanel implements ActionListener
{

    public competition currentcomp;
    private jswVerticalPanel headermenu;
    private jswHorizontalPanel compheader;
    private jswHorizontalPanel editpanel = null;
    private jswHorizontalPanel scorespanel;
    private ranksmatrix pointsmatrix = null;
    private jswTextBox classfield;
    private jswTextBox yearfield;
    private jswTextBox countfield;
    private jswTextBox namefield;
    private jswMenuBar menubar;
    private JMenu filemenu;
    private JMenu viewmenu;
    private JMenu actionmenu;
    private jswHorizontalPanel sailpanel;
    private int maxsailors;
    private org.lerot.raceresults.scoresmatrix scoresmatrix;


    public competition_gui(String compfile)
    {
        super("mainpanel", true, false);
        currentcomp = new competition();
        currentcomp.competitionfile = compfile;
        String cfile = utils.fileexists(compfile);
        currentcomp.loadCompetition(cfile);
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        refreshcompetition_gui();
        editpanel.setVisible(false);
        sailpanel.setVisible(false);
        revalidate();
        repaint();
    }

    public void refreshcompetition_gui()
    {
        removeAll();
        headermenu = makeheadermenu();
        add(" FILLW HEIGHT=200  ", headermenu);
        compheader = displayheader();
        add(" FILLW HEIGHT=200 ", compheader);
        compheader.applyStyle();
        scorespanel = displayscorepanels();
        add(" FILLH  FILLW ", scorespanel);
        editpanel = editcompetition();
        add(" FILLW HEIGHT=200 ", editpanel);
        sailpanel = new jswHorizontalPanel("Sail List", false, true);
        add(" FILLW HEIGHT=200 ", sailpanel);
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
            racespanel.add("  ", raceday.displayraceresults(this, smalltable2styles(), i));
        }
        racespanel.setStyleAttribute("backgroundcolor", "pink");
        racespanel.applyStyle();
        return racespanel;
    }

    private jswHorizontalPanel displayscorepanels()
    {
        jswHorizontalPanel racespanel = new jswHorizontalPanel("scores", false, false);
        racespanel.add(" WIDTH=50 ", currentcomp.displaysailcolumn(smalltable2styles()));
        for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
        {
            racedaymatrix raceday = currentcomp.getracedaymatrix(i);
            racespanel.add("  ", raceday.displayscoreresults(this, smalltable1styles(), i));
        }
        if (scoresmatrix != null)
        {
           racespanel.add(" FILLW ", scoresmatrix.displayresults(this, smalltable1styles(),maxsailors));
        }
        if (pointsmatrix != null)
        {
            racespanel.add(" FILLW RIGHT ", pointsmatrix.displayresults(this, smalltable1styles(),maxsailors));
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
        menubar = new jswMenuBar("mainmenu", this);
        filemenu = menubar.addMenuHeading("File");
        menubar.addMenuItem(filemenu, "Load Competition", "loadcompetition");
        menubar.addMenuItem(filemenu, "Save Competition", "savecompetition");
        menubar.addMenuItem(filemenu, "Export All Racedays to HTML", "racedaystohtml");
        if (pointsmatrix != null)
        {
            menubar.addMenuItem(filemenu, "Export Results to HTML", "competitionhtml");
        }
        viewmenu = menubar.addMenuHeading("View");
        menubar.addMenuItem(viewmenu, "Edit Competition", "editcompetition");
        menubar.addMenuItem(viewmenu, "View Saillist", "viewsailist");
        actionmenu = menubar.addMenuHeading("Action");
        menubar.addMenuItem(actionmenu, "Make Scores", "makescore");
        menubar.addMenuItem(actionmenu, "Add New Raceday", "addnewraceday");
        menubar.addMenuItem(actionmenu, "Add Random Raceday", "addrandomraceday");
        menubar.addMenuItem(actionmenu, "Load Raceday", "loadraceday");
        menubar.addMenuItem(actionmenu, "New Competition", "newcompetition");
        menubar.setBackground(jswStyle.transparentColor());
        add(" FILLW  ", menubar);
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
        if (currentcomp.boatlist.isEmpty())
        {
            currentcomp.boatlist = new Vector<>(mainrace_gui.mframe.getBoatlist());
        }
        String cmd = e.getActionCommand();
        System.out.println(" here we are y " + cmd);
        HashMap<String, String> cmdmap = jswUtils.parsecsvstring(cmd);
        String cduc = cmdmap.get("command");
        String command = "dummy";
        if (cduc != null)
            command = cmdmap.get("command").toLowerCase();

        if (command.startsWith("editraceday:"))
        {
            mode = RACEDAY;
            String seq = command.replace("editraceday:", "");
            int intseq = Integer.parseInt(seq);
            mainpanel.removeAll();
            racedaymatrix aracedaymatrix = currentcomp.getracedaymatrix(intseq);
            mainpanel.add(" ", new raceday_gui(aracedaymatrix));
            mainpanel.repaint();
        }
        if (command.startsWith("editcompetition"))
        {
            refreshcompetition_gui();
            compheader.setVisible(false);
            scorespanel.setVisible(false);
            editpanel.setVisible(true);
            sailpanel.setVisible(false);
        }
        if (command.startsWith("viewsailist"))
        {
            mode = SAILLIST;
            refreshcompetition_gui();
            sailpanel.removeAll();
            sailpanel.add(" ", showsaillist(smalltable2styles()));
            sailpanel.setVisible(true);
            compheader.setVisible(false);
            scorespanel.setVisible(false);
            editpanel.setVisible(false);
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
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("racedaystohtml"))
        {
            String outfile = "raceday_results_" + currentcomp.getRaceclass() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "html", "html");
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
        if (command.equalsIgnoreCase("competitionhtml"))
        {
            String outfile = "competition_results_" + currentcomp.getRaceclass() + "_" + currentcomp.getCompyear() + ".html";
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            //  JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this html file: " + selfile);
                HashMap<String, String> sailorlist = mainrace_gui.mframe.makeList(currentcomp.getRaceclass());
                pointsmatrix.printResultsToHTML(selfile, currentcomp.getCompetitionname(), sailorlist, maxsailors);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("savecompetition"))
        {
            String outfile = "competition_" + currentcomp.getRaceclass() + "_" + currentcomp.getCompyear() + ".xml";
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this xml file: " + selfile);
                currentcomp.printfileToXML(selfile);
                mainrace_gui.mframe.saveProperties();
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        }
        if (command.equalsIgnoreCase("makescore"))
        {
            maxsailors=currentcomp.getSaillist().size();
            System.out.println("Max sailors" + maxsailors);
            int[][] rankmatrix = makePointsMatrix();
            scoresmatrix = new scoresmatrix(currentcomp);
            scoresmatrix.makeMatrix(rankmatrix);
            pointsmatrix = new ranksmatrix(currentcomp);
            pointsmatrix.setColnames(utils.makePositionNames(maxsailors));
            pointsmatrix.makeMatrix();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
            sailpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("loadcompetition"))
        {
            final File directorylock = new File(mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            String outfile = mysailinghome + currentcompetitionfile;
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " + selfile);
                currentcomp = new competition();
                currentcomp.loadCompetition(selfile);
                currentcompetitionfile = selfile;
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("newcompetition"))
        {
            currentcomp.setRaceclass("DF95");
            currentcomp.setCompyear("2099");
            currentcomp.setCompetitionname("new competition");
            currentcomp.setRacecount(4);
            currentcomp.createSaillist("DF95");
            currentcomp.racedayfilenames.clear();
            currentcomp.getRacedaymatrixlist().clear();
            pointsmatrix = null;
            currentcomp.generateranklist(currentcomp.getSaillist().size());
            currentcomp.addEmptyRacedayMatrix(4, 16);
            currentcomp.makeCompMatrices();
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("loadraceday"))
        {
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getName();
                System.out.println("You chose to open this file: " + selfile);
                currentcomp.addRaceday(selfile);
                racedaymatrix raceday = new racedaymatrix(currentcomp);
                raceday.readXML(mainrace_gui.mysailinghome + selfile);
                currentcomp.getRacedaymatrixlist().add(raceday);
            }
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("addnewraceday"))
        {
            int nc = 4;
            int nr = getSailVector().size();
            String boatclass = currentcomp.getRaceclass();
            String racedate = "12/12/" + currentcomp.getCompyear();
            racedaymatrix racedayresults = new racedaymatrix(currentcomp,boatclass, racedate, nc, nr);
            racedayresults.makecompmatrix(getSailVector());
            racedayresults.filename = "raceday_" + boatclass + "_" + racedate + ".xml";
            currentcomp.getRacedaymatrixlist().add(racedayresults);
            currentcomp.racedayfilenames.add(racedayresults.filename);
            racedayresults.saved = false;
            refreshcompetition_gui();
            compheader.setVisible(true);
            scorespanel.setVisible(true);
            editpanel.setVisible(false);
        } else if (command.equalsIgnoreCase("addrandomraceday"))
        {
            int nc = 4;
            int nsailors = 15;
            int nr = currentcomp.boatlist.size();
            String boatclass = currentcomp.getRaceclass();
            String racedate = "12/12/" + currentcomp.getCompyear();
            racedaymatrix racedayresults = new racedaymatrix(boatclass, racedate, nc, nsailors, currentcomp.boatlist);
            racedayresults.makecompmatrix(getSailVector());
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

    private jswHorizontalPanel showsaillist(jswStyles tablestyles)
    {

        jswHorizontalPanel editpanel = new jswHorizontalPanel("Sail List", false, false);
        jswTable datagrid = new jswTable(null, "form1", tablestyles);

        datagrid.addCell(new jswLabel("Sail Number"), 0, 0);
        datagrid.addCell(new jswLabel("Class"), 0, 1);
        datagrid.addCell(new jswLabel("Sailor"), 0, 2);

        int nrows = currentcomp.boatlist.size();
        for (int r = 0; r < nrows; r++)
        {
            sail boat = currentcomp.boatlist.get(r);
            datagrid.addCell(new jswLabel(boat.getSailnumber()), r + 1, 0);
            datagrid.addCell(new jswLabel(boat.getBoatclass()), r + 1, 1);
            datagrid.addCell(new jswLabel(boat.getSailorname()), r + 1, 2);
        }

        editpanel.add(" FILLW FILLH ", datagrid);
        editpanel.applyStyle();
        return editpanel;
    }


    public int[][] makePointsMatrix()
    {
        TreeSet<Integer> sailors = new TreeSet<Integer>();
        Vector<Integer> saillist = currentcomp.getSaillistInt();
        int maxsails=saillist.size();
        maxsailors=0;
        int[][] matrix2 = new int[maxsails ][maxsails];

            for (int i = 0; i < currentcomp.racedayfilenames.size(); i++)
            {
                racedaymatrix aracedaymatrix = currentcomp.getRacedayNo(i);
                for (int c = 0; c < aracedaymatrix.GetNoRaces(); c++)
                {
                   int ns= aracedaymatrix.getNoSailors();
                    for (int r = 0; r < aracedaymatrix.getNoSailors(); r++)
                    {
                        if(r==15)
                        {
                            System.out.println("voila"+r);
                        }
                    String value = aracedaymatrix.getRacematrix().getValue(c, r);
                    int iv;
                    try
                    {
                        iv = Integer.parseInt(value.trim());
                        int si = saillist.indexOf(iv);
                        if(si<0)
                        {
                            System.out.println("voila");
                        }
                        matrix2[si][r] = matrix2[si][r] + 1;
                        sailors.add(iv);
                    } catch (Exception ex)
                    {
                        iv = 0;
                    }
                }
            }
            maxsailors = sailors.size();
        }
        return matrix2;
    }


}
