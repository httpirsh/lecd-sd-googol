package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

	@Override
	public String ping() {
		return "pong";
	}

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

	@Override
	public void indexNewURL(String url) throws RemoteException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'indexNewURL'");
	}
}