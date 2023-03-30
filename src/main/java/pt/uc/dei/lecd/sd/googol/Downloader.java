package pt.uc.dei.lecd.sd.googol;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A classe Downloader é responsável por realizar o download de uma página web, a analisa (usando o jsoup),
 * indexa o seu conteúdo e faz o uso de uma fila de URLs, para escalonar as futuras visistas a páginas.
 * 
 * Essas informações são então armazenadas pelos pelos objetos da classe IndexStorageBarrel, que recebem os 
 * dados de vários Downloaders por meio de Java RMI.
 * 
 * Cada URL é indexado apenas por um Downloader que irá passar os resultados para os Storage Barrels.
 * 
 */
public class Downloader implements Remote {

	private final String name;

	public Downloader(String name) {
		this.name = name;
	}

	/**
	 * O método main realiza o download de uma página web, indexa seu conteúdo e adiciona links encontrados
	 * em uma fila de URLs, que são processados pelos Storage Barrels, utilizando Java RMI.
	 * 
	 * Caso ocorra uma exceção ao atualizar o índice com o Storage Barrel, o programa tentará reconectar por 
	 * até cinco vezes antes de encerrar a execução.
	 */
		
	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		
		int tentativas = 0;
		while (tentativas < 5) {
			try {
				InterfaceBarrel ba = (InterfaceBarrel) Naming.lookup("rmi://localhost:1099/IndexStorageBarrel");
				
				String url = getNextURL(ba);
				indexURL(url, ba);
				System.out.println("Índice atualizado ...");
	    		break;
			
			} catch (RemoteException re) {
	    		System.out.println("Erro ao atualizar índice: " + re.getMessage());
	    		System.out.println("Tentando se reconectar em 5 segundos...");
	    		
	    		try {
	    			Thread.sleep(5000);
	    		
	    		} catch (InterruptedException ie) {
	    			Thread.currentThread().interrupt();
	    		}
	    		tentativas ++;
	    	}
    	}
    	
    	if(tentativas == 5)
    		System.out.println("Não foi possível atualizar o índice após 5 tentativas.");	    
	}
	
	/**
	 * O método indexURL tem como objetivo fazer o download da página web a partir de uma determinada URL, 
	 * extrair informações relevantes da página, atualizar os índices e continuar o processo de indexação 
	 * recursivamente até que a fila de URLs a serem indexados esteja vazia.
	 */
	
	public static void indexURL(String url, InterfaceBarrel ba) throws RemoteException { 
        try {
			// download da pagina Web
	        Document doc = Jsoup.connect(url.toString()).get();
	        
	        // titulo da pagina web
	        String title = doc.title();
	        
	        // texto da pagina web
	        String text = doc.text();
	        
	        // buscar os links da pagina
	        Elements links = doc.select("a[href]");
	        
	        // criar um arraylist para colocar os links
	        ArrayList<String> linkList = new ArrayList<String>();
	        
	        // adicionar link ao array
	        for (Element link : links) {
	            linkList.add(link.attr("abs:href"));
	        }
	        // dividir o texto em tokens individuais
	        StringTokenizer tokens = new StringTokenizer(text, " ,:/.?'_");
	        while (tokens.hasMoreElements())
	        	// adicionar a palavra ao indice invertido
	        	ba.addToIndex(tokens.nextToken().toLowerCase(), url);
	        
	        for (String link: linkList) {
	        	// Adicionar o link à fila
	        	ba.addToQueue(link);
	        	// Atualizar o número de ligações da página linkada
	        	ba.urlConnections(link);
	        }  
	        
	        // adicionar o titulo da pagina ao indice 
	        ba.addPageTitle(url, title);
	        // adicionar o texto da pagina ao indice
	        ba.addPageContents(url, text);
	        // adicionar os links encontrados na pagina ao indice
	        ba.addPageLinks(url, linkList);
        
        } catch (IOException e) {
        	e.printStackTrace();
        }
        // indexar recursivamente
        indexURL(getNextURL(ba), ba);
	}
	
	/**
	 * O método getNextURL é responsável por obter o próximo URL a ser indexado. 
	 * 
	 * Este método é "synchronized" para garantir que apenas uma thread possa acessar a lista de URLs 
	 * e executar as operações defenidas, evitando possíveis conflituos de acesso e garantindo que a 
	 * lista seja manipulada de maneira consistente e segura em ambientes onde múltiplas threads podem 
	 * estar acessando-a simultaneamente. 
	 */
	public synchronized static String getNextURL(InterfaceBarrel ba) throws RemoteException {
		ArrayList<String> urlsQueue = ba.getQueue();
		String url = urlsQueue.iterator().next();
		
		// remover o url já vistado da fila
		urlsQueue.remove(url);
		// atualizar a fila
		ba.newQueue(urlsQueue);
		// adicionar o url a lista de urls já visitados
		ba.addIndexedUrl(url);
		
		return url;
		
	}

}
