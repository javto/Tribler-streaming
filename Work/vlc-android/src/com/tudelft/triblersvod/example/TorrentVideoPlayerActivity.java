package com.tudelft.triblersvod.example;

import org.videolan.vlc.AudioServiceController;
import org.videolan.vlc.gui.video.VideoPlayerActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class TorrentVideoPlayerActivity extends VideoPlayerActivity {

	@Override
	@TargetApi(11)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TorrentVideoPlayerActivity", "onCreate");

		if (getIntent().getAction().equals(
				TorrentVideoPlayerActivity.PLAY_FROM_VIDEOGRID)) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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

	public static void startT(Context context, String location, String title,
			int position, Boolean dontParse, Boolean fromStart) {
		Intent intent = new Intent(context, TorrentVideoPlayerActivity.class);
		intent.setAction(TorrentVideoPlayerActivity.PLAY_FROM_VIDEOGRID);
		intent.putExtra("itemLocation", location);
		intent.putExtra("itemTitle", title);
		intent.putExtra("dontParse", dontParse);
		intent.putExtra("fromStart", fromStart);
		intent.putExtra("itemPosition", position);

		if (dontParse)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		else {
			// Stop the currently running audio
			AudioServiceController asc = AudioServiceController.getInstance();
			asc.stop();
		}

		context.startActivity(intent);
	}
}
