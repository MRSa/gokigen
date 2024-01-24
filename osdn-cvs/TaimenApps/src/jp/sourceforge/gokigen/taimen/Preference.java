package jp.sourceforge.gokigen.taimen;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 *    Androidの設定画面
 * 
 * @author MRSa
 *
 */
public class Preference extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    /**
     *  コンストラクタ
     *
     **/
    public Preference()
    {
    	
    }
    
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
