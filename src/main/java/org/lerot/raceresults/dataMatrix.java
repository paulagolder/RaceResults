package org.lerot.raceresults;

import org.lerot.mywidgets.jswCell;
import org.lerot.mywidgets.jswLabel;
import org.lerot.mywidgets.jswStyles;
import org.lerot.mywidgets.jswTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.util.*;

public class dataMatrix
{
    Vector<Vector<String>> data;
    private Vector<String> rowname = new Vector<String>();
    private Vector<String> colname = new Vector<String>();
    private Vector<String> coltype = new Vector<String>();
    private Vector<Boolean> selected = new Vector<Boolean>();
    private String cssid = "datamatrix";
    private String cssclass = "datamatrix";

    public dataMatrix()
    {
        data = new Vector<Vector<String>>();
    }

    public dataMatrix(int ncols, int nrows)
    {
        data = new Vector<Vector<String>>();
        for (int c = 0; c < ncols; c++)
        {
            Vector<String> root = new Vector<String>();
            for (int r = 0; r < nrows; r++)
            {
                root.add(" ");
            }
            data.add(root);
        }
    }

    public static dataMatrix randomMatrix(int nc, int nsailors, Vector<String> saillist)
    {
        dataMatrix rm = new dataMatrix(nc, nsailors);
        int ncols = nc;
        int nrows = nsailors;
        if (nrows > saillist.size()) nrows = saillist.size();
        int max = nrows;
        int min = nrows / 2;
        Random rn = new Random();
        ArrayList<String> root = new ArrayList<>(saillist);
        rm.data = new Vector<Vector<String>>();
        for (int c = 0; c < ncols; c++)
        {
            Collections.shuffle(root);
            Vector<String> col = new Vector<>();
            rm.getColname().add("Race " + ((char) (65 + c)));
            rm.getColtype().add("string");
            rm.getSelected().add(true);
            int nr = rn.nextInt((max - min) + 1) + min;
            for (int r = 0; r < nr; r++)
            {
                col.add(root.get(r));
            }
            rm.data.add(col);
        }
        for (int r = 0; r < nrows; r++)
        {
            rm.getRowname().add("Rank" + utils.pad(r + 1));
        }
        return rm;
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

    public void printToXML(BufferedWriter bw)
    {
        int ncols = getColname().size();
        int maxrows = 0;
        for (int c = 0; c < ncols; c++)
        {
            Vector<String> cdata = data.get(c);
            int nrows = getFilledLength(cdata)+2;
            if (nrows > maxrows) maxrows = nrows;
        }
        int r = getRowname().size();
        while (r < maxrows-1)
        {
            String label = "ROW " + (r+1);
            getRowname().add(label);
            r++;
        }

        try
        {
            bw.write("<colnames>\n");

            for (String aname : getColname())
            {
                bw.write("<cell colname=\"" + aname + "\"/>\n");
            }
            bw.write("</colnames>\n");
            bw.write("<rownames>\n");
            r = 0;
            while (r < maxrows-1)
            {
                String aname = getRowname().get(r);
                bw.write("<cell rowname =\"" + aname + "\"/>\n");
                r++;
            }
            bw.write("</rownames>\n");
            Vector<String> rownames = getRowname();
            for (int c = 0; c < ncols; c++)
            {
                String sel = "false";
                if (getSelected().get(c)) sel = "true";
                bw.write("<col name= \"" + getColname().get(c) + "\"  type=\"" + getColtype().get(c) + "\" select=\"" + sel + "\" >\n");
                Vector<String> cdata = data.get(c);
                int nrows = getFilledLength(cdata);
                for (r = 0; r < nrows+1; r++)
                {
                    //System.out.println(" saving row " + r);
                    bw.write("<cell rowname=\"" + rownames.get(r) + "\">" + cdata.get(r) + "</cell>\n");
                }
                bw.write("</col>\n");
            }
        } catch (Exception e)
        {
            System.out.println(" problem with saving xml");
        }
    }

    int getFilledLength(Vector<String> vs)
    {
        int max = vs.size() - 1;
        int n = max;
        boolean more = false;
        String mxvs = vs.get(max);
        if (vs.get(max) != null && vs.get(max).trim().length() > 0 && !vs.get(max).trim().equalsIgnoreCase("null") ) return n;
        while ( n > 1)
        {
            n--;
           // if (vs.get(n) == null) return n;
            if (vs.get(n) != null && !vs.get(n).isEmpty()  ) return n;
           // if( vs.get(n).trim().equalsIgnoreCase("null")) return n;
        }
        return n ;
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
                    getColname().set(c, acolname);
                    getColtype().set(c, acoltype);
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
        int n = Arrays.asList(getRowname()).indexOf(arowname);
        if (n > -1)
        {
            return n;
        } else
        {
            getRowname().add(arowname);
            return getRowname().size() - 1;
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
        {
            if (r >= data.get(c).size())
            {
                return " ";
            } else
                return data.get(c).get(r);
        }

    }

    public int getncols()
    {
        return getColname().size();
    }

    public int getnrows()
    {
        return getRowname().size();
    }

    public void loadfromXML(Element rootelement)
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
                getColname().add(cname);
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
                getRowname().add(cname);
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

            if (!getColname().contains(cname))
            {
                getColname().add(cname);
            }
            int cnum = getColname().indexOf(cname);
            getColtype().add(cnum, ctype);
            getSelected().add(isselected);
            cells = acol.getElementsByTagName("cell");
            for (int r = 0; r < cells.getLength(); r++)
            {
                Element acell = (Element) cells.item(r);
                String rname = acell.getAttribute("rowname");
                if (!getRowname().contains(rname))
                {
                    getRowname().add(rname);
                }
                String value = acell.getTextContent();
                putCell(cname, rname, value);
                //System.out.println("hello "+cname+":"+rname+":"+value);
            }
        }
    }

