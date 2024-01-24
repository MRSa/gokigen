package jp.sourceforge.gokigen.mr999ctl;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;;

/**
 *  
 * @author MRSa
 *
 */
public class MR999Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    
    /**
     *  コンストラクタ
     *
     **/

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     *  設定値が変更されたときの処理
     */
    public void onSharedPreferenceChanged(SharedPreferences shardPref, String key)
    {
        //
    }
}
