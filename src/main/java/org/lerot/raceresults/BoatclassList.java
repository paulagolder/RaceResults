package org.lerot.raceresults;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.maingui;

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

    public String[] makeKeyList(String instring)
    {
        Vector<String> keylist = new Vector();
        String[] list = instring.split(",");
        for (String atoken : list)
        {
            Boatclass aclass = get(atoken.toLowerCase().trim());
            if (aclass != null)
            {
                keylist.add(aclass.getKey());
            }
        }
        return keylist.toArray(new String[0]);
    }

    public String[] makeCypherList(String instring)
    {
        Vector<String> keylist = new Vector();
        String[] list = instring.split(",");
        for (String atoken : list)
        {
            Boatclass aclass = get(atoken.toLowerCase().trim());
            if (aclass != null)
            {
                keylist.add(aclass.getCypher());
            }
        }
        return keylist.toArray(new String[0]);
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

    public Vector<Boatclass> getClassVector()
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


    public void load(String text)
    {
        this.clear();
        String[] classes = text.toUpperCase().split(",");
        for(String aclass :classes)
        {
            String aclasskey = aclass.trim().toLowerCase(Locale.ROOT);
            Boatclass foundclass = maingui.classlist.get(aclasskey);
            if(foundclass != null)
            {
                this.put(aclasskey,foundclass);
            }
        }
    }

    public void makeClassList(String text)
    {
        this.clear();
        String[] classes = text.toUpperCase().split(",");
        for(String aclass :classes)
        {
            String aclasskey = aclass.trim().toLowerCase(Locale.ROOT);
            Boatclass foundclass = maingui.classlist.get(aclasskey);
            if(foundclass != null)
            {
                this.put(aclasskey,foundclass);
            }
        }

    }


    public String getDefaulKey()
    {
        return this.firstKey();
    }

    public Boatclass getDefault()
    {
        return this.firstEntry().getValue();
    }

}
