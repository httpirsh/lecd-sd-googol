package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface InterfaceSearchModule extends Remote {
	
	List<Page> search(String terms) throws RemoteException;
	
	void indexNewURL(String url)  throws RemoteException;
	
	String ping() throws RemoteException;

	List<String> getTopSearches() throws RemoteException;

	void listPages (String terms) throws RemoteException;

}
