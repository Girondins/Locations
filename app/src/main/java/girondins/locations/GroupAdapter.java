package girondins.locations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Girondins on 09/10/15.
 */
public class GroupAdapter extends ArrayAdapter<Group>{
    private LayoutInflater inflater;
    private String name;
    private String onMembers;
    private String maxMembers = "/20";
    private String membersInGroup;
    private Controller cont;

    public GroupAdapter(Context context, Group[] groups, Controller cont) {
        super(context,R.layout.group_listview,groups);
        this.cont = cont;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView groupName;
        TextView viewMembers;
        Button viewCon;
        if(convertView==null){
            convertView = (LinearLayout)inflater.inflate(R.layout.group_listview,parent,false);
        }
        groupName = (TextView)convertView.findViewById(R.id.groupnameID);
        viewMembers = (TextView)convertView.findViewById(R.id.viewMembOnID);
        viewCon = (Button)convertView.findViewById(R.id.viewBtnID);

        getGroupInfo(this.getItem(position));
        groupName.setText(name);
        viewMembers.setText(onMembers + maxMembers);
        viewCon.setOnClickListener(new viewClicker(this.getItem(position).getAllMembers(),this.getItem(position).getGroupname()));


        return convertView;
    }

    public void getGroupInfo(Group group){
        name = group.getGroupname();
        onMembers = "" + group.memberSize();
    }

    private class viewClicker implements View.OnClickListener {
        private String allMembers;
        private String groupName;
        public viewClicker(String allMembers, String groupName){
            this.allMembers = allMembers;
            this.groupName = groupName;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(groupName);
            builder.setMessage(allMembers);
            builder.setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (onMembers.equals("20")) {
                        cont.showMsg("GROUP IS CURRENTLY FULL!");
                    } else {
                        cont.requestJoin(name);
                    }
                }
            });
            builder.setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
