package com.tudelft.triblersvod.example;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.softwarrior.libtorrent.LibTorrent;

public class TorrentProgressActivity extends Activity {

	private final static String DEBUG_TAG = "TorrentProgressActivity";

	private final static String KEY_FILENAME = "key_filename";

	// private TorrentContainer mTorrentContainer = null;

	private View tLoadingBox;
	private View tContentbox;

	private TextView tStatus;
	private TextView tName;
	private TextView tSavePath;
	private TextView tContentName;
	private TextView tProgress;
	private ProgressBar tProgressBar;

	private static int LISTEN_PORT = 0;
	private static int LIMIT_UL = 0;
	private static int LIMIT_DL = 0;

	public static Intent getStartIntent(Context packageContext, String fileName) {
		Intent i = new Intent(packageContext, TorrentProgressActivity.class);
		i.putExtra(KEY_FILENAME, fileName);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_torrentprogress);

		tLoadingBox = findViewById(R.id.torrentprogress_loadingbox);
		tLoadingBox.setVisibility(View.GONE);
		tContentbox = findViewById(R.id.torrentprogress_contentbox);
		tContentbox.setVisibility(View.VISIBLE);

		tStatus = (TextView) findViewById(R.id.torrentprogress_status);
		tName = (TextView) findViewById(R.id.torrentprogress_name);
		tSavePath = (TextView) findViewById(R.id.torrentprogress_savepath);
		tContentName = (TextView) findViewById(R.id.torrentprogress_contentname);
		tProgress = (TextView) findViewById(R.id.torrentprogress_progress);
		tProgressBar = (ProgressBar) findViewById(R.id.torrentprogress_progressbar);

		libTorrent().SetSession(LISTEN_PORT, LIMIT_UL, LIMIT_DL);

		// Check if savedInstanceState == null;
		// if (savedInstanceState == null)
		// new TorrentAdder().execute();
		// else {
		// new UIUpdater().execute();
		// }
		// Toast.makeText(this, "check if progressbar restores itself",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
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

			for (int i = 0; i < 300; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				publishProgress();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			Log.d(DEBUG_TAG, "UIUpdater: onProgressUpdate");
			String status = libTorrent().GetTorrentStatusText(contentName);
			long totalSize = libTorrent().GetTorrentSize(getTorrentFileName());
			long progressSize = libTorrent()
					.GetTorrentProgressSize(contentName);
			// long progress = libTorrent().GetTorrentProgress(contentName);

			tStatus.setText(status);
			tProgress.setText(/* "(" + progress + ") " + */progressSize
					+ " MB / " + totalSize + " MB");
			tProgressBar.setIndeterminate(false);
			tProgressBar.setMax((int) totalSize);
			tProgressBar.setProgress((int) progressSize);

			if (status.length() > 0) {
				tLoadingBox.setVisibility(View.GONE);
				tContentbox.setVisibility(View.VISIBLE);
			}
		}
	}

	// new AsyncTask<Void, Integer, Void>() {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// String sourceTorrent = getTorrentFile();
	// File path = Environment.getExternalStorageDirectory();
	// File file = new File(path, "movie2");
	// TorrentProgressActivity.this.tName.setText(sourceTorrent);
	// TorrentProgressActivity.this.tSavePath.setText(file.toString());
	//
	// // set session for downloading (port 0 will use standard port)
	// libTorrent.SetSession(0, 0, 0);
	// // add a torrent to download
	// libTorrent.AddTorrent(file.toString(), sourceTorrent, 0);
	//
	// String contentName = libTorrent.GetTorrentName(sourceTorrent);
	// long totalSize = libTorrent.GetTorrentSize(sourceTorrent);
	// TorrentProgressActivity.this.tContentName.setText(contentName);
	// TorrentProgressActivity.this.tProgress.setText(totalSize + "");
	// TorrentContainer tc = new TorrentContainer(sourceTorrent,
	// contentName, 0, 0, totalSize, 0, file.toString());
	//
	// return null;
	// }
	//
	// protected void onProgressUpdate(Integer[] values) {
	//
	// };
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// Log.d("ASYNC", "onPostExecute");
	// // Start lengthy operation in a background thread
	// // new Thread(new Runnable() {
	// // public void run() {
	// // while (mProgressStatus < 100) {
	// // mProgressStatus = doWork();
	// //
	// // // Update the progress bar
	// // mHandler.post(new Runnable() {
	// // public void run() {
	// // mProgress.setProgress(mProgressStatus);
	// // }
	// // });
	// // }
	// // }
	// // }).start();
	// }
	//
	// }.execute();
	//
	// }
	//
	// private String getTorrentFile() {
	// File f = new File(getCacheDir() + "/luna.torrent");
	// if (!f.exists())
	// try {
	//
	// InputStream is = getAssets().open("luna.torrent");
	// int size = is.available();
	// byte[] buffer = new byte[size];
	// is.read(buffer);
	// is.close();
	//
	// FileOutputStream fos = new FileOutputStream(f);
	// fos.write(buffer);
	// fos.close();
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	//
	// return f.getPath();
	// }

	// private LibTorrent libTorrent = null;
	// private static int LISTEN_PORT = 0;
	// private static int LIMIT_UL = 0;
	// private static int LIMIT_DL = 0;
	//
	// private LibTorrent getLibTorrent() {
	// if (this.libTorrent == null) {
	// this.libTorrent = new LibTorrent();
	// this.libTorrent.SetSession(LISTEN_PORT, LIMIT_UL, LIMIT_DL);
	// }
	// return this.libTorrent;
	// }

	private LibTorrent libTorrent() {
		return ((TorrentVODApplication) getApplication()).getLibTorrent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		libTorrent().AbortSession();
	}

	// private int doWork() {
	// long progress = 0;
	// tc.Progress = libTorrent.GetTorrentProgress(tc.ContentName);
	// progress = (tc.Progress / tc.TotalSize) * 100;
	//
	// return (int) progress;
	// }
}
