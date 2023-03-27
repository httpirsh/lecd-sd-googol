package pt.uc.dei;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
public class IndexStorageBarrel extends UnicastRemoteObject implements InterfaceBarrel{
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, HashSet<String>> invertedIndex;
    private HashMap<String, String> pageTitles;
    private HashMap<String, String> pageContents;
    private HashMap<String, ArrayList<String>> pageLinks;
    private HashMap<String, Integer> pageLinkCounts;
    
    public IndexStorageBarrel() throws RemoteException{
        this.invertedIndex = new HashMap<>();
        this.pageTitles = new HashMap<>();
        this.pageContents = new HashMap<>();
        this.pageLinks = new HashMap<>();
        this.pageLinkCounts = new HashMap<>();
    }

    public void addPage(String url, String title, String content, ArrayList<String> links) {
        // Adicionar o título e conteúdo da página aos seus respectivos HashMaps
        pageTitles.put(url, title);
        pageContents.put(url, content);
        pageLinks.put(url, links);

        // Adicionar cada termo da página ao índice invertido
        for (String term : content.split("\\s+")) {
            term = term.toLowerCase();
            // o método getOrDefault retorna o valor associado à chave no mapa, ou se a chave não existir
            // no mapa retorna null
            HashSet<String> urls = invertedIndex.getOrDefault(term, null);
            urls.add(url);
            invertedIndex.put(term, urls);
        }
        
        // Atualizar o número de ligações da página linkada
        for (String link: links) {
        	Integer num = pageLinkCounts.getOrDefault(link, 0);
            pageLinkCounts.put(link, num + 1);
        
        }
    }
    
    // Retornar a lista de URLs que contenham o termo procurado
    public HashSet<String> searchTerm(String term) {
        return invertedIndex.getOrDefault(term.toLowerCase(), null);
    }
    
    // Retornar a lista de URLs que contenham um conjunto de termos
    public LinkedHashMap <Integer, String> searchTerms(String terms) {
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
    public LinkedHashMap <Integer, String> sortImp(HashSet <String> results){
    	LinkedHashMap <Integer, String> sortedImp = new LinkedHashMap<>();
    	int max, count;
    	String mostRelevant;
    	for(int i=1; i<=results.size(); i++) {
    		max=0; count=i;
    		mostRelevant = "";
    		for(String result: results) {
    			if(!sortedImp.containsValue(result) && pageLinkCounts.get(result) >= max) {
    				max = pageLinkCounts.get(result);
    				mostRelevant = result;
    			}
    		}
    	sortedImp.put(count, mostRelevant);
    	}
    	return sortedImp;
    }   
    
    public String getPageTitle(String url) {
        // Retornar o título da página correspondente à URL fornecida
        return pageTitles.get(url);
    }

    public String getPageContent(String url) {
        // Retornar o conteúdo da página correspondente à URL fornecida
        return pageContents.get(url);
    }

    public ArrayList<String> getLinksToPage(String url) {
        // Retornar a lista de links da página correspondente à URL fornecida
        return pageLinks.get(url);
    }

    // Consultar lista de páginas com ligação para uma página específica
    public ArrayList<String> getPagesWithLinkTo(String url) {
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


}
