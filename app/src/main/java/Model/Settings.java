package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vhernanm on 9/13/17.
 */

public class Settings {
    private static Settings instance = null;
    private String ipAddress = "172.20.10.3:4321";

    protected Settings() {
        // Exists only to defeat instantiation.
    }

    public static Settings getInstance (){
        if (instance == null){
            instance = new Settings();
        }
        return instance;

    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
