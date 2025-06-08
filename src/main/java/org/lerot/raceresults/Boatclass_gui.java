package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import static org.lerot.raceresults.Mainrace_gui.*;

public class Boatclass_gui extends jswVerticalPanel implements ActionListener
{

    //private final ActionListener parentlistener;
    private jswOptionset classoptions;
    private int indexselected=0;
    private BoatclassList classlist;
    private String selectedclasskey = "DF95";
    Boatclass selectedclass = null;
    private jswTextBox keyfield;
    private jswTextBox fullnamefield;
    private jswTextBox cypherfield;

    public Boatclass_gui(BoatclassList cl)
    {
        super("Class Gui",false,false);
        //makeheadermenu();
        classlist = cl;
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
        JMenuItem fmenuItem1 = new JMenuItem("Save BoatclassList");
        fmenuItem1.addActionListener(this);
        fmenu.add(fmenuItem1);
    }

    public jswVerticalPanel makeBoatclassgui()
    {
        makeheadermenu();
            jswVerticalPanel editpanel = new jswVerticalPanel("Class List", false, false);
            jswTable datagrid = new jswTable(null, "", Mainrace_gui.smalltable3styles());
            datagrid.addCell(new jswLabel("Select"), 0, 0);
            datagrid.addCell(new jswLabel("key"), 0, 1);
            datagrid.addCell(new jswLabel("fullname"), 0, 2);
            datagrid.addCell(new jswLabel("cypher"), 0, 3);
            selectedclass =  classlist.getClassVector().get(0);
            int r= 0;
            classoptions = new jswOptionset(this,"cluboptions",true,false,false);
            for (Map.Entry<String,Boatclass> anentry : classlist.entrySet())
            {
                Boatclass aclub = anentry.getValue();
                jswOption opt1 = classoptions.addNewOption("", aclub.getKey(),true);
                datagrid.addCell(opt1,r+1,0);
                datagrid.addCell(new jswLabel(aclub.getKey()), r + 1, 1);
                datagrid.addCell(new jswLabel(aclub.getFullName()), r + 1, 2);
                datagrid.addCell(new jswLabel(aclub.getCypher()), r + 1, 3);

                if(aclub.getKey().equals(selectedclasskey))
                {
                    opt1.setSelected();
                    selectedclass =aclub;
                }
                r++;
            }

            editpanel.add("  ", datagrid);
            editpanel.applyStyle();
            editpanel.add(classForm(this, selectedclass));
            jswHorizontalPanel buttonpanel = new jswHorizontalPanel("buttons", false, false);
            jswButton save = new jswButton(this,"Save","saveclass");
            jswButton delete = new jswButton(this,"Delete","deleteclass");
            jswButton newclub = new jswButton(this,"New Class","newclass");
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
        if (command.startsWith("cluboptions"))
        {
            String foundclub = classoptions.getSelectedoption();
            System.out.println(classoptions.getSelectedoption());
            for (Map.Entry<String,Boatclass> anentry : classlist.entrySet())
            {
                System.out.println(anentry.getKey());
                if (foundclub.equalsIgnoreCase(anentry.getKey()))
                {
                    selectedclass = anentry.getValue();
                    selectedclasskey = anentry.getKey();
                }
            }
            System.out.println(selectedclass);
        }
        if (command.startsWith("newclass"))
        {
            Boatclass aclub = new Boatclass("NC","newclass","x");
                classlist.put((aclub.getKey()),aclub);
        }
        if (command.startsWith("saveclass"))
        {
            Boatclass aclub = new Boatclass(keyfield.getText(),fullnamefield.getText(),cypherfield.getText());
            classlist.put(aclub.getKey(), aclub);
        }
        if (command.startsWith("deleteclass"))
        {
            classlist.remove(selectedclass.getKey());
            for (Map.Entry<String,Boatclass> anentry : classlist.entrySet())
            {
                Boatclass aclub = anentry.getValue();
                if(aclub.getKey().equals(homeclub))
                {
                    classoptions.setSelected(selectedclasskey);
                    selectedclass = aclub;
                }
            }
        }
        if (command.startsWith("Save BoatclassList"))
        {
            try
            {
                File file = new File(mframe.Boatclassfile);
                if (!file.exists())
                {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                bw.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
                bw.write(classlist.toXML());
                bw.close();
            } catch (Exception e2)
            {
                System.out.println(e2);
            }
        }
        mframe.refreshGui();
    }

    public  jswVerticalPanel classForm(ActionListener al, Boatclass aclass)
    {
        jswVerticalPanel form = new jswVerticalPanel("classform", false, false);
        jswHorizontalPanel row1 = new jswHorizontalPanel("row1", false, false);
        jswLabel label1 = new jswLabel("Key");
        keyfield = new jswTextBox(al, aclass.getKey(), 200, "classform");
        keyfield.setText(aclass.getKey());
        row1.add(" ", label1);
        row1.add(" ", keyfield);
        form.add(" ", row1);
        jswHorizontalPanel row2 = new jswHorizontalPanel("row2", false, false);
        jswLabel label2 = new jswLabel("FullName");
        fullnamefield = new jswTextBox(al, aclass.getFullName(), 200, "classorm");
        fullnamefield.setText(aclass.getFullName());
        row2.add(" ", label2);
        row2.add(" ", fullnamefield);
        form.add(" ", row2);
        jswHorizontalPanel row3 = new jswHorizontalPanel("row3", false, false);
        jswLabel label3 = new jswLabel("Cypher");
        cypherfield = new jswTextBox(al, aclass.getCypher(), 200, "classform");
        cypherfield.setText(aclass.getCypher());
        row3.add(" ", label3);
        row3.add(" ", cypherfield);
        form.add(" ", row3);
        return form;
    }
}
