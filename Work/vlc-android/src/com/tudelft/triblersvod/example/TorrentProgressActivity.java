package com.tudelft.triblersvod.example;

import java.io.File;

import org.videolan.vlc.VLCApplication;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softwarrior.libtorrent.LibTorrent;

public class TorrentProgressActivity extends Activity {

	private final static String DEBUG_TAG = "TorrentProgressActivity";

	private final static String KEY_FILENAME = "key_filename";

	private TextView tStatus;
	private TextView tName;
	private TextView tSavePath;
	private TextView tContentName;
	private TextView tProgress;
	private ProgressBar tProgressBar;

	private static int LISTEN_PORT = 0;
	private static int LIMIT_UL = 0;
	private static int LIMIT_DL = 0;

	private boolean opened;

	public static Intent getStartIntent(Context packageContext, String fileName) {
		Intent i = new Intent(packageContext, TorrentProgressActivity.class);
		i.putExtra(KEY_FILENAME, fileName);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "onCreate");
		setContentView(R.layout.activity_torrentprogress);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		tStatus = (TextView) findViewById(R.id.torrentprogress_status);
		tName = (TextView) findViewById(R.id.torrentprogress_name);
		tSavePath = (TextView) findViewById(R.id.torrentprogress_savepath);
		tContentName = (TextView) findViewById(R.id.torrentprogress_contentname);
		tProgress = (TextView) findViewById(R.id.torrentprogress_progress);
		tProgressBar = (ProgressBar) findViewById(R.id.torrentprogress_progressbar);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new TorrentAdder()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new TorrentAdder().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(DEBUG_TAG, "onResume");
		libTorrent().ResumeSession();
		new UIUpdater().execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getTorrentFileName() {
		return getIntent().getExtras().getString(KEY_FILENAME).substring(7);
	}

	private String getContentName() {
		return libTorrent().GetTorrentName(getTorrentFileName());
	}

	private String getSavePath() {
		return new File(Environment.getExternalStorageDirectory(),
				"TorrentDownloadDir").getAbsolutePath();
	}

	private class TorrentAdder extends AsyncTask<Void, Void, Void> {

