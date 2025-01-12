package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import static org.lerot.raceresults.mainrace_gui.*;


public class raceday_gui extends jswVerticalPanel implements ActionListener
{
    private final jswVerticalPanel raceresults;
    private final jswHorizontalPanel racedayheader;
    private racedaymatrix racedayresults;
    private HashMap<String, Integer> activecell;
    private HashMap<String, Integer> moveto;
    private boolean editheader;
    private jswTextBox racedate;
    private jswDropDownBox boatclass;
    private jswTextBox activebox;
    private org.lerot.raceresults.competition competition;


    public raceday_gui(racedaymatrix nresults)
    {
        super("mainpanel", true, true);
        activecell = new HashMap<String, Integer>();
        activecell.put("r", -1);
        activecell.put("c", -1);
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));

        if (nresults == null)
            racedayresults = racedaymatrix.demo();
        else
            racedayresults = nresults;
        add(" FILLW   minheight=90 ", makeheadermenu());
        racedayheader = new jswHorizontalPanel("header", false, false);
        racedayheader.add(" FILLW ", displayheader());
        add(" FILLW   ", racedayheader);
        raceresults = new jswVerticalPanel("RaceResults..", true, true);
        jswVerticalPanel results_gui = displayresults(table1styles());
        results_gui.revalidate();
        raceresults.add(" FILLW FILLH  ", results_gui);
        raceresults.applyStyle();
        add(" FILLW FILLH  ", raceresults);
        revalidate();
        repaint();
    }

    private jswVerticalPanel makeheadermenu()
    {
        jswVerticalPanel raceheader = new jswVerticalPanel("raceheader", true, false);
        jswHorizontalPanel panel1 = new jswHorizontalPanel("Title Panel", true, false);
        jswLabel title = new jswLabel("Race Results");
        title.applyStyle(defaultStyles().getStyle("h1"));
        panel1.add(" width=200 MIDDLE  ", title);
        jswDate date = new jswDate(" EE dd MMM yyyy");
        panel1.add("  RIGHT ", date);
        raceheader.add("   FILLW  ", panel1);

        jswMenuBar menubar = new jswMenuBar("mainmenu", this);
        JMenu filemenu = menubar.addMenuHeading("File");
        menubar.addMenuItem(filemenu, "Save Raceday", "saveraceday");
        menubar.addMenuItem(filemenu, "Reload Raceday", "reloadraceday");
        menubar.addMenuItem(filemenu, "Export Raceday to HTML", "racedaytohtml");
        JMenu viewmenu = menubar.addMenuHeading("View");
        menubar.addMenuItem(viewmenu, "View Competition", "viewcompetition");
        JMenu actionmenu = menubar.addMenuHeading("Action");
        menubar.addMenuItem(actionmenu, "Make Result Matrix", "makeresults");
        menubar.addMenuItem(actionmenu, "Make New Raceday", "makenewraceday");
       // menubar.addMenuItem(actionmenu, "New Competitiion", "newcompetition");
        menubar.setBackground(jswStyle.transparentColor());
        raceheader.add(" FILLW  ", menubar);

        return raceheader;
    }

    private boolean isactivecell(int c, int r)
    {
        if (activecell == null) return false;
        if (activecell.get("r") == null) return false;
        if ((activecell.get("r") == r) && (activecell.get("c") == c))
        {
            return true;
        }
        return false;
    }

    public jswHorizontalPanel displayheader()
    {
        moveto = null;
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("RaceHeader", true, false);
        if (!racedayresults.saved)
        {
            racedayheader.setStyleAttribute("backgroundcolor", "pink");
            racedayheader.applyStyle();
        }
        if (editheader)
        {
            jswLabel label00 = new jswLabel(" Class ");
            label00.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" left ", label00);
            boatclass = new jswDropDownBox(this, "classlist", "select");
            racedayheader.add(" ", boatclass);
            boatclass.setEnabled(false);
            boatclass.addItem("DF95");
            boatclass.addItem("VICTORIA");
            boatclass.setSelected(racedayresults.getBoatclass_str());
            jswLabel label01 = new jswLabel(" Date ");
            label01.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" middle  ", label01);
            racedate = new jswTextBox(this, racedayresults.getRacedate_str());
            racedate.setStyleAttribute("mywidth", 100);
            racedate.setStyleAttribute("myheight", 50);
            racedate.applyStyle();
            racedate.setText(racedayresults.getRacedate_str());
            racedate.setPanelname("racedate");
            racedayheader.add(" ", racedate);
            jswButton editheader = new jswButton(this, "SAVE", "saveheader");
            racedayheader.add(" right ", editheader);
        } else
        {
            jswLabel label00 = new jswLabel(" Class ");
            label00.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", label00);
            jswLabel addb = new jswLabel(racedayresults.getBoatclass_str());
            addb.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", addb);
            jswLabel label01 = new jswLabel(" Date ");
            label01.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" middle ", label01);
            jswLabel dt = new jswLabel(racedayresults.getRacedate("/"));
            dt.applyStyle(mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", dt);
            jswButton editheader = new jswButton(this, "EDIT", "editheader");
            racedayheader.add(" right ", editheader);
        }
        racedayheader.applyStyle();
        return racedayheader;
    }


    public jswVerticalPanel displayresults(jswStyles tablestyles)
    {
        moveto = null;
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);

        int ncols = racedayresults.getNcols();
        int nrows = racedayresults.getNrows();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        //raceresults.setTrace(true);
        jswTable datagrid = new jswTable(this, "form1", tablestyles);
        raceresults.add(" FILLW FILLH middle ", datagrid);
        datagrid.addCell(new jswLabel("corner"), 0, 0);
        for (int c = 1; c < ncols + 1; c++)
        {
            datagrid.addCell(new jswLabel(racedayresults.getColname(c - 1)), 0, c);
        }
        for (int r = 1; r < nrows + 1; r++)
        {
            datagrid.addCell(new jswLabel(racedayresults.getRowname(r - 1)), r, 0);
        }
        for (int c = 0; c < ncols; c++)
        {
            for (int r = 0; r < nrows; r++)
            {
                if (isactivecell(c + 1, r + 1))
                {
                    activebox = new jswTextBox(this, racedayresults.getRacematrix().getValue(c, r));
                    activebox.setStyleAttribute("mywidth", 60);
                    activebox.applyStyle();
                    jswCell acell = datagrid.addCell(activebox, r + 1, c + 1);
                    activebox.setStyleAttribute("mywidth", 100);
                    activebox.setStyleAttribute("myheight", 50);
                    activebox.applyStyle();
                    EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
                } else
                {
                    jswLabel alabel = new jswLabel(racedayresults.getRacematrix().getValue(c, r));
                    jswCell acell = datagrid.addCell(alabel, r + 1, c + 1);
                    alabel.addMouseListener(acell);
                }
            }
        }
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.applyStyle();
        return raceresults;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        System.out.println(" here we are x " + cmd);
        HashMap<String, String> cmdmap = jswUtils.parsecsvstring(cmd);
        String  command = "";
        if(cmdmap.get("command")!= null)
        {
            command = cmdmap.get("command").toLowerCase();
        }else
        {
            command = cmdmap.get("commandstringjsw").toLowerCase();
        }
        System.out.println(" here we are x " + command);
        if (command.equalsIgnoreCase("View Competition"))
        {
            mode = COMPETITION;
            mframe.mainpanel.removeAll();
            mframe.mainpanel.add(" FILLH FILLW ", compgui);
            mframe.mainpanel.repaint();
        }
        if (command.equalsIgnoreCase("racedaytohtml"))
        {
            activecell.put("r", -1);
            String outfile = "raceday_" + racedayresults.getBoatclass_str() + "_" + racedayresults.getRacedate("-") + ".html";
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
                System.out.println("You chose to save to this file to html: " + selfile);
                racedayresults.printToHTML(selfile);
            }
        }
        if (command.equalsIgnoreCase("saveraceday"))
        {
            activecell.put("r", -1);
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            //  JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            String outfile = racedayresults.filename;
            System.out.println("++" + outfile);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this file: " + selfile);
                racedayresults.setfilename(selfile);
                racedayresults.printfileToXML(selfile);
                racedayresults.saved = true;
                racedayheader.removeAll();
                racedayheader.add(" FILLW ", displayheader());
                repaint();
            }
        } else if (command.equalsIgnoreCase("reloadraceday"))
        {
            final File directorylock = new File(mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            String outfile = racedayresults.filename;
            System.out.println("++" + outfile);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                activecell.put("r", -1);
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " + selfile);
                racedayresults.setfilename(selfile);
                racedayresults.readXML(selfile);
                racedayheader.removeAll();
                racedayheader.add(" FILLW ", displayheader());
                raceresults.removeAll();
                jswPanel apanel = displayresults(table1styles());
                raceresults.add(" FILLW FILLH ", apanel);
                repaint();
            }
        } else if (command.equalsIgnoreCase("makenewraceday"))
        {
            activecell.put("r", -1);
            //removeAll();
            int nc = 4;
            int nr = 10;
            String boatclass = "DF95";
            String racedate = "01/01/2025";
            racedayresults = new racedaymatrix(boatclass, racedate, nc, nr);
            racedayresults.filename = "raceday_" + racedayresults.getBoatclass_str() + "_" + racedayresults.getRacedate("-") + ".xml";
            racedayresults.saved = false;
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswVerticalPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
            // repaint();
        } else if (command.equalsIgnoreCase("editheader"))
        {
            activecell.put("r", -1);
            System.out.println("editing header");
            editheader = true;
            racedayresults.saved = false;
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("saveheader"))
        {
            activecell.put("r", -1);
            racedayresults.setRacedate_str(racedate.getText());
            racedayresults.setBoatclass_str(boatclass.getSelectedValue());
            editheader = false;
            racedayresults.saved = false;
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW  ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);

        } else if (command.equalsIgnoreCase("mouseclick"))
        {
            editheader = false;
            System.out.println("editing cell " + cmdmap.get("columnjsw") + ":" + cmdmap.get("rowjsw") + "=" + cmdmap.get("cellcontentjsw"));
            int c = Integer.parseInt(cmdmap.get("columnjsw"));
            int r = Integer.parseInt(cmdmap.get("rowjsw"));
            String value = cmdmap.get("cellcontentjsw");
            racedayresults.getRacematrix().setCell(c, r, value);
            activecell.put("r", r);
            activecell.put("c", c);
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW  ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if ((cmdmap.get("handlerclass")).equalsIgnoreCase("jswTextBox"))
        {
            int c = activecell.get("c");
            int r = activecell.get("r");
            activecell.put("r", r + 1);
            System.out.println("edited cell " + cmdmap.get("column") + ":" + cmdmap.get("row") + "=" + cmdmap.get("textboxvalue"));
            System.out.println(activecell);
            try
            {
                int ti = Integer.parseInt(cmdmap.get("textboxvalue"));
                racedayresults.getRacematrix().setCell(c, r, "" + ti);
            } catch (NumberFormatException ex)
            {
                racedayresults.getRacematrix().setCell(c, r, " ");
            }
            racedayresults.saved = false;
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
            EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
            repaint();
        }  else if (command.equalsIgnoreCase("makeresults"))
        {
            racedayresults.makecompmatrix(compgui.getSailVector());
        }
        else if (command.equalsIgnoreCase("viewcompetition"))
        {
            mode = COMPETITION;
            String selfile = mysailinghome + currentcompetitionfile;

                System.out.println("reopening: " + selfile);
                mainrace_gui.mainpanel.removeAll();
                   // compgui = new  competition_gui(selfile);
                   mainrace_gui.mainpanel.add( " FILLH FILLW ", compgui);

                revalidate();

        }

        mainrace_gui.mframe.revalidate();
        mainrace_gui.mframe.repaint();
        mainrace_gui.mframe.pack();
    }


}
