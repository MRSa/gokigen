package jp.sourceforge.gokigen.mr999ctl;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *  
 * @author MRSa
 *
 */
public class PreferenceHolder
{
    private Activity parent = null;
    private boolean autoPilotMode = false;  // 自動制御モードフラグ

    /**
     *   コンストラクタ
     * 
     */
    public PreferenceHolder(Activity argument)
    {
        parent = argument;
    }

    /**
     *  自動制御モードを解除する
     */
    public void clearAutoPilotMode()
    {
        autoPilotMode = false;
/*        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("setAutoPilot", false);
*/
    }
    
    /**
     *  現在の自動制御モードを応答する
     * @return true : オートパイロット, false : マニュアル操作
     */
    public boolean getAutoPilotMode()
    {
        return (autoPilotMode);
/*        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getBoolean("setAutoPilot", false));
*/
    }

    /**
     *  自動制御モードフラグをリセットする 
     *  (Preferenceパラメータから読み込んで設定する)
     */
    public void resetAutoPilotMode()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        autoPilotMode = pref.getBoolean("setAutoPilot", false);
    }
    
    
    /**
     *  現在設定されているスクリプトファイル名を応答する
     * @return スクリプトファイル名
     */
    public String getScriptFileName()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString("autoPilotScript", ""));
    }
    
    /**
     *  パラメータデータの読み出しを行う (文字列で応答する)
     * @param id
     * @return
     */
    public String getParameterString(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString(id, "0"));
    }
    
}
