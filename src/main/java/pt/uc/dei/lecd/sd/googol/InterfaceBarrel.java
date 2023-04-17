package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Essa interface é usada para permitir que outras classes, que podem estar em
 * diferentes máquinas virtuais, possam acessar o objeto IndexStorageBarrel
 * remotamente através do Java RMI.
 * Sem a interface, essas classes não seriam capazes de interagir com o objeto,
 * já que a interface especifica quais métodos e argumentos são necessários para
 * acessá-lo. Em resumo, a interface InterfaceBarrel define um conjunto de
 * métodos que a classe
 * IndexStorageBarrel deve implementar e que outras classes podem usar para
 * interagir com o objeto IndexStorageBarrel remotamente através do Java RMI.
 */
public interface InterfaceBarrel extends Remote {

	void addToIndex(String term, String url) throws RemoteException;

	void addPageTitle(String url, String title) throws RemoteException;

	void addPageContents(String url, String text) throws RemoteException;

	void addPageLinks(String url, ArrayList<String> links) throws RemoteException;

	void urlConnections(String url) throws RemoteException;

	HashSet<String> searchTerm(String term) throws RemoteException;

	HashSet<String> searchTerms(String terms) throws RemoteException;

	HashSet<String> sortImp(HashSet<String> results) throws RemoteException;

	String getPageTitle(String url) throws RemoteException;

	String getShortQuote(String url) throws RemoteException;

	String ping() throws RemoteException;

    void callback(String string) throws RemoteException;

    String getCallbacks() throws RemoteException;
}

