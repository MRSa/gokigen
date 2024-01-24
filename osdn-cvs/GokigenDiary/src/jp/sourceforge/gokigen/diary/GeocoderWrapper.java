package jp.sourceforge.gokigen.diary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *  �W�I�R�[�f�B���O�p �A�N�Z�X���b�p (�񓯊����������s)
 *  
 *  AsyncTask
 *    MyLocation : ���s���ɓn���N���X(Param)
 *    Integer    : �r���o�߂�`����N���X(Progress)
 *    String     : �������ʂ�`����N���X(Result)
 *    
 * @author MRSa
 *
 */
public class GeocoderWrapper extends AsyncTask<MyLocation, Integer, MyLocation>
{
    private Context  parent   = null;
	private Geocoder geocoder = null;
    private IGeocoderResultReceiver receiver = null;

    /**
     *   ���ԏ��𕶎���ɉ��H����
     * @param targetTime
     * @return
     */
    public static String getDateTimeString(long targetTime)
    {
        String data = "";
        try
        {
            Date currentDate = new Date(targetTime);
            DateFormat dateF = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss", Locale.ENGLISH);
            data = dateF.format(currentDate);
        }
        catch (Exception ex)
        {
            data = "" + targetTime;
        }
        return (data);
    }

    /**
     *  �ܓx�E�o�x���𕶎���ŉ�������
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getLocationString(double latitude, double longitude)
    {
        return ("[" + (Math.round(latitude * 1E6) / 1E6)+ ", " + (Math.round(longitude * 1E6) / 1E6) + "]");
    }

    /**
     *  �R���X�g���N�^
     * @param context
     * @param locale
     */
    public GeocoderWrapper(Context context, IGeocoderResultReceiver listener, Locale locale)
    {
    	parent = context;
    	receiver = listener;
        geocoder = new Geocoder(context, locale);
    }

    /**
     *  �ʒu��񂩂�Z���̕�������擾����
     *  (�������A�I�t���C�����[�h�̏ꍇ�ɂ́A�ܓx�E�o�x�𐔒l�ŉ�������)
     * @param data �ʒu���
     * @return  �ʒu���(�Z���̕�����)
     */
    private MyLocation updateLocationInfo(MyLocation data)
    {
        String result = "";

        double latitude = data.getLatitude();
        double longitude = data.getLongitude();
        
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean offline = preferences.getBoolean("offlineMode", false);
        if (offline == true)
        {
        	// �Ȃɂ����Ȃ�
        	return (data);
        }
        try
        {
            List<Address> list_address = geocoder.getFromLocation(latitude, longitude, 1);
            if (list_address.isEmpty() == true)
            {
                // �G���R�[�h���s...
                return (data);
            }

            result = "";
            for (Iterator<Address> lp = list_address.iterator(); lp.hasNext();) 
            {
                Address address = lp.next();
                // int maxCount = address.getMaxAddressLineIndex();  // �Ȃ�������Ń��[�v������ƂƂ�Ȃ�...
                String infoX = "";
                String info  = "";
                for (int loop = 0; ((info = address.getAddressLine(loop)) != null); loop++)
                {
                    infoX = infoX + info + " ";
                }
                result = result + infoX;
                String postalCode = address.getPostalCode();
                if (postalCode != null)
                {
                	// �X�֔ԍ�����ꂽ�甲���Ă��܂�
                    result = result + "(" + address.getPostalCode() + ")";  
                    break;
                }
            }            
            data.setLocationInfo(result);
        }
        catch (Exception ex)
        {
            Log.v(Main.APP_IDENTIFIER, "x (" + ex.toString() + ") " + ex.getMessage() + ", " + result);
        }
        return (data);
    }
    

    /**
     *  �񓯊��������{�O�̑O����
     * 
     */
    @Override
    protected void onPreExecute()
    {
         // ����͉������Ȃ�
    }

    /**
     *  �񓯊�����
     *  �i�o�b�N�O���E���h�Ŏ��s����(���̃��\�b�h�́AUI�X���b�h�ƕʂ̂Ƃ���Ŏ��s)�j
     * 
     */
    @Override
    protected MyLocation doInBackground(MyLocation... datas)
    {
    	 return (updateLocationInfo(datas[0]));
    }

    /**
     *  �񓯊������̐i���󋵂̍X�V
     * 
     */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
        // ����͉������Ȃ�
	}

    /**
     *  �񓯊������̌㏈��
     *  (���ʂ���������)
     */
    @Override
    protected void onPostExecute(MyLocation result)
    {
    	try
    	{
            if (receiver != null)
            {
            	receiver.receivedResult(result);
            }
    	}
    	catch (Exception ex)
    	{
    		Log.v(Main.APP_IDENTIFIER, "GeocoderWrapper::onPostExecute() : " + ex.toString());
    	}
        return;
    }     
}
