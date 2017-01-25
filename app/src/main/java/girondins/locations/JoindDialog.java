package girondins.locations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Girondins on 19/12/15.
 */
public class JoindDialog extends DialogFragment{
    private LayoutInflater inflater;
    private View v;
    private Controller cont;
    private ListView groupList;
    private Group[] groups;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.joindialog, null);
        groups = (Group[]) getArguments().getSerializable("group");
        groupList = (ListView) v.findViewById(R.id.groupListID);
        groupList.setAdapter(new GroupAdapter(getActivity(), groups, cont));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle(R.string.join);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
