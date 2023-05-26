package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceAdminConsole extends Remote {
    public void onDownloaderIndex(String downloaderName, String url) throws RemoteException;
}
