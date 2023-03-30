package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

@Slf4j
public class IndexStorageBarrel implements InterfaceBarrel{

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, HashSet<String>> invertedIndex;
    private HashMap<String, String> pageTitles;
    private HashMap<String, String> pageContents;
    private HashMap<String, ArrayList<String>> pageLinks;
    private HashMap<String, Integer> pageLinkCounts;
    private ArrayList<String> urlsQueue;
    private HashSet<String> indexedUrls;    
    
    public IndexStorageBarrel(String name) throws RemoteException {
    	super();
        this.invertedIndex = new HashMap<>();
        this.pageTitles = new HashMap<>();
        this.pageContents = new HashMap<>();
        this.pageLinks = new HashMap<>();
        this.pageLinkCounts = new HashMap<>();
        this.urlsQueue = new ArrayList<>();
        this.indexedUrls = new HashSet<>();
    }
    
    public static void main(String[] args) throws RemoteException {
	    int tentativas = 0;
    	while (tentativas < 5) {
    		try {
	    		IndexStorageBarrel sb = new IndexStorageBarrel();
	    		LocateRegistry.createRegistry(1099).rebind("IndexStorageBarrel", sb);
	    		System.out.println("Armazenamento iniciado...");
	    		break;
	    		
	    	} catch (RemoteException re) {
	    		System.out.println("Erro ao iniciao o armazenamento: " + re.getMessage());
	    		System.out.println("Tentando se reconectar em 5 segundos...");
	    		
	    		try {
	    			Thread.sleep(5000);
	    		
	    		} catch (InterruptedException ie) {
	    			Thread.currentThread().interrupt();
	    		}
	    		tentativas ++;
	    	}
    	}
    	
    	if(tentativas == 5) {
    		System.out.println("Não foi possível iniciar o armazenamento após 5 tentativas.");
    	}    
    }

    private Map<String, Integer> termCounts = new HashMap<>();


    @Override
    public List<String> getSearchHistory() throws RemoteException {
        return null;
    }

    public void addToIndex(String term, String url) {
        HashSet<String> urls = invertedIndex.get(term);
        if (urls == null) {
            urls = new HashSet<String>();
            invertedIndex.put(term,  urls);
        }
        urls.add(url);

        // Increment the count for the term
        termCounts.merge(term, 1, Integer::sum);
    }

    
    // Adicionar titulo do url
    public void addPageTitle(String url, String title) {
    	pageTitles.put(url, title);
    }
    
    // Adicionar o conteudo da página
    public void addPageContents(String url, String text) {
    	pageContents.put(url, text);
    }
    
    // Adicionar a lista de links "encontrados" em cada página
    public void addPageLinks(String url, ArrayList<String> links) {
    	pageLinks.put(url, links);
    }
    
    // retornar os urls da fila
    public ArrayList<String> getQueue (){
    	return urlsQueue;
    }
    
    // atualização da fila
    public void newQueue(ArrayList<String> newQueue) {
    	this.urlsQueue = newQueue;
    }
    
    public void addIndexedUrl(String url) {
    	this.indexedUrls.add(url);
    }
    
    // adicionar um url à fila, que ainda não tenha sido indexado
    public void addToQueue(String url) {
    	if(!indexedUrls.contains(url))
    		this.urlsQueue.add(url);
    		
    }
    
    // Atualizar o número de ligações da página linkada
    public void urlConnections(String url) {
    	Integer num = pageLinkCounts.getOrDefault(url, 0);
        pageLinkCounts.put(url, num + 1);
    }
    
    
    // Retornar a lista de URLs que contenham o termo procurado
    public HashSet<String> searchTerm(String term)  {
        return invertedIndex.getOrDefault(term.toLowerCase(), null);
    }
    
    // Retornar a lista de URLs que contenham um conjunto de termos
    public HashSet <String> searchTerms(String terms)  {
        StringTokenizer tokens = new StringTokenizer(terms, " ,:/.?'_");
        HashSet<String> results = searchTerm(tokens.nextToken());
        if(results == null)
        	return null;
        
        while(tokens.hasMoreElements()) {
        	HashSet<String> token_result = searchTerm(tokens.nextToken());
        	token_result.retainAll(results);
        	if (token_result.size() == 0)
        		return null;
        	
        	results = token_result;
        }  
        return sortImp(results);
    }
    
    // ordenar os resultados da pesquisa por importancia
    public HashSet <String> sortImp(HashSet <String> results) {
    	HashSet <String> sortedImp = new HashSet<>();
    	int max;
    	String greaterImportance;
    	for(int i=1; i<=results.size(); i++) {
    		max=0;
    		greaterImportance = "";
    		for(String result: results) {
    			if(!sortedImp.contains(result) && pageLinkCounts.get(result) >= max) {
    				max = pageLinkCounts.get(result);
    				greaterImportance = result;
    			}
    		}
    	sortedImp.add(greaterImportance);
    	}
    	return sortedImp;
    }   
    
    public String getPageTitle(String url)  {
        // Retornar o título da página correspondente à URL fornecida
        return pageTitles.get(url);
    }

    // Consultar lista de páginas com ligação para uma página específica
    public ArrayList<String> getPagesWithLinkTo(String url){
        ArrayList<String> pagesWithLink = new ArrayList<String>();
        for (String pageUrl : pageLinks.keySet()) {
            ArrayList<String> links = pageLinks.get(pageUrl);
            if (links.contains(url)) {
                pagesWithLink.add(pageUrl);
            }
        }
        return pagesWithLink;
    }
    // Este método percorre todas as páginas armazenadas em pageLinks 
    // e verifica se cada uma contém um link para a página especificada pelo parâmetro url. 
    // Se uma página contém um link para essa página, seu URL é adicionado à lista pagesWithLink. 
    // Finalmente, a lista é retornada como resultado da consulta.
    
    public String getShortQuote(String url) {
    	String text = pageContents.get(url);
    	// Obter a citação com os primeiros 50 caracteres e adiciona "..." no final para indicar que há mais texto a seguir.
    	String shortQuote = text.substring(0, Math.min(text.length(), 50)) + "...";
    	
    	return shortQuote;
    }


    @Override
    public String ping() throws RemoteException {
        log.info("ping was called, answering pong.");
        return "pong";
    }
}
