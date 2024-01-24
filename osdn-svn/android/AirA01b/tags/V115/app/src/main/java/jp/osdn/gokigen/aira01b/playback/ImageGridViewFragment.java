package jp.osdn.gokigen.aira01b.playback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraFileInfo;
import jp.co.olympus.camerakit.OLYCameraKitException;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import jp.osdn.gokigen.aira01b.R;

public class ImageGridViewFragment extends Fragment
{
	private final String TAG = this.toString();
    private final String MOVIE_SUFFIX = ".mov";
    private final String JPEG_SUFFIX = ".jpg";
    private final String RAW_SUFFIX = ".orf";

    private GridView gridView;
	private boolean gridViewIsScrolling;
		
	private OLYCamera camera = null;
    private List<OLYCameraContentInfoEx> contentList;

	private ExecutorService executor;
	private LruCache<String, Bitmap> imageCache;

	public void setCamera(OLYCamera camera) {
		this.camera = camera;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Log.v(TAG, "ImageGridViewFragment::onCreate()");

		executor = Executors.newFixedThreadPool(1);
		imageCache = new LruCache<>(100);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.v(TAG, "ImageGridViewFragment::onCreateView()");
		View view = inflater.inflate(R.layout.fragment_image_grid_view, container, false);
		
		gridView = (GridView)view.findViewById(R.id.gridView1);
		gridView.setAdapter(new GridViewAdapter(inflater));
		gridView.setOnItemClickListener(new GridViewOnItemClickListener());
		gridView.setOnScrollListener(new GridViewOnScrollListener());
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.image_grid_view, menu);

