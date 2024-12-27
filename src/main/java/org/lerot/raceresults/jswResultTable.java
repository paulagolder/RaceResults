package org.lerot.raceresults;


import org.lerot.mywidgets.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class jswResultTable extends jswPanel implements ActionListener
{


    public jswResultTable(ActionListener al, String name)
    {
        super(name);
        ActionListener actionlistener = al;
        jswTable grid2 = new jswTable(this, name, table2styles());
        add(" FILLW FILLH ",grid2);
        int nraces=5;
        int nparts=20;
        jswPanel[][] cellMap2 = new  jswPanel[nraces][nparts];;
        for (int r= 0; r < nraces; r++)
        {
            for (int p = 0; p < nparts; p++)
            {
                cellMap2[r][p] = new jswHorizontalPanel();
                grid2.addCell(cellMap2[r][p], " minwidth=100 ", p, r);
                cellMap2[r][p].applyStyle();
            }
        }
        for (int r= 1; r < nraces; r++)
        {
            cellMap2[r][0].add(" middle ",new jswLabel("Race "+(char)(65+r)));
            //  grid2.addCell(cellMap2[r][0], " minwidth=100 ", 0, r);
            cellMap2[r][0].applyStyle();
        }

        jswLabel label00= new jswLabel("Position");
        label00.setStyleAttribute("fontsize",16);
        label00.applyStyle();
        cellMap2[0][0].add(" ",label00);
        for (int p = 1; p < nparts; p++)
        {
            cellMap2[0][p].add(" middle ",new jswLabel(p));
            cellMap2[0][p].applyStyle();
        }
        for (int r= 1; r < nraces; r++)
        {
            for (int p = 1; p < nparts; p++)
            {
                jswTextBox tb = new jswTextBox(this, " " + r + ":" + p);
                tb.setText("??");
                cellMap2[r][p].add(" middle FILLW FILLH ",tb);
                cellMap2[r][p].applyStyle(cellStyle());
            }
        }
    }



    public jswStyle cellStyle()
    {
        jswStyle newstyle = new jswStyle();
        newstyle.setPadding(0);
        newstyle.setBackgroundcolor("green");
        return newstyle;
    }

    public static jswStyles table2styles()
    {
        jswStyles tablestyles = new jswStyles();
        tablestyles = jswStyles.getDefaultTableStyles();
        tablestyles.name = "defaulttable";

        jswStyle tablestyle = tablestyles.makeStyle("table");
        tablestyle.putAttribute("borderwidth", 2);
        tablestyle.putAttribute("bordercolor", "green");

        jswStyle rowstyle = tablestyles.makeStyle("row");
        rowstyle.putAttribute("minheight", 100);

        jswStyle colstyle = tablestyles.makeStyle("col");
        colstyle.putAttribute("horizontalAlignment", "RIGHT");
        colstyle.putAttribute("maxwidth", 100);
        colstyle.putAttribute("foregroundcolor", "yellow");

        jswStyle col0style = tablestyles.makeStyle("col0");
        col0style.putAttribute("horizontalAlignment", "MIDDLE");
        col0style.putAttribute("maxwidth", 100);
        col0style.putAttribute("foregroundcolor", "yellow");

        jswStyle cellstyle = tablestyles.makeStyle("cell");
        cellstyle.putAttribute("foregroundcolor", "yellow");
        cellstyle.putAttribute("borderWidth", "1");
        cellstyle.putAttribute("borderColor", "blue");
        cellstyle.putAttribute( "fontsize",16);
        return tablestyles;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println(" here we are 2 ");
        HashMap<String, String> ae = utils.parseaction(e.getActionCommand());
        String cmd =  ae.get("handlerclass");
        if( cmd.equalsIgnoreCase("jswTextBox"))
        {
            System.out.println( "cell :"+ ae.get("handlername"));
            System.out.println( "value:"+ ae.get("commandstring"));
            String strvalue = ae.get("commandstring");
        }
        else
            System.out.println(" not cell ");
    }







}
