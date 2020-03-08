package tw.org.iii.brad.brad05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private boolean isRunning;
    private Button leftBtn, rightBtn;
    private Timer timer = new Timer();
    private int hs;
    private Counter counter;
    private TextView clock;
    private UIHander uiHander = new UIHander();

    private ListView listView;
    private SimpleAdapter adapter;
    private LinkedList<HashMap<String,String>> data = new LinkedList<>();
    private String[] from = {"lap","time1","time2"};
    private int[] to = {R.id.lap_rank, R.id.lap_time1, R.id.lap_time2};
    private int lapCounter;
    private int lastHs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        clock = findViewById(R.id.clock);
        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        changeDisplay();
        clock.setText(parseHS(hs));

        initLap();
    }

    private void initLap(){
        adapter = new SimpleAdapter(this, data, R.layout.layout_lap, from, to);
        listView.setAdapter(adapter);
    }

    private void changeDisplay(){
        rightBtn.setText(isRunning?"STOP":"START");
        leftBtn.setText(isRunning?"LAP":"RESET");
    }

    public void doLeft(View view) {
        if (isRunning){
            // LAP
            doLap();
        }else{
            // RESET
            doReset();
        }
    }

    private void doReset(){
        hs = 0;
        lastHs = 0;
        lapCounter = 0;
        data.clear();
        adapter.notifyDataSetChanged();
        clock.setText(parseHS(hs));
    }

    private void doLap(){
        int dHs = hs - lastHs;
        lastHs = hs;
        HashMap<String,String> row = new HashMap<>();
        row.put(from[0], "lap " + ++lapCounter);
        row.put(from[1], parseHS(dHs));
        row.put(from[2], parseHS(hs));
        data.add(0, row);
        adapter.notifyDataSetChanged();
    }

    public void doRight(View view) {
        isRunning = !isRunning;
        changeDisplay();

        if (isRunning){
            counter = new Counter();
            timer.schedule(counter, 10, 10);
        }else{
            counter.cancel();
            counter = null;
        }

    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            hs++;
            uiHander.sendEmptyMessage(0);
        }
    }

    private class UIHander extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(parseHS(hs));
        }
    }

    private static String parseHS(int hs){
        int phs = hs % 100;
        int ts = hs / 100;
        int hh = ts / (60*60);
        int mm = (ts - hh*60*60) / 60;
        int ss = ts % 60;

        return String.format("%d:%d:%d.%s", hh, mm, ss, (phs<10?"0"+phs:phs));


    }

    @Override
    public void finish() {
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        super.finish();
    }
}
