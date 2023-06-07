package pt.uc.dei.lecd.sd.googol;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Monitor extends UnicastRemoteObject implements InterfaceMonitor {

    private HashSet<String> downloaders;
    private GoogolRegistry registry;
    private HashSet<String> barrels;
    private List<String> topSearches;

    public Monitor() throws RemoteException {
        downloaders = new HashSet<>();
        barrels = new HashSet<>();
        topSearches = new ArrayList<>();
    }

    public void connect(String host, int port) throws RemoteException, AlreadyBoundException {
        registry = new GoogolRegistry(host, port);
        registry.bind(this);
        loadRegistry();
    }

    public void disconnect() throws AccessException, RemoteException, NotBoundException {
        registry.unbind(this);
    }

    public Set<String> getBarrelsNames() {
        return barrels;
    }

    public Set<String> getDownloadersNames() {
        return downloaders;
    }

    @Override
    public void downloaderNotification(String name) throws RemoteException {
        downloaders.clear();
        downloaders.addAll(registry.getListOfDownloaders());
    }

    @Override
    public void barrelNotification(String name) throws RemoteException {
        barrels.clear();
        barrels.addAll(registry.getListOfBarrels());
    }

    @Override
    public void topSearchChangedNotification(List<String> topSearches) {
        this.topSearches = topSearches;
    }

    public List<String> getTopSearches() {
        return topSearches;
    }

    private void loadRegistry() throws AccessException, RemoteException {
        downloaders.addAll(registry.getListOfDownloaders());
        barrels.addAll(registry.getListOfBarrels());
    }

}
