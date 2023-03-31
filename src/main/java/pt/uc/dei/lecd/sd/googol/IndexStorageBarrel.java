package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


/**
 * A classe IndexStorageBarrel é responsável por armazenar informações sobre páginas web,
 * permitindo que os usuários possam realizar as funcionalidades estabelecidas para este
 * projeto.
 *
 * A classe utiliza a tecnologia RMI que permite a invocação de métodos remotos entre
 * diferentes processos em uma rede. Para que isso seja possível, a classe implementa a
 * interface InterfaceBarrel, para permitir que objetos do tipo IndexStorageBarrel sejam
 * acessíveis remotamente. Isso significa que um utilizador pode chamar métodos em objetos
 * IndexStorageBarrel em outra máquina, como se estivessem sendo chamados localmente.
 */

@Slf4j
public class IndexStorageBarrel extends UnicastRemoteObject implements InterfaceBarrel{

	private static final long serialVersionUID = 1L;
    private final String name;
    private final HashMap<String, HashSet<String>> invertedIndex; // mapa, com o termo como chave e o conjunto de URLs como valor
    private final HashMap<String, String> pageTitles; // mapa de título das páginas, com a URL como chave e o título como valor
    private final HashMap<String, String> pageContents; // mapa de conteúdos de página, com a URL como chave e o conteúdo como valor
    private final HashMap<String, ArrayList<String>> pageLinks; // mapa de links encontrados em cada página, com a URL como chave e a lista de links como valor
    private final HashMap<String, Integer> pageLinkCounts; // mapa de contagem de links de cada página, com a URL como chave e o número de links como valor
    private ArrayList<String> urlsQueue; // fila de URLs a serem indexadss
    private final HashSet<String> indexedUrls; // conjunto de URLs já indexados

    private final Map<String, Integer> termCounts;

    /**
     * Construtor da classe que inicializa os atributos da classe
     */
    public IndexStorageBarrel(String name) throws RemoteException {
        this.name = name;
        this.invertedIndex = new HashMap<>();
        this.pageTitles = new HashMap<>();
        this.pageContents = new HashMap<>();
        this.pageLinks = new HashMap<>();
        this.pageLinkCounts = new HashMap<>();
        this.urlsQueue = new ArrayList<>();
        this.indexedUrls = new HashSet<>();
        this.termCounts = new HashMap<>();
    }


    @Override
    public List<String> getSearchHistory() throws RemoteException {
        return null;
    }

    /**
     * O método addToIndex adiciona um URL ao índice invertido para o termo fornecido.
     * Caso o termo em questão não possua nenhuma lista de URLs associada, uma nova lista é criada e
     * posteriormente adicionada ao índice.
     */
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


    /**
     * O método addPageTitle adiciona o título da página ao índice.
     * Ele recebe como entrada o URL e o título da página e adiciona essas informações ao HasMap pageTitles.
     * Este método será útil para recuperar o título de uma determinada página web, e assim, apresentar os
     * resultados da pesquisa realizada pelo usuário.
     */
    public void addPageTitle(String url, String title) {
    	pageTitles.put(url, title);
    }

    /**
     * O método addPageContents adiciona o conteúdo da página ao índice.
     * Ele recebe como entrada o URL e o conteúdo da página e adiciona essas informações ao HasMap pageContents.
     * Este método será útil para recuperar o conteúdo de uma determinada página web, e extrair uma pequena
     * citação do mesmo, a fim de apresentá-la nos resultados da pesquisa realizada pelo usuário.
     */
    public void addPageContents(String url, String text) {
    	pageContents.put(url, text);
    }

    /**
     * O método addPageLinks adiciona a lista de links "encontrados" na página ao índice.
     * Ele recebe como entrada o URL e o lista de links e adiciona essas informações ao HasMap pageLinks.
     * Este método será útil para consultar a lista de páginas com ligação para uma página específica.
     */
    public void addPageLinks(String url, ArrayList<String> links) {
    	pageLinks.put(url, links);
    }

    /**
     * O método getQueue retorna a lista de URLs armazenados na fila de rastreamento (urlsQueue).
     */
    public ArrayList<String> getQueue (){
    	return urlsQueue;
    }

    /**
     * O método newQueue é utilizado para atualizar a fila de URLs.
     */
    public void newQueue(ArrayList<String> newQueue) {
    	this.urlsQueue = newQueue;
    }

    /**
     * O método addIndexedURL é responsável por adicionar um URL à lista de URLs já visatados.
     * Esta lista é importante para evitar que o mesmo URL seja visitado novamente durante o
     * processo de indexação.
     */
    public void addIndexedUrl(String url) {
    	this.indexedUrls.add(url);
    }

