package gamgultony.myshellf;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EmerActivity extends AppCompatActivity {
    int i;
    boolean is=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer);

        final TextView textView = (TextView) findViewById(R.id.count);
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is=false;
                finishAffinity();
            }
        });
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("2");
                Handler delayHandler2 = new Handler();
                delayHandler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("1");
                        Handler delayHandler3 = new Handler();
                        delayHandler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(is) {
                                    Toast.makeText(getApplicationContext(), " 실행", Toast.LENGTH_SHORT).show();//메세지 띄우기
                                    stopService(new Intent(EmerActivity.this,WigetService.class));
                                    Intent intent=new Intent(EmerActivity.this,EmerAction.class);
                                    startActivity(intent);

                                }
                            }
                        }, 1000);
                    }
                }, 1000);
            }
        }, 1000);

    }
}
