package org.lerot.raceresults;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.lerot.raceresults.mainrace_gui.mysailinghome;

public class utils
{


    public static HashMap<String, String> parseaction(String text)
    {
        HashMap<String, String> cmdmap = new HashMap<String, String>();
        String text2 = text.replace("{", "");
        String text3 = text2.replace("}", "");
        String[] cmdarray = text3.split(",");
        for (String acmd : cmdarray)
        {
            //System.out.println( acmd)   ;
            String[] cmdpair = acmd.split("=");
            cmdmap.put(cmdpair[0].trim(), cmdpair[1].trim());
        }
        //    System.out.println(cmdmap);
        return cmdmap;
    }

    public static HashMap<String, Integer> parsecoords(String text)
    {
        HashMap<String, Integer> cmdmap = new HashMap<String, Integer>();
        String text2 = text.replace("{", "");
        String text3 = text2.replace("}", "");
        String[] cmdarray = text3.split(",");
        for (String acmd : cmdarray)
        {
            //System.out.println( acmd)   ;
            String[] cmdpair = acmd.split("=");
            cmdmap.put(cmdpair[0].trim(), Integer.parseInt(cmdpair[1].trim()));
        }
        //    System.out.println(cmdmap);
        return cmdmap;
    }

    public String getDate(String date,String format)
    {
       try
    {
        Date ddate = new SimpleDateFormat(format).parse(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String fdate = simpleDateFormat.format(ddate);
        return fdate;
    } catch( ParseException e)
       {
        return " format error " + date;
    }
}

public static String fileexists(String filepath)
{
    Path file = Paths.get(filepath);
    if (!Files.exists(file))
    {
        // File does not exist
    } else if (!Files.isRegularFile(file))
    {
        // File is not a file, maybe a directory
    } else if (!Files.isReadable(file))
    {
        // File is not readable.
    } else
    {
        return filepath;
    }
    file = Paths.get(mysailinghome + filepath);
    if (!Files.exists(file))
    {
        // File does not exist
    } else if (!Files.isRegularFile(file))
    {
        // File is not a file, maybe a directory
    } else if (!Files.isReadable(file))
    {
        // File is not readable.
    } else
    {
        return mysailinghome + filepath;
    }
    return null;
}


}
