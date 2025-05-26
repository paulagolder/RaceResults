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

public class BoatclassList extends TreeMap<String, Boatclass>
{

    public String toCSV()
    {
        String out = "";
        int r = 0;
        for (  Map.Entry<String, Boatclass> anentry : this.entrySet())
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
        for (  Map.Entry<String, Boatclass> anentry : this.entrySet())
        {
            if (r > 0)
                out += "," + anentry.getKey();

            else
                out = anentry.getKey();
            r++;
        }
        return out;
    }

    public Vector<Boatclass> getClubVector()
    {
        Vector<Boatclass> clubvector = new Vector<Boatclass>();
        for (  Map.Entry<String, Boatclass> anentry : this.entrySet())
        {
            Boatclass aclub = anentry.getValue();
            clubvector.add(aclub);
        }
        return clubvector;
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
        NodeList clubnodes = rootele.getElementsByTagName("boatclass");
        for (int i = 0; i < clubnodes.getLength(); i++)
        {
            Element acell = (Element) clubnodes.item(i);
            String sname = acell.getAttribute("key");
            String fname = acell.getAttribute("fullname");
            String cypher = acell.getAttribute("cypher");
            Boatclass newclub = new Boatclass(sname, fname, cypher);
            put(sname,newclub);
        }
    }

    public String toXML()
    {
        StringBuilder out = new StringBuilder("<clubs>\n");
        for (  Map.Entry<String, Boatclass> anentry : this.entrySet())
        {
            Boatclass aclub = anentry.getValue();
            out.append(aclub.toXML());
        }
        out.append("</clubs>\n");
        return out.toString();
    }


}
