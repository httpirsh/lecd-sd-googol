package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class RmiSearchModule extends UnicastRemoteObject implements InterfaceSearchModule {

	private final String name;
	List<String> searchLogs = new ArrayList<>();
	private static final long serialVersionUID = 1L;
	private InterfaceBarrel ba;

	public RmiSearchModule(String name) throws MalformedURLException, NotBoundException, RemoteException {
		this.name = name;
	}

	public void connect(String url) throws MalformedURLException, NotBoundException, RemoteException {
		this.ba = (InterfaceBarrel) Naming.lookup(url);
	}

	public List<String> searchResults(String terms) throws RemoteException {
		HashSet<String> urls = ba.searchTerms(terms);
		List<String> results = new ArrayList<>();

		if (urls == null) {
			results.add("Não existem páginas que contenham esses termos");
		} else {
			int i = 0;
			for (String url : urls) {
				if (i % 10 == 0) {
					results.add("\n-------- Página " + (i / 10 + 1) + " --------\n");
				}
				results.add(ba.getPageTitle(url));
				results.add(url);
				results.add(ba.getShortQuote(url));
				i++;
			}
		}

		return results;
	}

	// Indexar um novo url
	public void indexNewURL(String url) throws RemoteException {
		ba.addToQueue(url);
		System.out.println("O novo URL foi indexado");
	}

	@Override
	public String ping() {
		return "pong";
	}
//
//	// getBarrels
//	// não pode ter nenhuma entrada como input
//	public List<InterfaceBarrel> getBarrels() throws RemoteException {
//		List<InterfaceBarrel> barrels = new ArrayList<>();
//		for (InterfaceBarrel barrel : barrels) {
//			try {
//				InterfaceBarrel barrel = (InterfaceBarrel) Naming.lookup("rmi://" + url + ":1099/IndexStorageBarrel");
//				barrels.add(barrel);
//			} catch (Exception e) {
//				System.err.println("Failed to connect to " + url + ": " + e.getMessage());
//			}
//		}
//		return barrels;
//	}





	// counting searches
	private Map<String, Integer> getTermCounts() throws RemoteException {
		Map<String, Integer> termCounts = new HashMap<>();

		// Iterate through each search log and count the frequency of each search term
		for (int i = 0; i < searchLogs.size(); i++) {
			String[] terms = searchLogs.get(i).split("\\s+"); // split the search term into words
			for (String term : terms) {
				termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
			}
		}

		return termCounts;
	}

	// top 10 searches
	public List<String> getTopSearches(int limit) throws RemoteException {
		// Get all search terms and their frequency
		Map<String, Integer> termCounts = getTermCounts();

		// Sort the terms by frequency in descending order
		List<Map.Entry<String, Integer>> sortedTerms = new ArrayList<>(termCounts.entrySet());
		sortedTerms.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		// Extract the top N terms (up to the limit) and return them as a list of strings
		List<String> topTerms = new ArrayList<>();
		for (int i = 0; i < limit && i < sortedTerms.size(); i++) {
			topTerms.add(sortedTerms.get(i).getKey());
		}
		return topTerms;
	}

//	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
//
//		int tentativas = 0;
//		while (tentativas < 5) {
//			try {
//				InterfaceBarrel b1 = (InterfaceBarrel) Naming.lookup("rmi://localhost:1099/IndexStorageBarrel1");
//
//				RmiSearchModule sm = new RmiSearchModule("search-1");
//				LocateRegistry.createRegistry(1099).rebind("SearchModule", sm);
//
//				RmiSearchModule.ba = ba;
//
//				System.out.println("RMI Search Module ativo ...");
//
//			} catch (RemoteException | MalformedURLException | NotBoundException re) {
//				System.out.println("Erro ao iniciar o RMI Search Module: " + re.getMessage());
//				System.out.println("Tentando se reconectar em 5 segundos...");
//
//				try {
//					Thread.sleep(5000);
//
//				} catch (InterruptedException ie) {
//					Thread.currentThread().interrupt(); // interrompe a thread atual
//				}
//				tentativas++;
//			}
//		}
//		if (tentativas == 5)
//			System.out.println("Não foi possível ativar o RMI Search Module.");
//
//	}
}