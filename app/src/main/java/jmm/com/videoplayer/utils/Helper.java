package jmm.com.videoplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Helper {

    public static String LongToDate(String longV)
    {
        long input=Long.parseLong(longV.trim());
        Date date = new Date(input*1000); // *1000 gives accurate date otherwise returns 1970
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
    }

    public static String Time(String t){
        int hrs = (Integer.parseInt(t) / 3600000);
        int mns = (Integer.parseInt(t) / 60000) % 60000;
        int scs = Integer.parseInt(t) % 60000 / 1000;
        return (hrs+":"+mns+":"+scs);
    }


}
