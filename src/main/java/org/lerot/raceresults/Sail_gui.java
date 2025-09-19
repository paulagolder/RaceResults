package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.*;

public class Sail_gui extends jswVerticalPanel implements ActionListener
{

    private final SailList allsaillist;
    private final Vector<jswOption> optlist = new Vector<jswOption>();
    private final String csssaillist = "saillist";
    jswOptionset sailoptions;
    private SailList saillist;
    private Sail selectedsail;
    private jswTextBox sailnofield;
    private jswTextBox sailornamefield;
    private jswTextBox classfield;
    private jswTextBox clubfield;
    private String classfilter = "DF95";
    private jswPushButtonset classoptions;
    private String clubfilter = homeclub;
    private jswPushButtonset cluboptions;

    public Sail_gui(SailList sl)
    {
        super("Sail Gui", false, false);
        allsaillist = sl;

        allsaillist.makefilters();
    }

    public jswVerticalPanel makeSailgui()
    {
        allsaillist.makefilters();
        saillist = allsaillist.makeList(classfilter, clubfilter);
        makeheadermenu();
        jswVerticalPanel saillistpanel = new jswVerticalPanel("Sail List", false, false);
        saillistpanel.setStyleAttribute("backgroundcolor", "white");
        jswHorizontalPanel cluboptionpanel = new jswHorizontalPanel("cluboptions", false, false);
        cluboptions = new jswPushButtonset(this, "cluboptions", false, false, false);
        for (String aclub : allsaillist.clubsfilterlist)
        {
            jswPushButton clubbutton = cluboptions.addNewButton(aclub, aclub);
            cluboptionpanel.add(" ", clubbutton);
            if (clubfilter.equalsIgnoreCase(aclub)) clubbutton.setSelected(true);
        }
        saillistpanel.add(" ", cluboptionpanel);
        jswHorizontalPanel optionpanel = new jswHorizontalPanel("options", false, false);
        classoptions = new jswPushButtonset(this, "classoptions", false, false, false);
        for (String aclass : allsaillist.classesfilterlist)
        {
            jswPushButton sailbutton = classoptions.addNewButton(aclass, aclass);
            optionpanel.add(" ", sailbutton);
            if (classfilter.equalsIgnoreCase(aclass)) sailbutton.setSelected(true);
        }
        saillistpanel.add(" ", optionpanel);
        jswHorizontalPanel fillerpanel = new jswHorizontalPanel("options", false, false);
        saillistpanel.add(" minheight=10 ", fillerpanel);
        if (saillist.size() > 0)
        {
            jswTable datagrid = new jswTable(null, "", Mainrace_gui.smalltable3styles());
            datagrid.addCell(new jswLabel("Select"), 0, 0);
            datagrid.addCell(new jswLabel("sailNo"), 0, 1);
            datagrid.addCell(new jswLabel("Sailor"), 0, 2);
            datagrid.addCell(new jswLabel("Class"), 0, 3);
            datagrid.addCell(new jswLabel("Club"), 0, 4);
            int r = 0;
            sailoptions = new jswOptionset(this, "sailoptions", true, false, false);
            Sail firstsail = null;
            for (Sail asail : saillist)
            {
                if (firstsail == null) firstsail = asail;
                jswOption opt1 = sailoptions.addNewOption("", asail.getSailnumberStr(), true);
                opt1.setTag("" + r);
                optlist.add(opt1);
                datagrid.addCell(opt1, r + 1, 0);
                opt1.setStyleAttribute("backgroundcolor", "white");
                opt1.applyStyle();
                datagrid.addCell(new jswLabel(asail.getSailnumberStr()), r + 1, 1);
                datagrid.addCell(new jswLabel(asail.getSailorname()), r + 1, 2);
                datagrid.addCell(new jswLabel(asail.getBoatclass()), r + 1, 3);
                datagrid.addCell(new jswLabel(asail.getClub()), r + 1, 4);
                if (selectedsail != null && asail.matches(selectedsail))
                {
                    opt1.setSelected(true);
                }
                r++;
            }
            if (selectedsail == null)
            {
                optlist.get(0).setSelected(true);
                selectedsail = firstsail;
            }
            saillistpanel.add(" ", datagrid);
            saillistpanel.applyStyle();
            jswHorizontalPanel fillerpanel3 = new jswHorizontalPanel("options", false, false);
            saillistpanel.add(" minheight=10 ", fillerpanel3);
            saillistpanel.add(sailForm(this, getSelectedsail()));
            jswHorizontalPanel fillerpanel2 = new jswHorizontalPanel("options", false, false);
            saillistpanel.add(" minheight=10 ", fillerpanel2);
            jswHorizontalPanel buttonpanel = new jswHorizontalPanel("buttons", false, false);
            jswButton save = new jswButton(this, "Save", "savesail");
            jswButton delete = new jswButton(this, "Delete", "deletesail");
            jswButton newsail = new jswButton(this, "New Sail", "newsail");
            buttonpanel.add(" ", save);
            buttonpanel.add(" ", delete);
            buttonpanel.add(" ", newsail);
            saillistpanel.add(" ", buttonpanel);
        } else
        {
            jswHorizontalPanel noitemspanel = new jswHorizontalPanel("noitems", false, false);
            jswLabel nil = new jswLabel(" NO ITEMS IN LIST ");
            noitemspanel.add(" MIDDLE ", nil);
            saillistpanel.add(" ", noitemspanel);
        }
        saillistpanel.setStyleAttribute("backgroundcolor", "white");
        saillistpanel.applyStyle();

        return saillistpanel;
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        //System.out.println(" here we are sl " + command);
        if (command.startsWith("sailoption"))
        {
            String foundsail = sailoptions.getSelectedoption();
            System.out.println(sailoptions.getSelectedoption());
            int rownumber = Integer.parseInt(foundsail);
            int r = 0;
            for (Sail asail : saillist)
            {
                if (r == rownumber)
                {
                    selectedsail = asail;
                }
                r++;
            }
        } else if (command.startsWith("classoptions"))
        {
            String foundclass = classoptions.getSelection();
            if (foundclass != null) classfilter = foundclass;
            selectedsail = null;
        } else if (command.startsWith("cluboptions"))
        {
            String foundclub = cluboptions.getSelection();
            if (foundclub != null) clubfilter = foundclub;
            selectedsail = null;
        } else if (command.startsWith("newsail"))
        {
            Sail asail = new Sail("000", "new", "sailor", classfilter, clubfilter);
            allsaillist.add(asail);
            saillist.add(asail);
            selectedsail = asail;
        } else if (command.startsWith("savesail"))
        {
            String boatclass= classfield.getText();
            String club = clubfield.getText();
            Sail asail = new Sail(sailnofield.getText(), sailornamefield.getText(), boatclass, club);
            if(boatclass.equalsIgnoreCase(classfilter) && club.equalsIgnoreCase(clubfilter))
            {
                saillist.remove(getSelectedsail());
                allsaillist.remove(selectedsail);
                saillist.add(asail);
                allsaillist.add(asail);
            }
            else
            {

                allsaillist.add(asail);
            }
            selectedsail = asail;
        } else if (command.startsWith("deletesail"))
        {
            allsaillist.remove(selectedsail);
            saillist.remove(selectedsail);
            selectedsail = null;
        } else if (command.startsWith("Save SailList"))
        {
            try
            {
                File file = new File(dotmysailing + maingui.saillistfile);
                saveSailist(dotmysailing + maingui.saillistfile, homeclub);
                for(String aclub : allsaillist.clubsfilterlist)
                {
                    saveSailist(mysailinghome + aclub+"_saillist.xml", aclub);
                }
            } catch (Exception e2)
            {
                System.out.println(e2);
            }
        } else if (command.equalsIgnoreCase("Print SailList To HTML"))
        {
            String outfile = "saillist_" + homeclub + ".html";
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
                allsaillist.printSailnumberToHTML(selfile);
            }
        } else if (command.equalsIgnoreCase("Print Sailor List To HTML"))
        {
            String outfile = "namelist_" + homeclub + ".html";
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
                allsaillist.printBySailorToHTML(selfile);
            }
        }
        maingui.refreshGui();
    }


    public jswVerticalPanel sailForm(ActionListener al, Sail asail)
    {
        jswVerticalPanel form = new jswVerticalPanel("clubform", false, false);
        jswHorizontalPanel row1 = new jswHorizontalPanel("row1", false, false);
        jswLabel label1 = new jswLabel("Sail No");
        sailnofield = new jswTextBox(al, asail.getSailnumberStr(), 200, "clubform");
        sailnofield.setText(asail.getSailnumberStr());
        row1.add(" ", label1);
        row1.add(" ", sailnofield);
        form.add(" ", row1);
        jswHorizontalPanel row2 = new jswHorizontalPanel("row2", false, false);
        jswLabel label2 = new jswLabel("Sailor Name");
        sailornamefield = new jswTextBox(al, asail.getSailorname(), 200, "clubform");
        sailornamefield.setText(asail.getSailorname());
        row2.add(" ", label2);
        row2.add(" ", sailornamefield);
        form.add(" ", row2);
        jswHorizontalPanel row3 = new jswHorizontalPanel("row3", false, false);
        jswLabel label3 = new jswLabel("Class");
        classfield = new jswTextBox(al, asail.getBoatclass(), 200, "clubform");
        classfield.setText(asail.getBoatclass());
        row3.add(" ", label3);
        row3.add(" ", classfield);
        form.add(" ", row3);
        jswHorizontalPanel row4 = new jswHorizontalPanel("row4", false, false);
        jswLabel label4 = new jswLabel("Club");
        clubfield = new jswTextBox(al, asail.getClub(), 200, "clubform");
        clubfield.setText(asail.getClub());
        row4.add(" ", label4);
        row4.add(" ", clubfield);
        form.add(" ", row4);
        return form;
    }

    public Sail getSelectedsail()
    {
        return selectedsail;
    }

    public void setSelectedsail(Sail selectedsail)
    {
        this.selectedsail = selectedsail;
    }

    private void makeheadermenu()
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
        JMenuItem fmenuItem1 = new JMenuItem("Save SailList");
        fmenuItem1.addActionListener(this);
        fmenu.add(fmenuItem1);
        JMenuItem fmenuItem2 = new JMenuItem("Print SailList To HTML");
        fmenuItem2.addActionListener(this);
        fmenu.add(fmenuItem2);
        JMenuItem fmenuItem3 = new JMenuItem("Print Sailor List To HTML");
        fmenuItem3.addActionListener(this);
        fmenu.add(fmenuItem3);
    }

    public void saveSailist(String filename, String club)
    {
        try
        {
            File file = new File(filename);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
            bw.write(allsaillist.toXML(club, utils.makeList(allsaillist.classesfilterlist)));
            bw.close();
            System.out.println("You chose to save to this file to xml: " + file.getAbsoluteFile());
        } catch (Exception e2)
        {
            System.out.println(e2);
        }
    }

}
