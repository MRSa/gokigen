package jp.sourceforge.gokigen.taimen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

/**
 *   �e�L�X�g�f�[�^�̔��f
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
     *    �R���X�g���N�^
     * 
     */
	public TextEditReceiver(Activity argument, String prefId, int resId)
    {
        textId = prefId;
        parent = argument;
        textResId = resId;
    }
	
	/**
	 *   �f�[�^�̍X�V
	 * 
	 */
    public boolean finishTextEditDialog(String message)
    {
    	// ��������L�^
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(textId, message);
        editor.commit();

        // ��ʕ\���̍X�V
    	final TextView infoText = (TextView) parent.findViewById(textResId);
    	infoText.setText(message);
        
        return (true);
    }

    /**
     *   �f�[�^���X�V���Ȃ��Ƃ�...
     */
    public boolean cancelTextEditDialog()
    {
        return (false);
    }
}
