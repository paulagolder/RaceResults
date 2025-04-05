package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.*;

public class Club_gui  extends jswVerticalPanel implements ActionListener
{

    //private final ActionListener parentlistener;
    private jswOptionset cluboptions;
    private int indexselected=0;
    private ClubList clublist;
    private String selectedclubkey = homeclub;
    Club selectedclub= null;
    private jswTextBox keyfield;
    private jswTextBox fullnamefield;
    private jswTextBox cypherfield;

    public Club_gui(ClubList cl)
    {
        super("Club Gui",false,false);
        makeheadermenu();
        clublist = cl;
    }

    private void makeheadermenu()
    {
        if(mainmenubar.getMenuCount()>2)
        {
            mainmenubar.remove(2);
        }
        if(mainmenubar.getMenuCount()>1)
        {
            mainmenubar.remove(1);
        }
        JMenu fmenu = new JMenu("File");
        mainmenubar.add(fmenu);
        JMenuItem fmenuItem1 = new JMenuItem("Save ClubList");
        fmenuItem1.addActionListener(this);
        fmenu.add(fmenuItem1);
    }

    public jswVerticalPanel makeClubgui()
    {
        makeheadermenu();
            jswVerticalPanel editpanel = new jswVerticalPanel("Club List", false, false);
            jswTable datagrid = new jswTable(null, "", Mainrace_gui.smalltable3styles());
            datagrid.addCell(new jswLabel("Select"), 0, 0);
            datagrid.addCell(new jswLabel("key"), 0, 1);
            datagrid.addCell(new jswLabel("fullname"), 0, 2);
            datagrid.addCell(new jswLabel("cypher"), 0, 3);

            int r= 0;
            cluboptions = new jswOptionset(this,"cluboptions",true,false,false);
            for (Map.Entry<String,Club> anentry :clublist.entrySet())
            {
                Club aclub = anentry.getValue();
                jswOption opt1 = cluboptions.addNewOption("", aclub.getKey(),true);
                datagrid.addCell(opt1,r+1,0);
                datagrid.addCell(new jswLabel(aclub.getKey()), r + 1, 1);
                datagrid.addCell(new jswLabel(aclub.getFullName()), r + 1, 2);
                datagrid.addCell(new jswLabel(aclub.getCypher()), r + 1, 3);

                if(aclub.getKey().equals(selectedclubkey))
                {
                    opt1.setSelected();
                    selectedclub=aclub;
                }
                r++;
            }
            editpanel.add("  ", datagrid);
            editpanel.applyStyle();
            editpanel.add(clubForm(this, selectedclub));
            jswHorizontalPanel buttonpanel = new jswHorizontalPanel("buttons", false, false);
            jswButton save = new jswButton(this,"Save","saveclub");
            jswButton delete = new jswButton(this,"Delete","deleteclub");
            jswButton newclub = new jswButton(this,"New Club","newclub");
            buttonpanel.add (" ",save);
            buttonpanel.add (" ",delete);
            buttonpanel.add (" ",newclub);
            editpanel.add(" ",buttonpanel);
            return editpanel;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        System.out.println(" here we are cl " + command );
        if (command.startsWith("cluboptions"))
        {
            jswOptionset optionset = (jswOptionset) e.getSource();
            selectedclubkey =  optionset.getSelectedoption();
            System.out.println( optionset.getSelectedoption());
            for (Map.Entry<String,Club> anentry :clublist.entrySet())
            {
                Club aclub = anentry.getValue();
                if(aclub.getKey().equals(selectedclubkey))
                {
                   cluboptions.setSelected(selectedclubkey);
                   selectedclub = aclub;
                }
            }
        }
        if (command.startsWith("newclub"))
        {
                Club aclub = new Club("NC","newclub","x");
                clublist.put((aclub.getKey()),aclub);
        }
        if (command.startsWith("saveclub"))
        {
            Club aclub = new Club(keyfield.getText(),fullnamefield.getText(),cypherfield.getText());
            clublist.put(aclub.getKey(), aclub);
        }
        if (command.startsWith("deleteclub"))
        {
            clublist.remove(selectedclub.getKey());
            for (Map.Entry<String,Club> anentry :clublist.entrySet())
            {
                Club aclub = anentry.getValue();
                if(aclub.getKey().equals(homeclub))
                {
                    cluboptions.setSelected(selectedclubkey);
                    selectedclub = aclub;
                }
            }
        }
        if (command.startsWith("Save ClubList"))
        {
            try
            {
                File file = new File(mframe.clubsfile);
                if (!file.exists())
                {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                bw.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
                bw.write(clublist.toXML());

                bw.close();
            } catch (Exception e2)
            {
                System.out.println(e2);
            }
        }

       
        mframe.refreshGui();
    }

    public  jswVerticalPanel clubForm(ActionListener al, Club aclub)
    {
        jswVerticalPanel form = new jswVerticalPanel("clubform", false, false);
        jswHorizontalPanel row1 = new jswHorizontalPanel("row1", false, false);
        jswLabel label1 = new jswLabel("Key");
        keyfield = new jswTextBox(al, aclub.getKey(), 200, "clubform");
        keyfield.setText(aclub.getKey());
        row1.add(" ", label1);
        row1.add(" ", keyfield);
        form.add(" ", row1);
        jswHorizontalPanel row2 = new jswHorizontalPanel("row2", false, false);
        jswLabel label2 = new jswLabel("FullName");
        fullnamefield = new jswTextBox(al, aclub.getFullName(), 200, "clubform");
        fullnamefield.setText(aclub.getFullName());
        row2.add(" ", label2);
        row2.add(" ", fullnamefield);
        form.add(" ", row2);
        jswHorizontalPanel row3 = new jswHorizontalPanel("row3", false, false);
        jswLabel label3 = new jswLabel("Cypher");
        cypherfield = new jswTextBox(al, aclub.getCypher(), 200, "clubform");
        cypherfield.setText(aclub.getCypher());
        row3.add(" ", label3);
        row3.add(" ", cypherfield);
        form.add(" ", row3);
        return form;
    }
}
