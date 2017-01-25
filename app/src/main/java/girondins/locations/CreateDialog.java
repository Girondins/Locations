package girondins.locations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.Serializable;

/**
 * Created by Girondins on 10/10/15.
 */
public class CreateDialog extends DialogFragment{
    private LayoutInflater inflater;
    private View v;
    private Controller cont;
    private EditText groupName;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.createdialog, null);
        groupName = (EditText) v.findViewById(R.id.createGrID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.create);
        builder.setPositiveButton(R.string.conCreate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cont.requestJoin(groupName.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    public void setController(Controller cont){
        this.cont = cont;
    }
}
