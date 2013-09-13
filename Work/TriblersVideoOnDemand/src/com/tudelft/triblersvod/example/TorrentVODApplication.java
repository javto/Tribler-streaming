package com.tudelft.triblersvod.example;

import com.softwarrior.libtorrent.LibTorrent;

import android.app.Application;

public class TorrentVODApplication extends Application {

	private LibTorrent libTorrent = null;

	public LibTorrent getLibTorrent() {
		if (this.libTorrent == null) 
			this.libTorrent = new LibTorrent();
		return this.libTorrent;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.libTorrent.AbortSession();
	}
}
