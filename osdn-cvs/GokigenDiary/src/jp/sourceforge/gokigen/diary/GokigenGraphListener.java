package jp.sourceforge.gokigen.diary;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 *  ��������O���t��ʂ̏����N���X
 * 
 * @author MRSa
 *
 */
public class GokigenGraphListener  implements OnClickListener, OnTouchListener, OnKeyListener, ICanvasDrawer
{
    // ���j���[
	public static final int MENU_ID_SHOW_DAILY = (Menu.FIRST + 1);   // �����ƏW�v
    public static final int MENU_ID_SHOW_WEEKLY = (Menu.FIRST + 2);  // �T���ƏW�v
    public static final int MENU_ID_SHOW_MONTHLY = (Menu.FIRST + 3); // �����ƏW�v
    
    // �W�v�^�C�v
    public static final int REPORTTYPE_DAILY = 10;   // �����ƏW�v���[�h
    public static final int REPORTTYPE_WEEKLY = 11;  // �T���ƏW�v���[�h
    public static final int REPORTTYPE_MONTHLY = 12; // �����ƏW�v���[�h
    private int reportType = REPORTTYPE_DAILY;     // ���݂̏W�v�^�C�v

    // �t���b�N�A�N�V�����̋��
    private static final int FLICK_NOTHING = 0;               // �s��
    private static final int FLICK_UP = 1;                    // �������
    private static final int FLICK_DOWN = 2;                  // ��������
    private static final int FLICK_LEFT= 3;                   // ��������
    private static final int FLICK_RIGHT = 4;                 // �E������
    //private static final int FLICK_DIAGONAL_LEFT_DOWN = 5;    // ���ォ��E���֎΂ߕ�����
    //private static final int FLICK_DIAGONAL_RIGHT_DOWN = 6;   // �E�ォ�獶���֎΂ߕ�����
    //private static final int FLICK_DIAGONAL_LEFT_UP = 7;      // ��������E��֎΂ߕ�����
    //private static final int FLICK_DIAGONAL_RIGHT_UP = 8;     // �E�����獶��֎΂ߕ�����

    // �t���b�N���o�p���[�N�ϐ�
    private float onTouchPosX = 0;
    private float onTouchPosY = 0;
    private boolean flicking = false;
    
    // �\���f�[�^�̔N�E���E��
    private int showYear = 0;
    private int showMonth = 0;
    private int showDay = 0;
    
    // ���p����N���X
    private Activity parent = null;                           // �e���̃N���X
    private ProgressDialog progressDialog = null;             // ���s���_�C�A���O�\���N���X

    private GokigenGraphDataHolder gokigenDataHolder = null;  // �f�[�^�ێ��N���X
    
    private IGokigenGraphDrawer    currentGraphDrawer = null; // �`��Ɏg�p���Ă���`��N���X
    private IGokigenGraphDrawer    pieChartDrawer = null;     // �~�O���t�`��N���X
    private IGokigenGraphDrawer    lineChartDrawer = null;    // �܂���O���t�`��N���X
    private IGokigenGraphDrawer    barChartDrawer = null;     // �_�O���t�`��N���X

    /**
     *   �R���X�g���N�^
     * @param arg
     */
    public GokigenGraphListener(Activity arg)
    {
        parent = arg;

        // �v���O���X�_�C�A���O�̐���
        progressDialog = new ProgressDialog(parent);

        gokigenDataHolder = new GokigenGraphDataHolder();        
        pieChartDrawer = new GokigenPieChart(parent);
        lineChartDrawer = new GokigenLineChart(parent);
        barChartDrawer = new GokigenBarChart(parent);

        // ���ۂɕ`��Ɏg�p����N���X��ݒ肷��
        currentGraphDrawer = lineChartDrawer;
        currentGraphDrawer.prepare();
    }

