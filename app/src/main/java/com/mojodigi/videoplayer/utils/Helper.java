package com.mojodigi.videoplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Helper {




   public static boolean isPaused=false;
    //convert date
    public static String LongToDate(String longV) {
        long input = Long.parseLong(longV.trim());
        Date date = new Date(input * 1000); // *1000 gives accurate date otherwise returns 1970
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
    }

    //convert time

    public static String convertDuration(long duration) {

        int dur = (int) duration;
        int hrs = (dur / 3600000);

        if (hrs > 0) {
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        } else {
            return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }

    }

    //share video
    public static void ShareSingleFile(String name, Context ctx, String authority) {
        //share the file for  NoughtAndAll

        try {
            Uri uri = null;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            File file = new File(name);
            String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
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
            } else {

                // in case of Android N and above Uri will be  made through provider written in Manifest file;
                uri = FileProvider.getUriForFile(ctx, authority,
                        file);

                if (uri != null) {
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
                    ctx.startActivity(Intent.createChooser(intent, "Choose an Application:"));
                }
                //

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //for video size convert in MBKB
    public static String humanReadableByteCount(long bytes, boolean si) {

       /* int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);*/

        int unit = 1024;
        if (bytes < unit) return bytes + " Byte";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);

    }

    public static String getScreenOrientation(Activity activity) {
        String orientation = "";
        int currentOrientation = activity.getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = "Landscape";
        }

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = "Portrait";
        }
        return orientation;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static Typeface typeFace_corbel(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "corbel.ttf");
    }

    public static Typeface typeFace_FFF(Context ctx) {
        return Typeface.createFromAsset(ctx.getAssets(), "FFF_Tusj.ttf");
    }


    public static String appendUrl(String jsonUrl)
    {

        //  as  per discussion with  gyan  on 15-05-2019;

        String appendStr1="https://m.khulasa-news.com/";
        String appendStr2="/eng";

        return  appendStr1+jsonUrl+appendStr2;

    }
}
