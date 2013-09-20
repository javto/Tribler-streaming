package com.tudelft.triblersvod.example;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		String path = cleanUriPath(getIntent().getDataString());
		if (!(path == null || path.equals("")))
			tryStartingTorrent(path);
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
			String fileName = cleanUriPath(data.getDataString());
			tryStartingTorrent(fileName);
		}
	}

	private String cleanUriPath(String uri) {
		if (uri == null)
			return "";
		else
			return StringEscapeUtils.unescapeHtml4(uri);
	}

	private void tryStartingTorrent(String fileName) {
		Log.d(DEBUG_TAG, "Trying to start: " + fileName);
		if (fileName.endsWith(".torrent"))
			startActivity(TorrentProgressActivity
					.getStartIntent(this, fileName));
		else
			Toast.makeText(this, "Error, filename did not end with '.torrent'",
					Toast.LENGTH_LONG).show();
	}

}
