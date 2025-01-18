package org.lerot.raceresults;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class sail
{
    private String sailnumber;
    private String boatclass;
    private String sailorname;

    public sail(String asailnumber, String aboatclass, String asailorname)
    {
        sailnumber = asailnumber;
        boatclass = aboatclass;
        sailorname= asailorname;
    }




    public String getSailnumber()
    {
        return sailnumber;
    }

    public void setSailnumber(String sailnumber)
    {
        this.sailnumber = sailnumber;
    }

    public String getBoatclass()
    {
        return boatclass;
    }

    public void setBoatclass(String boatclass)
    {
        this.boatclass = boatclass;
    }

    public String getSailorname()
    {
        return sailorname;
    }

    public void setSailorname(String sailorname)
    {
        this.sailorname = sailorname;
    }

    public static Vector<sail> readSaillistXML(String fileNameWithPath)
    {
        Vector<sail> boatlist = new Vector<sail>();
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();



            NodeList sails = rootele.getElementsByTagName("sail");
            for (int i = 0; i < sails.getLength(); i++)
            {
                Element acell = (Element) sails.item(i);
                String sailnumber = acell.getAttribute("sailnumber");
                String sailorname = acell.getAttribute("sailorname");
                String boatclass = acell.getAttribute("boatclass");
                sail asail = new sail(sailnumber, boatclass,sailorname);
                boatlist.add(asail);
            }

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return boatlist;
    }
}
