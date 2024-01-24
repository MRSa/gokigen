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
 *  ジオコーディング用 アクセスラッパ (非同期処理を実行)
 *  
 *  AsyncTask
 *    MyLocation : 実行時に渡すクラス(Param)
 *    Integer    : 途中経過を伝えるクラス(Progress)
 *    String     : 処理結果を伝えるクラス(Result)
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
     *   時間情報を文字列に加工する
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
     *  緯度・経度情報を文字列で応答する
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getLocationString(double latitude, double longitude)
    {
        return ("[" + (Math.round(latitude * 1E6) / 1E6)+ ", " + (Math.round(longitude * 1E6) / 1E6) + "]");
    }

    /**
     *  コンストラクタ
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
     *  位置情報から住所の文字列を取得する
     *  (ただし、オフラインモードの場合には、緯度・経度を数値で応答する)
     * @param data 位置情報
     * @return  位置情報(住所の文字列)
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
        	// なにもしない
        	return (data);
        }
        try
        {
            List<Address> list_address = geocoder.getFromLocation(latitude, longitude, 1);
            if (list_address.isEmpty() == true)
            {
                // エンコード失敗...
                return (data);
            }

            result = "";
            for (Iterator<Address> lp = list_address.iterator(); lp.hasNext();) 
            {
                Address address = lp.next();
                // int maxCount = address.getMaxAddressLineIndex();  // なぜかこれでループさせるととれない...
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
                	// 郵便番号が取れたら抜けてしまう
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
     *  非同期処理実施前の前処理
     * 
     */
    @Override
    protected void onPreExecute()
    {
         // 今回は何もしない
    }

    /**
     *  非同期処理
     *  （バックグラウンドで実行する(このメソッドは、UIスレッドと別のところで実行)）
     * 
     */
    @Override
    protected MyLocation doInBackground(MyLocation... datas)
    {
    	 return (updateLocationInfo(datas[0]));
    }

    /**
     *  非同期処理の進捗状況の更新
     * 
     */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
        // 今回は何もしない
	}

    /**
     *  非同期処理の後処理
     *  (結果を応答する)
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
