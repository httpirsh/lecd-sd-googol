package pt.uc.dei.lecd.sd.googol;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class AdminConsole extends UnicastRemoteObject implements InterfaceAdminConsole {

    private ArrayList<String> downloaders;
    private GoogolRegistry registry;

    public AdminConsole() throws RemoteException {
        downloaders = new ArrayList<>();
    }

    public void connect(String host, int port) throws RemoteException, AlreadyBoundException {
        registry = new GoogolRegistry(host, port);
        registry.bind(this);
    }

    public String getBarrelsNames() {
        return null;
    }

    public List<String> getDownloadersNames() {
        return downloaders;
    }

    public String getBarrelPages(String string) {
        return null;
    }

    @Override
    public void onDownloaderIndex(String downloaderName, String url) throws RemoteException {
        if (!downloaders.contains(downloaderName))
            downloaders.add(downloaderName);
    }

}
