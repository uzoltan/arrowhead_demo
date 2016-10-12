package eu.arrowhead.arrowheaddemo;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UserInputDialogFragment extends DialogFragment {

    public static final String TAG = "UserInputDialogFragment";

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface UserIdDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    UserIdDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_user_input, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(UserInputDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(UserInputDialogFragment.this);
                    }
                });
        builder.setTitle(R.string.user_input_dialog_title);

        //Fill in the EditTexts with saved values
        SharedPreferences prefs = getActivity().getSharedPreferences("eu.arrowhead.arrowheaddemo", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if(userId != null && !userId.isEmpty()){
            EditText userIdField = (EditText) view.findViewById(R.id.user_id_edittext);
            userIdField.setText(userId);
            int position = userId.length();
            userIdField.setSelection(position);
        }
        String EVId = prefs.getString("EVId", null);
        if(EVId != null && !EVId.isEmpty()){
            EditText EVIdField = (EditText) view.findViewById(R.id.licence_plate_edittext);
            EVIdField.setText(EVId);
        }

        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the UserIdDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the UserIdDialogListener so we can send events to the host
            mListener = (UserIdDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement UserIdDialogListener");
        }
    }
}
