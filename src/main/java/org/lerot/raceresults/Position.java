package org.lerot.raceresults;

import java.util.ArrayList;

public class Position
{
    private Sailor sailor;
    private String sails;
    private int scoredraces;
    private int totalraces;
    private float average_points;
    private int points;
    private int race_points;
    private int missing_race_points;
    private ArrayList<Integer> ranks;

    public int getTotalraces()
    {
        return totalraces;
    }

    public void setTotalraces(int totalraces)
    {
        this.totalraces = totalraces;
    }



    public Position(Sailor asailor )
    {
        this.setSailor(asailor);
        this.setScoredraces(0);
        this.setPoints(0);
        this.setRace_points(0);
        this.setMissing_race_points(0);
        ranks = null;
        sails="";
    }

    public Sailor getSailor()
    {
        return sailor;
    }

    public void setSailor(Sailor sailor)
    {
        this.sailor = sailor;
    }

    public int getScoredraces()
    {
        return scoredraces;
    }

    public void setScoredraces(int scoredraces)
    {
        this.scoredraces = scoredraces;
    }

    public int getPoints()
    {
        return points;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public int getRace_points()
    {
        return race_points;
    }

    public void setRace_points(int race_points)
    {
        this.race_points = race_points;
    }

    public int getMissing_race_points()
    {
        return missing_race_points;
    }

    public void setMissing_race_points(int missing_race_points)
    {
        this.missing_race_points = missing_race_points;
    }

    public String toHTML(int maxrank)
    {
        StringBuilder out = new StringBuilder("<tr><td>" + this.sailor.getName() + "</td><td>" + this.sails + "</td><td>" + this.totalraces + "</td>");
        ArrayList<Integer> ranklist = getRanks();
        for(int r=0;r<maxrank;r++)
        {

            out.append("<td>").append(ranklist.get(r)).append("</td>");
        }
        out.append("</tr>\n");
        return out.toString();
    }

    public String toHTML_short(String defclass, String defclub)
    {
        StringBuilder out = new StringBuilder("<tr><td>" + this.sailor.getName() + "</td><td>" + this.sails + "</td><td>" + this.totalraces + "</td>\n");
        out.append("<td>").append(this.getScoredraces()).append("</td><td>").append(this.getRace_points()).append("</td></tr>");
        return out.toString();
    }

    public String toHTML_short_scored(String defclass, String defclub)
    {
        StringBuilder out = new StringBuilder("<tr><td>" + this.sailor.getName() + "</td><td>" + this.sails + "</td><td>" + this.totalraces + "</td>");
        out.append("<td>").append(this.getScoredraces()).append("</td><td>").append(this.getRace_points()).append("</td></tr>\n");
        return out.toString();
    }

    public String toHTML_short_averaged(String defclass, String defclub)
    {
        StringBuilder out = new StringBuilder("<tr><td>" + this.sailor.getName() + "</td><td>" + this.sails + "</td><td>" + this.totalraces + "</td>");
        out.append("<td>").append(this.getRace_points()).append("</td><td>").append(this.getAverage_points_formated()).append("</td></tr>\n");
        return out.toString();
    }


    public String toHTML_short_v2(String defclass, String defclub)
    {
        StringBuilder out = new StringBuilder("<tr><td>" + this.sailor.getName() + "</td><td>" + this.sails + "</td><td>" + this.totalraces + "</td>");
        out.append("<td>").append(this.getScoredraces()).append("</td><td>").append(this.getRace_points()).append("</td><td>").append(this.getAverage_points_formated()).append("</td></tr>\n");
        return out.toString();
    }

    public String getSails()
    {
        return sails;
    }

    public void setSails(String sails)
    {
        this.sails = sails;
    }
    public void addSail(String sail)
    {
        if(this.sails.contains(sail)) return;
        else
        this.sails += " " +sail;
    }

    public void setRanks(int nr)
    {
         ranks = new ArrayList<Integer>();
         for(int r=0;r<nr; r++) ranks.add(0);
    }

    public ArrayList<Integer> getRanks()
    {
        return ranks;
    }

    public void setRanks(ArrayList<Integer> ranks)
    {
        this.ranks = ranks;
    }

    public void incrementTotalraces()
    {
        this.totalraces++;
    }

    public float getAverage_points()
    {
        return (float) race_points /scoredraces;
    }

    public String getAverage_points_formated()
    {
        float av = (float) race_points / scoredraces;
        String roundedValue = String.format("%.2f", av);
        return roundedValue;
    }



}
