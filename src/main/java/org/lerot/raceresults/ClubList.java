package org.lerot.raceresults;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.mframe;

public class ClubList extends TreeMap<String, Club>
{

    public String toCSV()
    {
        String out = "";
        int r = 0;
        for (  Map.Entry<String, Club> anentry : this.entrySet())
        {
            if (r > 0)
                out += "," + anentry.getKey();

            else
                out = anentry.getKey();
            r++;
        }
        return out;
    }

    public String toString()
    {
        String out = "";
        int r = 0;
        for (  Map.Entry<String, Club> anentry : this.entrySet())
        {
            if (r > 0)
                out += "," + anentry.getKey();

            else
                out = anentry.getKey();
            r++;
        }
        return out;
    }

    public Vector<Club> getClubVector()
    {
        Vector<Club> clubvector = new Vector<Club>();
        for (  Map.Entry<String, Club> anentry : this.entrySet())
        {
            Club aclub = anentry.getValue();
            clubvector.add(aclub);
        }
        return clubvector;
    }

    public String getDefaulKey()
    {
        return this.firstKey();
    }

    public Club getDefault()
    {
        return this.firstEntry().getValue();
    }
    
    
   public void loadXML(String fileNameWithPath)
   {
       Document document;
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       DocumentBuilder builder = null;
       try
       {
           builder = factory.newDocumentBuilder();
           document = builder.parse(new File(fileNameWithPath));
           Element rootele = document.getDocumentElement();
           loadXML( rootele );
       } catch (Exception e)
       {
           throw new RuntimeException(e);
       }
   }

    public void loadXML(Element rootele)
    {
        NodeList clubnodes = rootele.getElementsByTagName("club");
        for (int i = 0; i < clubnodes.getLength(); i++)
        {
            Element acell = (Element) clubnodes.item(i);
            String sname = acell.getAttribute("key");
            String fname = acell.getAttribute("fullname");
            String cypher = acell.getAttribute("cypher");
            Club newclub = new Club(sname, fname, cypher);
            put(sname,newclub);
        }
    }

    public String toXML()
    {
        StringBuilder out = new StringBuilder("<clubs>\n");
        for (  Map.Entry<String, Club> anentry : this.entrySet())
        {
            Club aclub = anentry.getValue();
            out.append(aclub.toXML());
        }
        out.append("</clubs>\n");
        return out.toString();
    }

     public void makeClubList(String text)
    {
        this.clear();
        String[] clubs = text.toUpperCase().split(",");
        for(String aclub :clubs)
        {
            String aclubkey = aclub.trim();
            Club foundclub = mframe.clublist.get(aclubkey);
            if(foundclub != null)
            {
                this.put(aclubkey,foundclub);
            }
        }

    }


}
