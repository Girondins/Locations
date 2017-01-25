package girondins.locations;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Girondins on 09/10/15.
 */
public class Group implements Serializable{
    private String groupname;
    private LinkedList<Member> members = new LinkedList<Member>();
    private String id;

    public Group(String groupname){
        this.groupname = groupname;
    }

    public void addMember(Member member){
        members.add(member);
    }

    public int memberSize(){
        return members.size();
    }

    public Member getMemberIndex(int index){
        return members.get(index);
    }

    public String getGroupname(){
        return this.groupname;
    }

    public String getAllMembers(){
        String membersInGroup = "Members Online:" + "\n";
        for(int i = 0; i<members.size(); i++){
            membersInGroup += members.get(i).getName() + "\n";
        }
        return membersInGroup;
    }
    public String getID(){
        return this.id;
    }
    public void setID(String id){
        this.id = id;
    }

    public boolean contains(Object obj){
        if(members.contains(obj)){
            return true;
        }
        return false;
    }
}
