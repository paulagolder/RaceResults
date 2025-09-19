package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.*;


public class Raceday_gui extends jswVerticalPanel implements ActionListener, KeyListener, MouseListener
{
    private final jswHorizontalPanel racedayheader;
    private final HashMap<String, Integer> activecell;
    jswTable datagrid;
    boolean editheader;
    dataMatrix racematrix = null;
    private jswVerticalPanel raceresults = null;
    private Raceday raceday;
    private HashMap<String, Integer> moveto;
    private jswTextBox racedate;
    private jswTextBox raceclass;
    private jswTextBox activebox;
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
            racematrix = raceday.MakeMatrix();
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
            raceclass = new jswTextBox(this, raceday.getBoatclass_str(), 200, "raceclassbox");
            raceclass.setText(raceday.getBoatclass_str());
            racedayheader.add(" ", raceclass);
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
        //  int ncols = raceday.GetNoRaces();
        int nrows = raceday.getNoSailors();

        raceresults.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        datagrid = new jswTable(this, "form1", tablestyles);
        raceresults.add(" FILLW FILLH middle ", datagrid);
        datagrid.addCell(new jswLabel(" "), 0, 0);
        datagrid.addCell(new jswLabel("Select"), 1, 0);
        int c = 1;
        for (Race arace : raceday.getRacelist())
        {
            datagrid.addCell(new jswLabel(arace.getName()), 0, c);
            c++;
        }
        c = 1;
        for (Race arace : raceday.getRacelist())
        {
            jswCheckbox no = new jswCheckbox(this, " ");
            no.setActionCommand("selectrace:" + c);
            no.setSelected(arace.isIncompetition());
            jswCell acell = datagrid.addCell(no, 1, c);
            c++;
        }
        int maxr = raceday.getNoSailors() + 2;
        if (maxr < 3)
        {
            maxr = 10;

        }
        Vector<String> rownames = utils.makeRownames(maxr);
        for (int r = 0; r < maxr; r++)
        {
            datagrid.addCell(new jswLabel(rownames.get(r)), r + 2, 0);
        }
        c = 0;
        for (Race arace : raceday.getRacelist())
        {

            for (int r = 0; r < maxr; r++)
            {
                String avalue = "";
                jswLabel alabel = null;
                if (r > maxr)
                {
                    avalue = null;
                    alabel = new jswLabel("..");
                } else
                {
                    Result aresult = arace.getResult(r);

                    if (aresult == null)
                    {
                        alabel = new jswLabel("");
                    } else
                    {
                        Sail foundsail;
                        foundsail = arace.getResult(r).sail;
                        if (foundsail == null || foundsail.getSailnumber() == null)
                        {
                            alabel = new jswLabel(arace.getResult(r).flag);
                            alabel.setStyleAttribute("foregroundcolor", "red");
                            alabel.applyStyle();
                        } else
                        {
                            //  String cs = foundsail.toCypherString();
                            //   alabel = new jswLabel(cs);
                            avalue = foundsail.toCypherString();
                            alabel = new jswLabel(avalue);
                        }
                    }
                    jswCell acell = datagrid.addCell(alabel, r + 2, c + 1);
                    acell.addMouseListener(acell);
                    alabel.addMouseListener(acell);
                }
                if (isactivecell(c + 1, r + 1))
                {
                    //avalue ="***";
                    activebox = new jswTextBox(this, avalue, 100, "activecelltextbox");
                    activebox.setText(avalue);
                    jswCell acell = datagrid.addCell(activebox, r + 2, c + 1);
                    activebox.addMouseListener(acell);
                    activebox.addKeyListener(this);
                    //  activebox.setMinimumSize(new Dimension(100,40));
                    activebox.applyStyle();
                    EventQueue.invokeLater(() -> activebox.getTextField().requestFocusInWindow());
                }
            }
            c++;
        }
        raceresults.setStyleAttribute("borderwidth", 2);
        raceresults.setPadding(5, 5, 5, 5);
        raceresults.applyStyle();
        return raceresults;
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

