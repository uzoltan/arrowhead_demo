package eu.arrowhead.arrowheaddemo;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ChargingResponseFragment extends DialogFragment {

    public static final String TAG = "ChargingResponseFragment";

    public static ChargingResponseFragment newInstance(long chargingReqId, String status) {
        ChargingResponseFragment fragment = new ChargingResponseFragment();
        Bundle args = new Bundle();
        args.putLong("chargingReqId", chargingReqId);
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_charging_response, null);

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        String status = getArguments().getString("status");
        long chargingReqId = getArguments().getLong("chargingReqId");
        TextView statusText = (TextView) view.findViewById(R.id.status_textview);
        TextView requestIdText = (TextView) view.findViewById(R.id.request_id_textview);
        statusText.setText("Status: " + status);
        requestIdText.setText("Charging request ID:\n" + chargingReqId);

        return builder.create();
    }


}
