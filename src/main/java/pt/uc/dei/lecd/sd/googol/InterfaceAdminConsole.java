package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface InterfaceAdminConsole extends Remote {
    public void downloaderNotification(String name) throws RemoteException;

    public void barrelNotification(String name) throws RemoteException;

    public void topSearchChangedNotification(Set<String> topSearches) throws RemoteException;

}
