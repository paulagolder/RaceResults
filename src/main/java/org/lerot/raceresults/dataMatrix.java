package org.lerot.raceresults;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;

import org.lerot.mywidgets.jswCell;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswTable;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.*;

public class dataMatrix
{
    Vector<String> rowname = new Vector<String>();
    Vector<String> colname = new Vector<String>();
    Vector<String> coltype = new Vector<String>();
    Vector<Boolean> selected = new Vector<Boolean>();
    Vector<Vector<String>> data;
    private String cssid = "datamatrix";
    private String cssclass = "datamatrix";

    public dataMatrix()
    {
        data = new Vector<Vector<String>>();
    }

    public dataMatrix(int nc, int nr, int nn,boolean empty)
    {
        int ncols = nc;
        int nrows = nr;
        Random rn = new Random();
        data = new Vector<Vector<String>>();
        Vector<Integer> root = new Vector<Integer>();
        for (int r = 1; r < nrows + 1; r++)
        {
            int j = rn.nextInt(10) + 1;
            root.add(r + j * 10);
        }
        for (int c = 0; c < ncols; c++)
        {
            Collections.shuffle(root);
            colname.add("Race " + ((char) (65 + c)));
            coltype.add("string");
            selected.add(true);
            Vector<String> coldata = new Vector<String>();
            for (int r = 0; r < nrows; r++)
            {
                coldata.add(" " + root.get(r));
            }
            data.add(coldata);
        }
        for (int r = 0; r < nrows; r++)
        {
            rowname.add("Rank" + utils.pad(r + 1));
        }
    }

    public dataMatrix(int nc, Vector<String> saillist)
    {
        int ncols = nc;
        int nrows = saillist.size();
        Random rn = new Random();
        data = new Vector<Vector<String>>();
        for (int c = 0; c < ncols; c++)
        {
            Vector<String> root = new Vector<String>();
            for (int r = 0; r < nrows; r++)
            {
                root.add(saillist.get(r));
            }
            Collections.shuffle(root);
            colname.add("Race " + ((char) (65 + c)));
            coltype.add("string");
            selected.add(true);
            data.add(root);
        }
        for (int r = 0; r < nrows; r++)
        {
            rowname.add("Rank" + utils.pad(r + 1));
        }
    }

    public dataMatrix(int nc, int nr)
    {
        int ncols = nc;
        int nrows = nr;
        Random rn = new Random();
        data = new Vector<Vector<String>>();
        for (int c = 0; c < ncols; c++)
        {
            Vector<String> root = new Vector<String>();
            for (int r = 0; r < nrows; r++)
            {
                root.add(" ");
            }

            colname.add("Race " + ((char) (65 + c)));
            coltype.add("string");
            selected.add(true);
            data.add(root);
        }
        for (int r = 0; r < nrows; r++)
        {
            rowname.add("Rank" + utils.pad(r + 1));
        }
    }

    public static dataMatrix demo(int ncols, int nrows)
    {
        dataMatrix dm = new dataMatrix(ncols, nrows);
        return dm;
    }

    public String getCssclass()
    {
        return cssclass;
    }

    public void setCssclass(String cssclass)
    {
        this.cssclass = cssclass;
    }

    public String getCssid()
    {
        return cssid;
    }

    public void setCssid(String cssid)
    {
        this.cssid = cssid;
    }

    public void printToXML(BufferedWriter bw) throws IOException
    {
        bw.write("<colnames>\n");
        for (String aname : colname)
        {
            bw.write("<cell colname=\"" + aname + "\"/>\n");
        }
        bw.write("</colnames>\n");
        bw.write("<rownames>\n");
        for (String aname : rowname)
        {
            bw.write("<cell rowname =\"" + aname + "\"/>\n");
        }
        bw.write("</rownames>\n");
        int ncols = colname.size();

        for (int c = 0; c < ncols; c++)
        {
            String sel = "false";
            if(selected.get(c)) sel="true";
            bw.write("<col name= \"" + colname.get(c) + "\"  type=\"" + coltype.get(c) + "\" select=\"" + sel+"\" >\n");
            Vector cdata = data.get(c);
            int nrows = cdata.size();
            for (int r = 0; r < nrows; r++)
            {
                bw.write("<cell rowname=\"" + rowname.get(r) + "\">" + cdata.get(r) + "</cell>\n");
            }
            bw.write("</col>\n");
        }
    }