    void putCell(String cname, String rname, String val)
    {
        data.setSize(getColname().size());
        int coln = getColname().indexOf(cname);
        if (coln < 0)
        {
            getColname().add(cname);
            coln = getColname().indexOf(cname);
        }
        Vector<String> col = data.get(coln);
        int a;
        if (col == null)
        {
            data.set(coln, new Vector<String>());
            col = data.get(coln);
            col.setSize(getRowname().size());
        }
        int rown = getRowname().indexOf(rname);
        if (col.size() > rown)
        {
            col.set(rown, val);
        } else
            col.add(rown, val);
    }

    public void putaCell(int coln, String rname, String val)
    {
        Vector<String> col = data.get(coln);
        int rown = getRowname().indexOf(rname);
        if (rown < 0)
        {
            // System.out.println(" not found x :"+rname+":");
        } else
        {
            //  System.out.println(" adding :" + coln + ":" + rown + ":" + val + ":");
            col.set(rown, val);
        }
    }

    public void setCell(int c, int r, String value)
    {
        Vector<String> col = data.get(c - 1);
        if (r > col.size())
        {
            col.add(value);
        } else
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
        return values;
    }

    public TreeSet<String> getValueSet()
    {
        TreeSet<String> values = new TreeSet<String>();
        for (int r = 0; r < getnrows(); r++)
        {
            for (int c = 0; c < getncols(); c++)
            {
                String token = data.get(c).get(r);
                if (token != null && !token.equalsIgnoreCase("null"))
                {
                    values.add(token.trim());
                }
            }
        }
        return values;
    }

  /*  public dataMatrix getValueMatrix(Vector<String> colnames, Vector<String> rownames)
    {
        int ncols = colnames.size();
        int nrows = rownames.size();
        dataMatrix vm = new dataMatrix(ncols, nrows);
        vm.setRowname(rownames);
        vm.setColname(colnames);
        //   vm.getColname().set(2,"two");
        vm.setColtype("String", ncols);
        vm.setSelected(true, ncols);
        for (int c = 0; c < data.size(); c++)
        {
            Vector<String> values = data.get(c);
            for (int r = 0; r < values.size(); r++)
            {
                String avalue = values.get(r);
                if (avalue != null)
                {
                    avalue = getValue(c, r);

                    if(!avalue.trim().isEmpty() )
                    {
             //           Sail asail = Sail.parse(avalue.trim(),compclasslist.getDefaulKey(),compclublist.getDefaulKey());
                        if (asail != null)
                        {

                            String token = asail.toCypherString();

                            if (!token.trim().isEmpty())
                            {
                                vm.putaCell(c, token, " " + (r + 1));
                            }
                        }
                    }
                }
            }
        }
        return vm;
    }*/

