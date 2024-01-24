package jp.osdn.gokigen.aira01a;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.co.olympus.camerakit.OLYCamera;
import jp.co.olympus.camerakit.OLYCameraFileInfo;
import jp.co.olympus.camerakit.OLYCamera.ProgressEvent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class ImagePagerViewFragment extends Fragment
{

	private OLYCamera camera = null;
	private List<OLYCameraFileInfo> contentList = null;
	private int contentIndex = 0;

	private LayoutInflater layoutInflater = null;
	private ViewPager viewPager = null;
	private LruCache<String, Bitmap> imageCache =null;

	private MyImageDownloader imageDownloader = null;
	private MyMovieDownloader movieDownloader = null;

	public void setCamera(OLYCamera camera) {
		this.camera = camera;
	}
	
	public void setContentList(List<OLYCameraFileInfo> contentList) {
		this.contentList = contentList;
	}
	
	public  void setContentIndex(int contentIndex) {
		this.contentIndex = contentIndex;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageCache = new LruCache<String, Bitmap>(5);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		layoutInflater = inflater;
		View view = layoutInflater.inflate(R.layout.fragment_image_pager_view, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.viewPager1);
		viewPager.setAdapter(new ImagePagerAdapter());
		viewPager.addOnPageChangeListener(new ImagePageChangeListener());
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		OLYCameraFileInfo file = contentList.get(contentIndex);
		String path = file.getDirectoryPath() + "/" + file.getFilename();

		AppCompatActivity activity = (AppCompatActivity)getActivity();
		ActionBar bar = activity.getSupportActionBar();
		if (bar != null)
		{
			bar.show();
			bar.setTitle(path);
		}

        String lowerCasePath = path.toLowerCase();
        if (lowerCasePath.endsWith(".jpg"))
        {
            inflater.inflate(R.menu.image_view, menu);
            MenuItem downloadMenuItem = menu.findItem(R.id.action_download);
            downloadMenuItem.setEnabled(true);
        }
        else
        {
            inflater.inflate(R.menu.movie_view, menu);
            MenuItem downloadMenuItem = menu.findItem(R.id.action_download_movie);
            downloadMenuItem.setEnabled(true);
        }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean doDownload = false;
		float downloadSize = 0;

		if (item.getItemId() == R.id.action_download_original_size) {
			downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_2048x1536) {
			downloadSize = OLYCamera.IMAGE_RESIZE_2048;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1920x1440) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1920;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1600x1200) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1600;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_1024x768) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1024;
			doDownload = true;
		} else if (item.getItemId() == R.id.action_download_original_movie) {
            downloadSize = OLYCamera.IMAGE_RESIZE_NONE;
            doDownload = true;
        }
		
		if (doDownload)
		{
			OLYCameraFileInfo file = contentList.get(contentIndex);
			String path = file.getDirectoryPath() + "/" + file.getFilename();
			String lowerCasePath = path.toLowerCase();
			String suffix = lowerCasePath.substring(lowerCasePath.lastIndexOf("."));
			Calendar calendar = Calendar.getInstance();
			String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime()) + suffix;

			//  ダイアログを表示して保存する
			saveImageWithDialog(filename, downloadSize);
            /*****
            //  Layoutの Fragment が ImagePagerViewFragment を 継承していた時にはイメージを保存する
			Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment1);
			if (ImagePagerViewFragment.class.isAssignableFrom(fragment.getClass())) {
				ImagePagerViewFragment imagePagerViewFragment = (ImagePagerViewFragment)fragment;
				imagePagerViewFragment.saveImage(filename, downloadSize);
				return true;
			}
			******/
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume()
    {
		super.onResume();
		AppCompatActivity activity = (AppCompatActivity)getActivity();
		ActionBar bar = activity.getSupportActionBar();
		if (bar != null)
		{
			bar.setDisplayShowHomeEnabled(true);
			bar.show();
		}
		viewPager.setCurrentItem(contentIndex);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		AppCompatActivity activity = (AppCompatActivity)getActivity();
		ActionBar bar = activity.getSupportActionBar();
		if (bar != null)
		{
			bar.hide();
		}
	}

	
	private class ImagePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return contentList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = (ImageView)layoutInflater.inflate(R.layout.view_image_page, container, false);
			container.addView(view);
			downloadImage(position, view);
			return view;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView((ImageView)object);
		}
		
	}

	private class ImagePageChangeListener implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int state)
		{

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{

		}

		@Override
		public void onPageSelected(int position)
		{
			contentIndex = position;
			
			OLYCameraFileInfo file = contentList.get(contentIndex);
			String path = file.getDirectoryPath() + "/" + file.getFilename();

			AppCompatActivity activity = (AppCompatActivity)getActivity();
			ActionBar bar = activity.getSupportActionBar();
			if (bar != null)
			{
				bar.setTitle(path);
			}
			activity.getSupportActionBar().setTitle(path);
			activity.getFragmentManager().invalidateOptionsMenu();
		}

	}

	private void downloadImage(int position, final ImageView view) {
		OLYCameraFileInfo file = contentList.get(position);
		final String path = file.getDirectoryPath() + "/" + file.getFilename();

		// Get the cached image.
		Bitmap bitmap = imageCache.get(path);
		if (bitmap != null) {
			if (view != null && viewPager.indexOfChild(view) > -1) {
				view.setImageBitmap(bitmap);
			}
			return;
		}		
		
		// Download the image.
		camera.downloadContentScreennail(path, new OLYCamera.DownloadImageCallback() {
			@Override
			public void onProgress(ProgressEvent e) {			
				// MARK: Do not use to cancel a downloading by progress handler.
			 	//       A communication error may occur by the downloading of the next image when
				//       you cancel the downloading of the image by a progress handler in
				//       the current version.
			}
			
			@Override
			public void onCompleted(final byte[] data, final Map<String, Object> metadata) {
				// Cache the downloaded image.
				final Bitmap bitmap = createRotatedBitmap(data, metadata);
				imageCache.put(path, bitmap);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (view != null && viewPager.indexOfChild(view) > -1) {
							view.setImageBitmap(bitmap);
						}
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

	public void saveImageWithDialog(final String filename, float downloadSize)
	{
		if (filename.endsWith(".jpg"))
		{
			// 静止画の取得
			imageDownloader = new MyImageDownloader(filename, downloadSize);
			imageDownloader.startDownload();
		}
		else
		{
			// 動画の取得
			movieDownloader = new MyMovieDownloader(filename);
			movieDownloader.startDownload();
		}
	}


	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	
	private void presentMessage(String title, String message)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title).setMessage(message);
		builder.show();
	}
	
	private void runOnUiThread(Runnable action)
    {
		if (getActivity() == null)
        {
			return;
		}
		getActivity().runOnUiThread(action);
	}
	
	
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			bitmap = null;
			System.gc();
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
				bitmap = null;
				System.gc();
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
			}
            catch (IOException e)
            {
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

    /**
     *   静止画のダウンロード
     *
     */
	private class MyImageDownloader implements OLYCamera.DownloadImageCallback
	{
		private ProgressDialog downloadDialog = null;
		private String filename = null;
		private float downloadImageSize = 0.0f;

		/**
		 *   コンストラクタ
		 *
		 * @param filename  ファイル名
		 * @param downloadSize  ダウンロードのサイズ
         */
		public MyImageDownloader(final String filename, float downloadSize)
		{
			this.filename = filename;
			this.downloadImageSize = downloadSize;
		}

		/**
		 *   ダウンロードの開始
		 *
		 */
		public void startDownload()
		{
			downloadDialog = new ProgressDialog(getContext());
			downloadDialog.setTitle(getString(R.string.dialog_download_title));
			downloadDialog.setMessage(getString(R.string.dialog_download_message) + " " + filename);
			downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			downloadDialog.setCancelable(false);
			downloadDialog.show();

			// Download the image.
			OLYCameraFileInfo file = contentList.get(contentIndex);
			String path = file.getDirectoryPath() + "/" + file.getFilename();
			camera.downloadImage(path, downloadImageSize, this);
		}

		/**
		 *   進行中の表示
		 *
		 * @param progressEvent 進捗情報
         */
		@Override
		public void onProgress(ProgressEvent progressEvent)
		{
			//
			if (downloadDialog != null)
			{
				int percent = (int)(progressEvent.getProgress() * 100.0f);
				downloadDialog.setProgress(percent);
				//downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
			}
		}

		/**
		 *   ファイル受信終了
		 *
		 * @param bytes  受信バイト数
         * @param map    ファイルの情報
         */
		@Override
		public void onCompleted(byte[] bytes, Map<String, Object> map)
		{
			final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name) + "/";
			String filepath = new File(directoryPath.toLowerCase(), filename).getPath();

			// ファイルを保存する
			try
			{
				final File directory = new File(directoryPath);
				if (!directory.exists())
				{
					directory.mkdirs();
				}
				FileOutputStream outputStream = new FileOutputStream(filepath);
				outputStream.write(bytes);
				outputStream.close();
			}
			catch (IOException e)
			{
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
				// ダウンロード失敗時には、ギャラリーにデータ登録を行わない。
				return;
			}

			// ギャラリーに受信したファイルを登録する
			try
			{
				long now = System.currentTimeMillis();
				ContentValues values = new ContentValues();
				ContentResolver resolver = getActivity().getContentResolver();
				values.put(Images.Media.MIME_TYPE, "image/jpeg");
				values.put(Images.Media.DATA, filepath);
				values.put(Images.Media.DATE_ADDED, now);
				values.put(Images.Media.DATE_TAKEN, now);
				values.put(Images.Media.DATE_MODIFIED, now);
				values.put(Images.Media.ORIENTATION, getRotationDegrees(bytes, map));
				resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						Toast.makeText(getActivity(), getString(R.string.download_control_save_success) + " " + filename, Toast.LENGTH_SHORT).show();
					}
				});
			} catch (Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
			}
			System.gc();
		}

		@Override
		public void onErrorOccurred(Exception e)
		{
			final String message = e.getMessage();
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (downloadDialog != null)
					{
						downloadDialog.dismiss();
					}
					presentMessage(getString(R.string.download_control_download_failed), message);
				}
			});

		}
	}


	/**
	 *   動画のダウンロード
	 *
	 */
	private class MyMovieDownloader implements  OLYCamera.DownloadLargeContentCallback
	{
		private ProgressDialog downloadDialog = null;
		private String filename = null;
		private String filepath = null;
		private FileOutputStream outputStream = null;

		/**
		 *   コンストラクタ
		 *
		 * @param filename ファイル名
		 */
		public MyMovieDownloader(final String filename)
		{
			this.filename = filename;
		}

		/**
		 *   ダウンロードの開始
		 *
		 */
		public void startDownload()
		{
			downloadDialog = new ProgressDialog(getContext());
			downloadDialog.setTitle(getString(R.string.dialog_download_movie_title));
			downloadDialog.setMessage(getString(R.string.dialog_download_message) + " " + filename);
			downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			downloadDialog.setCancelable(false);
			downloadDialog.show();

			// Download the image.
			OLYCameraFileInfo file = contentList.get(contentIndex);
			String path = file.getDirectoryPath() + "/" + file.getFilename();
			camera.downloadLargeContent(path, this);

			final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name) + "/";
			filepath = new File(directoryPath.toLowerCase(), filename).getPath();
			try
			{
					final File directory = new File(directoryPath);
					if (!directory.exists())
					{
						directory.mkdirs();
					}
					outputStream = new FileOutputStream(filepath);
			}
			catch (Exception e)
			{
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
			}
		}

		@Override
		public void onProgress(byte[] bytes, ProgressEvent progressEvent)
		{
			if (downloadDialog != null)
			{
				int percent = (int)(progressEvent.getProgress() * 100.0f);
				downloadDialog.setProgress(percent);
				//downloadDialog.setCancelable(progressEvent.isCancellable()); // キャンセルできるようにしないほうが良さそうなので
			}
			try
			{
				if (outputStream != null)
				{
					outputStream.write(bytes);
				}
			}
            catch (Exception e)
			{
                e.printStackTrace();
			}
		}

		@Override
		public void onCompleted()
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.flush();
					outputStream.close();
				}

				// ギャラリーに受信したファイルを登録する
				long now = System.currentTimeMillis();
				ContentValues values = new ContentValues();
				ContentResolver resolver = getActivity().getContentResolver();
				values.put(Images.Media.MIME_TYPE, "video/mp4");
				values.put(Images.Media.DATA, filepath);
				values.put(Images.Media.DATE_ADDED, now);
				values.put(Images.Media.DATE_TAKEN, now);
				values.put(Images.Media.DATE_MODIFIED, now);
				resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

				runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						Toast.makeText(getActivity(), getString(R.string.download_control_save_success) + " " + filename, Toast.LENGTH_SHORT).show();
					}
				});
			}
			catch (Exception e)
			{
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (downloadDialog != null)
						{
							downloadDialog.dismiss();
						}
						presentMessage(getString(R.string.download_control_save_failed), message);
					}
				});
			}
            System.gc();
		}

		@Override
		public void onErrorOccurred(Exception e)
		{
			final String message = e.getMessage();
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (downloadDialog != null)
					{
						downloadDialog.dismiss();
					}
					presentMessage(getString(R.string.download_control_download_failed), message);
				}
			});
			System.gc();
		}
	}
}
