package eu.arrowhead.arrowheaddemo.Utility;


import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

//Initializing the custom date/time library to make life easier.
//This is an adaption of the java.time library from Java 8 (not yet available officially in Android)
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