    void setColtype(String string, int nc)
    {
        coltype = new Vector<String>();
        for (int c = 0; c < nc; c++)
        {
            coltype.add(string);
        }
    }

    void setSelected(Boolean b, int nc)
    {
        selected = new Vector<Boolean>();
        for (int c = 0; c < nc; c++)
        {
            selected.add(b);
        }
    }

    public jswTable makedatapanel(Vector<String> rownames, jswStyles tablestyles)
    {
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        datagrid.addCell(new jswLabel("corner"), 0, 0);
        int ncols = getColname().size();
        for (int c = 0; c < ncols; c++)
        {
            String value = getColname().get(c);
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
                    value = " ";
                else
                    value = getValue(c, r);
                jswLabel alabel = new jswLabel(value);
                jswCell acell = datagrid.addCell(alabel, r + 1, c + 1);
            }
        }
        return datagrid;
    }

    public Component makesmalldatapanel(SailList rownames, jswStyles tablestyles)
    {
        jswTable datagrid = new jswTable(null, "form1", tablestyles);
        int ncols = getColname().size();
        int nrows = getRowname().size();
        int nc = 0;
        for (int c = 0; c < ncols; c++)
        {
            if (getSelected().get(c))
            {
                String value = getColname().get(c);
                datagrid.addCell(new jswLabel(value), 0, nc);
                nc++;
            }
        }
        nc = 0;
        for (int c = 0; c < ncols; c++)
        {
            if (getSelected().get(c))
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

    public jswTable makeresultspanel(SailList rownames, jswStyles tablestyles, int maxsailors)
    {
        jswTable datagrid = new jswTable(null, "positions", tablestyles);
        int ncols = getColname().size();
        int nrows = getRowname().size();
        int nsel = getSelected().size();
        int nc = 0;
        int maxranks = data.size();
        for (int c = 0; c < maxranks; c++)
        {
           // if (getSelected().get(c))
            {
                String value = getColname().get(c);
                datagrid.addCell(new jswLabel(value), 0, nc);
                nc++;
            }
        }
        nc = 0;

        for (int c = 0; c < maxranks; c++)
        {
            if (getSelected().get(c))
            {
                int snrows = data.get(c).size();
                for (int r = 0; r < nrows; r++)
                {
                    String value;
                    if (r >= snrows)
                        value = " ";
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

    public void addColumn(String name, int nrows)
    {
        getColname().add(name);
        getSelected().add(Boolean.TRUE);
        getColtype().add("string");
        Vector<String> nv = new Vector<String>();
        for (int r = 0; r < nrows; r++)
        {
            nv.add(" ");
        }
        data.add(nv);
    }

    public void setColnames(Vector<String> strings)
    {
    }

    public Vector<String> getRowname()
    {
        return rowname;
    }

    public void setRowname(Vector<String> rowname)
    {
        this.rowname = rowname;
    }

    public Vector<String> getColname()
    {
        return colname;
    }

    public void setColname(Vector<String> colname)
    {
        this.colname = colname;
    }

    public Vector<String> getColtype()
    {
        return coltype;
    }

    public void setColtype(Vector<String> coltype)
    {
        this.coltype = coltype;
    }

    public Vector<Boolean> getSelected()
    {
        return selected;
    }

    public void setSelected(Vector<Boolean> selected)
    {
        this.selected = selected;
    }

    public void setSelected(boolean b)
    {
        selected = new Vector<Boolean>();
        for (int r = 0; r < data.size() + 1; r++)
        {
            selected.add(b);
        }
    }

    public int size()
    {
        return data.size();
    }

    public Vector<String> getColumn(int c)
    {
        return data.get(c);
    }
}
