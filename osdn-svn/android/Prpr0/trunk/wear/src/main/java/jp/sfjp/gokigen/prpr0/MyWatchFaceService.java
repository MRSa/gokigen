package jp.sfjp.gokigen.prpr0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Wearable;

import java.util.TimeZone;
import static java.util.concurrent.TimeUnit.*;

/**
 *  ウォッチフェースのメイン処理クラス
 *  （サンプルコードを参考に作成）
 * Created by MRSa on 2014/12/20.
 */
public class MyWatchFaceService extends CanvasWatchFaceService
{
    private static final String TAG = "MyWatchFaceService";
    private static final int MSG_UPDATE_TIME = 0;
    private static final long INTERACTIVE_UPDATE_RATE_MS = SECONDS.toMillis(10);

    /* a time object */
    private Time mTime = null;

    @Override
    public Engine onCreateEngine()
    {
        /* provide your watch face implementation */
        return new Engine();
    }

    /* implement service callback methods */
    //private class Engine extends CanvasWatchFaceService.Engine
    private class Engine extends CanvasWatchFaceService.Engine implements DataApi.DataListener,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MyWatchFaceService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)                // Request access only to the Wearable API
                .build();

        MyWatchFaceHolder myHolder = null;
        MyWatchFaceDrawer myDrawer = null;

        @Override
        public void onCreate(SurfaceHolder holder)
        {
            super.onCreate(holder);

            /* configure the system UI */
            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            /* initialize your watch face */
            myHolder = new MyWatchFaceHolder(MyWatchFaceService.this);
            myDrawer = new MyWatchFaceDrawer(myHolder);
            myDrawer.initialize(false);

            /* allocate an object to hold the time */
            mTime = new Time();
        }

        @Override
        public void onPropertiesChanged(Bundle properties)
        {
            super.onPropertiesChanged(properties);

            /* get device features (burn-in, low-bit ambient) */
            if (myHolder != null)
            {
                myHolder.onPropertiesChanged(properties);
            }
        }

        @Override
        public void onTimeTick()
        {
            super.onTimeTick();

            /* the time changed */
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode)
        {
            boolean previousMode = isInAmbientMode();
            super.onAmbientModeChanged(inAmbientMode);

             /* the wearable switched between modes */
            if (myHolder != null)
                if (myHolder.setAmbientMode(inAmbientMode, previousMode))
                {
                    invalidate();

                    // Whether the timer should be running depends on whether we're in ambient mode (as well
                    // as whether we're visible), so we may need to start or stop the timer.
                    updateTimer();
                }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds)
        {
            // Update the time
            mTime.setToNow();

            /* draw your watch face */
            if (myDrawer != null)
            {
                myDrawer.doDraw(canvas, bounds, mTime);
            }
            else
            {
                /* my watch face drawer is not exist */
                if (Log.isLoggable(TAG, Log.DEBUG))
                {
                    Log.d(TAG, "onDraw: myDrawer is null.");
                }
            }
        }

        @Override
        public void onDestroy()
        {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (myHolder != null)
            {
                myHolder.dispose();
                myHolder = null;
            }
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            /* the watch face became visible or invisible */
            super.onVisibilityChanged(visible);

            if (visible)
            {
                mGoogleApiClient.connect();

                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            }
            else
            {
                unregisterReceiver();
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                {
                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }

            // Whether the timer should be running depends on whether we're visible and
            // whether we're in ambient mode), so we may need to start or stop the timer
            updateTimer();
        }

        /**
         *
         *
         */
        private void registerReceiver()
        {
            if (mRegisteredTimeZoneReceiver)
            {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        /**
         *
         *
         */
        private void unregisterReceiver()
        {
            if (!mRegisteredTimeZoneReceiver)
            {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets)
        {
            boolean isRoundShape = insets.isRound();
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onApplyWindowInsets: " + (isRoundShape ? "round" : "square"));
            }
            super.onApplyWindowInsets(insets);
            myHolder.setIsRound(isRoundShape, MyWatchFaceService.this.getResources());
        }

        /* handler to update the time once a second in interactive mode */
        final Handler mUpdateTimeHandler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                switch (message.what)
                {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning())
                        {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;

                    default:
                        /* a received message is unknown */
                        if (Log.isLoggable(TAG, Log.DEBUG))
                        {
                            Log.d(TAG, "handleMessage: unknown message.");
                        }
                        /*  */
                        break;
                }
            }
        };

        /* receiver to update the time zone */
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer()
        {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning())
            {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning()
        {
            return (isVisible() && !isInAmbientMode());
        }

        @Override
        public void onConnected(Bundle bundle)
        {
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onConnected: " + bundle);
            }
            // Now you can use the Data Layer API
            Wearable.DataApi.addListener(mGoogleApiClient, Engine.this);
        }

       @Override
        public void onDataChanged(DataEventBuffer dataEvents)
        {
            if (Log.isLoggable(TAG, Log.INFO))
            {
                Log.i(TAG, "MyWatchFaceService::onDataChanged() ");
            }
        }

        @Override
        public void onConnectionSuspended(int cause)
        {
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        }

        @Override
        public void onConnectionFailed(@Nullable ConnectionResult connectionResult)
        {
            if (Log.isLoggable(TAG, Log.DEBUG))
            {
                Log.d(TAG, "onConnectionFailed: " + connectionResult);
            }
        }
    }
}
