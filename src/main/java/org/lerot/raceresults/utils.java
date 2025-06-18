package org.lerot.raceresults;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import static org.lerot.raceresults.Mainrace_gui.mysailinghome;

public class utils
{


    public static HashMap<String, String> parseaction(String text)
    {
        HashMap<String, String> cmdmap = new HashMap<String, String>();
        String text2 = text;
        // String text2 = text.replace("{", "");
        if (text2.substring(0, 1).equals("{")) text2 = text2.substring(1);
        if (text2.substring(text2.length() - 1).equals("}")) text2 = text2.substring(0, text2.length() - 1);

        // String text3 = text2.replace("}", "");
        String[] cmdarray = text2.split(",");
        for (String acmd : cmdarray)
        {
            //System.out.println( acmd)   ;
            String[] cmdpair = acmd.split("=");
            if(cmdpair.length>1)
              cmdmap.put(cmdpair[0].trim(), cmdpair[1].trim());
            else
                cmdmap.put(cmdpair[0].trim(), "");
        }
        //    System.out.println(cmdmap);
        return cmdmap;
    }


    public static String sailmaker(int j)
    {
        String si = "     " + j;
        return si.substring(si.length() - 4);

    }

    public static String getDate(String date, String format)
    {
        try
        {
            Date ddate = new SimpleDateFormat(format).parse(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            String fdate = simpleDateFormat.format(ddate);
            return fdate;
        } catch (ParseException e)
        {
            return date;
        }
    }

    public static String fileexists(String filepath)
    {
        Path file = Paths.get(filepath);
        if (file.startsWith("/") && Files.exists(file) && Files.isRegularFile(file) && Files.isReadable(file))
        {
            // File gfile = new File(filepath);
            return file.toAbsolutePath().toString();
        } else
        {
            file = Paths.get(mysailinghome + filepath);
            if (Files.exists(file) && Files.isRegularFile(file) && Files.isReadable(file))
            {
                return file.toAbsolutePath().toString();
            } else
            {
                    return null;
            }

        }
    }

    public static String pad(int i)
    {
        String si = "     " + i;
        return si.substring(si.length() - 4);
    }

    public static Vector<String> makeRownames(int nrows)
    {
        Vector<String> rownames = new Vector<String>();
        for (int r = 0; r < nrows; r++)
        {
            rownames.add("Rank" + utils.pad(r + 1));
        }
        return rownames;
    }

    public static Vector<String> makeColnames(int ncols)
    {
        Vector<String> colnames = new Vector<String>();
        for (int c = 0; c < ncols; c++)
        {
            colnames.add("Race " + ((char) (65 + c)));
        }
        return colnames;
    }


    public static Vector<String> makePositionNames(int ncols)
    {
        Vector<String> collabels = new Vector<String>();
        collabels.add(" ");
        collabels.add("1st");
        collabels.add("2nd");
        collabels.add("3rd");
        for (int r = 4; r < ncols + 1; r++)
        {
            collabels.add(r + "th");
        }
        return collabels;
    }

    public static String makeList(String[] textarray)
    {
        StringBuilder out = new StringBuilder();
        int r=0;
        for(String atext:textarray)
        {
            if(r>0)
               out.append(", ").append(atext);
            else
                out.append(atext);
        }
        return out.toString();
    }
}
