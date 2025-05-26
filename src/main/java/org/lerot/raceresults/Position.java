package org.lerot.raceresults;

public class Position
{
    private String sail;
    private String sailor;
    private int scoredraces;
    private int totalraces;
    private int points;
    private int race_points;
    private int missing_race_points;

    public int getTotalraces()
    {
        return totalraces;
    }

    public void setTotalraces(int totalraces)
    {
        this.totalraces = totalraces;
    }

    public Position()
    {
    }

    public Position(String sail, String sailor, int races, int points, int race_points, int missing_race_points)
    {
        this.setSail(sail);
        this.setSailor(sailor);
        this.setScoredraces(races);
        this.setPoints(points);
        this.setRace_points(race_points);
        this.setMissing_race_points(missing_race_points);
    }

    public String getSail()
    {
        return sail;
    }

    public void setSail(String sail)
    {
        this.sail = sail;
    }

    public String getSailor()
    {
        return sailor;
    }

    public void setSailor(String sailor)
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

    public String toHTML()
    {
       return  "<tr><td>"+this.sail+"</td><td>"+this.sailor+"</td><td>"+this.totalraces +"</td><td>"+this.scoredraces +"</td><td>"+this.points+"</td><td>"+this.race_points+"</td><td>"+this.missing_race_points+"</td></tr>";
    }

    public String toHTML_short()
    {
        return  "<tr><td>"+this.sail+"</td><td>"+this.sailor+"</td><td>"+this.totalraces +"</td><td>"+this.scoredraces +"</td><td>"+this.race_points+"</td></tr>";
    }
}