    /**
     * O método addToQueue é responsável por adicionar um URL à fila de URLs, desde que esse URL
     * ainda não tenha sido indexado. Essa verificação é feita utilizando o método 'contains',
     * que verifica se o URL já está contido no conjunto de URLs indexados.
     */
    public void addToQueue(String url) {
    	if(!indexedUrls.contains(url))
    		this.urlsQueue.add(url);
    		
    }

    /**
     * O método urlConnections é usado para contabilizar o número de ligações de outras páginas
     * para uma determinada página.
     *
     * Ele recebe um URL como entrada e, em seguida, incrementa uma unidade ao valor associado a
     * esse URL no HashMap pageLinkCounts, o qual representa o número de ligações para essa página.
     */
    public void urlConnections(String url) {
    	Integer num = pageLinkCounts.getOrDefault(url, 0);
        pageLinkCounts.put(url, num + 1);
    }


    /**
     * O método searchTerm tem como objetivo retornar a lista de URLs contenham um determinado termo
     * (passado como parâmetro) ou null, caso não haja URLs correspondentes.
     */
    public HashSet<String> searchTerm(String term)  {
        return invertedIndex.getOrDefault(term.toLowerCase(), null);
    }

    /**
     * O método searchTerms é responsável por pesquisar a lista de URLs que contêm um conjunto de
     * termos fornecidos como parâmetro.
     *
     * O método recebe uma string de termos separadaros por delimitadores e, em seguida, usa a classe
     * StringTokenizer para separar os termos em tokens. Em seguida, ele chama o método searchTerm
     * para obter a lista de URLs que correspondem ao primeiro token.
     * Posteriormente, para cada token adicional, o método utiliza novamene o método searchTerm e,
     * em seguida, usa o método retainAll para reter apenas os URLs que aparecem nos dois HashSet.
     * Esse processo é realizado iterativamente para cada token adicional até que todos os tokens
     * tenham sido processados.
     *
     * Por fim, o método retorna a lista resultante de URLs ordenada por importância. Se nenhum URL
     * for encontrado, o método retorna null.
     */
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

    /**
     * O método sortImp retorna os resultados da pesquisa ordenados por importância, baseando-se
     * no número de ligações que cada página possui. Para isso, o método utiliza um ciclo for que
     * itera sobre as URLs do HashSet recebido.
     *
     * Em cada iteração, o método encontra o URL com o maior número de ligações e adiciona esse URL
     * a um novo HashSet de URLs ordenados. Esse processo é repetido até que todos os URLs tenham
     * sido adicionados ao novo HashSet.
     *
     * Para garantir que os URLs não sejam adicionados duas vezes, o método verifica se o URL em
     * análise não está no HashSet de URLs ordenados, utilizando o método "contains".
     */
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

    /**
     * O método getPageTitle retorna o título da página correspondente à URL fornecida.
     */
    public String getPageTitle(String url)  {
        // Retornar o título da página correspondente à URL fornecida
        return pageTitles.get(url);
    }

    /**
     * O método getPagesWithLinkTo retorna uma lista de URLs têm ligação para uma página
     * específica.
     *
     * O método percorre todas as páginas armazenadas no HashMap pageLinks e verifica se
     * cada uma contém ligação para a página especificada pelo parâmetro 'url'. Se uma
     * página contiver ligação para essa página, seu URL é adiconado à lista pagesWithLink.
     * Por fim, essa lista é retornada como resultado da consulta.
     */
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

    /**
     * O método getShortQuote retonar uma citação curta de uma determinada página, a qual é
     * utilizada para fornecer um contexto resumido do conteúdo da página nos resultados da
     * pesquisa.
     *
     * O método recebe um URL como entrada e utiliza esse URL para obter o contéudo da página
     * correspondente no HashMap pageContents. Em seguida, o método retorna uma substring do
     * conteúdo da página que contém os primeiros 50 caracteres, seguidos de reticências para
     * indicar que há mais texto disponível na página.
     */
    public String getShortQuote(String url) {
    	String text = pageContents.get(url);
    	// Obter a citação com os primeiros 50 caracteres e adiciona "..." no final para indicar que há mais texto a seguir.
    	String shortQuote = text.substring(0, Math.min(text.length(), 50)) + "...";
    	
    	return shortQuote;
    }

    @Override
    public String ping() {
        log.info("ping was called, answering pong.");
        return "pong";
    }
}
