package jp.sfjp.gokigen.prpr0;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by MRSa on 2014/12/26.
 */
public class MyWatchFaceMaintenanceActivity  extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<DataApi.DataItemResult>, View.OnClickListener, DataApi.DataListener
{
    private static final String TAG = "MyWatchConfig";

    private static final String PATH_WITH_FEATURE = "/gokigen/prpr/feature";
    public static final String PATH_WITH_IMAGE = "/gokigen/prpr/image";
    public static final String KEY_COMMAND_FILE_CLEAR = "CMD_FILE_CLEAR";
    public static final String KEY_COMMAND_CLEAR_RESULT = "CMD_CLEAR_RESULT";
    public static final int COMMAND_VALUE_ALL = -1;
    public static final int COMMAND_VALUE_SELECT_PICTURES = 11;

    private GoogleApiClient mGoogleApiClient = null;
    private String mPeerId = null;
    private int mScaledX = 320;
    private int mScaledY = 320;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainte);

        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        layoutScreen();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnected: " + connectionHint);
        }
        if (mPeerId != null)
        {
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("wear").path(PATH_WITH_FEATURE).authority(mPeerId).build();
            Wearable.DataApi.getDataItem(mGoogleApiClient, uri).setResultCallback(this);
        }
        else
        {
            displayNoConnectedDeviceDialog();
        }
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override // ResultCallback<DataApi.DataItemResult>
    public void onResult(DataApi.DataItemResult dataItemResult)
    {
        if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null)
        {
            DataItem configDataItem = dataItemResult.getDataItem();
            DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
            //DataMap config = dataMapItem.getDataMap();
            //setUpAllPickers(config);
        }
        else
        {
            // If DataItem with the current config can't be retrieved, select the default items on
            // each picker.
            //setUpAllPickers(null);
        }
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    }

    @Override // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionFailed: " + result);
        }
    }

    private void displayNoConnectedDeviceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String messageText = getResources().getString(R.string.title_no_device_connected);
        String okText = getResources().getString(R.string.text_ok);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(okText, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *
     *
     * @param configKey  送信コマンド名
     * @param command    コマンド番号
     */
    private void sendCommandTo(String configKey, int command, int stringId)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "sendCommandTo: " + command);
        }
        if (mPeerId != null)
        {
            DataMap config = new DataMap();
            config.putInt(configKey, command);
            byte[] rawData = config.toByteArray();
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, PATH_WITH_FEATURE, rawData);

            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "Sent watch face config message: " + configKey + " -> " + Integer.toHexString(command));
            }
            Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     *
     * @param bitmap    送信するビットマップ
     */
    private void sendBitmapTo(Bitmap bitmap)
    {
        Asset asset = createAssetFromBitmap(bitmap);
        PutDataMapRequest dataMap = PutDataMapRequest.create(PATH_WITH_IMAGE);
        dataMap.getDataMap().putAsset("prprImage", asset);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);

        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "Sent bitmap image.");
        }
        Toast.makeText(this, R.string.message_sent_picture, Toast.LENGTH_SHORT).show();
    }

    /**
     *   データとして送信するビットマップを変換する
     * @param bitmap
     * @return
     */
    private static Asset createAssetFromBitmap(Bitmap bitmap)
    {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    private void layoutScreen()
    {
        ComponentName name = getIntent().getParcelableExtra(WatchFaceCompanion.EXTRA_WATCH_FACE_COMPONENT);
        final Button selectPicture = (Button)findViewById(R.id.add_picture);
        selectPicture.setOnClickListener(this);

        final Button clearPicture = (Button)findViewById(R.id.reset_pictures);
        clearPicture.setOnClickListener(this);
    }

    /**
     *   ボタンが押されたときの処理
     *
     * @param v
     */
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.add_picture)
        {
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onClick: SELECT A PICTURE");
            }
            try
            {
                // 転送する画像を選択する （INTENTで画像を選択してもらう）
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, COMMAND_VALUE_SELECT_PICTURES);
            }
            catch (Exception ex)
            {
                if (Log.isLoggable(TAG, Log.WARN))
                {
                    Log.w(TAG, "EX: " + ex.getMessage());
                }
            }
        }
        else if (id == R.id.reset_pictures)
        {
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onClick: RESET PICTURE");
            }
            // 画像を全てクリアする
            clearAllPictures();
        }
        else
        {
            // unknown click event
        }
    }



    @Override    // DataApi.DataListener
    public void onDataChanged (DataEventBuffer dataEvents)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchFaceMaintenanceActivity::onDataChanged()");
        }
        try
        {
            for (DataEvent dataEvent : dataEvents)
            {
                if (dataEvent.getType() != DataEvent.TYPE_CHANGED)
                {
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "DATA EVENT TYPE: NOT CHANGED");
                    }
                    continue;
                }

                DataItem dataItem = dataEvent.getDataItem();
