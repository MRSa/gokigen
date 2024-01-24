package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

/**
 *  テンキーのイベントリスナ
 * @author MRSa
 *
 */
public class TenKeyListener implements OnClickListener, OnKeyListener
{
    private Activity parent = null;
    
    private final int MAX_INPUT_LENGTH = 7; // 七桁以上のデータは入力できない

    /**
     *  コンストラクタ...親分だけはおぼえておく。
     * @param argument
     */
    public TenKeyListener(Activity argument)
    {
        super();
        parent = argument;
    }

    /**
     *  ボタンが押されたときの処理
     */
    public void onClick(View v)
    {
        int btnId = v.getId();

        // OKボタンが押されたときの処理
        if (btnId == R.id.button_Enter)
        {
            // データが入力された、、、ので値を応答して終了する
            finishInput();
            return;
        }

        // Cancelボタンが押されたときの処理
        if (btnId == R.id.button_Cancel)
        {
        	// データの入力をキャンセルする
        	cancelInput();
            return;
        }
        
        // CLRボタンが押されたときのしょり
        if (btnId == R.id.button_Clear)
        {
            // 入力した値をクリアする
            clearEntryValue();
            return;
        }

        //  以降、数字ボタン(0～9)が押されたときの処理
        if (btnId == R.id.button_Zero)
        {
            entryValue(0);  // 0ボタン
            return;
        }
        if (btnId == R.id.button_One)
        {
            entryValue(1);  // 1ボタン
            return;
        }
        if (btnId == R.id.button_Two)
        {
            entryValue(2);  // 2ボタン
            return;
        }
        if (btnId == R.id.button_Three)
        {
            entryValue(3);  // 3ボタン
            return;
        }
        if (btnId == R.id.button_Four)
        {
            entryValue(4);  // 4ボタン
            return;
        }
        if (btnId == R.id.button_Five)
        {
            entryValue(5);  // 5ボタン
            return;
        }
        if (btnId == R.id.button_Six)
        {
            entryValue(6);  // 6ボタン
            return;
        }
        if (btnId == R.id.button_Seven)
        {
            entryValue(7);  // 7ボタン
            return;
        }
        if (btnId == R.id.button_Eight)
        {
            entryValue(8);  // 8ボタン
            return;
        }
        if (btnId == R.id.button_Nine)
        {
            entryValue(9);  // 9ボタン
            return;
        }
        if (btnId == R.id.button_ZeroZero)
        {
            // 00ボタン...0を２回入力したことにする
            entryValue(0);
            entryValue(0);
            return;
        }
        
        // 上記以外...何もしない
        return;
    }

    /**
     *  キー入力
     */
    public  boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return (onKeyDown(keyCode, event));
    }

    /**
     *  入力値を確定してダイアログを抜ける
     */
    private void finishInput()
    {
        exitTenKeyWithValue();
        parent.finish();    	
    }
    
    /**
     *  入力をキャンセルしてダイアログを抜ける
     */
    private void cancelInput()
    {
        // 値を入れずに終了させる
        parent.setResult(Activity.RESULT_OK);
        parent.finish();
        return;
    }

    /**
     *  値を加算する
     * @param data  入力した数値
     */
    private void entryValue(int data)
    {
        try
        {
            String value = "";
            EditText valueField = null;
            valueField = (EditText) parent.findViewById(R.id.numericData);
            value = valueField.getText().toString();
            if (value.length() >= MAX_INPUT_LENGTH)
            {
                // 入力可能な文字数をオーバーしているので、何もしない
                return;
            }
            // データを(文字列に変更して)入力する
            value = value + data;
            valueField.setText(value.toCharArray(), 0, value.length());
            valueField.setSelection(value.length());
        }
        catch (Exception ex)
        {
            //
        }
        return;
    }

    /**
     *  入力フィールドをクリアする
     */
    private void clearEntryValue()
    {
        try
        {
            String value = "";
            EditText valueField = null;
            valueField = (EditText) parent.findViewById(R.id.numericData);
            valueField.setText(value.toCharArray(), 0, value.length());
            valueField.setSelection(value.length());
        }
        catch (Exception ex)
        {
            // なにもしない
        }
        return;
    }
    
    /**
     *  入力された値を応答コードとして詰める。
     *  (エラー発生時には、RESULT_OK (-1) を格納する。)
     */
    private void exitTenKeyWithValue()
    {
        EditText valueField = null;
        try
        {
            // 画面の数値入力フィールドから値を取得し、数値に変換して応答する
            valueField = (EditText) parent.findViewById(R.id.numericData);
            String value = valueField.getText().toString();
            if (value.length() == 0)
            {
                // 値が入力されていないときには、ゼロにする
                parent.setResult(0);
                return;
            }
            int intValue = Integer.valueOf(value);
            parent.setResult(intValue);
        }
        catch (Exception e)
        {
            // 値をいれずに終了させる
            parent.setResult(Activity.RESULT_OK);
        }
        return;
    }

    /**
     *  テンキーのキー入力
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	boolean ret = true;
    	
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||
        	 (keyCode == KeyEvent.KEYCODE_ENTER))
        {
        	// センターボタン、Enter、データ入力を確定させる。
            finishInput();
            return (true);
        }

        if ((keyCode == KeyEvent.KEYCODE_CLEAR))
        {
            // クリアボタン、、、入力をクリアする
      	    clearEntryValue();
      	    return (true);
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            // バックボタン、、、入力をキャンセルする
        	cancelInput();
        	return (true);
        }
/*
        else if ((keyCode == KeyEvent.KEYCODE_0))
        {
            //  数字キーのゼロを入力
        	entryValue(0);
        }
        else if ((keyCode == KeyEvent.KEYCODE_1))
        {
            //  数字キーの1を入力
        	entryValue(1);
        }        
        else if ((keyCode == KeyEvent.KEYCODE_2))
        {
            //  数字キーの2を入力
        	entryValue(2);
        }
        else if ((keyCode == KeyEvent.KEYCODE_3))
        {
            //  数字キーの3を入力
        	entryValue(3);
        }
        else if ((keyCode == KeyEvent.KEYCODE_4))
        {
            //  数字キーの4を入力
        	entryValue(4);
        }
        else if ((keyCode == KeyEvent.KEYCODE_5))
        {
            //  数字キーの5を入力
        	entryValue(5);
        }
        else if ((keyCode == KeyEvent.KEYCODE_6))
        {
            //  数字キーの6を入力
        	entryValue(6);
        }
        else if ((keyCode == KeyEvent.KEYCODE_7))
        {
            //  数字キーの7を入力
        	entryValue(7);
        }
        else if ((keyCode == KeyEvent.KEYCODE_8))
        {
            //  数字キーの8を入力
        	entryValue(8);
        }
        else if ((keyCode == KeyEvent.KEYCODE_9))
        {
            //  数字キーの9を入力
        	entryValue(9);
        }
*/
        else
        {
        	// 上記以外が入力されたとき
        	ret = false;
        }
        return (ret);
    }   
}
