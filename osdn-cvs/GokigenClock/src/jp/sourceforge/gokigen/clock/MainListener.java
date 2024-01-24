package jp.sourceforge.gokigen.clock;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MainListener implements OnClickListener, OnTouchListener
{
    public static final int MENU_ID_PREFERENCES = (Menu.FIRST + 1);

    private SensorListener sensorHandler = null; // �Z���T�C�x���g�����N���X
    private SensorWrapper  sensors       = null;  // �Z���T���b�p�[

    private Activity parent = null;  // �e��
//    private MainUpdater updater = null;
        
    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public MainListener(Activity argument)
    {
        parent = argument;
//        updater = new MainUpdater(argument);
    
        sensorHandler = new SensorListener(argument, Sensor.TYPE_ORIENTATION);
        sensors  = new SensorWrapper(argument, sensorHandler);
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {   
        /** �Z���T�̊Ď����� **/        
        sensors.prepareSensor();

        /** �Z���T����`��N���X�ƂȂ� **/
        try
        {
            final GokigenGLSurfaceView glSurfaceView  = (GokigenGLSurfaceView) parent.findViewById(R.id.GlGraphicView);
            glSurfaceView.setOrientationHolder(sensorHandler);
        }
        catch (Exception ex)
        {
            // �_�~�[�̃K�x�R��
            System.gc();
        }
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
        // �Z���T�̊Ď��J�n
        sensors.startWatch();        
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
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
/*
        try
        {
            // �ݒ��ʂ��Ăяo��
            Intent prefIntent = new Intent(parent,jp.sourceforge.gokigen.clock.Preference.class);
            parent.startActivityForResult(prefIntent, 0);
        }
        catch (Exception e)
        {
             // ��O����...�Ȃɂ����Ȃ��B
             updater.showMessage("ERROR", MainUpdater.SHOWMETHOD_DONTCARE);
        }
*/
    }
}
