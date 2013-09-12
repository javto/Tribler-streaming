package com.example.fibonacci;

import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FibonacciActivity extends Activity implements OnClickListener {

	private EditText input;
	private RadioGroup type;
	private TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fibonacci);
		this.input = (EditText) super.findViewById(R.id.input);
		this.type = (RadioGroup) super.findViewById(R.id.type);
		this.output = (TextView) super.findViewById(R.id.output);
		Button button = (Button) super.findViewById(R.id.button1);
		button.setOnClickListener(this);

		// InputStream databaseInputStream =
		// getResources().openRawResource(R.raw.luna).;
		//Uri torrent = Uri.parse("android.resource://com.example.fibonacci/raw/luna.torrent");
		//FibLib.javamain(1, torrent.getPath().toCharArray());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fibonacci, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		String s = this.input.getText().toString();
		if (TextUtils.isEmpty(s)) {
			return;
		}

		final ProgressDialog dialog = ProgressDialog.show(this, "", "calculating...", true);
		final long n = Long.parseLong(s);
		final Locale locale = super.getResources().getConfiguration().locale;
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				long result = 0;
				long t = SystemClock.uptimeMillis();
				switch (FibonacciActivity.this.type.getCheckedRadioButtonId()) {
				case R.id.radio0:
					result = FibLib.fibJR(n);
					break;
				case R.id.radio1:
					result = FibLib.javamain(n, "lol");
					break;
				}
				t = SystemClock.uptimeMillis() - t;
				return String.format(locale, "fib(%d)=%d in %d ms", n, result, t);
			}

			@Override
			protected void onPostExecute(String result) {
				dialog.dismiss();
				FibonacciActivity.this.output.setText(result);
			}

		}.execute();


	}

}
