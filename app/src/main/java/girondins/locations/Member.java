package girondins.locations;

import java.io.Serializable;

/**
 * Created by Girondins on 10/10/15.
 */
public class Member implements Serializable{
    private String name;
    private String longitude;
    private String latitude;

    public Member(String name){
        this.name = name;
    }

    public void setPosition(String longitude,String latitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLongitude(){
        return this.longitude;
    }

    public String getLatitude(){
        return this.latitude;
    }

    public String getName(){
        return this.name;
    }
}
