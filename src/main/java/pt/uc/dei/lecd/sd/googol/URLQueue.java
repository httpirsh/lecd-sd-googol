package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;

public class URLQueue implements Remote {
    private final String name;

    public URLQueue(String name) {
        this.name = name;
    }
}
