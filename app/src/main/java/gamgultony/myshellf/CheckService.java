package gamgultony.myshellf;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class CheckService  extends Service implements SensorEventListener {
    private static IntentFilter plugIntentFilter;
    private static IntentFilter screenFilter;
    private static BroadcastReceiver plugStateChangeReceiver;
    private static BroadcastReceiver screenOffReceiver;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    SharedPreferences settings;//설정 저장소
    int FINISH_INTERVAL_TIME=1200;
    long lastTouchedTime;
    boolean isPlugged;
    boolean charge=false;
    AudioManager audio;
    public static int cnt;
    boolean plug,power,shake;
    private static int SHAKE_THRESHOLD = 3000;//강도
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;
    public int limit;
    public int cntv;
    int cnts=0;
    int ofcnts=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("aaaaaaa","b started");


        plugStateChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                audio = (AudioManager) getSystemService(CheckService.this.AUDIO_SERVICE);
                isPlugged = (intent.getIntExtra("state", 0) > 0) ? true : false;
                if (isPlugged) {
                    Log.d("aaaaa", "Earphone is plugged");
                    charge=true;
                }else {
                    Log.d("aaaaaa", "Earphone is unPlugged");
                    if (charge) {
                        charge = false;
                        Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                        startActivity(emerIntent);
                    }
                }
                }
            };

        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                    Log.d("aaaaa","hahaha");
                    Log.d("aaaaa",Integer.toString(cnt));
                    if(System.currentTimeMillis() < lastTouchedTime + FINISH_INTERVAL_TIME)
                    {
                        cnt++;
                        if(cnt>=limit)
                        {
                            Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                            startActivity(emerIntent);
                            cnt=0;
                        }
                    }else {
                        cnt=0;
                    }
                    lastTouchedTime=System.currentTimeMillis();
                }
            }
        };
        if(plug) {
            registerReceiver(plugStateChangeReceiver, plugIntentFilter);
        }
        if(power)
        {
            registerReceiver(screenOffReceiver, screenFilter);

        }if(shake){
            if (accelerormeterSensor != null)
                sensorManager.registerListener(this, accelerormeterSensor,
                        SensorManager.SENSOR_DELAY_GAME);

        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        screenFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        settings=getSharedPreferences("SETTINGS",Activity.MODE_PRIVATE);
        plugIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Log.d("aaaaaa","a started");
        cnt=0;
        power=settings.getBoolean("power",false);
        shake=settings.getBoolean("shake",false);
        plug=settings.getBoolean("plug",false);
        SHAKE_THRESHOLD=(1000+(settings.getInt("pow",3)*200));
        limit=(settings.getInt("poc",3)+3);
        cntv=(settings.getInt("rate",2)+2);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 200) {

                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    cnts++;
                    if(cnts>=cntv) {
                        Intent emerIntent = new Intent(CheckService.this, EmerActivity.class);
                        startActivity(emerIntent);
                        cnts=0;
                    }
                }else
                {
                    ofcnts++;
                    if(ofcnts>=4)
                    {
                        ofcnts=0;
                        cnts=0;
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}
