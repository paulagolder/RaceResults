package org.lerot.raceresults;

import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.*;

import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.lerot.raceresults.Mainrace_gui.smalltable1styles;

public class PositionList
{
    LinkedHashMap<String, Position> list;
    Competition currentcomp;
    private String cssid = "datamatrix";
    private String cssclass = "datamatrix";

    public PositionList(Competition comp)
    {
        this.list = new LinkedHashMap<String, Position>();
        currentcomp = comp;
    }

    public void add(Position aposition)
    {
        String key = aposition.getSail();
        list.put(key, aposition);
    }

    public jswVerticalPanel displayresults()
    {
        jswVerticalPanel scores = new jswVerticalPanel("Scores", false, false);
        scores.setStyleAttribute("borderwidth", 2);
        scores.setPadding(10, 10, 10, 10);
        int ncols = 5;
        int nrows = list.size();
        scores.setStyleAttribute("horizontallayoutstyle", jswLayout.MIDDLE);
        jswHorizontalPanel scoresheader = new jswHorizontalPanel("heading", false, false);
        jswLabel addb = new jswLabel(currentcomp.getRaceclasses());
        addb.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        scoresheader.add(" ", addb);
        jswLabel dt = new jswLabel(currentcomp.getCompyear());
        dt.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        scoresheader.add(" ", dt);
        ActionListener al = null;
        scores.add("  ", scoresheader);
        scores.add("  ", makedatapanel(smalltable1styles()));
        scores.applyStyle();
        scores.setPadding(10, 10, 10, 10);
        return scores;
    }

    private jswTable makedatapanel(jswStyles tablestyles)
    {
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("Sail"), 0, 0);
        datagrid.addCell(new jswLabel("Sailor"), 0, 1);
        datagrid.addCell(new jswLabel("Races"), 0, 2);
        datagrid.addCell(new jswLabel("Points"), 0, 3);
        datagrid.addCell(new jswLabel("<html>Race<br/>Points</html>"), 0, 4);
        datagrid.addCell(new jswLabel("<html>Missing<br/>RacePoints</html>"), 0, 5);
        int r = 0;
        for (Map.Entry<String, Position> anentry : list.entrySet())
        {
            Position aposition = anentry.getValue();
            datagrid.addCell(new jswLabel(aposition.getSail()), r + 1, 0);
            datagrid.addCell(new jswLabel(aposition.getSailor()), r + 1, 1);
            datagrid.addCell(new jswLabel(aposition.getRaces()), r + 1, 2);
            datagrid.addCell(new jswLabel(aposition.getPoints()), r + 1, 3);
            datagrid.addCell(new jswLabel(aposition.getRace_points()), r + 1, 4);
            datagrid.addCell(new jswLabel(aposition.getMissing_race_points()), r + 1, 5);
            r++;
        }
        return datagrid;
    }

    public void printResultsToHTML(String path, String comptitle)
    {
        try
        {
            File file = new File(path);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(" <!DOCTYPE html>\n<html>\n");
            bw.write("<head>\n<style>");
            bw.write(" td { text-align: center; }\n");
            bw.write(" table, th, td { border: 1px solid; }\n");
            bw.write("</style>\n</head>\n<body>\n");
            bw.write("<table id=\"" + getCssid() + "\" class=\"" + getCssclass() + "\">\n");
            bw.write("<tr><td>Sail</td><td>sailor</td><td>No Races</td><td>Points</td><td>Race</br>Points</td><td>Missing<br>racepoints</td></tr>");
            for(Map.Entry<String, Position> entry: list.entrySet())
            {
                Position posn = entry.getValue();
                bw.write(posn.toHTML());
            }
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public String getCssid()
    {
        return cssid;
    }

    public void setCssid(String cssid)
    {
        this.cssid = cssid;
    }

    public String getCssclass()
    {
        return cssclass;
    }

    public void setCssclass(String cssclass)
    {
        this.cssclass = cssclass;
    }

    public PositionList rankMap()
    {
        TreeMap<Integer,String> rmap = new TreeMap<Integer,String>();
        int n=1;
        for(Map.Entry<String, Position> entry: list.entrySet())
        {
            Position posn = entry.getValue();
            rmap.put( posn.getPoints()*1000+n, entry.getKey());
            n++;
        }
        PositionList rposmap = new PositionList(currentcomp);
        for(Map.Entry<Integer,String> entry: rmap.entrySet())
        {
            String key = entry.getValue();
            Position apos = list.get(key);
            rposmap.add(apos);
        }
        return rposmap;
    }
}
