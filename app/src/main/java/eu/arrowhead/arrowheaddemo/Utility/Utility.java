package eu.arrowhead.arrowheaddemo.Utility;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public final class Utility {

    private static final Gson gson = new Gson();

    private Utility(){
    }

    public static <T> String toJson(T object){
        return gson.toJson(object);
    }

    public static <T> T fromJsonObject(String json, Class<T> parsedClass){
        return gson.fromJson(json, parsedClass);
    }

    public static <T> List<T> fromJsonArray(String json, final Class<T> parsedClass){
        return gson.fromJson(json, new ListOfJson<T>(parsedClass));
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String createLatestStopTime(int hourOfDay, int minute){
        Calendar cal = Calendar.getInstance();

        //User entered a time, which is already happened today so we assume it's on tomorrow
        if(cal.get(Calendar.HOUR_OF_DAY) > hourOfDay ||
                (cal.get(Calendar.HOUR_OF_DAY) == hourOfDay && cal.get(Calendar.MINUTE) > minute)){
            cal.add(Calendar.DATE, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        //DEFAULT FORMAT: "EEE MMM dd HH:mm:ss z yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(cal.getTime());
    }


}
