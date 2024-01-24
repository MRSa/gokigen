package jp.sourceforge.gokigen.diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class LocationMapListener implements OnClickListener, OnTouchListener, IGeocoderResultReceiver
{
    private final int DEFAULT_ZOOM_LEVEL = 18;
	private Activity parent = null;        // �e��
    private String locationFile = "";       // �O�Ղ��L�^���ꂽ�t�@�C��
    private boolean isCurrentLocaiton = true;  // ���݈ʒu��\������\
    
    private TextEditDialog  textDialog = null;


    private static final int MENU_ID_LOCATIONDETAIL = (Menu.FIRST + 1);
/*
    private static final int MENU_ID_MYLOCATION = (Menu.FIRST + 1);
    private static final int MENU_ID_ZOOMIN = (Menu.FIRST + 2);
    private static final int MENU_ID_ZOOMOUT = (Menu.FIRST + 3);
*/
    private static final int MENU_ID_SHARE = (Menu.FIRST + 4);
    
    private LocationMapIndicator indicator = null;  // �}�b�v��Ƀ}�[�N������Ƃ���
    private GeocoderWrapper geocoder = null;        // �ʒu�����擾������
    private MyLocation locationHolder = null;       // �ʒu���̋L���N���X

    /**
     *  �R���X�g���N�^
     * @param argument
     */
    public LocationMapListener(Activity argument)
    {
        parent = argument;
    }

    /**
     *  �����肱�̃N���X�ɃC�x���g���X�i��ڑ�����
     * 
     */
    public void prepareListener()
    {
        // ���݈ʒu�m�F�{�^���Ƃ̃����N
        final ImageButton locationButton = (ImageButton) parent.findViewById(R.id.MyLocationButton);
        locationButton.setOnClickListener(this);

        // �Y�[���C���{�^���Ƃ̃����N
        final ImageButton zoomInButton = (ImageButton) parent.findViewById(R.id.ZoomInButton);
        zoomInButton.setOnClickListener(this);

        // �Y�[���A�E�g�{�^���Ƃ̃����N
        final ImageButton zoomOutButton = (ImageButton) parent.findViewById(R.id.ZoomOutButton);
        zoomOutButton.setOnClickListener(this);

        // �}�[�J�[�摜���w��...�\�����[�h�ɍ��킹�ĕύX����
        Drawable marker = null;
        isCurrentLocaiton = parent.getIntent().getBooleanExtra(LocationMap.LOCATION_ISCURRENT, true);
        if (isCurrentLocaiton == true)
        {
            // ���݈ʒu��\������ꍇ
        	marker = parent.getResources().getDrawable(R.drawable.emo_im_cool);
        }
        else
        {
        	// �w��ʒu��\������ꍇ
        	int iconId = parent.getIntent().getIntExtra(LocationMap.LOCATION_ICONID, R.drawable.emo_im_wtf);
        	marker = parent.getResources().getDrawable(iconId);
        }

        // �ʒu�����L�^���Ă���t�@�C��
        locationFile = parent.getIntent().getStringExtra(LocationMap.LOCATION_FILE);        
        Log.v(Main.APP_IDENTIFIER, "location file : " + locationFile);
        
        // �}�b�v�ɏ悹��ʒu�摜�̈��o�^
        final MapView mapview = (MapView)  parent.findViewById(R.id.MapAreaView);        
        indicator = new LocationMapIndicator(marker);
        mapview.getOverlays().add(indicator);
        com.google.android.maps.MapController controller = mapview.getController();
        controller.setZoom(DEFAULT_ZOOM_LEVEL);

        locationHolder = new MyLocation();
    }

    /**
     *  �X�^�[�g����
     */
    public void prepareToStart()
    {
        // ���݈ʒu�ֈړ������Ă݂�B
        moveToCurrentLocation();
    }

    /**
     *  �I������
     */
    public void shutdown()
    {
        
    }
    
    /**
     *  �폜����...
     */
    public void finishListener()
    {
        try
        {
            indicator.clearPoint();

            final MapView mapview = (MapView)  parent.findViewById(R.id.MapAreaView);        
            mapview.getOverlays().remove(indicator);
        }
        catch (Exception ex)
        {
            //
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

    }

    /**
     *   �N���b�N���ꂽ�Ƃ��̏���
     */
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
          case R.id.ZoomInButton:
            //  �n�}���g�傷��
            zoomLocation(false);
              break;

          case R.id.ZoomOutButton:
            //  �n�}���k������
            zoomLocation(true);
              break;

          case R.id.MyLocationButton:
            //  ���݈ʒu�ֈړ�����
            moveToCurrentLocation();
            break;

          default:
            break;
        }
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
        // ���L���j���[��ǉ�����
    	MenuItem menuItem = menu.add(Menu.NONE, MENU_ID_SHARE, Menu.NONE, parent.getString(R.string.shareContent));
        menuItem.setIcon(android.R.drawable.ic_menu_share);
        
        menuItem = menu.add(Menu.NONE, MENU_ID_LOCATIONDETAIL, Menu.NONE, parent.getString(R.string.locationDetail));
        menuItem.setIcon(android.R.drawable.ic_menu_info_details);

        return (menu);
    }
    
    /**
     *   ���j���[�\���O�̏���
     * @param menu
     * @return
     */
    public void onPrepareOptionsMenu(Menu menu)
    {
       	menu.findItem(MENU_ID_SHARE).setVisible(true);
       	menu.findItem(MENU_ID_LOCATIONDETAIL).setVisible(true);
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
          case MENU_ID_SHARE:
            // "���L" ��I�������Ƃ�... Intent�𔭍s����
            shareContent();        	
        	result = true;
        	break;

          case MENU_ID_LOCATIONDETAIL:
            // "���ӏ��" ��I�������Ƃ�... ����\������
        	parent.showDialog(MENU_ID_LOCATIONDETAIL);
          	result = true;
            break;

          default:
            result = false;
            break;
        }
        return (result);
    }
 
    /**
     * 
     * 
     * @param id
     * @return
     */
    public Dialog onCreateDialog(int id)
    {
    	if (id == MENU_ID_LOCATIONDETAIL)
    	{
    		// �ʒu�̏���\��
    		textDialog = null;
            textDialog = new TextEditDialog((Context) parent);
            textDialog.prepare(null, 0, null, createLocationInfoString());
            return (textDialog.getDialog());
    	}
    	return (null);
    }

    /**
     *  �}�b�v�̊g��k���w��
     * @param isZoomOut
     */
    private void moveToCurrentLocation()
    {
        String message = "";
        int currentLatitude = 0;
        int currentLongitude = 0;
        int toastMessageId = 0;
        try
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        	if (isCurrentLocaiton == true)
        	{
                // ���݈ʒu��\�����郂�[�h�F Preference �p�����[�^����ʒu�����擾����I
                currentLatitude = preferences.getInt("Latitude",   35000000);
                currentLongitude = preferences.getInt("Longitude", 135000000);
                message = preferences.getString("LocationName", "?????");
                toastMessageId = R.string.moveToCurrentLocation;
        	}
        	else
        	{
        		// �w��ʒu��\�����郂�[�h�F Intent����ʒu�����擾����
            	currentLatitude = parent.getIntent().getIntExtra(LocationMap.LOCATION_LATITUDE, 35000000);
                currentLongitude = parent.getIntent().getIntExtra(LocationMap.LOCATION_LONGITUDE, 135000000);
                message = parent.getIntent().getStringExtra(LocationMap.LOCATION_MESSAGE);
                toastMessageId = R.string.moveToSpecifiedLocation;
        	}

            if (message.startsWith("[") == true)
            {
                // �ʒu�����擾����
            	double latitude = currentLatitude / 1E6;
                double longitude = currentLongitude / 1E6;
                locationHolder.setLocation(-1, latitude, longitude);

                String myLocale = preferences.getString("myLocale", "en");
                geocoder = null;
                geocoder = new GeocoderWrapper(parent, this, new Locale(myLocale));
                geocoder.execute(locationHolder);
            }

            TextView txtArea = (TextView) parent.findViewById(R.id.InfoArea);
            txtArea.setText(message);

            MapView mapView = (MapView) parent.findViewById(R.id.MapAreaView);

            // ���݈ʒu�Ƀ}�[�N��\������
            GeoPoint current = new GeoPoint(currentLatitude, currentLongitude);
            indicator.clearPoint();
            indicator.addPoint(current);                        
            mapView.invalidate();
            
            com.google.android.maps.GeoPoint point = new com.google.android.maps.GeoPoint(currentLatitude, currentLongitude);
            com.google.android.maps.MapController controller = mapView.getController();
            controller.animateTo(point);
            
            message = parent.getString(toastMessageId);
            Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();                
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "animateTo " + ex.getMessage() + currentLatitude + "," + currentLongitude);                
        }
    }

    /**
     *  �}�b�v�̊g��k���w��
     * @param isZoomOut
     */
    private void zoomLocation(boolean isZoomOut)
    {
        boolean ret = true;
        String message = "";
        try
        {
            MapView mapView = (MapView) parent.findViewById(R.id.MapAreaView);
            com.google.android.maps.MapController controller = mapView.getController();
            if (isZoomOut == true)
            {
                ret = controller.zoomOut();
                message = parent.getString(R.string.cannotZoomOut);
            }
            else
            {
                ret = controller.zoomIn();
                message = parent.getString(R.string.cannotZoomIn);
            }
            if (ret == false)
            {
                Toast.makeText(parent, message, Toast.LENGTH_SHORT).show();                
            }
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "zoom " + ex.getMessage() + " " + message);
        }
    }
    
    /**
     *   �u���L�v�{�^����\������
     * 
     */
    private void shareContent()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        double  currentLatitude = 0.0;
        double  currentLongitude = 0.0;

        try
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        	String message = "";
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
            
            isCurrentLocaiton = parent.getIntent().getBooleanExtra(LocationMap.LOCATION_ISCURRENT, true);

            int rating = 50;
        	TextView txtArea = (TextView) parent.findViewById(R.id.InfoArea);
            message = txtArea.getText().toString();

            
        	if (isCurrentLocaiton == true)
        	{
                // ���݈ʒu��\�����郂�[�h�F Preference �p�����[�^����ʒu�����擾����I
                currentLatitude  = (preferences.getInt("Latitude",   35000000) / 1E6);
                currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);
            	rating = 50;
        	}
        	else
        	{
        		// �w��ʒu��\�����郂�[�h�F Intent�Ƃ�������}�[�N����ʒu�����擾����
            	currentLatitude  = (parent.getIntent().getIntExtra(LocationMap.LOCATION_LATITUDE, 35000000) / 1E6);
                currentLongitude = (parent.getIntent().getIntExtra(LocationMap.LOCATION_LONGITUDE, 135000000) / 1E6);
            	rating = parent.getIntent().getIntExtra(LocationMap.LOCATION_ICONID, R.drawable.emo_im_wtf);
        	}
            
            int useAA = Integer.parseInt(preferences.getString("useAAtype", "0"));
            int useLocation = Integer.parseInt(preferences.getString("useLocationtype", "0"));
        	if (useAA == 1)
        	{
        	    message = message + " " + DecideEmotionIcon.decideEmotionString(rating);
        	}
        	else if (useAA == 2)
        	{
        	    message =  message + " " + DecideEmotionIcon.decideEmotionJapaneseStyleString(rating);        	    
        	}
        	if (useLocation == 1)
        	{
        		// �ʒu��������
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
        		    message = message + " " + "Location:" + currentLatitude + "," + currentLongitude;
        		}
        	}
        	else if (useLocation == 2)
        	{
        		// �ʒu��������
        		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
        		{
            		message = message + " " + "http://maps.google.co.jp/maps?q=" + currentLatitude + "," + currentLongitude;
        		}
        	}
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, parent.getString(R.string.app_name) + " | " + outFormat.format(calendar.getTime()));
            intent.putExtra(Intent.EXTRA_TEXT, message);

            parent.startActivityForResult(intent, 0);          	
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(parent, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.v(Main.APP_IDENTIFIER, "xxx : " + e.getMessage());
        }
    	
    }

    /**
     *  ���ӏ���\������
     * 
     */
    private String createLocationInfoString()
    {
        String detailData = "";
        double  currentLatitude = 0.0;
        double  currentLongitude = 0.0;
    	try
    	{
            // ��ʏ㕔�ɕ\�����Ă���ʒu���
    		TextView txtArea = (TextView) parent.findViewById(R.id.InfoArea);
            detailData = txtArea.getText().toString();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);            
            isCurrentLocaiton = parent.getIntent().getBooleanExtra(LocationMap.LOCATION_ISCURRENT, true);
        	if (isCurrentLocaiton == true)
        	{
                // ���݈ʒu��\�����郂�[�h�F Preference �p�����[�^����ʒu�����擾����I
                currentLatitude  = (preferences.getInt("Latitude",   35000000) / 1E6);
                currentLongitude = (preferences.getInt("Longitude", 135000000) / 1E6);
        	}
        	else
        	{
        		// �w��ʒu��\�����郂�[�h�F Intent�Ƃ�������}�[�N����ʒu�����擾����
            	currentLatitude  = (parent.getIntent().getIntExtra(LocationMap.LOCATION_LATITUDE, 35000000) / 1E6);
                currentLongitude = (parent.getIntent().getIntExtra(LocationMap.LOCATION_LONGITUDE, 135000000) / 1E6);
        	}

        	// �ʒu��������
    		if ((currentLatitude != 0.0)&&(currentLongitude != 0.0))
    		{
        		detailData = detailData + " " + "http://maps.google.co.jp/maps?q=" + currentLatitude + "," + currentLongitude;
    		}
    		
    	}
    	catch (Exception ex)
    	{
    		
    	}
    	return (detailData);
    }
    
    /**
     *  �ʒu������ʂɔ��f������
     * 
     */
    public void  receivedResult(MyLocation location)
    {
        try
        {
            // �ʒu������ʕ\��
        	String message = location.getLocationInfo();
            TextView txtArea = (TextView) parent.findViewById(R.id.InfoArea);
            txtArea.setText(message);        	
        }
        catch (Exception ex)
        {
        	
        }
    }
}
