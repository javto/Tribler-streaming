package com.tudelft.triblersvod.example;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class SplashScreenFragment extends Fragment {

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.splashscreen, container, false);
        mProgressBar = (ProgressBar) view.findViewById (R.id.progress_bar);
        return view;
    }

    /**
     * Sets the progress of the ProgressBar
     *
     * @param progress int the new progress between 0 and 100
     */
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }
}
