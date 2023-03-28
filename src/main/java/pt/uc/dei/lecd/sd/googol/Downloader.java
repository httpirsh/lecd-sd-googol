package pt.uc.dei.lecd.sd.googol;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Downloader {
	// Os Storage Barrel recebem informação processada pelos Downloaders atraves da java RMI
	// Cada URL é indexado apenas por um Downloader que irá passar os resultados para os Storage Barrels
	// Os downloaders obtem as paginas Web, as analisam utilizando o jsoup e fazem o uso de uma fila de URLS
	// para escalonarem as futuras visitas a páginas e atualizam o índice atraves de chamadas RMI aos Barrels
	
	
	
	public static void main(String[] agrs) throws RemoteException, MalformedURLException, NotBoundException {
		
		int tentativas = 0;
		while (tentativas < 5) {
			try {
				InterfaceBarrel ba = (InterfaceBarrel) Naming.lookup("rmi://localhost/indexBarrel");
				
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
	        StringTokenizer tokens = new StringTokenizer(text);
	        while (tokens.hasMoreElements())
	        	ba.addToIndex(tokens.nextToken().toLowerCase(), url);
	        
	        for (String link: linkList) {
	        	// Adicionar o link à fila
	        	ba.addToQueue(link);
	        	// Atualizar o número de ligações da página linkada
	        	ba.urlConnections(link);
	        }  
	        
	        // adicionar o titulo da pagina
	        ba.addPageTitle(url, title);
	        // adicionar o texto da pagina
	        ba.addPageContents(url, text);
	        // adicionar os links encontrados na pagina
	        ba.addPageLinks(url, linkList);
        
        } catch (IOException e) {
        	e.printStackTrace();
        }

	}
	
	
	public synchronized static String getNextURL(InterfaceBarrel ba) throws RemoteException {
		ArrayList<String> urlsQueue = ba.getQueue();
		String url = urlsQueue.iterator().next();
		
		urlsQueue.remove(url);
		ba.newQueue(urlsQueue);
		ba.addIndexedUrl(url);
		
		return url;
		
	}

}