		String title = getString(R.string.app_name);
		AppCompatActivity activity = (AppCompatActivity)getActivity();
		ActionBar bar = activity.getSupportActionBar();
		if (bar != null)
		{
			bar.setTitle(title);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.action_refresh)
		{
			refresh();
			return true;
		} else if (item.getItemId() == R.id.action_info) {
			String cameraVersion;
			try
			{
				Map<String, Object> hardwareInformation = camera.inquireHardwareInformation();
				cameraVersion = (String)hardwareInformation.get(OLYCamera.HARDWARE_INFORMATION_CAMERA_FIRMWARE_VERSION_KEY);
			}
			catch (OLYCameraKitException e)
			{
				cameraVersion = "Unknown";
			}
			Toast.makeText(getActivity(), "Camera " + cameraVersion + " / " + "CameraKit " + OLYCamera.getVersion(), Toast.LENGTH_SHORT).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.v(TAG, "onResume() Start");
		AppCompatActivity activity = (AppCompatActivity)getActivity();
		ActionBar bar = activity.getSupportActionBar();
		if (bar != null)
		{
			// ActionBarの表示を消す
			bar.hide();
		}
        ////////////////////////////////////////////////////////////////////////////////////
        try
        {
            if (camera != null)
            {
                // 再生モードかどうかを確認して、再生モードでなかった場合には再生モードに切り替える。
                OLYCamera.RunMode runMode = camera.getRunMode();
                if (runMode != OLYCamera.RunMode.Playback)
                {
					Thread thread = new Thread()
					{
						@Override
						public void run()
						{
							try
							{
								Log.v(TAG, "changeRunMode(OLYCamera.RunMode.Playback) : Start");
								camera.changeRunMode(OLYCamera.RunMode.Playback);
								Log.v(TAG, "changeRunMode(OLYCamera.RunMode.Playback) : End");
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
                            refresh();
                        }
					};
					thread.run();
                }
                else
                {
                    refresh();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ////////////////////////////////////////////////////////////////////////////////////

        Log.v(TAG, "onResume() End");
	}
	
	@Override
	public void onPause()
	{
        Log.v(TAG, "onPause() Start");
		if (!executor.isShutdown())
		{
			executor.shutdownNow();
		}
		super.onPause();
        Log.v(TAG, "onPause() End");
    }

	@Override
	public void onStop()
	{
        if (camera == null)
		{
			super.onStop();
			return;
		}
		super.onStop();
	}

	public void refresh()
	{
		contentList = null;
		
		camera.downloadContentList(new OLYCamera.DownloadContentListCallback() {			
			@Override
			public void onCompleted(List<OLYCameraFileInfo> list) {
				// Sort contents in chronological order (or alphabetical order).
				Collections.sort(list, new Comparator<OLYCameraFileInfo>() {
					@Override
					public int compare(OLYCameraFileInfo lhs, OLYCameraFileInfo rhs) {
						long diff = rhs.getDatetime().getTime() - lhs.getDatetime().getTime();
						if (diff == 0)
                        {
							diff = rhs.getFilename().compareTo(lhs.getFilename());
						}
						return (int)Math.min(Math.max(-1, diff), 1);
					}
				});

                List<OLYCameraContentInfoEx> contentItems = new ArrayList<>();
                HashMap<String, OLYCameraContentInfoEx> rawItems = new HashMap<>();
                for (OLYCameraFileInfo item : list)
                {
                    String path = item.getFilename().toLowerCase(Locale.getDefault());
                    if ((path.endsWith(JPEG_SUFFIX))||(path.endsWith(MOVIE_SUFFIX)))
                    {
                        contentItems.add(new OLYCameraContentInfoEx(item, false));
                    }
                    else if (path.endsWith(RAW_SUFFIX))
                    {
                        rawItems.put(path, new OLYCameraContentInfoEx(item, true));
                    }
                }

                for (OLYCameraContentInfoEx item : contentItems)
                {
                    String path = item.getFileInfo().getFilename().toLowerCase(Locale.getDefault());
                    if (path.endsWith(JPEG_SUFFIX))
                    {
                        String target = path.replace(JPEG_SUFFIX, RAW_SUFFIX);
                        OLYCameraContentInfoEx raw = rawItems.get(target);
                        if (raw != null)
                        {
                            item.setHasRaw(true);
                            Log.v(TAG, "DETECT RAW FILE: " + target);
                        }
                    }
                }
                contentList = contentItems;

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gridView.invalidateViews();
					}
				});
			}
			
			@Override
			public void onErrorOccurred(Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						presentMessage("Load failed", message);						
					}
				});
			}
		});

	}

	private static class GridCellViewHolder
    {
		public ImageView imageView;
		public ImageView iconView;


	}
	
	private class GridViewAdapter extends BaseAdapter
    {
		private LayoutInflater inflater;

		public GridViewAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		private List<?> getItemList()
        {
            return (contentList);
		}
		
		@Override
		public int getCount() {
			if (getItemList() == null) {
				return 0;
			}
			return getItemList().size();
		}

		@Override
		public Object getItem(int position) {
			if (getItemList() == null) {
				return null;
			}
			return getItemList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GridCellViewHolder viewHolder;
			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.view_grid_cell, parent, false);
				
				viewHolder = new GridCellViewHolder();
				viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageViewY);
				viewHolder.iconView = (ImageView)convertView.findViewById(R.id.imageViewZ);

				convertView.setTag(viewHolder);
			}
            else
            {
				viewHolder = (GridCellViewHolder)convertView.getTag();
			}

			OLYCameraContentInfoEx infoEx = (OLYCameraContentInfoEx) getItem(position);
			OLYCameraFileInfo item = (infoEx != null) ? infoEx.getFileInfo() : null;
			if (item == null)
            {
				viewHolder.imageView.setImageDrawable(null);
				viewHolder.iconView.setImageDrawable(null);
				return convertView;
			}
			String path = new File(item.getDirectoryPath(), item.getFilename()).getPath();
			Bitmap thumbnail = imageCache.get(path);
			if (thumbnail == null)
            {
				viewHolder.imageView.setImageDrawable(null);
				viewHolder.iconView.setImageDrawable(null);
				if (!gridViewIsScrolling)
                {
					if (executor.isShutdown())
                    {
						executor = Executors.newFixedThreadPool(1);
					}
					executor.execute(new ThumbnailLoader(viewHolder, path, infoEx.hasRaw()));
				}
			}
            else
            {
				viewHolder.imageView.setImageBitmap(thumbnail);
				if (path.toLowerCase().endsWith(MOVIE_SUFFIX))
                {
					viewHolder.iconView.setImageResource(R.drawable.icn_movie);
				}
                else if (infoEx.hasRaw())
                {
                    viewHolder.iconView.setImageResource(R.drawable.ic_raw_black_1x);
                }
                else
                {
					viewHolder.iconView.setImageDrawable(null);
				}
			}
			return convertView;
		}
	}
	
	private class GridViewOnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        ImagePagerViewFragment fragment = new ImagePagerViewFragment();	// Use an advanced viewer.
	        fragment.setCamera(camera);
	        fragment.setContentList(contentList);
	        fragment.setContentIndex(position);
	        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
	        transaction.replace(getId(), fragment);
	       	transaction.addToBackStack(null);
	       	transaction.commit();
		}
	}
	
	private class GridViewOnScrollListener implements AbsListView.OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// No operation.
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == SCROLL_STATE_IDLE) {
				gridViewIsScrolling = false;
				gridView.invalidateViews();
			} else if ((scrollState == SCROLL_STATE_FLING) || (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
				gridViewIsScrolling = true;
				if (!executor.isShutdown()) {
					executor.shutdownNow();
				}
			}
		}
	}

	private class ThumbnailLoader implements Runnable
    {
		private GridCellViewHolder viewHolder;
		private String path;
        private final boolean hasRaw;
		
		ThumbnailLoader(GridCellViewHolder viewHolder, String path, boolean hasRaw)
        {
			this.viewHolder = viewHolder;
			this.path = path;
            this.hasRaw = hasRaw;
		}
		
		@Override
		public void run()
        {
			class Box {
				boolean isDownloading = true;
			}
			final Box box = new Box();
			
			camera.downloadContentThumbnail(path, new OLYCamera.DownloadImageCallback() {
				@Override
				public void onProgress(ProgressEvent e) {
				}
				
				@Override
				public void onCompleted(byte[] data, Map<String, Object> metadata) {
					final Bitmap thumbnail = createRotatedBitmap(data, metadata);
					imageCache.put(path, thumbnail);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							viewHolder.imageView.setImageBitmap(thumbnail);
							if (path.toLowerCase().endsWith(MOVIE_SUFFIX)) {
								viewHolder.iconView.setImageResource(R.drawable.icn_movie);
							} else if (hasRaw)
                            {
                                viewHolder.iconView.setImageResource(R.drawable.ic_raw_black_1x);
                            }
                            else
                            {
								viewHolder.iconView.setImageDrawable(null);
							}
						}
					});
					box.isDownloading = false;  
				}
				
				@Override
				public void onErrorOccurred(Exception e) {
					box.isDownloading = false;
				}
			});

			// Waits to realize the serial download.
			while (box.isDownloading) {
				Thread.yield();
			}
		}
	}
	
	
	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	
	private void presentMessage(String title, String message) {
		Context context = getActivity();
		if (context == null) return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message);
		builder.show();
	}
	
	private void runOnUiThread(Runnable action) {
		Activity activity = getActivity();
		if (activity == null) return;
		
		activity.runOnUiThread(action);
	}
	
	
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		if (bitmap == null) {
			return null;
		}
		
		int degrees = getRotationDegrees(data, metadata);
		if (degrees != 0) {
			Matrix m = new Matrix();
			m.postRotate(degrees);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		
		return bitmap;
	}
	
	private int getRotationDegrees(byte[] data, Map<String, Object> metadata) {
		int degrees = 0;
		int orientation = ExifInterface.ORIENTATION_UNDEFINED;
		
		if (metadata != null && metadata.containsKey("Orientation")) {
			orientation = Integer.parseInt((String)metadata.get("Orientation"));
		} else {
			// Gets image orientation to display a picture.
			try {
				File tempFile = File.createTempFile("temp", null);
				{
					FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
					outStream.write(data);
					outStream.close();
				}
				
				ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
				orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

				tempFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			degrees = 0;
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			degrees = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			degrees = 180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			degrees = 270;
			break;
		default:
			break;
		}
		
		return degrees;
	}
}
