package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;

import static org.lerot.raceresults.Mainrace_gui.*;


public class Raceday_gui extends jswVerticalPanel implements ActionListener, KeyListener, MouseListener
{
    private final jswHorizontalPanel racedayheader;
    private final HashMap<String, Integer> activecell;
    jswTable datagrid;
    boolean editheader;
    private jswVerticalPanel raceresults = null;
    private Raceday raceday;
    private HashMap<String, Integer> moveto;
    private jswTextBox racedate;
    private jswDropDownBox boatclass;
    private jswTextBox activebox;
    private Competition competition;
    private cell currentcell;
    private int keycount;

    public Raceday_gui(Raceday araceday)
    {
        super("mainpanel", true, true);
        makeMenu();
        activecell = new HashMap<String, Integer>();
        activecell.put("r", -1);
        activecell.put("c", -1);
        applyStyle(jswStyle.getDefaultStyle());
        setBorder(jswStyle.makeLineBorder(Color.red, 4));
        editheader = true;
        if (araceday == null)
        {
            add(" FILLW   minheight=90 ", makeheader());
            racedayheader = new jswHorizontalPanel("header", false, false);
            jswLabel message = new jswLabel(" NO RACE DAY FOUND ");
            racedayheader.add(" FILLW ", message);
            add(" FILLW   ", racedayheader);
        } else
        {
            raceday = araceday;
            add(" FILLW   minheight=90 ", makeheader());
            racedayheader = new jswHorizontalPanel("header", false, false);
            racedayheader.add(" FILLW ", displayheader());
            add(" FILLW   ", racedayheader);
            raceresults = new jswVerticalPanel("RaceResults..", true, true);
            jswVerticalPanel results_gui = displayresults(table1styles());
            results_gui.revalidate();
            raceresults.add(" FILLW FILLH  ", results_gui);
            raceresults.applyStyle();
            add(" FILLW FILLH  ", raceresults);
        }
        revalidate();
        repaint();
    }

    private jswVerticalPanel makeheader()
    {
        jswVerticalPanel raceheader = new jswVerticalPanel("raceheader", true, false);
        jswHorizontalPanel panel1 = new jswHorizontalPanel("Title Panel", true, false);
        jswLabel title = new jswLabel("Race Results");
        title.applyStyle(defaultStyles().getStyle("h1"));
        panel1.add(" width=200 MIDDLE  ", title);
        jswDate date = new jswDate(" EE dd MMM yyyy");
        panel1.add("  RIGHT ", date);
        raceheader.add("   FILLW  ", panel1);
        return raceheader;
    }

