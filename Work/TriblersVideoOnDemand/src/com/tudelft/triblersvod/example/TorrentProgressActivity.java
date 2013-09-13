package com.tudelft.triblersvod.example;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softwarrior.libtorrent.LibTorrent;

public class TorrentProgressActivity extends Activity {

	private final static String DEBUG_TAG = "TorrentProgressActivity";

	private final static String KEY_FILENAME = "key_filename";

	// private TorrentContainer mTorrentContainer = null;

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
		setContentView(R.layout.activity_torrentprogress);

		tStatus = (TextView) findViewById(R.id.torrentprogress_status);
		tName = (TextView) findViewById(R.id.torrentprogress_name);
		tSavePath = (TextView) findViewById(R.id.torrentprogress_savepath);
		tContentName = (TextView) findViewById(R.id.torrentprogress_contentname);
		tProgress = (TextView) findViewById(R.id.torrentprogress_progress);
		tProgressBar = (ProgressBar) findViewById(R.id.torrentprogress_progressbar);

		new TorrentAdder().execute();
	}

	private String getTorrentFileName() {
		return getIntent().getExtras().getString(KEY_FILENAME);
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
			Log.d(DEBUG_TAG, "TorrentAdder: doInBackground");
			libTorrent().SetSession(LISTEN_PORT, LIMIT_UL, LIMIT_DL);
			libTorrent().AddTorrent(getSavePath(), torrentFileName,
					StorageModes.ALLOCATE.ordinal());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.d(DEBUG_TAG, "TorrentAdder: onPostExecute");
			tName.setText(torrentFileName);
			tSavePath.setText(getSavePath());
			tContentName.setText(libTorrent().GetTorrentName(torrentFileName));

			new UIUpdater().execute();
		}
	}

	private class UIUpdater extends AsyncTask<Void, Void, Void> {

		private String contentName = getContentName();

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(DEBUG_TAG, "UIUpdater: doInbackground");

			while (!isCancelled()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
				}

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
			// long progress = libTorrent().GetTorrentProgress(contentName);

			tStatus.setText(status);

			String progressText = TorrentState.values()[state].getName();
			if (progressSize > 0) {
				progressText += " <> "
						+ /* "(" + progress + ") " + */progressSize + " MB / "
						+ totalSize + " MB";
			}

			tProgress.setText(progressText);
			tProgressBar.setIndeterminate(false);
			tProgressBar.setMax((int) totalSize);
			tProgressBar.setProgress((int) progressSize);

			if (state == TorrentState.SEEDING.ordinal() && !opened) {
				opened = true;
				String files = libTorrent().GetTorrentFiles(contentName);
				Log.e(DEBUG_TAG, "SHOULD OPEN!!!");
				openFiles(files.split("\\r?\\n"), contentName);
			}
		}
	}

	private LibTorrent libTorrent() {
		return ((TorrentVODApplication) getApplication()).getLibTorrent();
	}

	public void openFiles(String[] files, String contentFile) {
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
				// Open download directory
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				File file = new File(getSavePath(), contentFile);
				Log.d(DEBUG_TAG, "path: " + file.getAbsolutePath());
				intent.setData(Uri.fromFile(file));
				startActivity(intent);
			}
		} catch (Exception e) {
			Log.e(DEBUG_TAG, e.getLocalizedMessage());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		libTorrent().AbortSession();
	}
}
