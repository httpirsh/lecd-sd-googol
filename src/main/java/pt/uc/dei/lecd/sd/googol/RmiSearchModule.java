package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * A classe SearchModule é um componente essencial do sistema de busca e é responsável por atuar
 * como porta de entrada para os clientes. Ela implementa os métodos que procuram responder às
 * pesquisas realizadas pelos clientes.
 * Para possibilitar essa comunicação, a classe implementa a interface InterfaceSearchModule.
 * Isso permite que os objetos desta classe possam ser acessados remotamente por outros processos
 * Java que usam RMI.
 *
 * Além disso, o Search Module comunica-se com os Storage Barrels (IndexStorageBarrel) por meio da
 * interface InterfaceBarrel. Essa comunicação é realizada usando RMI, permitindo que o Search Module
 * tenha acesso às informações que foram armazenadas no processo de armazenamento.
 * Essa comunicação é essencial para que a classe seja capaz de buscar as informações necessárias e
 * fornecer os resultados precisos aos clientes.
 */
public class RmiSearchModule extends UnicastRemoteObject implements InterfaceSearchModule {

	private final String name;
	List<String> searchLogs = new ArrayList<>();
	private InterfaceBarrel barrel;
	private static final long serialVersionUID = 1L;

	public RmiSearchModule(String name) throws MalformedURLException, NotBoundException, RemoteException {
		this.name = name;
	}
	public void connectToBarrel(String url) throws MalformedURLException, NotBoundException, RemoteException {
		this.barrel = (InterfaceBarrel) Naming.lookup(url);
	}
	/**
	 * O método searchResults tem como objetivo obter os resultados de uma pesquisa a partir dos
	 * termos passados como argumento. Para isso, ele invoca o método "searchTerms" da instância
	 * de "InterfaceBarrel" com o objetivo de obter um conjunto de URLs que correspondam aos termos
	 * de pesquisa fornecidos. Caso a pesquisa não encontre nenhum resultado, o método exibe uma
	 * mensagem indicando tal situação.
	 *
	 * Por outro lado, se a pesquisa retornar resultados, o método itera pelo conjunto de URLs
	 * obtidos e, para cada URL, exibe o título da página, a própria URL e uma citação curta do
	 * conteúdo da página. Cada página de resultados é exibida em grupos de 10, utilizando um
	 * separador que indica o início de uma nova página de resultados.
	 */
	public void searchResults(String terms) throws RemoteException {
		HashSet<String> urls = barrel.searchTerms(terms);
		if (urls == null) {
			results.add("Não existem páginas que contenham esses termos");
		} else {
			int i = 0;
			for (String url : urls) {
				if (i % 10 == 0) {
					System.out.println("\n-------- Página " + (i / 10 + 1) + " --------\n");
				}
				System.out.println(barrel.getPageTitle(url));
				System.out.println(url);
				System.out.println(barrel.getShortQuote(url));
				i++;
			}
		}
		return results;
	}

	/**
	 * O método indexNewURL é responsável por adicionar um novo URL à fila de páginas a serem
	 * indexadas. O método recebe um parâmetro de entrada "url", que representa o endereço da página
	 * a ser adicionada à fila.
	 *
	 * Para adicionar o URL à fila, o método utiliza a referência à interface "InterfaceBarrel" para
	 * invocar o método "addToQueue" no objeto remoto do servidor RMI. Esse método é responsável por
	 * adicionar o URL recebido à fila de páginas a serem indexadas pelo servidor.
	 *
	 * Após adicionar o URL à fila com sucesso, o método exibe uma mensagem "O novo URL foi indexado".
	 * Caso ocorra algum problema ao adicionar o URL à fila, o método lançará uma exceção do tipo
	 * RemoteException.
	 */
	public void indexNewURL(String url) throws RemoteException {
		barrel.addToQueue(url);
		System.out.println("O novo URL foi indexado");
	}

	@Override
	public String ping() {
		return "pong";
	}




	// contar as pesquisas
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


	/**
	 * O método listPages imprime a lista de páginas que têm ligação para um determinado URL.
	 *
	 * Para tal, o método utiliza a referência à interface "InterfaceBarrel" para invocar o método
	 * "getPagesWithLinkTo" que contêm a respetiva lista de páginas.
	 *
	 * Caso ocorra algum problema ao imprimir a lista de páginas, o método lançará uma exceção do
	 * tipo RemoteException.
	 */
	public void listPages (String terms) throws RemoteException{
		HashSet <String> urls = barrel.searchTerms(terms);
		if(urls == null)
			System.out.println("Não existem páginas que contenham esses termos");

		else {
			for (String url: urls) {
				System.out.println("Lista de páginas com ligação ao url " + url);
				System.out.println(barrel.getPagesWithLinkTo(url));
			}
		}
	}
}