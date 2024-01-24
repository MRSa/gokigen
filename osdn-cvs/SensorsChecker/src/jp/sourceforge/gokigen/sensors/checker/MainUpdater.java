package jp.sourceforge.gokigen.sensors.checker;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  画面表示の更新クラス
 *  
 * @author MRSa
 */
public class MainUpdater
{
    private Activity parent = null;

    public static final int SHOWMETHOD_DONTCARE = 0;
    public static final int SHOWMETHOD_TOAST = 1;

    /**
     *  コンストラクタ
     *  
     * @param argument 親クラス
     */
    public MainUpdater(Activity argument)
    {
        super();
        parent = argument;
    }

    /**
     *   画面表示の更新
     */
    public void updateScreen()
    {
         // 
    }

    /**
     *  TextViewにString型の値を設定する
     * @param id
     * @param value
     */
    public void setValueToTextView(int id, String stringData)
    {
        try
        {
            TextView view = (TextView) parent.findViewById(id);
            view.setText(stringData.toCharArray(), 0, stringData.length());
        }
        catch (Exception ex)
        {
            //
        }
    }
    
    /**
     *  メッセージを表示する
     * @param message     表示するメッセージ
     * @param showMethod  表示方式
     */
    public void showMessage(String message, int showMethod)
    {
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
    }
}
