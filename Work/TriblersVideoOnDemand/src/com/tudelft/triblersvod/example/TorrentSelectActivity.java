package com.tudelft.triblersvod.example;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TorrentSelectActivity extends Activity {

	private final static String DEBUG_TAG = "TorrentSelectActivity";
	private int TORRENT_REQ_CODE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_torrentselect);
	}

	public void selectTorrent(View button) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, TORRENT_REQ_CODE);
		// TODO Catch no file-explorer present exception
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && requestCode == TORRENT_REQ_CODE) {
			String fileName = data.getDataString().replaceFirst("file://", "");
			fileName = Html.fromHtml(fileName).toString();

			// File file = new File(fileName);
			// file.renameTo(file.)
			Log.d(DEBUG_TAG, "Got file: " + fileName);
			if (fileName.endsWith(".torrent"))
				startActivity(TorrentProgressActivity.getStartIntent(this,
						fileName));
			else
				Toast.makeText(this,
						"Error, filename did not end with '.torrent'",
						Toast.LENGTH_LONG).show();
		}
	}
}
