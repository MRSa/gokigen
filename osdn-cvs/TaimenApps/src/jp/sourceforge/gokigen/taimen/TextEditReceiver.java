package jp.sourceforge.gokigen.taimen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 *   テキストデータの反映
 * 
 * @author MRSa
 *
 */
public class TextEditReceiver implements TextEditDialog.ITextEditResultReceiver
{
	Activity parent = null;
	String textId = null;
	int     textResId = -1;
	
    /**
     *    コンストラクタ
     * 
     */
	public TextEditReceiver(Activity argument, String prefId, int resId)
    {
        textId = prefId;
        parent = argument;
        textResId = resId;
    }
	
	/**
	 *   データの更新
	 * 
	 */
    public boolean finishTextEditDialog(String message)
    {
    	// 文字列を記録
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(textId, message);
        editor.commit();

        // 画面表示の更新
    	final TextView infoText = (TextView) parent.findViewById(textResId);
    	infoText.setText(message);
        
        return (true);
    }

    /**
     *   データを更新しないとき...
     */
    public boolean cancelTextEditDialog()
    {
        return (false);
    }
}
