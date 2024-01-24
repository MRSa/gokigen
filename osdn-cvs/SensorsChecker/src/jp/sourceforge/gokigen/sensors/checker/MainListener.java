package jp.sourceforge.gokigen.sensors.checker;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import java.io.FileOutputStream;
import android.location.Location;

public class MainListener implements OnClickListener, OnTouchListener, ISensorDataWriter
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);

    
    private FileOutputStream fileOutputStream         = null;
    private FileOutputStream locationFileOutputStream = null;
    private PreferenceUtility prefUtil = null;
    private long writeTime = 0;
    
    private SensorListener sensorHandler = null; // �Z���T�C�x���g�����N���X
    private SensorWrapper  sensors       = null;  // �Z���T���b�p�[

    private Activity parent = null;  // �e��
    private MainUpdater updater = null;
    
    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
        updater = new MainUpdater(argument);
    
        sensorHandler = new SensorListener(argument);
        sensorHandler.setSensorWriter(this);

        sensors  = new SensorWrapper(argument, sensorHandler);
        
        prefUtil = new PreferenceUtility(argument);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        /** �Z���T�̊Ď����� **/        
        sensors.prepareSensor();
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
        // �Z���T�̊Ď��J�n
    	sensors.startWatch(prefUtil);    	

    	// �ʒu�����L�^����t�@�C���̃I�[�v��
    	closeFileStream(locationFileOutputStream);
        locationFileOutputStream = openFileStream(prefUtil.getValueBoolean("isRecordLocation"), 
        		                                  prefUtil.getValueString("locationFileNameValue"),
        		                                  prefUtil.getValueBoolean("isAppendLocation"));

        // �Z���T�����L�^����t�@�C���̃I�[�v��
        closeFileStream(fileOutputStream);       
        fileOutputStream = openFileStream(prefUtil.getValueBoolean("isRecord"),
        		                          prefUtil.getValueString("fileNameValue"),
        		                          prefUtil.getValueBoolean("isAppend"));
    }

    /**
     *   �Z���T�����t�@�C���o�͂���  
     */
	public void writeLocationData(Location location, long duration)
	{
		if (locationFileOutputStream == null)
		{
			return;
		}

		String outData = "";
		try
        {
            // �Z���T���𕶎��񉻂���
            outData = outData + String.valueOf(location.getTime()) + ", ";      // ����
            outData = outData + String.valueOf(location.getLatitude()) + ", ";  // �ܓx
            outData = outData + String.valueOf(location.getLongitude()) + ", "; // �o�x
            outData = outData + String.valueOf(location.getAccuracy()) + ", ";  // ���m���H
            outData = outData + String.valueOf(location.getAltitude()) + ", ";  // ���x
            outData = outData + String.valueOf(location.getSpeed()) + ", ";     // ���x
            outData = outData + String.valueOf(location.getBearing()) + ", ";   // �ӂ�܂��H
            outData = outData + String.valueOf(duration) + "\r\n";			    // �O�񂩂�̎���
			locationFileOutputStream.write(outData.getBytes());	
        }
        catch (Exception e)
        {
        	
        }
        return;		
	}    
    
    /**
     *   �Z���T�����t�@�C���o�͂���  
     */
	public void writeSensorData(long currentTime, int sensorType, String data)
	{
		String outData = currentTime + "\t" + data + "\r\n";
		if ((fileOutputStream != null)&&(writeTime + 100 <= currentTime))
		{
			try
			{
				if (sensorType == Sensor.TYPE_ACCELEROMETER)
				{
			        fileOutputStream.write(outData.getBytes());
				}
			}
			catch (Exception e)
			{
				//
			}
			writeTime = currentTime;
		}
		return;
	}

    /**
     *  �L�^�t�@�C�����I�[�v������	
     * @param isRecord  �I�[�v�����邩�ǂ����m�F
     * @param fileName  �t�@�C����
     * @return  �t�@�C���X�g���[��
     */
	private FileOutputStream openFileStream(boolean isRecord, String fileName, boolean isAppend)
	{
        if (isRecord == false)
        {
            return (null);
        }

		try
        {
            if (fileName.startsWith("/sdcard/") == false)
            {
                fileName = "/sdcard/sensor.txt";
            }
            return (new FileOutputStream(fileName, isAppend));
        }
        catch (Exception e)
        {
        	
        }
		return (null);
	}
	
	/**
	 *  �L�^�t�@�C�����N���[�Y����
	 * @param stream �t�@�C���X�g���[��
	 */
	private void closeFileStream(FileOutputStream stream)
	{
        try
        {
            if (stream != null)
            {
            	stream.close();
            }
        }
        catch (Exception ex)
        {
        	
        }
	
	}
	
	/**
     *  �I������
     */
    public void shutdown()
    {
    	closeFileStream(locationFileOutputStream);
        locationFileOutputStream = null;
        
        closeFileStream(fileOutputStream);
        fileOutputStream = null;

    	// �Z���T�̊Ď���~
    	sensors.finishWatch();	
    }
    
    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // �t�@�C�����I�[�v�����邩...?
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        // int id = v.getId();

    }


    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        // int action = event.getAction();

        return (false);
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_PREFERENCES, Menu.NONE, parent.getString(R.string.preference_name));
    	menuItem.setIcon(android.R.drawable.ic_menu_preferences);
    	
    	return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
    	menu.findItem(MENU_ID_PREFERENCES).setVisible(true);
    	return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	boolean result = false;
    	switch (item.getItemId())
    	{
    	  case MENU_ID_PREFERENCES:
    	    showPreference();
    		result = true;
    		break;

    	  default:
    		result = false;
    		break;
    	}
    	return (result);
    }

    /**
     *  �ݒ��ʂ�\�����鏈��
     */
    private void showPreference()
    {
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.sensors.checker.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
        	 updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
        }
    }
}
