package org.lerot.raceresults;

import org.lerot.mywidgets.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ranksmatrix
{
    private String boatclass;
    private String raceyear;
    private dataMatrix datamatrix;
    private Competition currentcompetition;
    private int maxsailors;

    public ranksmatrix(Competition acompetition)
    {
        setCurrentcompetition(acompetition);
        setBoatclass(acompetition.getRaceclasses());
        setRaceyear(acompetition.getCompyear());
        int nsailors = currentcompetition.noCompetitors;
        datamatrix = new dataMatrix(nsailors, nsailors);
        datamatrix.setSelected(true);
        datamatrix.setRowname(currentcompetition.competitors.getVector());
        datamatrix.setColname(utils.makePositionNames(nsailors));
        datamatrix.getColname().set(0, "SAIL.");
        makeMatrix();
    }

    public void makeMatrix()
    {
        int ns = currentcompetition.noCompetitors;
        int maxsails = ns;
        maxsailors = 0;
        int[][] matrix2 = new int[ns + 1][ns + 1];
        for (int r = 0; r < maxsails; r++)
        {
            int totalscore = 0;
            for (int i = 0; i < currentcompetition.racedayfilenames.size(); i++)
            {
                Raceday aracedaymatrix = currentcompetition.getRacedayNo(i);
                for (int c = 0; c < aracedaymatrix.GetNoRaces(); c++)
                {
                    dataMatrix compmatrix = aracedaymatrix.getRacematrix().getValueMatrix(aracedaymatrix.getRacematrix().getColname(), currentcompetition.competitors.getVector());
                    String value = compmatrix.getValue(c, r);
                    int iv;
                    try
                    {
                        if (value != null)
                        {
                            iv = Integer.parseInt(value.trim());
                           // if(iv<ns+1)
                           // {
                                System.out.println(" saving " + iv + " " + r);
                                matrix2[iv][r] = matrix2[iv][r] + 1;
                          //  }
                            if (iv > maxsailors) maxsailors = iv;
                        }
                    } catch (NumberFormatException ex)
                    {
                        iv = 0;
                    }
                }
            }
        }
        maxsailors++;
        datamatrix.data.clear();
        Vector<String> sails = new Vector<String>();
        datamatrix.data.add(sails);

        for (Sail asail : currentcompetition.competitors)
        {
            sails.add(asail.getSailnumberStr());
        }

        for (int c = 1; c < maxsailors + 1; c++)
        {
            Vector<String> acol = new Vector<String>();
            int nrows = matrix2[c].length;
            for (int r = 0; r < nrows; r++)
            {
                int value = matrix2[c][r];
                acol.add(" " + value);
            }
            datamatrix.data.add(acol);
        }
    }

    public jswVerticalPanel displayresults(Competition_gui parent, jswStyles tablestyles, int maxsailors)
    {
        jswVerticalPanel raceresults = new jswVerticalPanel("RaceResults", false, false);
        raceresults.setPadding(2, 2, 2, 4);
        raceresults.setStyleAttribute("borderwidth", 1);
        int ncols = getNcols();
        int nrows = getNrows();
        jswHorizontalPanel racedayheader = new jswHorizontalPanel("heading", false, false);
        jswLabel dt = new jswLabel(getRacedate("/"));
        dt.applyStyle(Mainrace_gui.defaultStyles().getStyle("mediumtext"));
        racedayheader.add(" minheight=30 ", dt);
        raceresults.add(" FILLW minheight=30   ", racedayheader);
        raceresults.add("    ", datamatrix.makeresultspanel(parent.currentcomp.competitors, tablestyles, maxsailors));
        racedayheader.setStyleAttribute("height", 50);
        racedayheader.applyStyle();
        raceresults.applyStyle();
        raceresults.setPadding(5, 5, 5, 5);
        return raceresults;
    }

    public String getBoatclass()
    {
        return boatclass;
    }

    public void setBoatclass(String boatclass)
    {
        this.boatclass = boatclass;
    }

    public String getRaceyear()
    {
        return raceyear;
    }

    public void setRaceyear(String raceyear)
    {
        this.raceyear = raceyear;
    }

    public String getRacedate(String ch)
    {
        return raceyear.replace("/", ch);
    }

    public dataMatrix getDatamatrix()
    {
        return datamatrix;
    }

    public void setDatamatrix(dataMatrix datamatrix)
    {
        this.datamatrix = datamatrix;
    }

    public String getValue(int c, int r)
    {
        return datamatrix.getValue(c, r);
    }

    public int getNcols()
    {
        return datamatrix.getncols();
    }

    public int getNrows()
    {
        return datamatrix.getnrows();
    }

    public String getColname(int c)
    {
        return datamatrix.getColname().get(c);
    }

    public String getRowname(int r)
    {
        return datamatrix.getRowname().get(r);
    }


    public Competition getCurrentcompetition()
    {
        return currentcompetition;
    }

    public void setCurrentcompetition(Competition currentcompetition)
    {
        this.currentcompetition = currentcompetition;
    }

    public void printResultsToHTML(String path, String comptitle, TreeMap<SailNumber, Sail> boatlist, int maxsailors)
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
            printScoresToHTML(bw, comptitle + " " + boatclass + " " + raceyear, boatlist, maxsailors);
            bw.write("\n</body>\n</html>\n");
            bw.close();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }


    public void printScoresToHTML(BufferedWriter bw, String title, TreeMap<SailNumber, Sail> boatlist, int maxsailors) throws IOException
    {
        dataMatrix compmatrix = datamatrix;
        int ncols = compmatrix.data.size();
        int nrows = compmatrix.getRowname().size();
        bw.write("<table id=\"" + compmatrix.getCssid() + "\" class=\"" + compmatrix.getCssclass() + "\">\n");
        bw.write("<tr>\n<th  colspan=" + (ncols + 2) + ">" + title + "</th>\n</tr>");
        bw.write("<tr>\n<th>Sail No</th>");
        bw.write("<th>Sailor</th>");
        for (int c = 1; c < ncols; c++)
        {
            bw.write("<th>" + compmatrix.getColname().get(c) + "</th>");
        }
        bw.write("</tr>\n");
        for (int r = 0; r < nrows; r++)
        {
            bw.write("<tr><td>" + compmatrix.getRowname().get(r) + "</td>");
            bw.write("<td>" + boatlist.get(compmatrix.getRowname().get(r).trim()).getSailorname() + "</td>");
            for (int c = 1; c < ncols; c++)
            {
                bw.write("<td >" + compmatrix.data.get(c).get(r) + "</td>");
            }
            bw.write("</tr>\n");
        }
        bw.write("</table>\n");
    }

    public void setColnames(Vector<String> strings)
    {
        datamatrix.setColnames(strings);
    }

    public PositionList makePositionMap()
    {
        PositionList posmap = new PositionList(currentcompetition);
        int racecount = currentcompetition.getRacecount();
        Vector<String> saillist = currentcompetition.competitors.getVector();
        System.out.println(saillist.toString());
        TreeMap<String, Sail> competitors = currentcompetition.competitors.getMap();
        int totalscore = 0;
        System.out.println("here:" + currentcompetition.noCompetitors);
        int ncols = +datamatrix.data.size();
        TreeMap<String, Position> positionMap = new TreeMap<String, Position>();

        for (int r = 0; r < currentcompetition.noCompetitors; r++)
        {
            int totalraces = 0;
            String sn = datamatrix.data.get(0).get(r);
            Sail asail = competitors.get(sn);
            Position aposition = new Position();
            int score = 0;
            int races = 0;
            for (int c = 1; c < ncols; c++)
            {
                Vector<String> col = datamatrix.data.get(c);
                String value = col.get(r);
                int rankcount = Integer.parseInt(datamatrix.getValue(c, r).trim());
                totalraces +=rankcount;
                if (rankcount > 0)
                {
                    if (racecount > 0)
                    {
                        if (rankcount + races < racecount)
                        {
                            races += rankcount;
                            score += rankcount * (c);
                        } else if (races < racecount)
                        {
                            score += (racecount - races) * (c);
                            races = racecount;
                        } else
                        {
                        }
                    } else
                    {
                        races += rankcount;
                        score += rankcount * (c);
                    }
                }
            }

            aposition.setSailor(asail.getSailorname());
            aposition.setSail(asail.getSailnumberStr());
            aposition.setScoredraces(races);
            if(racecount>0)
            {
                int missingracepoints = (racecount - races) * maxsailors;
                aposition.setPoints(score + missingracepoints);
                aposition.setMissing_race_points(missingracepoints);
                aposition.setTotalraces(totalraces);
            }
            aposition.setRace_points(score);
            posmap.add(aposition);
        }

        return posmap;
    }
}
