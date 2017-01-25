package girondins.locations;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Girondins on 05/10/15.
 */
public class Controller {
    private Activity activity;
    private String user;
    private boolean connected = false, bound = false;
    private final String IP = "195.178.232.7";
    private final int PORT = 7117;
    private TCPConnect tcpConnect;
    private ConnectionListener conList;
    private JSONObject task;
    private Group[] availGroups;
    private ArrayList<Group> gettingGroups = new ArrayList<>();
    private Group registredGroup;
    private Bundle savedState;
    private LocationActivity locationActivity;
    private String id;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Controller(Activity activity, Bundle savedState) {
        this.activity = activity;
        this.savedState = savedState;
        //   connectService(this.savedState);
        conn();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Controller(Activity activity,String user, Bundle savedState) {
        this.activity = activity;
        this.user = user;
        this.savedState = savedState;
        conn();
    }




    private void conn(){
        tcpConnect = new TCPConnect(IP,PORT);
        tcpConnect.start();
        conList = new ConnectionListener();
        conList.start();
        tcpConnect.enableConnect();
    }

    public void setMapActivity(LocationActivity locationActivity) {
        this.locationActivity = locationActivity;
    }

    public void getGroups() {

        task = new JSONObject();
        try {
            task.put("type", "groups");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tcpConnect.task(task);
    }


    public void requestJoin(String groupName) {

        if(id != null){
            leaveGroup();
            Log.d("LEAVING","LEFT" + id);
        }

        task = new JSONObject();
        try {
            task.put("type", "register");
            task.put("group", groupName);
            task.put("member", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        registredGroup = new Group(groupName);
        tcpConnect.task(task);
    }

    public void leaveGroup() {
        task = new JSONObject();
        try {
            task.put("type", "unregister");
            task.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tcpConnect.task(task);
    }

    public void getGroupMembers(Group group) {
        task = new JSONObject();
        try {
            task.put("type", "members");
            task.put("group", group.getGroupname());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tcpConnect.task(task);

    }

    public void sendPosition(LatLng position) {
        task = new JSONObject();
        try {
            task.put("type", "location");
            task.put("id", id);
            task.put("longitude", ""+position.longitude);
            task.put("latitude", ""+position.latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tcpConnect.task(task);
    }


    public void onSavedState(Bundle outState) {
        outState.putBoolean("CONNECTED", connected);
    }


    private class ConnectionListener extends Thread {
        Object object;
        JSONObject jsonObj;
        JSONArray jsonAry;

        public void run() {

            while (conList != null) {
                try {
                    object = tcpConnect.response();
                    connected = true;
                    Log.d("Controller", "RECIEVED");
                    if (object instanceof JSONObject) {
                        jsonObj = (JSONObject) object;
                        handleJSON(jsonObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    conList = null;
                }
            }
        }

        public void stopListen() {
            interrupt();
            conList = null;

        }

        private void handleJSON(JSONObject jsonObj) throws JSONException {
            String type;
            type = (String) jsonObj.get("type");
            Log.d("task", type);
            Member inMember;

            if (type.equals("groups")) {
                JSONArray groups = (JSONArray) jsonObj.get("groups");
                JSONObject current;
                Group currentGroup;
                gettingGroups = new ArrayList<>();
                for (int i = 0; i < groups.length(); i++) {
                    current = groups.getJSONObject(i);
                    currentGroup = new Group((String) current.get("group"));
                    gettingGroups.add(currentGroup);

                }
                for (int j = 0; j < gettingGroups.size(); j++) {
                    getGroupMembers(gettingGroups.get(j));
                }
                startGroups();
            }
            if (type.equals("members")) {
                JSONObject member;
                String group = (String) jsonObj.get("group");
                if (registredGroup != null) {
                    if (registredGroup.getGroupname().equals(group)) {
                        JSONArray members = (JSONArray) jsonObj.get("members");
                        Log.d("MEMBERSREGIST", members.toString());
                        for (int y = 0; y < members.length(); y++) {
                            member = members.getJSONObject(y);
                            inMember = new Member((String) member.get("member"));
                            Log.d("RegistredAdding", inMember + " to " + registredGroup.getGroupname());
                            if(!registredGroup.contains(inMember)) {
                                registredGroup.addMember(inMember);
                            }
                        }
                    }
                }
                for (int i = 0; i < gettingGroups.size(); i++) {
                    if (gettingGroups.get(i).getGroupname().equals(group)) {
                        JSONArray members = (JSONArray) jsonObj.get("members");
                        for (int j = 0; j < members.length(); j++) {
                            member = members.getJSONObject(j);
                            inMember = new Member((String) member.get("member"));
                            if(!gettingGroups.get(i).contains(inMember)) {
                                gettingGroups.get(i).addMember(inMember);
                            }
                        }
                    }
                }
            }

            if (type.equals("register")) {
                String id = (String) jsonObj.get("id");
                getGroupMembers(registredGroup);
                registredGroup.setID(id);
                Log.d("RegieterU", id);
                setID(id);
                locationActivity = (LocationActivity) activity;
                ViewGroup show = new ViewGroup(registredGroup.getGroupname(),locationActivity);
                ShowMessage message = new ShowMessage(activity.getResources().getString(R.string.joined)+ " " + registredGroup.getGroupname(),locationActivity);
                activity.runOnUiThread(show);
                activity.runOnUiThread(message);


            }

            if(type.equals("unregister")){
                String id = (String) jsonObj.get("id");
                locationActivity = (LocationActivity) activity;
                ShowMessage message = new ShowMessage(activity.getResources().getString(R.string.left) + " " + id,locationActivity);
                ClearMarker clr = new ClearMarker(locationActivity);
                activity.runOnUiThread(message);
                activity.runOnUiThread(clr);
            }

            if (type.equals("locations")) {
                JSONArray locations = (JSONArray) jsonObj.get("location");
                JSONObject location;
                for (int i = 0; i < registredGroup.memberSize(); i++) {
                    for (int j = 0; j < locations.length(); j++) {
                        location = locations.getJSONObject(j);
                        Log.d("Cont,locatleng", location.getString("member"));
                        Log.d("Cont,locAtLEng", registredGroup.getGroupname());
                        if (registredGroup.getMemberIndex(i).getName().equals(location.getString("member"))) {
                            registredGroup.getMemberIndex(i).setPosition(location.getString("longitude"), location.getString("latitude"));
                        }
                    }
                }
                activity.runOnUiThread(new updatePositions(locationActivity, registredGroup));
            }


            if(type.equals("exception")){
                String exception = (String) jsonObj.get("message");

            }
        }
    }


    private class updatePositions implements Runnable {
        private LocationActivity locAct;
        private Group activeGroup;

        private updatePositions(LocationActivity locAct, Group activeGroup) {
            this.locAct = locAct;
            this.activeGroup = activeGroup;
        }

        @Override
        public void run() {
            Log.d("UPPPP", activeGroup.getGroupname());
            for(int i = 0 ; i<activeGroup.memberSize(); i++){
                Log.d("locationmember",activeGroup.getMemberIndex(i).getName());
            }
                locAct.addMarkers(activeGroup);
        }

    }



    private class ViewGroup implements Runnable {
        private LocationActivity locAct;
        private String grpName;

        private ViewGroup(String grpName, LocationActivity locAct) {
            this.locAct = locAct;
            this.grpName = grpName;
        }

        @Override
        public void run() {
            locAct.showGroup(grpName);
        }

    }


    public void setID(String id){
        this.id = id;
    }

    public void startGroups(){

            availGroups = new Group[gettingGroups.size()];
            for (int i = 0; i < availGroups.length; i++) {
                availGroups[i] = gettingGroups.get(i);
            }
        joinDialog();
    }

    public void start(String user){
        this.user = user;
        Intent locationAct = new Intent(activity,LocationActivity.class);
        locationAct.putExtra("groups", gettingGroups);
        locationAct.putExtra("user",user);
        activity.startActivity(locationAct);
    }

    public void joinDialog(){
        Bundle avail = new Bundle();
        avail.putSerializable("group",availGroups);
        JoindDialog join = new JoindDialog();
        join.setArguments(avail);
        join.setController(this);
        join.show(activity.getFragmentManager(), "JoinDialog");
    }

    public void showMsg(String message){
        locationActivity = (LocationActivity) activity;
        ShowMessage msg = new ShowMessage(message,locationActivity);
        activity.runOnUiThread(msg);
    }

    private class ShowMessage implements Runnable{
        private String msg;
        private LocationActivity locAct;


        public ShowMessage(String msg, LocationActivity locAct){
            this.msg = msg;
            this.locAct = locAct;

        }

        @Override
        public void run() {
                locAct.showMessage(msg);
        }
    }

    private class ClearMarker implements Runnable{
        private LocationActivity locAct;


        public ClearMarker(LocationActivity locAct){
            this.locAct = locAct;

        }

        @Override
        public void run() {
            locAct.clearMarker();
        }
    }

}