    public void printToHTML(BufferedWriter bw, String title) throws IOException
    {
        int ncols = colname.size();
        int nrows = rowname.size();
        bw.write("<table id=\"" + cssid + "\" class=\"" + cssclass + "\">\n");
        bw.write("<tr>\n<th  colspan=" + (ncols + 1) + ">" + title + "</th>\n</tr>");

        bw.write("<tr>\n<th>Sail No</th>");
        for (int c = 0; c < ncols; c++)
        {
            bw.write("<th>" + colname.get(c) + "</th>");
        }
        bw.write("</tr>\n");
        for (int r = 0; r < nrows; r++)
        {
            bw.write("<tr><td>" + rowname.get(r) + "</td>");
            for (int c = 0; c < ncols; c++)
            {
                bw.write("<td >" + data.get(c).get(r) + "</td>");
            }
            bw.write("</tr>\n");
        }
        bw.write("</table>\n");
    }

    public Document readXML(String fileNameWithPath)
    {
        Document document;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(fileNameWithPath));
            Element rootele = document.getDocumentElement();
            NodeList children = rootele.getChildNodes();
            String dt = rootele.getAttribute("date");
            String cl = rootele.getAttribute("class");
            NodeList columns = rootele.getElementsByTagName("col");
            int nc = columns.getLength();
            Node current = rootele.getFirstChild();
            int c = 0;
            while (null != current)
            {
                if (current.getNodeName().equalsIgnoreCase("col"))
                {
                    String acolname = ((Element) current).getAttribute("name");
                    String acoltype = ((Element) current).getAttribute("type");
                    colname.set(c, acolname);
                    coltype.set(c, acoltype);
                    System.out.println(acolname + "(" + acoltype + ")");
                    NodeList cells = current.getChildNodes();
                    System.out.println(cells.getLength());
                    //      NodeList childNodes = current.getChildNodes();
                    for (int i = 0; i < cells.getLength(); i++)
                    {
                        Node childNode = cells.item(i);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            String arowname = ((Element) childNode).getAttribute("rowname");
                            int r = getRowIndex(arowname);
                            System.out.println("Child node name " + i + ":" + ((Element) childNode).getAttribute("rowname"));
                            System.out.println("Child node value: " + i + ":" + childNode.getTextContent());
                        }
                    }
                    c++;
                }
                current = current.getNextSibling();

            }
            // System.out.println(rootele.toString());
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return document;
    }

    private int getRowIndex(String arowname)
    {
        int n = Arrays.asList(rowname).indexOf(arowname);
        if (n > -1)
        {
            return n;
        } else
        {
            rowname.add(arowname);
            return rowname.size() - 1;
        }
    }

    public int getIntValue(int c, int r)
    {
        return Integer.parseInt(data.get(c).get(r));
    }

    public String getValue(int c, int r)
    {
        if (data.get(c) == null)
            return null;
        else
            return data.get(c).get(r);
    }

    public int getncols()
    {
        return colname.size();
    }

    public int getnrows()
    {
        return rowname.size();
    }

    public void loadresults(Element rootelement)
    {

        //   Element dm = ((Element) children.item(0));
        NodeList colnames = rootelement.getElementsByTagName("colnames");
        if (colnames.getLength() > 0)
        {
            Element acolname = (Element) colnames.item(0);
            NodeList cells = acolname.getElementsByTagName("cell");
            for (int c = 0; c < cells.getLength(); c++)
            {
                Element acell = (Element) cells.item(c);
                String cname = acell.getAttribute("colname");
                colname.add(cname);
            }
        }
        NodeList rownames = rootelement.getElementsByTagName("rownames");
        NodeList cells;
        if (rownames.getLength() > 0)
        {
            cells = ((Element) rownames.item(0)).getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String cname = acell.getAttribute("rowname");
                rowname.add(cname);
            }
        }
        NodeList cols = rootelement.getElementsByTagName("col");
        for (int c = 0; c < cols.getLength(); c++)
        {
            Element acol = (Element) cols.item(c);
            String cname = acol.getAttribute("name");
            String ctype = acol.getAttribute("type");
            String asel = acol.getAttribute("select");
            Boolean isselected = true;
            if (asel != null)
            {
                isselected = ("true".equalsIgnoreCase(asel));
            }

            if (!colname.contains(cname))
            {
                colname.add(cname);
            }
            int cnum = colname.indexOf(cname);
            coltype.add(cnum, ctype);
            selected.add(isselected);
            cells = acol.getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String rname = acell.getAttribute("rowname");
                if (!rowname.contains(rname))
                {
                    rowname.add(rname);
                }
                String value = acell.getTextContent();
                putCell(cname, rname, value);
                //System.out.println("hello "+cname+":"+rname+":"+value);
            }
        }
    }

    private void putCell(String cname, String rname, String val)
    {
        data.setSize(colname.size());
        int coln = colname.indexOf(cname);
        if (coln < 0)
        {
            colname.add(cname);
            coln = colname.indexOf(cname);
        }
        Vector<String> col = data.get(coln);
        int a;
        if (col == null)
        {
            data.set(coln, new Vector<String>());
            col = data.get(coln);
            col.setSize(rowname.size());
        }
        int rown = rowname.indexOf(rname);
        if (col.size() > rown)
        {

            col.set(rown, val);
        } else
            col.add(rown, val);
    }


    public void setCell(int c, int r, String value)
    {
        Vector<String> col = data.get(c - 1);
        col.set(r - 1, value);
    }

    public String getCell(int c, int r)
    {
        Vector<String> col = data.get(c - 1);
        return col.get(r - 1);
    }


    public Vector<String> getValuevector()
    {
        Vector<String> values = new Vector<String>();
        for (int r = 0; r < getnrows(); r++)
        {
            for (int c = 0; c < getncols(); c++)
            {
                String token = "    " + data.get(c).get(r);
                String tlabel = token.substring(token.length() - 4);
                if (!values.contains(tlabel) && !tlabel.equalsIgnoreCase("    "))
                {
                    values.add(tlabel);
                }
            }
        }
        // Collections.sort(values);
        return values;
    }

    public Vector<Vector<String>> getValueMatrix(Vector<String> sailnames)
    {
        Vector<Vector<String>> vm = new Vector<Vector<String>>();
        for (int c = 0; c < getncols(); c++)
        {

                Vector<String> col = new Vector<String>();
                vm.add(col);
                Vector<String> values = data.get(c);
                HashMap<String, String> colmap = new HashMap<String, String>();
                for (int r = 0; r < sailnames.size(); r++)
                {
                    String token = "        " + sailnames.get(r).trim();
                    String ttoken = token.substring(token.length() - 4);
                    colmap.put(ttoken, " ");
                }
                for (int r = 0; r < values.size(); r++)
                {
                    String token = "        " + values.get(r).trim();
                    String ttoken = token.substring(token.length() - 4);
                    if (!ttoken.trim().isEmpty())
                    {
                        String rowlabel = rowname.get(r).replace("Rank", "    ");
                        String tlabel = rowlabel.substring(rowlabel.length() - 4);
                        colmap.put(ttoken, tlabel);
                    }
                }

                for (int nr = 0; nr < sailnames.size(); nr++)
                {
                    String nv = colmap.get(sailnames.get(nr));
                    col.add(nv);
                }
            }
        return vm;
    }

    public jswTable makedatapanel(Vector<String> rownames, jswStyles tablestyles)
    {
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);
        int ncols = colname.size();
        for (int c = 0; c < ncols; c++)
        {
            String value = colname.get(c);
            datagrid.addCell(new jswLabel(value), 0, c + 1);
        }
        int nrows = rownames.size();
        for (int r = 0; r < nrows; r++)
        {
            datagrid.addCell(new jswLabel(rownames.get(r)), r + 1, 0);
        }
        for (int c = 0; c < ncols; c++)
        {
                int snrows = data.get(c).size();
                for (int r = 0; r < nrows; r++)
                {
                    String value;
                    if (r >= snrows)
                        value = "-";
                    else
                        value = getValue(c, r);
                    jswLabel alabel = new jswLabel(value);
                    jswCell acell = datagrid.addCell(alabel, r + 1, c + 1);
                }

        }
        return datagrid;
    }


    public void createcompmatrix(dataMatrix matrix2, int racecount)
    {
        data.clear();
        Vector<String> points = new Vector<String>();
        data.add(points);
        int nsails = rowname.size();
        for (int r = 0; r < nsails; r++)
        {
            int sum = 0;
            int nr = 0;
            int nraces =0;
            for (int c = 1; c < matrix2.getncols(); c++)
            {
                int value = matrix2.getIntValue(c,r);
                if(value>0)
                {
                    if (nr + value < racecount)
                    {
                        sum += value * c;
                        nr += value;
                    } else
                    {
                        int rem = racecount - nr;
                        if (rem > 0)
                        {
                            sum += rem * c;
                            nr += rem;
                        }
                    }
                    nraces++;
                }
            }
            for (int c = 1; c < matrix2.getncols(); c++)
            {
                int participants =0;
                boolean present = false;
                if(!matrix2.getValue(c,r).trim().isEmpty())
                {
                    for (int pr = 0; pr < matrix2.getnrows(); pr++)
                    {
                        if (!matrix2.getValue(c,pr).trim().isEmpty() ) participants++;
                    }
                    if (nraces < racecount)
                    {
                        sum += participants + 1;
                        nraces++;
                    }
                }
            }
                points.add(" " + sum);
        }

        for (int c = 1; c < matrix2.getncols(); c++)
        {
            Vector<String> acol = new Vector<String>();
            int nrows = matrix2.getnrows();
            for (int r = 0; r < nrows; r++)
            {
                int value = matrix2.getIntValue(c,r);
                acol.add(" " + value);
            }
            data.add(acol);
        }
    }


    public Component makesmalldatapanel(Vector<String> rownames, jswStyles tablestyles)
    {
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
       // datagrid.setPadding(20,0,20,40);
        int ncols = colname.size();
        int nrows = rownames.size();
        int nc = 0;
        for (int c = 0; c < ncols; c++)
        {
            if(selected.get(c))
            {
                String value = colname.get(c);
                datagrid.addCell(new jswLabel(value), 0, nc);
                nc++;
            }
        }
         nc = 0;
        for (int c = 0; c < ncols; c++)
        {
            if(selected.get(c))
            {
                int snrows = data.get(c).size();
                for (int r = 0; r < nrows; r++)
                {
                    String value;
                    if (r >= snrows)
                        value = "-";
                    else
                        value = getValue(c, r);
                    if (value == null)
                    {
                        value = " ";
                    }
                    jswLabel alabel = new jswLabel(value);
                    jswCell acell = datagrid.addCell(alabel, r + 1, nc);
                }
                nc++;
            }
        }
        return datagrid;
    }


    public void addRace()
    {
        int nc = colname.size();
        colname.add("Race "+((char) (65 + nc)));
        selected.add(Boolean.TRUE);
        coltype.add("string");
        Vector<String> nv = new Vector<String>();
        for (int r=0; r<rowname.size();r++)
        {
            nv.add(" ");
        }
        data.add(nv);
    }
}