/*
                if (dataItem.getUri().getPath().equals(PATH_WITH_IMAGE))
                {
                    //  画像を受信した
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
*/
                if (!dataItem.getUri().getPath().equals(PATH_WITH_FEATURE))
                {
                    /** データアイテム(URI)がCONFIGデータと違う **/
                    if (Log.isLoggable(TAG, Log.INFO))
                    {
                        Log.i(TAG, "RECEIVED UNKNOWN: " + dataItem.getUri().getPath());
                    }
                    continue;
                }

                if (Log.isLoggable(TAG, Log.INFO))
                {
                    Log.i(TAG, "RECEIVED COMMAND");
                }

                /** データを受信した **/
                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                DataMap config = dataMapItem.getDataMap();
                if (Log.isLoggable(TAG, Log.INFO))
                {
                    Log.i(TAG, "Config DataItem updated:" + config);
                }
                /** データ受信処理 **/
                parseDataMap(config);
            }
        }
        catch (Exception ex)
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "MyWatchFaceMaintenanceActivity::onDataChanged()(Ex.): " + ex.getMessage());
            }
        }
        finally
        {
            dataEvents.close();
        }
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchFaceMaintenanceActivity::onDataChanged() <E>");
        }
    }

    /**
     *
     *
     */
    private void clearAllPictures()
    {
        // confirmationを出して、削除をして良いか確認する
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String titleText = getResources().getString(R.string.title_confirm_reset);
        String messageText = getResources().getString(R.string.message_confirm_reset);
        String okText = getResources().getString(R.string.text_ok);
        String cancelText = getResources().getString(R.string.text_cancel);
        builder.setTitle(titleText);
        builder.setMessage(messageText)
                .setCancelable(true)
                .setPositiveButton(okText, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // 削除許可が出た！ 削除コマンドを送出する
                        sendCommandTo(KEY_COMMAND_FILE_CLEAR, COMMAND_VALUE_ALL, R.string.message_sent_reset);
                     }
                })
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // 何もしない
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *  子画面から応答をもらったときの処理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onActivityResult: request:" + requestCode + ", result:" + resultCode);
        }
        try
        {
            if ((requestCode == COMMAND_VALUE_SELECT_PICTURES)&&(resultCode == Activity.RESULT_OK))
            {
                // 画像が選択されたとき...
                Uri uri = data.getData();
                InputStream input = null;
                try
                {
                    // 選択された画像をbitmapにして、転送する
                    input = getContentResolver().openInputStream(uri);
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    opt.inDither = true;
                    Bitmap readImage = BitmapFactory.decodeStream(new BufferedInputStream(input));
                    sendBitmapTo(Bitmap.createScaledBitmap(readImage, mScaledX, mScaledY, false));
                }
                catch (Exception e)
                {
                    if (Log.isLoggable(TAG, Log.WARN))
                    {
                        Log.w(TAG, "onActivityResult(bitmap ex.): " + e.getMessage());
                    }
                }
                finally
                {
                    try
                    {
                        if (input != null)
                        {
                            input.close();
                        }
                    }
                    catch (Exception ee)
                    {
                        if (Log.isLoggable(TAG, Log.WARN))
                        {
                            Log.w(TAG, "onActivityResult(ex.): " + ee.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            // 例外が発生したときには、何もしない。
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "onActivityResult(exception) :" + ex.getMessage());
            }
        }
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
            if (configKey.equals(KEY_COMMAND_CLEAR_RESULT))
            {
                // ファイルクリアコマンドの結果！！
                if (Log.isLoggable(TAG, Log.INFO))
                {
                    // 消去したファイル数をログ出力する
                    Log.i(TAG, "deleted " + Integer.toString(value) + " files.");
                }
            }
        }
    }
}
