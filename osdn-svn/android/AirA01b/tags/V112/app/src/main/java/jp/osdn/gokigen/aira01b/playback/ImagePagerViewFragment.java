package jp.osdn.gokigen.aira01b.playback;

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
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import jp.osdn.gokigen.aira01b.R;


public class ImagePagerViewFragment extends Fragment
{
    private final String TAG = this.toString();
    private final float IMAGE_RESIZE_FOR_GET_INFORMATION = 640.0f;

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
        boolean getInformation = false;
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
        } else if (item.getItemId() == R.id.action_get_information) {
			downloadSize = OLYCamera.IMAGE_RESIZE_1024;
            getInformation = true;
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
			saveImageWithDialog(filename, downloadSize, getInformation);
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

            /**
            try
            {
                // 試しにコンテンツ情報を取得してみる ... アートフィルターの設定情報はとれる
                Map<String, Object> content = camera.inquireContentInformation(path);
                for (String key : content.keySet())
                {
                    Log.v(TAG, "INFO: " + key + " " + content.get(key).toString());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            **/

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
				try {
					if (imageCache != null)
					{
						imageCache.put(path, bitmap);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if ((bitmap != null)&&(view != null)&&(viewPager.indexOfChild(view) > -1))
						{
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

	/**
	 *   デバイスに画像ファイルをダウンロード（保存）する
	 *
	 * @param filename       ファイル名（カメラ内の）
	 * @param downloadSize   ダウンロードサイズ
     * @param isGetInformationMode 情報取得モードか？
     */
	public void saveImageWithDialog(final String filename, float downloadSize, boolean isGetInformationMode)
	{
		if (filename.endsWith(".jpg"))
		{
			// 静止画の取得
			imageDownloader = new MyImageDownloader(filename, downloadSize, isGetInformationMode);
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
	
	
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata)
	{
		Bitmap bitmap;
		try
		{
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
			bitmap = null;
			System.gc();
		}
		if (bitmap == null)
		{
			return (null);
		}

		// ビットマップの回転を行う
		int degrees = getRotationDegrees(data, metadata);
		if (degrees != 0)
		{
			Matrix m = new Matrix();
			m.postRotate(degrees);
			try
			{
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			}
			catch (OutOfMemoryError e)
			{
				e.printStackTrace();
				bitmap = null;
				System.gc();
			}
		}
		return (bitmap);
	}
	
	private int getRotationDegrees(byte[] data, Map<String, Object> metadata)
	{
		int degrees = 0;
		int orientation = ExifInterface.ORIENTATION_UNDEFINED;
		
		if (metadata != null && metadata.containsKey("Orientation")) {
			orientation = Integer.parseInt((String)metadata.get("Orientation"));
		}
		else
		{
			// Gets image orientation to display a picture.
			try
            {
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

		switch (orientation)
		{
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
     *   静止画のダウンロード（とEXIF情報の取得）
     *
     */
	private class MyImageDownloader implements OLYCamera.DownloadImageCallback
	{
        private boolean isGetInformation = false;
		private ProgressDialog downloadDialog = null;
		private String filename = null;
		private float downloadImageSize = 0.0f;

		/**
		 *   コンストラクタ
		 *
		 * @param filename  ファイル名
		 * @param downloadSize  ダウンロードのサイズ
         * @param isGetInformation  情報を取得するだけかどうか（trueなら情報を取得するだけ)
         */
		public MyImageDownloader(final String filename, float downloadSize, boolean isGetInformation)
		{
			this.filename = filename;
			this.downloadImageSize = downloadSize;
            this.isGetInformation = isGetInformation;
		}

		/**
		 *   静止画のダウンロード開始指示
		 *
		 */
		public void startDownload()
		{
			Log.v(TAG, "startDownload() " + filename);
			downloadDialog = new ProgressDialog(getContext());
            if (isGetInformation)
            {
                downloadDialog.setTitle(getString(R.string.dialog_get_information_title));
            }
            else
            {
                downloadDialog.setTitle(getString(R.string.dialog_download_title));
            }
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
		 *   進行中の表示 (進捗バーの更新)
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
		 *   ファイル受信終了時の処理
		 *
		 * @param bytes  受信バイト数
         * @param map    ファイルの情報
         */
		@Override
		public void onCompleted(byte[] bytes, Map<String, Object> map)
		{
            if (isGetInformation)
            {
                // Exif情報をダイアログ表示して終わる
                showExifInformation(bytes);
                System.gc();
                return;
            }

			final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name2) + "/";
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

        /**
         *   エラー発生時の処理
         *
         * @param e エラーの情報
         */
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
                    if (isGetInformation)
                    {
                        presentMessage(getString(R.string.download_control_get_information_failed), message);
                    }
                    else
                    {
                        presentMessage(getString(R.string.download_control_download_failed), message);
                    }
				}
			});
		}

        /**
         *   EXIF情報の表示 (ExifInterface を作って、表示クラスに渡す)
         *
         * @param bytes
         */
        private void showExifInformation(byte[] bytes)
        {
            ExifInterface exif = null;
            try
            {
                Calendar calendar = Calendar.getInstance();
                String filename = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(calendar.getTime());
                File tempFile = File.createTempFile(filename, null);
                {
                    FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
                    outStream.write(bytes);
                    outStream.close();
                }
                exif = new ExifInterface(tempFile.getAbsolutePath());
                tempFile.delete();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            runOnUiThread(new ExifInfoToShow(exif));
        }

        /**
         *   EXIF情報を表示する処理
         *   （クラス生成時に、表示情報を作り出す）
         */
        private class ExifInfoToShow implements Runnable
        {
            private String message = "";

            /**
             *   コンストラクタ
             * @param information メッセージ
             */
            public ExifInfoToShow(ExifInterface information)
            {
                this.message = formMessage(information);
            }

            /**
             *   Exif情報を表示に適した形式に変更し、情報を表示する
             * @param exifInterface Exif情報
             * @return 表示に適したExif情報
             */
            private String formMessage(ExifInterface exifInterface)
            {
                String msg = "";
                if (exifInterface != null)
                {
                    // 撮影時刻
                    msg = msg + getString(R.string.exif_datetime_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_DATETIME) + "\r\n"; //(string)

                    msg = msg + "\r\n";

                    // 焦点距離
                    double focalLength = exifInterface.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0.0f);
                    msg = msg + getString(R.string.exif_focal_length_title);
                    msg = msg + " " + String.valueOf(focalLength) + "mm ";
                    msg = msg + "(" + getString(R.string.exif_focal_35mm_equiv_title) + " " + String.valueOf(focalLength * 2.0d) + "mm)" + "\r\n";

                    msg = msg + "\r\n";

                    // 露光時間
                    msg = msg + getString(R.string.exif_exposure_time_title);
					String expTime =  getExifAttribute(exifInterface, ExifInterface.TAG_EXPOSURE_TIME);
					float val, inv = 0.0f;
					try
					{
						val = Float.parseFloat(expTime);
						if (val < 1.0f)
						{
							inv = 1.0f / val;
						}
						if (inv < 10.0f)
						{
							inv = 0.0f;
						}
					}
					catch (Exception e)
					{
						//
						e.printStackTrace();
					}
                    msg = msg + " " + expTime + "s "; //(string)
					if (inv > 0.0f)
					{
						// 分数で表示する
						msg = msg + "(1/" + (int) inv + ")";
						//msg = msg + "(1/" + (((int) (inv / 10.0f)) * 10) + ")";
					}
					msg = msg + "\r\n";

                    // 絞り値
                    msg = msg + getString(R.string.exif_aperture_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_APERTURE) + "\r\n";  // (string)

                    // ISO感度
                    msg = msg + getString(R.string.exif_iso_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_ISO) + "\r\n";  // (string)

                    msg = msg + "\r\n";

                    // カメラの製造元
                    msg = msg + getString(R.string.exif_maker_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_MAKE) + "\r\n";

                    // カメラのモデル名
                    msg = msg + getString(R.string.exif_camera_title);
                    msg = msg + " " + getExifAttribute(exifInterface, ExifInterface.TAG_MODEL)+ "\r\n";  // (string)

                    // その他の情報
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_FLASH);      // フラッシュ (int)
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_ORIENTATION);  // 画像の向き (int)
                    //msg = msg + getExifAttribute(exifInterface, ExifInterface.TAG_WHITE_BALANCE);  // ホワイトバランス (int)
                }
                else
                {
                    msg = getString(R.string.download_control_get_information_failed);
                }
                return (msg);
            }

            private String getExifAttribute(ExifInterface attr, String tag)
            {
                String value = attr.getAttribute(tag);
                if (value == null)
                {
                    value = "";
                }
                return (value);
            }

            @Override
            public void run()
            {
                if (downloadDialog != null)
                {
                    downloadDialog.dismiss();
                }
                presentMessage(getString(R.string.download_control_get_information_title), message);
                System.gc();
            }
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
			Log.v(TAG, "startDownload() " + filename);
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

			final String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/" + getString(R.string.app_name2) + "/";
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
