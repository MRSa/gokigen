package jp.sfjp.gokigen.prpr0;

import android.os.Bundle;
import android.support.wearable.companion.WatchFaceCompanion;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.util.concurrent.TimeUnit;

/**
 * Created by MRSa on 2014/12/23.
 */
public class MyWatchListenerService extends WearableListenerService
        implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "MyWatchListenerService";
    private static final long TIMEOUT_SEC = 30;

    private GoogleApiClient mGoogleApiClient = null;
    private MyWatchFaceReceiver mDataReceiver = null;

    @Override // WearableListenerService
    public void onCreate()
    {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)                 // Request access only to the Wearable API
                .build();

        mDataReceiver = new MyWatchFaceReceiver(mGoogleApiClient);
    }

    @Override
    public void onDestroy()
    {
        if ((mGoogleApiClient != null)&&(mGoogleApiClient.isConnected()))
        {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override // WearableListenerService
    public void onMessageReceived(MessageEvent messageEvent)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onMessageReceived: ");
        }
        if (!messageEvent.getPath().equals(MyWatchFaceReceiver.PATH_WITH_FEATURE))
        {
            return;
        }
        byte[] rawData = messageEvent.getData();
        DataMap receivedDataMap = DataMap.fromByteArray(rawData);
        mDataReceiver.parseDataMap(receivedDataMap);

        if (mGoogleApiClient == null)
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "onMessageReceived: mGoogleApiClient is null");
            }
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)                // Request access only to the Wearable API
                    .build();
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
                return;
            }
        }
    }

    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnected: " + connectionHint);
        }
        if (mGoogleApiClient == null)
        {
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "mGoogleApiClient is null...");
            }
        }

        // Now you can use the Data Layer API
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
/*
        Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>()
                {
                    @Override
                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult)
                    {
                        String localNode = getLocalNodeResult.getNode().getId();
                        Uri uri = new Uri.Builder()
                                .scheme("wear")
                                .path(PATH_WITH_FEATURE)
                                .authority(localNode)
                                .build();
                        Wearable.DataApi.getDataItem(mGoogleApiClient, uri)
                                .setResultCallback(new ResultCallback<DataApi.DataItemResult>()
                                {
                                    @Override
                                    public void onResult(DataApi.DataItemResult dataItemResult)
                                    {
                                        if (dataItemResult.getStatus().isSuccess())
                                        {
                                            if (dataItemResult.getDataItem() != null)
                                            {
                                                DataItem configDataItem = dataItemResult.getDataItem();
                                                DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
                                                DataMap config = dataMapItem.getDataMap();
                                                onDataMapFetched(config);
                                            }
                                            else
                                            {
                                                onDataMapFetched(new DataMap());
                                            }
                                        }
                                    }
                                });
                    }
                }
        );
*/
    }

/*
    private void onDataMapFetched(DataMap data)
    {
        if (Log.isLoggable(TAG, Log.INFO))
        {
            Log.i(TAG, "onDataMapFetched ");
        }
    }
*/

    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchListenerService::onDataChanged()");
        }
        mDataReceiver.onDataChanged(dataEvents);
    }

    @Override  // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    }

    @Override  // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onConnectionFailed: " + result);
        }
    }
}