    public void makeMenu()
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
        JMenuItem fmenuItem1 = new JMenuItem("Save Raceday");
        fmenuItem1.addActionListener(this);
        fmenu.add(fmenuItem1);
        JMenuItem fmenuItem2 = new JMenuItem("Reload Raceday");
        fmenuItem2.addActionListener(this);
        fmenu.add(fmenuItem2);
        JMenuItem fmenuItem3 = new JMenuItem("Export Raceday to HTML");
        fmenuItem3.addActionListener(this);
        fmenu.add(fmenuItem3);
        JMenu amenu = new JMenu("Action");
        mainmenubar.add(amenu);
        JMenuItem amenuItem0 = new JMenuItem("Edit Raceday");
        amenuItem0.addActionListener(this);
        amenu.add(amenuItem0);
        JMenuItem amenuItemd = new JMenuItem("Delete Raceday");
        amenuItemd.addActionListener(this);
        amenu.add(amenuItemd);
       /* JMenuItem amenuItem1 = new JMenuItem("Make New Raceday");
        amenuItem1.addActionListener(this);
        amenu.add(amenuItem1);*/
        JMenuItem amenuItem2 = new JMenuItem("Add Race");
        amenuItem2.addActionListener(this);
        amenu.add(amenuItem2);
    }

    private boolean isactivecell(int c, int r)
    {
        if (activecell == null) return false;
        if (activecell.get("r") == null) return false;
        return (activecell.get("r") == r) && (activecell.get("c") == c);
    }

    public jswHorizontalPanel displayheader()
    {
        moveto = null;
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("RaceHeader", true, false);
        if (editheader)
        {
            jswLabel label00 = new jswLabel(" Class ");
            label00.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" left ", label00);
            boatclass = new jswDropDownBox(this, "classlist", "select");
            racedayheader.add(" ", boatclass);
            boatclass.setEnabled(false);
            boatclass.addItem("DF95");
            boatclass.addItem("VICTORIA");
            boatclass.setSelected(raceday.getBoatclass_str());
            jswLabel label01 = new jswLabel(" Date ");
            label01.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" middle  ", label01);
            racedate = new jswTextBox(this, raceday.getRacedate_str());
            racedate.setStyleAttribute("mywidth", 100);
            racedate.setStyleAttribute("myheight", 50);
            racedate.applyStyle();
            racedate.setText(raceday.getRacedate_str());
            racedate.setPanelname("racedate");
            racedayheader.add(" ", racedate);
            jswButton save = new jswButton(this, "Save", "saveraceday");
            if (!raceday.saved)
            {
                racedayheader.add(" right  ", save);
                racedayheader.setStyleAttribute("backgroundcolor", "pink");
                racedayheader.applyStyle();
            }
        } else
        {
            jswLabel label00 = new jswLabel(" Class ");
            label00.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", label00);
            jswLabel addb = new jswLabel(raceday.getBoatclass_str());
            addb.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", addb);
            jswLabel label01 = new jswLabel(" Date ");
            label01.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" middle ", label01);
            jswLabel dt = new jswLabel(raceday.getRacedate("/"));
            dt.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", dt);
            jswLabel label02 = new jswLabel(" Clubs ");
            label02.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" RIGHT ", label02);
            jswLabel addcl = new jswLabel(raceday.competition.getClubString());
            addcl.applyStyle(Mainrace_gui.defaultStyles().getStyle("largetext"));
            racedayheader.add(" ", addcl);
        }
        racedayheader.applyStyle();
        return racedayheader;
    }


    public jswVerticalPanel displayresults(jswStyles tablestyles)
    {
        keycount = 0;
        SailList saillist = compgui.currentcomp.competitors;
        String defclubs = compgui.currentcomp.getClubString();
        String defclasses = compgui.currentcomp.compclasslist.toString();
        moveto = null;
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        int ncols = raceday.GetNoRaces();
        int nrows = raceday.getNoSailors();
        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        datagrid = new jswTable(this, "form1", tablestyles);
        raceresults.add(" FILLW FILLH middle ", datagrid);
        datagrid.addCell(new jswLabel(" "), 0, 0);
        datagrid.addCell(new jswLabel("Select"), 1, 0);
        for (int c = 1; c < ncols + 1; c++)
        {
            datagrid.addCell(new jswLabel(raceday.getColname(c - 1)), 0, c);
        }
        for (int c = 1; c < ncols + 1; c++)
        {
            jswCheckbox no = new jswCheckbox(this, " ");
            no.setActionCommand("selectrace:" + c);
            no.setSelected(raceday.getRacematrix().getSelected().get(c - 1));
            jswCell acell = datagrid.addCell(no, 1, c);
            //acell.addMouseListener(acell);
            //no.addMouseListener(this);
        }
        int maxr = 0;
        for (int r = 1; r < nrows; r++)
        {
            int tc = 0;
            for (int c = 0; c < ncols; c++)
            {
                if (raceday.getRacematrix().getValue(c, r) != null) tc++;
            }
            if (tc > 0) maxr = r;
        }
        if (maxr < 1) maxr = 10;
        for (int r = 1; r < maxr + 2; r++)
        {
            datagrid.addCell(new jswLabel(raceday.getRank(r - 1)), r + 1, 0);
        }
        for (int c = 0; c < ncols; c++)
        {

            for (int r = 0; r < maxr + 3; r++)
            {
                String avalue = "";
                jswLabel alabel = null;
                if (r > maxr)
                {
                    avalue = null;
                    alabel = new jswLabel(avalue);
                } else
                {
                    avalue = raceday.getRacematrix().getValue(c, r);
                    Sail foundsail = Sail.parse(avalue, raceday.competition.compclasslist.toString(), raceday.competition.getClubString());
                    if (foundsail == null)
                    {
                        if (avalue == null || avalue.isEmpty())
                        {
                            alabel = new jswLabel(avalue);
                            //  datagrid.addCell(alabel, r + 2, c + 1);
                        } else
                        {
                            alabel = new jswLabel(avalue);
                            //  datagrid.addCell(alabel, r + 2, c + 1);
                            alabel.setStyleAttribute("foregroundcolor", "red");
                            alabel.applyStyle();
                        }
                    } else
                    {
                        String cs = foundsail.toCypherString();
                        alabel = new jswLabel(cs);
                        //  datagrid.addCell(alabel, r + 2, c + 1);
                    }
                    jswCell acell = datagrid.addCell(alabel, r + 2, c + 1);
                    acell.addMouseListener(acell);
                    alabel.addMouseListener(acell);
                }
                if (isactivecell(c + 1, r + 1))
                {

                    activebox = new jswTextBox(this, avalue, 60, "activecelltextbox");
                    jswCell acell = datagrid.addCell(activebox, r + 2, c + 1);
                    activebox.addMouseListener(acell);
                    activebox.addKeyListener(this);
                    EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
                }

            }
        }
        // }
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.applyStyle();
        return raceresults;
    }


    public void jswActionPerformed(jswActionEvent e)
    {
        System.out.println(" here we are x 3:" + e);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        String action = command;
        HashMap<String, String> cmdmap = new HashMap<String, String>();
        System.out.println("command=" + action);
        if (command.contains("actiontype"))
        {
            cmdmap = utils.parseaction(command);
            command = cmdmap.get("command");
        }
        if (action.equalsIgnoreCase("activecelltextbox"))
        {
            jswCell selectedcell = ((jswTable) (e.getSource())).getSelectedCell();
            int r = selectedcell.getRow();
            int c = selectedcell.getCol();
            String cd = selectedcell.getSelection();
            int nc = activecell.get("c");
            int nr = activecell.get("r");
            activecell.put("r", nr + 1);
            System.out.println("edited cell xxx " + nc + ":" + nr + "=" + cd);
            System.out.println("cc :" + currentcell);
            System.out.println("ac :" + activecell);
            {
                if (!cd.isEmpty())
                {
                    Sail foundsail = compgui.currentcomp.competitors.getSail(cd, raceday.getBoatclass_str(), raceday.competition.getClubString());
                    if (foundsail == null)
                    {
                        //alabel = new jswLabel(cd);
                        raceday.getRacematrix().setCell(nc, nr, cd);
                    } else
                    {
                        // jswLabel alabel = new jswLabel(foundsail.toCypherString());
                        raceday.getRacematrix().setCell(nc, nr, foundsail.toCypherString());
                        //alabel.setStyleAttribute("foregroundcolor", "red");
                    }
                }
            }
            raceday.saved = false;
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
            EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
            repaint();
        } else if (command.equalsIgnoreCase("View Competition"))
        {
            mframe.mode = COMPETITION;
            mainpanel.removeAll();
            mainpanel.add(" FILLH FILLW ", compgui);
            mainpanel.repaint();
        } else if (command.equalsIgnoreCase("Add Race"))
        {
            raceday.addRace();
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW  ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("removeraceday"))
        {
            int nraceday = compgui.currentcomp.racedayfilenames.indexOf(raceday.filename);
            compgui.currentcomp.racedayfilenames.removeElementAt(nraceday);
            compgui.currentcomp.getRacedaymatrixlist().removeElementAt(nraceday);
            String compfile = compgui.currentcomp.competitionfile;
            String savefile = utils.fileexists(compfile);
            compgui.currentcomp.printfileToXML(savefile);
            compgui.currentcomp.loadCompetition(compfile);
            mframe.mode = COMPETITION;
            mframe.refreshGui();
            revalidate();
        } else if (command.equalsIgnoreCase("Export Raceday to HTML"))
        {
            activecell.put("r", -1);
            String outfile = "raceday_" + raceday.getBoatclass_str() + "_" + raceday.getRacedate("-") + ".html";
            final File directorylock = new File(Mainrace_gui.mysailinghome);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this file to html: " + selfile);
                raceday.printToHTML(selfile);
            }
        } else if (command.equalsIgnoreCase("Save Raceday"))
        {
            activecell.put("r", -1);
            final File directorylock = new File(Mainrace_gui.mysailinghome + raceday.competition.compdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            //  String filename = "raceday_" + raceday.getBoatclass_str() + "_" + raceday.getRacedate("-") + ".xml";
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            String outfile = raceday.filename;
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to save to this file: " + selfile);
                raceday.setfilename(selfile);
                int maxsailors = raceday.getNoSailors();
                raceday.getRacematrix().setRowname(utils.makeRownames(maxsailors));
                raceday.printfileToXML(selfile);
                raceday.saved = true;
                compgui.currentcomp.saveCompetition();
            }
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("Reload Raceday"))
        {
            final File directorylock = new File(Mainrace_gui.mysailinghome + competition.compdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            String outfile = raceday.filename;
            System.out.println("++" + outfile);
            chooser.setSelectedFile(new File(outfile));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                activecell.put("r", -1);
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " + selfile);
                raceday.setfilename(selfile);
                raceday.readXML(selfile);
                racedayheader.removeAll();
                racedayheader.add(" FILLW ", displayheader());
                raceresults.removeAll();
                jswPanel apanel = displayresults(table1styles());
                raceresults.add(" FILLW FILLH ", apanel);
                repaint();
            }
        } else if (command.equalsIgnoreCase("Edit Raceday"))
        {
            activecell.put("r", -1);
            System.out.println("editing header");
            editheader = true;
            raceday.saved = false;
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("saveraceday"))
        {
            activecell.put("r", -1);
            raceday.setRacedate_str(racedate.getText());
            raceday.setBoatclass_str(boatclass.getSelectedValue());
            editheader = false;
            raceday.filename = raceday.makefilename();
            raceday.saved = false;
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW  ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("mouseclick"))
        {
            editheader = false;
            System.out.println("editing cell b " + cmdmap.get("column") + ":" + cmdmap.get("row") + "=" + cmdmap.get("cellcontent"));
            //  currentcell = new cell(Integer.parseInt(cmdmap.get("column")), Integer.parseInt(cmdmap.get("row")), cmdmap.get("cellcontent"));
            int c = Integer.parseInt(cmdmap.get("column"));
            int r = Integer.parseInt(cmdmap.get("row"));
            String value = cmdmap.get("cellcontent");
            currentcell = new cell(c, r - 1, value);
            raceday.getRacematrix().setCell(c, r - 1, value);
            activecell.put("r", r - 1);
            activecell.put("c", c);
            raceresults.removeAll();
            racedayheader.removeAll();
            racedayheader.add(" FILLW  ", displayheader());
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.startsWith("selectrace"))
        {
            String[] colstr = command.split(":");
            int col = Integer.parseInt(colstr[1]);
            System.out.println("edited cell qcol =" + col);
            Boolean sel = raceday.getRacematrix().getSelected().get(col - 1);
            raceday.getRacematrix().getSelected().set(col - 1, !sel);


        } else if (command.equalsIgnoreCase("viewcompetition"))
        {
            mframe.mode = COMPETITION;
            String selfile = mysailinghome + currentcompetitionfile;
            System.out.println("reopening: " + selfile);
            Mainrace_gui.mainpanel.removeAll();
            // compgui = new  competition_gui(selfile);
            Mainrace_gui.mainpanel.add(" FILLH FILLW ", compgui);
            revalidate();
        } else if (cmdmap.get("jswCheckbox") != null)
        {
            int nc = activecell.get("c");
            int nr = activecell.get("r");
            System.out.println("edited cell qqq " + nc + ":" + nr + "=" + cmdmap.get("column"));
        } else if ((cmdmap.get("handlerclass")).equalsIgnoreCase("jswTextBox"))
        {
            int nc = activecell.get("c");
            int nr = activecell.get("r");
            activecell.put("r", nr + 1);

            System.out.println(currentcell);
            System.out.println(activecell);
            String cd = cmdmap.get("command");
            if (cd.equalsIgnoreCase("i"))
            {
                String thiscell = currentcell.value;
                int rn = nr;
                while (thiscell != null && !thiscell.trim().isEmpty())
                {
                    String nextcell = raceday.getRacematrix().getCell(nc, rn + 1);
                    raceday.getRacematrix().setCell(nc, rn + 1, thiscell);
                    thiscell = nextcell;
                    rn++;
                }
            } else if (cd.equalsIgnoreCase("d"))
            {
                int rn = nr;
                String nextcell = raceday.getRacematrix().getCell(nc, rn + 1);
                while (nextcell != null && !nextcell.trim().isEmpty())
                {
                    raceday.getRacematrix().setCell(nc, rn, nextcell);
                    nextcell = raceday.getRacematrix().getCell(nc, rn + 2);
                    rn++;
                }
                raceday.getRacematrix().setCell(nc, rn, " ");
            } else
            {
                if (!cd.isEmpty())
                {
                    try
                    {
                        int ti = Integer.parseInt(cmdmap.get("textboxvalue"));
                        raceday.getRacematrix().setCell(nc, nr, "" + ti);
                    } catch (NumberFormatException ex)
                    {
                        raceday.getRacematrix().setCell(nc, nr, " ");
                    }
                }
            }
            raceday.saved = false;
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
            EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
            repaint();
        } else
        {
            System.out.println("command not found : " + command);
        }
        Mainrace_gui.mframe.revalidate();
        Mainrace_gui.mframe.repaint();
        Mainrace_gui.mframe.pack();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent)
    {
        //System.out.println(" here we are x 3keyt:" + keyEvent);
    }

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        int nc = activecell.get("c");
        int nr = activecell.get("r");
        System.out.println(" keypressed =" + nc + " : " + nr + ":" + raceday.getNoSailors());
        String cd = "" + keyEvent.getKeyChar();
        int maxcol = raceday.getRacematrix().getColumn(nc - 1).size();
        keycount++;
        if (keycount < 2)
        {
            System.out.println(" keypressed ++" + cd + "++");
            if (cd.equalsIgnoreCase("i"))
            {
                String thiscell = currentcell.value;
                if (nr >= raceday.getNoSailors())
                {
                    String label = "Rank" + utils.pad(nr + 1);
                    raceday.getRacematrix().getRowname().add(label);
                    raceday.getRacematrix().data.get(nc - 1).add("");
                    //raceday.getRacematrix().;
                    datagrid.addCell(new jswLabel(label), nr + 1, 0);
                } else
                {
                    int rn = nr;
                    while (thiscell != null && !thiscell.trim().isEmpty() && rn < raceday.getNoSailors())
                    {
                        String nextcell = raceday.getRacematrix().getCell(nc, rn + 1);
                        raceday.getRacematrix().setCell(nc, rn + 1, thiscell);
                        thiscell = nextcell;
                        rn++;
                    }
                    raceday.getRacematrix().setCell(nc, nr, "?");
                }
            } else if (cd.equalsIgnoreCase("d"))
            {
                int rn = nr;
                String nextcell = raceday.getRacematrix().getCell(nc, rn + 1);
                while (nextcell != null && !nextcell.trim().isEmpty() && rn < maxcol)
                {
                    raceday.getRacematrix().setCell(nc, rn, nextcell);
                    nextcell = raceday.getRacematrix().getCell(nc, rn + 2);
                    rn++;
                }
                raceday.getRacematrix().setCell(nc, rn, " ");
            } else return;
        } else return;
        raceday.saved = false;
        racedayheader.removeAll();
        racedayheader.add(" FILLW ", displayheader());
        raceresults.removeAll();
        jswPanel apanel = displayresults(table1styles());
        raceresults.add(" FILLW FILLH ", apanel);
        EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
        repaint();
        Mainrace_gui.mframe.revalidate();
        Mainrace_gui.mframe.repaint();
        Mainrace_gui.mframe.pack();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        //System.out.println(" here we are x 3keyr:" + keyEvent);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent)
    {
        System.out.println("mouseclicled");
        System.out.println(mouseEvent);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent)
    {

    }


    private class cell
    {
        private final int column;
        private final int row;
        private final String value;

        public cell(int acolumn, int arow, String s)
        {
            column = acolumn;
            row = arow;
            value = s;
        }

        public String toString()
        {
            return "{" + column + "," + row + "," + value + ")";
        }
    }
}
