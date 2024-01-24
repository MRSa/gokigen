package jp.sourceforge.gokigen.mr999ctl;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  メイン画面の表示を更新する人
 *  (今回は使わないかな？)
 *  
 * @author MRSa
 *
 */
public class MainScreenUpdater
{
    private Activity parent = null;

    /**
     *  コンストラクタ
     * @param argument
     */
    public MainScreenUpdater(Activity argument)
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
     *  メッセージを表示する(デバッグ用)
     * @param message
     */
    public void toastMessage(String message)
    {
        Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();
    }
}