    /**
     *  �N�����Ƀf�[�^����������
     * 
     * @param myIntent
     */
    public void prepareExtraDatas(Intent myIntent)
    {
        try
        {
            // Intent�ŏE�����f�[�^��ǂݏo�� (�������f�[�^)
            showYear = myIntent.getIntExtra(GokigenGraph.TARGET_YEAR, 2010);
            showMonth = myIntent.getIntExtra(GokigenGraph.TARGET_MONTH, 9);
            showDay = myIntent.getIntExtra(GokigenGraph.TARGET_DAY, 10);

            // Preference�ɋL�����ꂽ�f�[�^������΂���𔽉f������i���������{���f�[�^�j
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            showYear = preferences.getInt("graphYear", showYear);
            showMonth = preferences.getInt("graphMonth", showMonth);
            showDay = preferences.getInt("graphDay", showDay);

            // �f�[�^���\�z���A��ʂ̕\�����X�V����
            prepareGokigenData();
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "EXCEPTION :" + ex.getMessage());
        }        
    }
    
    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ����� (��ʂ��\�ɂ������̏���)
     * 
     */
    public void prepareListener()
    {
        // �O���t�����̗̈�
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.setCanvasDrawer(this);
        
        // �O�̃f�[�^�\��
        final ImageButton previous = (ImageButton) parent.findViewById(R.id.changePrevious);
        previous.setOnClickListener(this);

        // ���̃f�[�^�\��
        final ImageButton next = (ImageButton) parent.findViewById(R.id.changeNext);
        next.setOnClickListener(this);

        // �~�O���t�\��        
        final ImageButton graphStyle = (ImageButton) parent.findViewById(R.id.changeGraphStyleButton);
        graphStyle.setOnClickListener(this);

        // �_�O���t�\��
        final ImageButton barChart = (ImageButton) parent.findViewById(R.id.changeBargraphStyleButton);
        barChart.setOnClickListener(this);

        // �܂���O���t�\��
        final ImageButton lineChart = (ImageButton) parent.findViewById(R.id.changeLinegraphStyleButton);
        lineChart.setOnClickListener(this);
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     * 
     */
    public void finishListener()
    {
    }
    
    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
    }
    
    /**
     *  �W�v�p�f�[�^��ǂݍ���
     * 
     */
    private void prepareGokigenData()
    {
        //  �v���O���X�_�C�A���O�i�uLoading...�v�j��\������B
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(parent.getString(R.string.dataLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    	
        /**
         *  �v���O���X�_�C�A���O�\�����Ɏ��{���鏈���̋L�q
         *  (secCancelable(false)�ɂ��Ă��邽�߁A������run()���I���Ȃ��ƁA
         *   ANR�ȊO�̏I�����@�͂Ȃ��Ȃ�̂Œ��ӁB)
         */
        Thread thread = new Thread(new Runnable()
        {  
            public void run()
            {
            	try
            	{
                    // �f�[�^���擾���ď�������
            		gokigenDataHolder.parseGokigenItems(reportType, showYear, showMonth, showDay);
            		handler.sendEmptyMessage(0);
            	}
            	catch (Exception ex)
            	{
            		Log.v(Main.APP_IDENTIFIER, "run() :" + ex.getMessage() + " " + showYear + "/" + showMonth + "/" + showDay);
            	}
            }

            /**
             *   ��ʂ̍X�V(�v���O���X�_�C�A���O�\���I���̏���)
             */
            private final Handler handler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                	// �����ŁA��ʂւ̕`��w�����o��
                	redrawScreen();
                    progressDialog.dismiss();
                }
            };   
        });
        try
        {
            thread.start();
        }
        catch (Exception ex)
        {
            // ��O�����͉������Ȃ�
        }
    }

    /**
     *  ����ʂ���߂��Ă����Ƃ�...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // �������x���Ȃ邩������Ȃ����A�A�A�K�x�R�������{����B
        System.gc();
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();

        int dataMoveCount = 0;
        if (id == R.id.changeGraphStyleButton)
        {
            // �~�O���t�ɂ���
            currentGraphDrawer = pieChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeLinegraphStyleButton)
        {
            // �܂���O���t�ɂ���
        	currentGraphDrawer = lineChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeBargraphStyleButton)
        {
            // �_�O���t�ɂ���
            currentGraphDrawer = barChartDrawer;
            currentGraphDrawer.prepare();
        }
        else if (id == R.id.changeNext)
        {
            // 1���̃f�[�^�ɕ\����ύX����
            dataMoveCount = 1;
        }
        else if (id == R.id.changePrevious)
        {
            // 1�O�̃f�[�^�ɕ\����ύX����
            dataMoveCount = -1;
        }
        
        // �f�[�^��(��)�`�悷��
        changeViewData(dataMoveCount);
    }
    
    /**
     *  �O���t�`�悷��N�������ꎞ�L������
     * 
     * @param year   �L������N
     * @param month  �L�����錎
     * @param day    �L�������
     */
    private void storeGraphToShow(int year, int month, int day)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("graphYear",  year);
        editor.putInt("graphMonth", month);
        editor.putInt("graphDay",   day);
        editor.commit();        
    }

    /**
     *  ���x���ɕ\������i�N�E���E���́j������𐶐�����
     * @return ���x��������
     */
    private String getLabelString()
    {
    	String labelString = "";
        switch (reportType)
        {
          case REPORTTYPE_MONTHLY:
            // �����f�[�^     
            labelString = " " + showYear + "/" + showMonth + " ";
      	    break;

          case REPORTTYPE_WEEKLY:
            // �T���ƃf�[�^
        	
      	    break;

          case REPORTTYPE_DAILY:
          default:
            // �����ƃf�[�^
            labelString = " " + showYear + "/" + showMonth + "/" + showDay + " ";
        	break;
        }
        return (labelString);
    }
    
    /**
     *  ��ʂ̍ĕ`������s
     * 
     */
    private void redrawScreen()
    {
        // ���x���̍ĕ`��
    	final TextView infoArea = (TextView) parent.findViewById(R.id.GokigenInfo);
        infoArea.setText(getLabelString());

        // �O���t�̍ĕ`����w������
        final GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);
        view.doDraw();         
    }

    /**
     *   �\�����Ă���O���t���X�V����
     *   
     * @param addValue
     */
    private void changeViewData(int value)
    {
        if (value == 0)
        {
            // �l���w�肳��Ă��Ȃ��ꍇ�ɂ́A��ʂ̍ĕ`��̂ݎ��s����
        	redrawScreen();
        	return;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.set(showYear, (showMonth - 1), showDay);
        switch (reportType)
        {
          case REPORTTYPE_MONTHLY:
            // �����f�[�^     
            calendar.add(Calendar.MONTH, value);
      	    break;

          case REPORTTYPE_WEEKLY:
            // �T���ƃf�[�^
            calendar.add(Calendar.DATE, (value * 7));
      	    break;

          case REPORTTYPE_DAILY:
          default:
            // �����ƃf�[�^
            calendar.add(Calendar.DATE, value);
        	break;
        }

        // ���t�f�[�^���擾���Ȃ���
        showYear = calendar.get(Calendar.YEAR);
        showMonth = calendar.get(Calendar.MONTH) + 1;
        showDay = calendar.get(Calendar.DATE);

        // �o�͂������ݒ肷��
        storeGraphToShow(showYear, showMonth, showDay);
        
        // ��������f�[�^��ǂݒ���...
        prepareGokigenData();
    }

    /**
     *    ���(GokigenSurfaceView)��G��ꂽ�Ƃ��̏���
     *    (�t���b�N�A�N�V�����̌��o���s���B)
     * 
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean ret = false;
        int action = event.getAction() & 0x000000ff;
        float currentPositionX = event.getX();
        float currentPositionY = event.getY();
        switch (action)
        {
          case MotionEvent.ACTION_DOWN:
            // �^�b�`���͂��߂�
            onTouchPosX = currentPositionX;
            onTouchPosY = currentPositionY;
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_UP:
            // �^�b�`�������ꂽ...�t���b�N����̊m�F�Ǝ��s
        	doFlickAction(flicking, onTouchPosX, currentPositionX, onTouchPosY, currentPositionY);
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_CANCEL:
            // �^�b�`���L�����Z�����ꂽ...�t���b�N����̊m�F�Ǝ��s
        	doFlickAction(flicking, onTouchPosX, currentPositionX, onTouchPosY, currentPositionY);
            flicking = false;
            ret = true;
            break;

          case MotionEvent.ACTION_MOVE:
            // �^�b�`���Ȃ���ړ�      
            flicking = true;
            ret = true;
            break;

          default:
            // �������Ȃ�
            break;
        }
        return (ret);
    }
    
    /**
     *   �t���b�N�A�N�V�����̎�ʂ𔻒肷��
     * @param startX  X���ړ��J�n�ʒu
     * @param endX    X���ړ��I���ʒu
     * @param startY  Y���ړ��J�n�ʒu
     * @param endY    Y���ړ��I���ʒu
     * @return        �t���b�N�A�N�V�����̌���
     */
    private int decideFlickActionType(float startX, float endX, float startY, float endY)
    {
    	int flickType = FLICK_NOTHING;
    	
    	float deltaX = endX - startX;
    	float deltaY = endY - startY;
    	
        if (Math.abs(deltaX) > Math.abs(deltaY))
        {
    	    // X�����̈ړ��������傫���ꍇ (���E)
        	flickType = (deltaX <= 0) ? FLICK_LEFT: FLICK_RIGHT;
        }
        else
        {
        	// Y�����̈ړ��������傫���ꍇ(�㉺)
        	flickType = (deltaY <= 0) ? FLICK_UP : FLICK_DOWN;
        }
        return (flickType);
    }

    /**
     *  �^�b�`�A�N�V�����i�t���b�N�A�N�V�����j�ɂ��킹�����������s����
     * 
     * @param isFlick
     * @param prevPosX
     * @param nextPosX
     */
    private void doFlickAction(boolean isFlick, float prevPosX, float nextPosX, float prevPosY, float nextPosY)
    {
        if (isFlick == false)
        {
            // �^�b�`�����܂܈ړ����Ă��Ȃ��ꍇ�ɂ́A�Ȃɂ����Ȃ�
            return;
        }

        // �t���b�N�A�N�V�����̎�ʂ𔻒�
        int actionType = decideFlickActionType(prevPosX, nextPosX, prevPosY, nextPosY);
        boolean needMove = false;
        int dataMoveCount = 0;
        switch (actionType)
        {
          case FLICK_UP:  // �������
            currentGraphDrawer.actionZoomIn();
          	break;

          case FLICK_DOWN: // ��������
          	currentGraphDrawer.actionZoomOut();
          	break;

          case FLICK_LEFT: // ��������
        	needMove = currentGraphDrawer.actionShowNextData();
        	if (needMove == true)
        	{
        		// �f�[�^���ЂƂ��̂��̂ֈړ�������
        		dataMoveCount = 1;
        	}
        	break;

          case FLICK_RIGHT: // �E������
        	needMove = currentGraphDrawer.actionShowPreviousData();
        	if (needMove == true)
        	{
        		// �f�[�^���ЂƂO�̂��̂ֈړ�������
        		dataMoveCount = -1;
        	}
          	break;

          case FLICK_NOTHING:
          default:
            // ���Ή��̑���...
        	return;
        }
        
        // �\���f�[�^���X�V����
		changeViewData(dataMoveCount);
        return;
    }

    /**
     *   �G��ꂽ�Ƃ��̏���
     * 
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        // int id = v.getId();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            //
        }
        return (false);
    }

    /**
     *  �L�[���������Ƃ��̑���
     */
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        int action = event.getAction();
        if ((action == KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
        	//
        }        
        return (false);
    }

    /**
     *   ���j���[�ւ̃A�C�e���ǉ�
     * @param menu
     * @return
     */
    public Menu onCreateOptionsMenu(Menu menu)
    {
        // �����ƏW�v
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_DAILY, Menu.NONE, parent.getString(R.string.showDaily));
        menuItem.setIcon(android.R.drawable.ic_menu_day);

        // �T���ƏW�v
        //menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_WEEKLY, Menu.NONE, parent.getString(R.string.showWeekly));
        //menuItem.setIcon(android.R.drawable.ic_menu_week);

        // �����ƏW�v
        menuItem = menu.add(Menu.NONE, MENU_ID_SHOW_MONTHLY, Menu.NONE, parent.getString(R.string.showMonthly));
        menuItem.setIcon(android.R.drawable.ic_menu_month);

        return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(MENU_ID_SHOW_DAILY).setVisible(true);    // �����ƏW�v
        //menu.findItem(MENU_ID_SHOW_WEEKLY).setVisible(true);    // �T���ƏW�v
        menu.findItem(MENU_ID_SHOW_MONTHLY).setVisible(true);  // �����ƏW�v
        return;
    }

    /**
     *   ���j���[�̃A�C�e�����I�����ꂽ�Ƃ��̏���
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        GokigenSurfaceView view = (GokigenSurfaceView) parent.findViewById(R.id.GraphicView);

        boolean result = false;
        switch (item.getItemId())
        {
          case MENU_ID_SHOW_DAILY:
            // �����ƏW�v���[�h
            reportType = REPORTTYPE_DAILY;
            result = true;
            break;

          case MENU_ID_SHOW_WEEKLY:
            // �T���W�v���[�h
            reportType = REPORTTYPE_WEEKLY;
            result = true;
            break;

          case MENU_ID_SHOW_MONTHLY:
            // �����W�v���[�h
            reportType = REPORTTYPE_MONTHLY;
            result = true;
            break;

          default:
        	// ���̑�...
            result = false;
            break;
        }
        if (result == true)
        {
            // ���[�h���؂�ւ�����ꍇ�A�f�[�^��ǂݒ����āA��ʂ��ĕ`�悷��
            prepareGokigenData();
            view.doDraw();
        }
        return (result);
    }

    /**
     *  �L�����o�X�Ƀf�[�^��`�悷��
     * 
     */
    public void drawOnCanvas(Canvas canvas)
    {
    	try
    	{
    		// ��ʂ�h��Ԃ��Ă���ĕ`������s
    		canvas.drawColor(Color.BLACK);
            currentGraphDrawer.drawOnCanvas(canvas, reportType, gokigenDataHolder);
    	}
    	catch (Exception ex)
    	{
    		// ��O����...�ł����̂Ƃ��ɂ͉������Ȃ�
    	}
    	return;
    }
}
