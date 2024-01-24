package jp.sourceforge.gokigen.memoma;

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
    	if ((message == null)||(message.length() == 0))
    	{
            // �f�[�^�����͂���Ă��Ȃ������̂ŁA�������Ȃ��B
    		return (false);
    	}
    	
    	// ��������L�^
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(textId, message);
        editor.commit();

        if (textResId != 0)
        {
            // ��ʕ\���̍X�V
        	final TextView infoText = (TextView) parent.findViewById(textResId);
        	infoText.setText(message);
        }
        else
        {
        	// ���\�[�XID���w�肳��Ă��Ȃ��ꍇ�́A�^�C�g�����X�V����
        	parent.setTitle(message);
        }
        
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
