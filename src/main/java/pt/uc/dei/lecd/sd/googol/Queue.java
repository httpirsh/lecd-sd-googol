package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Queue extends Remote {

    public void enqueue(Object element) throws RemoteException;

    public Object dequeue() throws RemoteException;

    public int size() throws RemoteException ;

    public boolean isEmpty() throws RemoteException;
}
