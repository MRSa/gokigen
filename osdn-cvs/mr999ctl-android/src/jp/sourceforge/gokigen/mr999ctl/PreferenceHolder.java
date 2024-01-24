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
    private boolean autoPilotMode = false;  // �������䃂�[�h�t���O

    /**
     *   �R���X�g���N�^
     * 
     */
    public PreferenceHolder(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �������䃂�[�h����������
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
     *  ���݂̎������䃂�[�h����������
     * @return true : �I�[�g�p�C���b�g, false : �}�j���A������
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
     *  �������䃂�[�h�t���O�����Z�b�g���� 
     *  (Preference�p�����[�^����ǂݍ���Őݒ肷��)
     */
    public void resetAutoPilotMode()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        autoPilotMode = pref.getBoolean("setAutoPilot", false);
    }
    
    
    /**
     *  ���ݐݒ肳��Ă���X�N���v�g�t�@�C��������������
     * @return �X�N���v�g�t�@�C����
     */
    public String getScriptFileName()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString("autoPilotScript", ""));
    }
    
    /**
     *  �p�����[�^�f�[�^�̓ǂݏo�����s�� (������ŉ�������)
     * @param id
     * @return
     */
    public String getParameterString(String id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return (pref.getString(id, "0"));
    }
    
}
