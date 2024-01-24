package jp.sourceforge.gokigen.aligner;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtility
{
    private Activity parent = null;
    public PreferenceUtility(Activity argument)
    {
        parent = argument;
    }

    /**
     *  パラメータデータの読み出しを行う (boolean値で応答する)
     * @param id
     * @return
     */
    public boolean getValueBoolean(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getBoolean(id, false));
    }

    /**
     *  パラメータデータの読み出しを行う (文字列で応答する)
     * @param id
     * @return
     */
    public String getValueString(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString(id, "0"));
    }

}
