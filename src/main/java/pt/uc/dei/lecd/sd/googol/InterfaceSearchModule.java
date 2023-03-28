package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceSearchModule extends Remote{
	
	void searchResults(String terms) throws RemoteException;
	
	void indexNewURL(String url)  throws RemoteException;
	
	void listPages (String terms);
}
