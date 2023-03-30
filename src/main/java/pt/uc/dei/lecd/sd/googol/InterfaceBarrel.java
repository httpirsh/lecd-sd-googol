package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

	List<String> getSearchHistory() throws RemoteException;

	void addToIndex(String term, String url);

	void addPageTitle(String url, String title);

	void addPageContents(String url, String text);

	void addPageLinks(String url, ArrayList<String> links);

	ArrayList<String> getQueue();

	void newQueue(ArrayList<String> newQueue);

	void addIndexedUrl(String url);

	void addToQueue(String url);

	void urlConnections(String url);

	HashSet<String> searchTerm(String term);

	HashSet<String> searchTerms(String terms);

	HashSet<String> sortImp(HashSet<String> results);

	String getPageTitle(String url);

	ArrayList<String> getPagesWithLinkTo(String url);

	String getShortQuote(String url);

	String ping();
}