                    Sail foundsail = raceday.competition.competitors.getSail(cd, raceday.boatcyphers, raceday.competition.getClubString());
                    if (foundsail == null)
                    {
                        //alabel = new jswLabel(cd);
                        // racematrix.setCell(nc, nr, cd);
                        raceday.getRace(nc - 1).extendrace(nr + 1);
                        raceday.getRace(nc - 1).setResult(nr - 1, cd + "*");
                    } else
                    {

                        //   racematrix.setCell(nc, nr, foundsail.toCypherString());
                        raceday.getRace(nc - 1).extendrace(nr + 1);
                        raceday.getRace(nc - 1).setResult(nr - 1, foundsail.toCypherString());
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
            maingui.mode = COMPETITION;
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
            raceday.saved = false;
        } else if (command.equalsIgnoreCase("removeraceday"))
        {
            raceday.competition.removeRaceday(raceday.infilename);
            String compfile = compgui.currentcomp.competitionfile;
            compgui.currentcomp.readCompetitionFromXML(compfile);
            maingui.mode = COMPETITION;
            maingui.refreshGui();
            revalidate();
        } else if (command.equalsIgnoreCase("Export Raceday to HTML"))
        {
            activecell.put("r", -1);
            final File directorylock = new File(raceday.competition.outputdir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("html", "html");
            chooser.setFileFilter(filter);

            String outfile = raceday.infilename.replace(".rxml", ".html");
            Path apath = Paths.get(outfile);
            chooser.setSelectedFile(new File(apath.getFileName().toString()));
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
            final File directorylock = new File(raceday.competition.racedir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Racedays", "rxml");
            chooser.setFileFilter(filter);
            String outfilename = raceday.outfilename;
            chooser.setSelectedFile(new File(outfilename));
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File outfile = chooser.getSelectedFile();
                String selfile = outfile.getPath();
                System.out.println("You chose to save to this file: " + selfile);
                raceday.outfilename = selfile;
                raceday.saveToXML_new(selfile);
                if (raceday.filenameHasChanged())
                {
                    raceday.competition.saved = false;
                    raceday.competition.saveCompetition();
                }
                raceday.saved = true;
            }
            racedayheader.removeAll();
            racedayheader.add(" FILLW ", displayheader());
            raceresults.removeAll();
            jswPanel apanel = displayresults(table1styles());
            raceresults.add(" FILLW FILLH ", apanel);
        } else if (command.equalsIgnoreCase("Reload Raceday"))
        {
            final File directorylock = new File(raceday.competition.racedir);
            JFileChooser chooser = new JFileChooser(directorylock);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("racedays", "rxml");
            chooser.setFileFilter(filter);
            String infile = raceday.infilename;
            System.out.println("Reloading" + infile);
            chooser.setSelectedFile(new File(infile));
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                activecell.put("r", -1);
                String selfile = chooser.getSelectedFile().getPath();
                System.out.println("You chose to open this file: " + selfile);
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
            raceday.setBoatclass_str(raceclass.getText());
            editheader = false;
            raceday.outfilename = raceday.makefilename();
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
            int c = Integer.parseInt(cmdmap.get("column"));
            int r = Integer.parseInt(cmdmap.get("row"));
            String value = cmdmap.get("cellcontent");
            currentcell = new cell(c, r - 1, value);
            racematrix.setCell(c, r - 1, value);
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
            Boolean sel = racematrix.getSelected().get(col - 1);
            racematrix.getSelected().set(col - 1, !sel);
        } else if (command.equalsIgnoreCase("viewcompetition"))
        {
            maingui.mode = COMPETITION;
            String selfile = mysailinghome + currentcompetitionfile;
            System.out.println("reopening: " + selfile);
            Mainrace_gui.mainpanel.removeAll();
            Mainrace_gui.mainpanel.add(" FILLH FILLW ", compgui);
            revalidate();
        } else if (cmdmap.get("jswCheckbox") != null)
        {
            int nc = activecell.get("c");
            int nr = activecell.get("r");
            System.out.println("edited cell qqq " + nc + ":" + nr + "=" + cmdmap.get("column"));
        } else if (cmdmap.get("handlerclass") != null && cmdmap.get("handlerclass").equalsIgnoreCase("jswTextBox"))
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
                    String nextcell = racematrix.getCell(nc, rn + 1);
                    racematrix.setCell(nc, rn + 1, thiscell);
                    thiscell = nextcell;
                    rn++;
                }
            } else if (cd.equalsIgnoreCase("d"))
            {
                int rn = nr;
                String nextcell = racematrix.getCell(nc, rn + 1);
                while (nextcell != null && !nextcell.trim().isEmpty())
                {
                    racematrix.setCell(nc, rn, nextcell);
                    nextcell = racematrix.getCell(nc, rn + 2);
                    rn++;
                }
                racematrix.setCell(nc, rn, " ");
            } else
            {
                if (!cd.isEmpty())
                {
                    try
                    {
                        int ti = Integer.parseInt(cmdmap.get("textboxvalue"));
                        racematrix.setCell(nc, nr, "" + ti);
                    } catch (NumberFormatException ex)
                    {
                        racematrix.setCell(nc, nr, " ");
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
        Mainrace_gui.maingui.revalidate();
        Mainrace_gui.maingui.repaint();
        Mainrace_gui.maingui.pack();
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
        int maxcol = racematrix.getColumn(nc - 1).size();
        keycount++;
        if (keycount < 2)
        {
            System.out.println(" keypressed ++" + cd + "++");
            if (cd.equalsIgnoreCase("i"))
            {
                // String thiscell = currentcell.value;

                int rn = nr;
                raceday.getRace(nc - 1).resultlist.add(rn - 1, null);

            } else if (cd.equalsIgnoreCase("d"))
            {
                int rn = nr;
                raceday.getRace(nc - 1).resultlist.remove(rn - 1);
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
        Mainrace_gui.maingui.revalidate();
        Mainrace_gui.maingui.repaint();
        Mainrace_gui.maingui.pack();
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
