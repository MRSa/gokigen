package jp.sfjp.gokigen.prpr0;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by MRSa on 2014/12/29.
 */
public class MyWatchFaceReceiver
{
    private static final String TAG = "MyWatchFaceReceiver";

    /**
     * The path for the {@link DataItem} containing {@link MyWatchFaceService} configuration.
     */
    public static final String PATH_WITH_FEATURE = "/gokigen/prpr/feature";
    public static final String PATH_WITH_IMAGE = "/gokigen/prpr/image";
    public static final String KEY_COMMAND_FILE_CLEAR = "CMD_FILE_CLEAR";
    public static final String KEY_COMMAND_CLEAR_RESULT = "CMD_CLEAR_RESULT";
    public static final int COMMAND_VALUE_ALL = -1;

    private static final long TIMEOUT_SEC = 30;

    private GoogleApiClient mGoogleApiClient = null;
    private ExternalStorageFileUtility fileUtility = null;

    public MyWatchFaceReceiver(GoogleApiClient client)
    {
        mGoogleApiClient = client;
        fileUtility = new ExternalStorageFileUtility("/prpr");
    }

    public void onDataChanged(DataEventBuffer dataEvents)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchFaceReceiver::onDataChanged() <S>");
        }
        try
        {
            for (DataEvent dataEvent : dataEvents)
            {
                if (dataEvent.getType() != DataEvent.TYPE_CHANGED)
                {
                    continue;
                }

                DataItem dataItem = dataEvent.getDataItem();
                if (dataItem.getUri().getPath().equals(PATH_WITH_IMAGE))
                {
                    /** 画像を受信した **/
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("prprImage");
                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                    String fileName = outputBitmap(bitmap, true);
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "RECEIVED IMAGE : " + fileName);
                    }
                    continue;
                }
                if (!dataItem.getUri().getPath().equals(PATH_WITH_FEATURE))
                {
                    /** データアイテム(URI)がCONFIGデータと違う **/
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "RECEIVED UNKNOWN: " + dataItem.getUri().getPath());
                    }
                    continue;
                }

                /** ここ以降は呼ばれないぽい(onMessageReceived() で処理される) **/
                if (Log.isLoggable(TAG, Log.INFO))
                {
                    Log.i(TAG, "RECEIVED COMMAND");
                }

                /** CONFIGデータを受信した **/
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                DataMap config = dataMapItem.getDataMap();
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "Config DataItem updated:" + config);
                }
                /** configデータ受信処理 **/
                parseDataMap(config);
            }
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "MyWatchFaceReceiver::onDataChanged()(Ex.): " + ex.getMessage());
            }
        }
        finally
        {
            dataEvents.close();
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchFaceReceiver::onDataChanged() <E>");
        }
    }

    /**
     *   ビットマップの読み出し
     *
     * @param asset
     * @return
     */
    private Bitmap loadBitmapFromAsset(Asset asset)
    {
        if (asset == null)
        {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        if (!mGoogleApiClient.isConnected())
        {
            ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(TIMEOUT_SEC, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess())
            {
                if (Log.isLoggable(TAG, Log.ERROR))
                {
                    Log.e(TAG, "Failed to connect to GoogleApiClient.");
                }
                return (null);
            }
        }

        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
        //mGoogleApiClient.disconnect();

        if (assetInputStream == null)
        {
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "Requested an unknown Asset.");
            }
            return null;
        }
        // decode the stream into a bitmap
        return (BitmapFactory.decodeStream(assetInputStream));
    }

    public void parseDataMap(DataMap config)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "parseDataMap");
        }
        for (String configKey : config.keySet())
        {
            if (!config.containsKey(configKey))
            {
                continue;
            }
            int value = config.getInt(configKey);
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "Found watch face config key: " + configKey + " -> " + Integer.toHexString(value));
            }
            if (configKey.equals(KEY_COMMAND_FILE_CLEAR))
            {
                // ファイルクリアコマンドを受信した！
                if (value == COMMAND_VALUE_ALL)
                {
                    // 全消去！
                    int nofFiles = clearAllBitmapFiles();
                    sendCommandTo(KEY_COMMAND_CLEAR_RESULT, nofFiles);   // 結果を送信する
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "deleted " + Integer.toString(nofFiles) + " files.");
                    }
                }
            }
        }
    }

    /**
     *   ビットマップファイルを全てクリアする
     *
     * @return  削除したビットマップファイル数
     */
    private int clearAllBitmapFiles()
    {
        int nofDeleteFiles = 0;
        String directory = fileUtility.getGokigenDirectory() + "/all";
        File files = new File(directory);
        File [] fileList = files.listFiles();
        if (fileList == null)
        {
            return (0);
        }
        for (File fileName : fileList)
        {
            String file = fileName.getName();
            boolean isDelete = fileName.delete();
            if (isDelete == true)
            {
                nofDeleteFiles++;
                if (Log.isLoggable(TAG, Log.INFO))
                {
                    Log.i(TAG, "delete file : " + file);
                }
            }
        }
        return (nofDeleteFiles);
    }
    /**
     *   ビットマップをファイルに出力する、ファイル名は現在時刻から自動生成
     *
     * @param bitmap  ファイル出力するビットマップ
     * @param isEntry 表示用に情報登録するかどうか
     */
    private String outputBitmap(Bitmap bitmap, boolean isEntry)
    {
        String fileName = "";
        if (bitmap == null)
        {
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "bitmap is null :" + fileName);
            }
            return (fileName);
        }
        fileName = fileUtility.putPngImageFromBitmap(null, "/all", bitmap);
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "outputBitmap :" + fileName);
        }
        /**
         if (isEntry == true)
         {
         // 受信したビットマップを表示用にデータ登録する
         nofBitmaps++;
         mBackgroundBitmaps.put(nofBitmaps, bitmap);
         }
         **/
        return (fileName);
    }
    /**
     *
     *
     * @param key   コマンド種別
     * @param value データ
     */
    private void sendCommandTo(String key, int value)
    {
        // そのうち、応答をコンパニオンアプリに返す用にする。
        if (Log.isLoggable(TAG, Log.INFO))
        {
            Log.i(TAG, "sendCommandTo: " + key + " " + value);
        }
        /*
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_WITH_FEATURE);
        DataMap configToPut = putDataMapRequest.getDataMap();
        configToPut.putInt(key, value);
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>()
                {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult)
                    {
                        if (Log.isLoggable(TAG, Log.DEBUG))
                        {
                            Log.d(TAG, "putDataItem result status: " + dataItemResult.getStatus());
                        }
                    }
                });
        */
    }
}
