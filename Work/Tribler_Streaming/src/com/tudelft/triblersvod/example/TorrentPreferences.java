package com.tudelft.triblersvod.example;

import java.io.File;
import java.io.FileNotFoundException;

import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class TorrentPreferences extends PreferenceFragment {
	public final static String NAME = "TorrentPreferences";
	public final static String KEY_LIBTORRENT_DEBUG = "libtorrent_debug";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.torrent_settings);

		Preference button = (Preference) findPreference("del_cache");
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				try {
					deleteRecursive(getCacheFolder());
					getActivity().finish();
					Toast.makeText(getActivity(), "Cache deleted.",
							Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					getActivity().finish();
					Toast.makeText(getActivity(), "Cache was already deleted.",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}

	public static boolean deleteRecursive(File path)
			throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	private File getCacheFolder() {
		return new File(Environment.getExternalStorageDirectory(),
				"TriblerStreamingCache");
		// return new File(new File(Environment.getExternalStorageDirectory(),
		// "Download"), "TriblerStreamingCache");
	}
}