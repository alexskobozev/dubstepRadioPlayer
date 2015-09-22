package com.wishnewjam.dubstepfm;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackInfoDialogFragment extends DialogFragment {

    public static final String TAG_TRACKNAME = "trackname";
    public static final String TAG_LINK = "link";


    public static DialogFragment newInstance(String trackname,
                                             String link) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG_TRACKNAME, trackname);
        bundle.putString(TAG_LINK, link);
        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_info, container, false);

        TextView tv = (TextView) v.findViewById(R.id.tv_trackname);

        tv.setText(getArguments().getString(TAG_TRACKNAME));

        return v;
    }

}
