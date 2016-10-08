package eu.arrowhead.arrowheaddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class ResponseActivity extends AppCompatActivity {

    private TextView startTime, stopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        //Setting up the textviews
        startTime = (TextView) findViewById(R.id.start_time_textview);
        stopTime = (TextView) findViewById(R.id.stop_time_textview);

        Calendar cal = Calendar.getInstance();
        startTime.setText("Charging starts at: " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
        stopTime.setText("Charging ends at: " + (cal.get(Calendar.HOUR_OF_DAY) + 2) + ":" + cal.get(Calendar.MINUTE));
    }
}
