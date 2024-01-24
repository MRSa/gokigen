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
     *  �p�����[�^�f�[�^�̓ǂݏo�����s�� (boolean�l�ŉ�������)
     * @param id
     * @return
     */
    public boolean getValueBoolean(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getBoolean(id, false));
    }

    /**
     *  �p�����[�^�f�[�^�̓ǂݏo�����s�� (������ŉ�������)
     * @param id
     * @return
     */
    public String getValueString(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString(id, "0"));
    }

}
