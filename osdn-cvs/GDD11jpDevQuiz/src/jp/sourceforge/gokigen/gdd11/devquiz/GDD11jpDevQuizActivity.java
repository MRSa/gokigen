package jp.sourceforge.gokigen.gdd11.devquiz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GDD11jpDevQuizActivity extends Activity implements OnClickListener
{
    private com.google.android.apps.gddquiz.IQuizService serviceIF = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(com.google.android.apps.gddquiz.IQuizService.class.getName());
        bindService(intent, gddQuizServiceConn, BIND_AUTO_CREATE);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unbindService(gddQuizServiceConn);
    }

    /**
     *   ボタンがクリックされたときの処理
     */
    public void onClick(View v)
    {
        if (serviceIF != null)
        {
            Log.v("GDD11jpDevQuiz", "+++START+++");
            try
            {
                String code = serviceIF.getCode();
                Log.v("GDD11jpDevQuiz", "CODE : " + code);

                TextView field = (TextView) findViewById(R.id.dataField);
                field.setText(code);
            
            }
            catch (Exception ex)
            {
                Log.v("GDD11jpDevQuiz", "EXCEPTION : " + ex.toString());            	
            }
            Log.v("GDD11jpDevQuiz", "+++ END +++");
        }
    }

    private ServiceConnection gddQuizServiceConn = new ServiceConnection()
    {		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			serviceIF = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			serviceIF = com.google.android.apps.gddquiz.IQuizService.Stub.asInterface(service);
		}
	};
}
