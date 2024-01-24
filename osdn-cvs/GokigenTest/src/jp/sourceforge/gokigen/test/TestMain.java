package jp.sourceforge.gokigen.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/** my sample app **/
public class TestMain extends Activity  implements OnClickListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prepareScreen();
        setListener();
    }

    private void prepareScreen()
    {
        TextView view = (TextView) findViewById(R.id.infoArea);
        view.setText("vibration duration (ms)");

        EditText textedit = (EditText) findViewById(R.id.inputArea);
        textedit.setText("500");
    }

    private void setListener()
    {
        Button btn = (Button) findViewById(R.id.pushButton);
        btn.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        long duration = 500;
        
        EditText textView = (EditText) findViewById(R.id.inputArea);
        String data = (textView.getText()).toString();
        try
        {
            duration = Long.parseLong(data);
        }
        catch (Exception ex)
        {
            Log.v(getString(R.string.app_name), "|" + ex.toString() + "|");
        }
        doVibrate(duration);
    }

    private void doVibrate(long duration)
    {
        try
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(duration);
        }
        catch (Exception ex)
        {
            Log.v(getString(R.string.app_name), ex.toString() + " " + " dur.: " + duration);
        }
    }
}