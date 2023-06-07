package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface InterfaceMonitor extends Remote {
    public void downloaderNotification(String name) throws RemoteException;

    public void barrelNotification(String name) throws RemoteException;

    public void topSearchChangedNotification(List<String> newTopSearches) throws RemoteException;

    public Set<String> getDownloadersNames() throws RemoteException;

    public Set<String> getBarrelsNames() throws RemoteException;

    public List<String> getTopSearches() throws RemoteException;

}
