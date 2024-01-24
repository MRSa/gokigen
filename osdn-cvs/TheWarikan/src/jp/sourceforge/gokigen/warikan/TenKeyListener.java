package jp.sourceforge.gokigen.warikan;

import jp.sourceforge.gokigen.warikan.R;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

/**
 *  �e���L�[�̃C�x���g���X�i
 * @author MRSa
 *
 */
public class TenKeyListener implements OnClickListener, OnKeyListener
{
    private Activity parent = null;
    
    private final int MAX_INPUT_LENGTH = 7; // �����ȏ�̃f�[�^�͓��͂ł��Ȃ�

    /**
     *  �R���X�g���N�^...�e�������͂��ڂ��Ă����B
     * @param argument
     */
    public TenKeyListener(Activity argument)
    {
        super();
        parent = argument;
    }

    /**
     *  �{�^���������ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int btnId = v.getId();

        // OK�{�^���������ꂽ�Ƃ��̏���
        if (btnId == R.id.button_Enter)
        {
            // �f�[�^�����͂��ꂽ�A�A�A�̂Œl���������ďI������
            finishInput();
            return;
        }

        // Cancel�{�^���������ꂽ�Ƃ��̏���
        if (btnId == R.id.button_Cancel)
        {
        	// �f�[�^�̓��͂��L�����Z������
        	cancelInput();
            return;
        }
        
        // CLR�{�^���������ꂽ�Ƃ��̂����
        if (btnId == R.id.button_Clear)
        {
            // ���͂����l���N���A����
            clearEntryValue();
            return;
        }

        //  �ȍ~�A�����{�^��(0�`9)�������ꂽ�Ƃ��̏���
        if (btnId == R.id.button_Zero)
        {
            entryValue(0);  // 0�{�^��
            return;
        }
        if (btnId == R.id.button_One)
        {
            entryValue(1);  // 1�{�^��
            return;
        }
        if (btnId == R.id.button_Two)
        {
            entryValue(2);  // 2�{�^��
            return;
        }
        if (btnId == R.id.button_Three)
        {
            entryValue(3);  // 3�{�^��
            return;
        }
        if (btnId == R.id.button_Four)
        {
            entryValue(4);  // 4�{�^��
            return;
        }
        if (btnId == R.id.button_Five)
        {
            entryValue(5);  // 5�{�^��
            return;
        }
        if (btnId == R.id.button_Six)
        {
            entryValue(6);  // 6�{�^��
            return;
        }
        if (btnId == R.id.button_Seven)
        {
            entryValue(7);  // 7�{�^��
            return;
        }
        if (btnId == R.id.button_Eight)
        {
            entryValue(8);  // 8�{�^��
            return;
        }
        if (btnId == R.id.button_Nine)
        {
            entryValue(9);  // 9�{�^��
            return;
        }
        if (btnId == R.id.button_ZeroZero)
        {
            // 00�{�^��...0���Q����͂������Ƃɂ���
            entryValue(0);
            entryValue(0);
            return;
        }
        
        // ��L�ȊO...�������Ȃ�
        return;
    }

    /**
     *  �L�[����
     */
    public  boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return (onKeyDown(keyCode, event));
    }

    /**
     *  ���͒l���m�肵�ă_�C�A���O�𔲂���
     */
    private void finishInput()
    {
        exitTenKeyWithValue();
        parent.finish();    	
    }
    
    /**
     *  ���͂��L�����Z�����ă_�C�A���O�𔲂���
     */
    private void cancelInput()
    {
        // �l����ꂸ�ɏI��������
        parent.setResult(Activity.RESULT_OK);
        parent.finish();
        return;
    }

    /**
     *  �l�����Z����
     * @param data  ���͂������l
     */
    private void entryValue(int data)
    {
        try
        {
            String value = "";
            EditText valueField = null;
            valueField = (EditText) parent.findViewById(R.id.numericData);
            value = valueField.getText().toString();
            if (value.length() >= MAX_INPUT_LENGTH)
            {
                // ���͉\�ȕ��������I�[�o�[���Ă���̂ŁA�������Ȃ�
                return;
            }
            // �f�[�^��(������ɕύX����)���͂���
            value = value + data;
            valueField.setText(value.toCharArray(), 0, value.length());
            valueField.setSelection(value.length());
        }
        catch (Exception ex)
        {
            //
        }
        return;
    }

    /**
     *  ���̓t�B�[���h���N���A����
     */
    private void clearEntryValue()
    {
        try
        {
            String value = "";
            EditText valueField = null;
            valueField = (EditText) parent.findViewById(R.id.numericData);
            valueField.setText(value.toCharArray(), 0, value.length());
            valueField.setSelection(value.length());
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }
        return;
    }
    
    /**
     *  ���͂��ꂽ�l�������R�[�h�Ƃ��ċl�߂�B
     *  (�G���[�������ɂ́ARESULT_OK (-1) ���i�[����B)
     */
    private void exitTenKeyWithValue()
    {
        EditText valueField = null;
        try
        {
            // ��ʂ̐��l���̓t�B�[���h����l���擾���A���l�ɕϊ����ĉ�������
            valueField = (EditText) parent.findViewById(R.id.numericData);
            String value = valueField.getText().toString();
            if (value.length() == 0)
            {
                // �l�����͂���Ă��Ȃ��Ƃ��ɂ́A�[���ɂ���
                parent.setResult(0);
                return;
            }
            int intValue = Integer.valueOf(value);
            parent.setResult(intValue);
        }
        catch (Exception e)
        {
            // �l�����ꂸ�ɏI��������
            parent.setResult(Activity.RESULT_OK);
        }
        return;
    }

    /**
     *  �e���L�[�̃L�[����
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	boolean ret = true;
    	
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)||
        	 (keyCode == KeyEvent.KEYCODE_ENTER))
        {
        	// �Z���^�[�{�^���AEnter�A�f�[�^���͂��m�肳����B
            finishInput();
            return (true);
        }

        if ((keyCode == KeyEvent.KEYCODE_CLEAR))
        {
            // �N���A�{�^���A�A�A���͂��N���A����
      	    clearEntryValue();
      	    return (true);
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            // �o�b�N�{�^���A�A�A���͂��L�����Z������
        	cancelInput();
        	return (true);
        }
/*
        else if ((keyCode == KeyEvent.KEYCODE_0))
        {
            //  �����L�[�̃[�������
        	entryValue(0);
        }
        else if ((keyCode == KeyEvent.KEYCODE_1))
        {
            //  �����L�[��1�����
        	entryValue(1);
        }        
        else if ((keyCode == KeyEvent.KEYCODE_2))
        {
            //  �����L�[��2�����
        	entryValue(2);
        }
        else if ((keyCode == KeyEvent.KEYCODE_3))
        {
            //  �����L�[��3�����
        	entryValue(3);
        }
        else if ((keyCode == KeyEvent.KEYCODE_4))
        {
            //  �����L�[��4�����
        	entryValue(4);
        }
        else if ((keyCode == KeyEvent.KEYCODE_5))
        {
            //  �����L�[��5�����
        	entryValue(5);
        }
        else if ((keyCode == KeyEvent.KEYCODE_6))
        {
            //  �����L�[��6�����
        	entryValue(6);
        }
        else if ((keyCode == KeyEvent.KEYCODE_7))
        {
            //  �����L�[��7�����
        	entryValue(7);
        }
        else if ((keyCode == KeyEvent.KEYCODE_8))
        {
            //  �����L�[��8�����
        	entryValue(8);
        }
        else if ((keyCode == KeyEvent.KEYCODE_9))
        {
            //  �����L�[��9�����
        	entryValue(9);
        }
*/
        else
        {
        	// ��L�ȊO�����͂��ꂽ�Ƃ�
        	ret = false;
        }
        return (ret);
    }   
}
