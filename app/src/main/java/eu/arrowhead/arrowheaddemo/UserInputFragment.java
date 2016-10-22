package eu.arrowhead.arrowheaddemo;


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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UserInputFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "UserInputFragment";
    private SharedPreferences prefs;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_user_input, null);

        Spinner userIdSpinner = (Spinner) view.findViewById(R.id.user_id_spinner);
        ArrayAdapter<CharSequence> userIdAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.user_id_array, android.R.layout.simple_spinner_item);
        userIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userIdSpinner.setAdapter(userIdAdapter);
        userIdSpinner.setOnItemSelectedListener(this);

        Spinner evIdSpinner = (Spinner) view.findViewById(R.id.licence_plate_spinner);
        ArrayAdapter<CharSequence> evIdAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.ev_id_array, android.R.layout.simple_spinner_item);
        evIdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        evIdSpinner.setAdapter(evIdAdapter);
        evIdSpinner.setOnItemSelectedListener(this);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setTitle(R.string.user_input_dialog_title);

        //Set the Spinners to the saved value
        prefs = getActivity().getSharedPreferences("eu.arrowhead.arrowheaddemo", Context.MODE_PRIVATE);
        int userIdPos = prefs.getInt("userIdPos", -1);
        if(userIdPos != -1){
            userIdSpinner.setSelection(userIdPos);
        }
        int evIdPos = prefs.getInt("evIdPos", -1);
        if(evIdPos != -1){
            evIdSpinner.setSelection(evIdPos);
        }

        return builder.create();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.user_id_spinner)
        {
            prefs.edit().putInt("userIdPos", pos).apply();
        }
        else if(spinner.getId() == R.id.licence_plate_spinner)
        {
            prefs.edit().putString("evId", (String) spinner.getItemAtPosition(pos)).apply();
            prefs.edit().putInt("evIdPos", pos).apply();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
