package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfaceMonitor extends Remote {
    public void downloaderNotification(String name) throws RemoteException;

    public void barrelNotification(String name) throws RemoteException;

    public void topSearchChangedNotification(List<String> newTopSearches) throws RemoteException;

    public Object getDownloadersNames() throws RemoteException;

    public Object getBarrelsNames() throws RemoteException;

    public Object getTopSearches() throws RemoteException;

}
