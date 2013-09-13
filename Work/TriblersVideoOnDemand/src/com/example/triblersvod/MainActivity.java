package com.example.triblersvod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softwarrior.libtorrent.LibTorrent;

class TorrentContainer{
	public volatile String  Status = "";
	public volatile String  Name = "";
	public volatile String  SavePath = "";
	public volatile String  ContentName = "";
	public volatile int	Progress = 0;
	public volatile long TotalSize = 0;
	public volatile long ProgressSize = 0;
	public volatile int StorageMode = 2; //0-storage_mode_allocate, 1-storage_mode_sparse, 2-storage_mode_compact

	public TorrentContainer(String fileName, String contentName, int progress, long progressSize, long totalSize, int storageMode, String savePath){
    	Name=fileName; ContentName=contentName; Progress=progress;  ProgressSize=progressSize; TotalSize=totalSize; StorageMode=storageMode; SavePath = savePath; 
    }
}

public class MainActivity extends Activity {

	public static final LibTorrent libTorrent = new LibTorrent();
	private TextView debugText;
	private TextView debugText2;
	private TorrentContainer tc = null;
	private ProgressBar mProgress;
	private int mProgressStatus = 0;
	
	private Handler mHandler = new Handler();
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.debugText = (TextView) super.findViewById(R.id.debugText);
		this.debugText2 = (TextView) super.findViewById(R.id.debugText2);
		mProgress = (ProgressBar) findViewById(R.id.progressBar1);
		
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String sourceTorrent = getTorrentFile();
				File path = Environment.getExternalStorageDirectory();
			    File file = new File(path, "movie2");
			    MainActivity.this.debugText.setText(file.toString());
			    MainActivity.this.debugText2.setText(sourceTorrent);
			    
			    //set session for downloading (port 0 will use standard port)
			    libTorrent.SetSession(0, 0, 0);
			    //add a torrent to download
			    libTorrent.AddTorrent(file.toString(), sourceTorrent, 0);
			    
			    String contentName = libTorrent.GetTorrentName(sourceTorrent);
			    long totalSize = libTorrent.GetTorrentSize(sourceTorrent);
			    MainActivity.this.debugText.setText(contentName);
			    MainActivity.this.debugText2.setText(totalSize + "");
				tc = new TorrentContainer(sourceTorrent, contentName, 0, 0, totalSize, 0, file.toString());

				return null;
			}
			
			protected void onProgressUpdate(Integer[] values) {
				
			};

			@Override
			protected void onPostExecute(Void result) {
				Log.d("ASYNC", "onPostExecute");
				// Start lengthy operation in a background thread
				new Thread(new Runnable() {
					public void run() {
				        while (mProgressStatus < 100) {
				            mProgressStatus = doWork();
				
				            // Update the progress bar
				            mHandler.post(new Runnable() {
				                public void run() {
				                    mProgress.setProgress(mProgressStatus);
				                }
				            });
				        }
				    }
				}).start();
			}
			
		}.execute();
		
		
	}
	private int doWork() {
		long progress = 0;
		tc.Progress = libTorrent.GetTorrentProgress(tc.ContentName);
		progress = (tc.Progress / tc.TotalSize) *100;
	    
	    return (int) progress;
	}
	
	private String getTorrentFile() {
		  File f = new File(getCacheDir()+"/luna.torrent");
		  if (!f.exists()) try {

		    InputStream is = getAssets().open("luna.torrent");
		    int size = is.available();
		    byte[] buffer = new byte[size];
		    is.read(buffer);
		    is.close();


		    FileOutputStream fos = new FileOutputStream(f);
		    fos.write(buffer);
		    fos.close();
		  } catch (Exception e) { throw new RuntimeException(e); }

		  return f.getPath();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