		private String torrentFileName = getTorrentFileName();

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(DEBUG_TAG, "TorrentAdder: doInBackground + "
					+ torrentFileName);
			libTorrent().SetSession(LISTEN_PORT, LIMIT_UL, LIMIT_DL);
			boolean sequential = libTorrent().AddTorrent(getSavePath(),
					torrentFileName, StorageModes.ALLOCATE.ordinal());
			Log.v(DEBUG_TAG, "Sequential: " + sequential);
			// libTorrent().ResumeTorrent(getContentName());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d(DEBUG_TAG, "TorrentAdder: adding done");
			tName.setText(torrentFileName);
			tSavePath.setText(getSavePath());
			tContentName.setText(libTorrent().GetTorrentName(torrentFileName));
		}
	}

	private class UIUpdater extends AsyncTask<Void, Void, Void> {

		private String contentName = getContentName();
		private int UPDATEINTERVAL = 300; // ms

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(DEBUG_TAG, "UIUpdater: doInBackground");
			while (!isFinishing()) {
				try {
					Thread.sleep(UPDATEINTERVAL);
				} catch (InterruptedException e) {
				}
				if (!opened)
					publishProgress();
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			String status = libTorrent().GetTorrentStatusText(contentName);
			int state = libTorrent().GetTorrentState(contentName);
			long totalSize = libTorrent().GetTorrentSize(getTorrentFileName());
			long progressSize = libTorrent()
					.GetTorrentProgressSize(contentName);

			tStatus.setText(status);
			String progressText;
			if (state != -1) {
				progressText = TorrentState.values()[state].getName();
				if (progressSize > 0) {
					progressText += " : " + progressSize + " MB / " + totalSize
							+ " MB";
				}
			} else {
				progressText = "please wait";
			}

			Log.v(DEBUG_TAG, "PROGRESS: " + progressText);
			tProgress.setText(progressText);
			tProgressBar.setIndeterminate(false);
			tProgressBar.setMax((int) totalSize);
			tProgressBar.setProgress((int) progressSize);

			// if ((progressSize >= totalSize || state == TorrentState.SEEDING
			// .ordinal()) && !opened) {
			// opened = true;
			// String files = libTorrent().GetTorrentFiles(contentName);
			// Log.d(DEBUG_TAG, "Should open " + contentName);
			// openFiles(files.split("\\r?\\n"), contentName);
			// }

			if (!opened &&
			// TorrentState.values()[state] == TorrentState.SEEDING
			// (progressSize * 10) > totalSize
					progressSize > 6) {
				opened = true;
				Log.v(DEBUG_TAG, "OPENING!!");
				String files = libTorrent().GetTorrentFiles(contentName);
				openFiles(files.split("\\r?\\n"), contentName, true);
				// File file = getLargestFile(getContentName());
				// VideoPlayerActivity.start(TorrentProgressActivity.this,
				// "file://" + file.getAbsolutePath(), false);
			}
		}
	}

	private LibTorrent libTorrent() {
		return ((VLCApplication) getApplication()).getLibTorrent();
	}

	public void openFiles(String[] files, String contentFile, boolean fromStart) {
		try {
			if (files.length == 1) {
				// Open file directly
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				String fileString = getSavePath() + "/" + files[0].trim();
				fileString = fileString.substring(0,
						fileString.lastIndexOf(" "));
				// replaceFirst("\\s+\\w+$","");
				Log.d(DEBUG_TAG, "FileString: " + fileString);
				File file = new File(fileString);
				MimeTypeMap mime = MimeTypeMap.getSingleton();
				String ext = file.getName().substring(
						file.getName().indexOf(".") + 1);
				String type = mime.getMimeTypeFromExtension(ext);
				intent.setDataAndType(Uri.fromFile(file), type);
				startActivity(intent);
			} else {
				libTorrent().PauseSession();
				// libTorrent().RemoveTorrent(getContentName());
				File file = getLargestFile(contentFile);
				final String path = "file://" + file.getAbsolutePath();
				Log.d(DEBUG_TAG, "Starting: " + path);
				VideoPlayerActivity.start(this, path, fromStart);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						libTorrent().ResumeSession();
					}
				}, 1000);
				// Log.d(DEBUG_TAG, "----updating: " + file.getParent() + " - "
				// + file.exists());
				// Intent update = new Intent(Intent.ACTION_MEDIA_MOUNTED,
				// Uri.parse("file://" + file.getParent()));
				// sendBroadcast(update);
				// MediaLibrary.getInstance(this).loadMediaItems(this, true);
				// Open download directory
				// Intent intent = new Intent();
				// intent.setAction(android.content.Intent.ACTION_VIEW);
				// Log.d(DEBUG_TAG, "path: " + movie.getAbsolutePath());
				// intent.setDataAndType(Uri.fromFile(movie), "video/*");
				// startActivity(intent);
			}
		} catch (Exception e) {
			Log.e(DEBUG_TAG, e.getLocalizedMessage());
		}
	}

	/*
	 * 99% of the time, largest file is the video you want to play: TODO
	 * subfolder support, folder.length()=0, so it won't go in to the folder
	 */
	private File getLargestFile(String contentFile) {

		File parentDir = new File("" + getSavePath() + "/" + contentFile);
		File[] dirFiles = null;
		if (parentDir.isDirectory()) {
			dirFiles = new File(parentDir.toString()).listFiles();
			int winner = -1;
			for (int i = 0; i < dirFiles.length; i++) {
				if (winner != -1) {
					if (dirFiles[i].length() > dirFiles[winner].length()) {
						winner = i;
					}
				} else {
					winner = i;
				}
			}
			return dirFiles[winner];
		} else {
			// TODO error: parentDir is not a dir
			return null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		libTorrent().AbortSession();
		// libTorrent().RemoveTorrent(getContentName());
	}

	public void watch(View button) {
		String contentName = getContentName();
		String files = libTorrent().GetTorrentFiles(contentName);
		openFiles(files.split("\\r?\\n"), contentName, false);
	}
}
