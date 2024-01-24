package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;


/**
 *  テンキー入力画面
 *   startActivityForResult()で呼ばれることを前提とし、入力値をResultで返す。
 *  エラーまたは入力キャンセルの場合には、Activity.RESULT_OK (-1) を応答する。
 *  
 *  ※ 処理はイベントリスナ側で行うので、このクラスでは大した処理は行わない。
 *  
 * @author MRSa
 *
 */
public class InputTenKey extends Activity
{
    static public final String INPUT_INFORMATION = "net.cornn.warikan.InputGuidance";
    static public final String INITIAL_VALUE     = "net.cornn.warikan.InputDefaultValue";
    static public final int    NO_INFORMATION_MESSAGE = -1;

    static public TenKeyListener listener = null;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tenkey);

        /**  イベントリスナとオブジェクト（ボタン）のリンク  **/
        listener = new TenKeyListener((Activity) this);
        final Button cancelButton = (Button) findViewById(R.id.button_Cancel);
        final Button okButton     = (Button) findViewById(R.id.button_Enter);
        final Button zeroButton   = (Button) findViewById(R.id.button_Zero);
        final Button oneButton    = (Button) findViewById(R.id.button_One);
        final Button twoButton    = (Button) findViewById(R.id.button_Two);
        final Button threeButton  = (Button) findViewById(R.id.button_Three);
        final Button fourButton   = (Button) findViewById(R.id.button_Four);
        final Button fiveButton   = (Button) findViewById(R.id.button_Five);
        final Button sixButton    = (Button) findViewById(R.id.button_Six);
        final Button sevenButton  = (Button) findViewById(R.id.button_Seven);
        final Button eightButton  = (Button) findViewById(R.id.button_Eight);
        final Button nineButton   = (Button) findViewById(R.id.button_Nine);
        final Button clearButton  = (Button) findViewById(R.id.button_Clear);
        final Button zero2Button  = (Button) findViewById(R.id.button_ZeroZero);
        final EditText fieldArea  = (EditText) findViewById(R.id.numericData);
        
        
        //  ボタンとそれぞれイベントリスナをつなぐ！
        okButton.setOnClickListener(listener);
        cancelButton.setOnClickListener(listener);
        clearButton.setOnClickListener(listener);
        zeroButton.setOnClickListener(listener);
        oneButton.setOnClickListener(listener);
        twoButton.setOnClickListener(listener);
        threeButton.setOnClickListener(listener);
        fourButton.setOnClickListener(listener);
        fiveButton.setOnClickListener(listener);
        sixButton.setOnClickListener(listener);
        sevenButton.setOnClickListener(listener);
        eightButton.setOnClickListener(listener);
        nineButton.setOnClickListener(listener);
        zero2Button.setOnClickListener(listener);
        
        fieldArea.setOnKeyListener(listener);
    }
    
    /**
     *  画面が裏に回ったときの処理
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }
    
    /**
     *  画面が表に出てきたときの処理
     *  （入力ガイドメッセージと初期値を設定する）
     */
    @Override
    public void onResume()
    {
        super.onResume();

        try
        {
            //  ガイドメッセージの表示
            String message = "";
            Intent intent = getIntent();
            int dummy = NO_INFORMATION_MESSAGE;
            int resId = intent.getIntExtra(INPUT_INFORMATION, dummy);
            if (resId != NO_INFORMATION_MESSAGE)
            {
                message = getString(resId);
            }
            TextView messageArea = (TextView) findViewById(R.id.tenKeyMessage);
            messageArea.setText(message.toCharArray(), 0, message.length());            
        
            // 初期値の表示
            int initialValue = intent.getIntExtra(INITIAL_VALUE, dummy);
            if (initialValue > 0)
            {
                message = "" + initialValue;
                EditText valueArea = (EditText) findViewById(R.id.numericData);
                valueArea.setText(message.toCharArray(), 0, message.length());
                valueArea.setSelection(message.length());
            }
        }
        catch (Exception ex)
        {
            // なんもしない...
        }
    }
    
    /**
     *   キー入力されたとき...
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	try
        {
        	// delegateして処理を継続する。
        	return (listener.onKeyDown(keyCode, event));
        }
        catch (Exception ex)
        {
            // 
        }
        return (false);
    }
}
