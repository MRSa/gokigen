package jp.sfjp.gokigen.okaken;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class ResultActivity extends Activity implements ClockTimer.ITimeoutReceiver, IActivityOpener, OnClickListener
{
	private ClockTimer myTimer = null;
	private Timer timer = null;
    private static final long duration = 500;   // 500ms
    private ResultDrawer drawer = null;

    private IResultProvider resultProvider = null;
	private List<SymbolListArrayItem> listItems = null;
	
	private DetailDialog detailDialog = null;
	private SymbolListArrayItem itemToShowDetail = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /** �S��ʕ\���ɂ��� **/
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** �^�C�g�������� **/       
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Preference���擾����
        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	// String colorString = preferences.getString("backgroundColor", "0xff004000");

        // ��ʕ\���̃��C�A�E�g��ݒ肷��
        setContentView(R.layout.result);

        // Intent���猋�ʃf�[�^��ǂݏo��
        QuestionnaireProvider questionProvider = null;
        try
        {
        	questionProvider = getIntent().getParcelableExtra(Gokigen.APP_INFORMATION_STORAGE);
        	resultProvider = questionProvider;
        }
        catch (Exception ex)
        {
            Log.v(Gokigen.APP_IDENTIFIER, "EXCEPTION(questionProvider) :" + ex.toString() + "  " + ex.getMessage());
        }

        // ���ʃf�[�^�̔��f
        deployDataToList();

        // �ڍ׃f�[�^�\���pDialog�̍쐬
        detailDialog = new DetailDialog(this);
        
        // ��ʕ`��N���X�̐ݒ�
        drawer = new ResultDrawer(this, questionProvider);
        final GokigenSurfaceView surfaceView = (GokigenSurfaceView) findViewById(R.id.ResultView);
        surfaceView.setCanvasDrawer(drawer);
    
    }

    /**
     *  ���j���[�̐���
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_ABOUT, Menu.NONE, getString(R.string.about_gokigen));
    	menuItem.setIcon(android.R.drawable.ic_menu_info_details);
        menuItem = menu.add(Menu.NONE, Gokigen.MENU_ID_SHARE, Menu.NONE, getString(R.string.share_content));
        menuItem.setIcon(android.R.drawable.ic_menu_share);    // ���L...
        return (super.onCreateOptionsMenu(menu));
    }
    
    /**
     *  ���j���[�A�C�e���̑I��
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        boolean result = false;
        switch (item.getItemId())
        {
          case Gokigen.MENU_ID_ABOUT:
        	// About���j���[���I�����ꂽ�Ƃ��́A�N���W�b�g�_�C�A���O��\������
        	showDialog(R.id.info_about_gokigen);
            result = true;
            break;

          case Gokigen.MENU_ID_SHARE:
        	//  ���L���j���[���Ăяo���ꂽ
        	shareContent();
        	result = true;
        	break;
          default:
            result = false;
            break;
        }
        return (result);
    }
    
    /**
     *  ���j���[�\���O�̏���
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(Gokigen.MENU_ID_ABOUT).setVisible(true);
        return (super.onPrepareOptionsMenu(menu));
    }

    /**
     *  ��ʂ����ɉ�����Ƃ��̏���
     */
    @Override
    public void onPause()
    {
        super.onPause();
        stopTimer();

    }
    
    /**
     *  ��ʂ��\�ɏo�Ă����Ƃ��̏���
     */
    @Override
    public void onResume()
    {
        super.onResume();
        startTimer();
    }
    
    /**
     *  �q��ʂ��牞������������Ƃ��̏���
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // �q��ʂ������������̉�������
        }
        catch (Exception ex)
        {
            // ��O�����������Ƃ��ɂ́A�������Ȃ��B
        }
    } 

    /**
     *  �_�C�A���O�\���i����j�̏���
     * 
     */
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == R.id.info_about_gokigen)
	    {
        	// �N���W�b�g�_�C�A���O��\��
		    CreditDialog dialog = new CreditDialog(this);
		    return (dialog.getDialog());
	    }
        else if (id == R.id.detail_dialog)
	    {
        	// �ڍ׃_�C�A���O��\��
		    return (detailDialog.getDialog());
	    }
    	return (null);
    }

    /**
     *  �_�C�A���O�\���̏���
     * 
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog)
    {
        if (id == R.id.info_about_gokigen)
	    {
            // �N���W�b�g�_�C�A���O��\������Ƃ��ɂ͉������Ȃ��B
        	return;
	    }
        else if (id == R.id.detail_dialog)
	    {
        	// �ڍ׃_�C�A���O�̕\�����X�V����
        	detailDialog.onPrepareDialog(dialog, itemToShowDetail);
		    return;
	    }
    	// �_�C�A���O�����X�V����ꍇ�ɂ́A�����ɒǉ�����
    	return;
    }  
    /**
     *   �^�C���A�E�g��M���̏���...
     * 
     */
    public void receivedTimeout()
    {
        // Log.v(Gokigen.APP_IDENTIFIER, "receivedTimeout()");    

    	// ��ʂ̍ĕ`��w���B�B�B�i0.5sec�����H�j
    	final GokigenSurfaceView surfaceView0 = (GokigenSurfaceView) findViewById(R.id.ResultView);
    	surfaceView0.doDraw();
    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        //int id = v.getId();

    	// ��ʂ̍ĕ`��w���B�B�B�i0.5sec�����H�j
    	final GokigenSurfaceView surfaceView1 = (GokigenSurfaceView) findViewById(R.id.ResultView);
    	surfaceView1.doDraw();

    }
    
    /**
     *  Activity��؂�ւ���
     * 
     * @param fileName
     */
    public void requestToStartActivity(int id)
    {
        try
        {
            // Activity���N������
            Intent intent = new Intent(this, jp.sfjp.gokigen.okaken.MoleGameActivity.class);
            startActivityForResult(intent, id);
        }
        catch (Exception ex)
        {
        	// ��O������...
        }
    }
    
    /**
     * 
     * 
     */
    private void stopTimer()
    {
        try
        {
            // TODO: ������~�߂�悤�C�x���g�����N���X�Ɏw������
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
           	myTimer = null;
        }
        catch (Exception ex)
        {
            // �������Ȃ�
        }    	
    }
    
    /**
     * 
     * 
     */
    private void startTimer()
    {
        try
        {
        	if (timer != null)
        	{
        		timer.cancel();
        		timer = null;
        	}
        	timer = new Timer();
        	
        	// �^�C�}�[�^�X�N�̏���
           	myTimer = null;
        	myTimer = new ClockTimer(this);
        	timer.scheduleAtFixedRate(myTimer, duration, duration);
        }
        catch (Exception ex)
        {
            // �Ȃɂ����Ȃ�
        }    	
    }

    /**
     *    �I�������A�C�e����\������
     * 
     * @param item
     */
    private void showDetailData(SymbolListArrayItem item)
    {
    	try
    	{
    		itemToShowDetail = item;
    		showDialog(R.id.detail_dialog);
    	}
    	catch (Exception ex)
    	{
        	// �f�[�^�����Ȃ�...�Ȃ̂ł����Ő܂�Ԃ�
	    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::showDetailData() : exception " + ex.toString() );    		
    		itemToShowDetail = null;
    	}
    }

    /**
     *    �f�[�^���ꗗ�ɕ\������
     * 
     */
	private void deployDataToList()
	{
		try
		{
	        // ���X�g�ɕ\������A�C�e���𐶐�����
	        listItems = null;
	        listItems = new ArrayList<SymbolListArrayItem>();
	        
	        if (resultProvider == null)
	        {
	        	// �f�[�^�����Ȃ�...�Ȃ̂ł����Ő܂�Ԃ�
		    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::deployDataToList() : resultProvider is null." );
	        	return;
	        }

	        // �񓚂����f�[�^��S�Ď擾���ăR���e�i�ɓ����
	        int count = resultProvider.getNumberOfAnsweredQuestions();
	        for (int index = 0; index < count; index++)
	        {
	        	 SymbolListArrayItem listItem = resultProvider.getAnsweredInformation(this, index);
	        	 listItems.add(listItem);
	        }

            // ���X�g�A�_�v�^�[�𐶐����A�ݒ肷��
            ListView listView = (ListView) findViewById(R.id.ResultListView);
            ListAdapter adapter = new SymbolListArrayAdapter(this,  R.layout.listarrayitems, listItems);
            listView.setAdapter(adapter);

            // ���X�g���̃A�C�e����I�������Ƃ��̏�����o�^����
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //@Override
                public void onItemClick(AdapterView<?> parentView, View view, int position, long id)
                {
                    ListView listView = (ListView) parentView;
                    SymbolListArrayItem item = (SymbolListArrayItem) listView.getItemAtPosition(position);

                    /// ���X�g���I�����ꂽ�Ƃ��̏���...�f�[�^���J��
                    showDetailData(item);
                }
            });
            System.gc();   // ����Ȃ��i�Q�Ƃ��؂ꂽ�j�N���X����������            

		} catch (Exception ex)
	    {
	        // ��O����...���O��f��
	    	Log.v(Gokigen.APP_IDENTIFIER, "ResultActivity::deployDataToList() : " + ex.toString());
	    }
	}
	
	/**
	 *    ���ʂ����L����...
	 * 
	 */
	private void shareContent()
	{
        int score = 0;
		String message = "";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        try
        {
        	if (resultProvider != null)
        	{
        		score = (int) ((float) resultProvider.getScore(0) * 100.0f);
            	if (drawer != null)
            	{
            		message = getString(R.string.app_name) + getString(R.string.share_message) + "\n- - - - -\n";
            		message = message + drawer.analysisResult(resultProvider) + "\n- - - - -\n";
            	}
            	message = message + getString(R.string.app_credit) + "\n\n";
        	}
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " (" + getString(R.string.game_score) +  score + getString(R.string.game_pts) + ")");
            intent.putExtra(Intent.EXTRA_TEXT, message + "  (" +   score + getString(R.string.game_pts) + ")");
/*
            String pictureString = null;
            if (pictureString != null)
            {
            	try
            	{
                	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); 
                	intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, ImageAdjuster.parseUri(pictureString));
                    Log.v(Gokigen.APP_IDENTIFIER, "Attached Pic.:" + pictureString);
            	}
            	catch (Exception ee)
            	{
            		// 
                    Log.v(Gokigen.APP_IDENTIFIER, "attach failure : " + pictureString + "  " + ee.getMessage());
            	}
            }
*/
            startActivityForResult(intent, Gokigen.MENU_ID_SHARE);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.v(Gokigen.APP_IDENTIFIER, "xxx : " + e.getMessage() + ", " + message);
        }
	}
}
