package com.wishnewjam.dubstepfm;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class ChooseBitrateDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_bitrate)
                .setItems(R.array.bitrates, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        changeBitrate(which);
                    }
                });
        return builder.create();
    }

    private void changeBitrate(int which) {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_24).apply();
        switch (which) {
            case 0:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_24).apply();
                dismiss();
                break;
            case 1:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_64).apply();
                dismiss();
                break;
            case 2:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_128).apply();
                dismiss();
                break;
            case 3:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit().putString(MainService.SP_KEY_BITRATE, Links.LINK_256).apply();
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
