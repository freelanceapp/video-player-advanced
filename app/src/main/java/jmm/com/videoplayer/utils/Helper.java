package jmm.com.videoplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;
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

    public static void ShareSingleFile(String name, Context ctx, String authority)
    {
        //share the file for  NoughtAndAll

        Uri uri=null;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        File file = new File(name);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            uri = Uri.fromFile(file);
            if (uri != null) {
                if (extension.equalsIgnoreCase("") || mimetype == null) {
                    // if there is no extension or there is no definite mimetype, still try to open the file
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } else {
                    intent.setType(mimetype);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }
                // custom message for the intent
                ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));
            }
        }
        else {

            // in case of Android N and above Uri will be  made through provider written in Manifest file;
            uri = FileProvider.getUriForFile(ctx, authority,
                    file);

            if(uri !=null) {
                if (extension.equalsIgnoreCase("") || mimetype == null) {
                    // if there is no extension or there is no definite mimetype, still try to open the file
                    intent.setType("text/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    intent.setType(mimetype);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                // custom message for the intent
                ctx. startActivity(Intent.createChooser(intent, "Choose an Application:"));
            }
            //

        }


    }
}
