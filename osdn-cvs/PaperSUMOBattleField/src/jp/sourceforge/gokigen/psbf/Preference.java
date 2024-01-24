package jp.sourceforge.gokigen.psbf;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 *    Android�̐ݒ���
 * 
 * @author MRSa
 *
 */
public class Preference extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    /**
     *  �R���X�g���N�^
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
     *  �ݒ�l���ύX���ꂽ�Ƃ��̏���
     */
    public void onSharedPreferenceChanged(SharedPreferences shardPref, String key)
    {
        //
    }
}
