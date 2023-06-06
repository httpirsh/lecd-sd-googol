package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class Search extends UnicastRemoteObject implements InterfaceSearchModule {

	private final List<String> searchLogs;
	private static final long serialVersionUID = 1L;
	private GoogolRegistry registry;
	private final Set<String> topSearches;

	/**
	 * O método main da classe tem a responsabilidade de iniciar o RMI Search Module, que permite
	 * satisfazer os pedidos dos clientes.
	 *
	 * Para isso, o método tenta localizar o objeto remoto IndexStorageBarrel (Storage Barrel) usando
	 * o método Naming.lookup(). Se o objeto for encontrado, uma instância da classe SearchModule é
	 * criada e registada no registo RMI com o nome "SearchModule" e no port 1099, permitindo assim
	 * que os clientes possam se comunicar com o servidor.
	 *
	 * No entanto, se ocorrer uma exceção durante a tentativa de localização do objeto remoto ou de
	 * registo no RMI, o programa exibe uma mensagem de erro e espera 5 segundos antes de tentar
	 * novamente. Isso é feito até que o número máximo de tentativas definido seja atingido. Se todas
	 * as tentativas falharem, uma mensagem de erro é exibida.
	 */
	public Search() throws MalformedURLException, NotBoundException, RemoteException {
		this.searchLogs = new ArrayList<>();
		this.topSearches = new HashSet<>();
	}

	/**
	 * O método search tem como objetivo obter os resultados de uma pesquisa a partir dos
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
	public List<Page> search(String terms) throws RemoteException {
		InterfaceBarrel barrel;
		try {
			barrel = registry.lookupBarrelInRoundRobin();
		
			updateSearchLogs(terms);
			HashSet<String> urls = barrel.searchTerms(terms);
			List<Page> results = new ArrayList<>();

			if (urls != null) {
				for (String url : urls) {
					Page page = new Page();
					page.url = url;
					page.summary = barrel.getShortQuote(url);
					page.title = barrel.getPageTitle(url);
					results.add(page);
				}
			}
			return results;
		} catch (MalformedURLException | NotBoundException e) {
			log.error("Unable to search due to error getting a barrel.", e);
			throw new RemoteException("Unable to search due to error getting a barrel.", e);
		}
	}

	/** 
	 * O método listPages realiza uma busca por URLs associadas aos termos fornecidos. 
	 * Recupera um objeto barrel de um registro, depois realiza a busca e imprime a lista de páginas com links para esses URLs. 
	 * Se houver algum erro ao obter o objeto barrel ou realizar a busca, uma exceção RemoteException é lançada.
	 */
	public void listPages (String terms) throws RemoteException{
		InterfaceBarrel barrel;
		try {
			barrel = registry.lookupBarrelInRoundRobin();
			HashSet<String> urls = barrel.searchTerms(terms);
			if (urls == null) {
				System.out.println("Não existem páginas que contenham esses termos");
			}
			else{
				for (String url : urls){
					System.out.println("Lista de páginas com ligação ao url " + url);
					System.out.println(barrel.getPagesWithLinkTo(url));
				}
			}

		} catch (MalformedURLException | NotBoundException e) {
			log.error("Unable to search due to error getting a barrel.", e);
			throw new RemoteException("Unable to search due to error getting a barrel.", e);
		}
	}

	private void updateSearchLogs(String terms) throws MalformedURLException, RemoteException, NotBoundException {
		searchLogs.add(terms);

		// Get all search terms and their frequency
		Map<String, Integer> termCounts = getTermCounts();

		// Sort the terms by frequency in descending order
		List<Map.Entry<String, Integer>> sortedTerms = new ArrayList<>(termCounts.entrySet());
		sortedTerms.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

		// Extract the top 10 terms (up to the limit) and return them as a list of strings
		List<String> newTopSearches = new ArrayList<>();
		for (int i = 0; i < 10 && i < sortedTerms.size(); i++) {
			newTopSearches.add(sortedTerms.get(i).getKey());
		}

		registry.topSearchChangedNotification(newTopSearches);
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
		InterfaceQueue queue = registry.lookupQueue();
		if (queue != null) {
			queue.enqueue(url);
			log.info("Index url {}", url);
		} else {
			log.warn("Not connected to queue. Index request for {} was ignored.", url);
		}
	}

	@Override
	public String ping() {
		return "pong";
	}

	/**
	 * O método getTermCounts() itera sobre cada log de busca registrado,
	 * separa as palavras de cada busca em termos, conta a frequência de cada termo e armazena essas contagens em um mapa.
	 * O mapa resultante tem como chave o termo de busca e como valor a frequência do termo.
	 */
	private Map<String, Integer> getTermCounts() {
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

	/**
	 * O método getTopSearches() utiliza o método getTermCounts()
	 * para obter a contagem de frequência de cada termo de busca e, em seguida, classifica esses termos por ordem decrescente de frequência.
	 * Depois extrai os 10 termos mais frequentes e retorna-os numa lista de strings.
	 */
	public Set<String> getTopSearches() throws RemoteException {
		return this.topSearches;
	}

    public boolean start(String host, int port) {
        try {
            log.info("Starting search at rmi://{}:{}/{}", host, port);
            registry = new GoogolRegistry(host, port);
            registry.bind(this);
            return true;
        } catch (RemoteException | AlreadyBoundException e) {
            log.error("Error starting search module.", e);
            return false;
        } 
    }


    public boolean stop() throws AccessException, RemoteException, NotBoundException {
        try {
            log.info("Stopping search module...");
            return registry.unbind(this);
        } catch (NoSuchObjectException e) {
            log.error("Error stopping search module.", e);
            return false;
        }
    }

	public static void main(String[] args) throws RemoteException {
		ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            System.out.println("Googol search module starting with registry at rmi://" + host + ":" + port);
            Search search = new Search();
            search.start(host, port);
            System.out.println("Googol search module started.");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to stop search...");
            scanner.nextLine();
            scanner.close();
            search.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}