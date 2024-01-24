package jp.sourceforge.gokigen.viewsensor;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

/**
 * Created by MRSa on 2015/01/04.
 */
public class NodeListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "NodeListenerService";
    private static final long TIMEOUT_SEC = 30;

    private GoogleApiClient mGoogleApiClient = null;

    @Override // WearableListenerService
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onPeerDisconnected(Node peer)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "You have been disconnected.");
        }
        if(!hasGPS())
        {
                // Notify user to bring tethered handset
                // Fall back to functionality that does not use location
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override // WearableListenerService
    public void onMessageReceived(MessageEvent messageEvent)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "onMessageReceived: ");
        }

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
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnected: " + connectionHint);
        }
        if (mGoogleApiClient == null)
        {
            if (Log.isLoggable(TAG, Log.WARN))
            {
                Log.w(TAG, "mGoogleApiClient is null...");
            }
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents)
    {
        if (Log.isLoggable(TAG, Log.DEBUG))
        {
            Log.d(TAG, "MyWatchListenerService::onDataChanged()");
        }
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

    /**
     *
     *
     * @return true GPS搭載, false GPS非搭載
     */
    private boolean hasGPS()
    {
        return (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));
    }

}
