package pt.uc.dei;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Downloader {

	//private ArrayList<URL> urlQueue; // guarda os URLs
	private IndexStorageBarrel storageBarrel;
	
	public Downloader(ArrayList<URL> urlQueue, IndexStorageBarrel storageBarrel) {
		// Os Storage Barrel recebem informação processada pelos Downloaders atraves da java RMI
		// Cada URL é indexado apenas por um Downloader que irá passar os resultados para os Storage Barrels
		// Os downloaders obtem as paginas Web, as analisam utilizando o jsoup e fazem o uso de uma fila de URLS
		// para escalonarem as futuras visitas a páginas e atualizam o índice atraves de chamadas RMI aos Barrels
	
		//this.urlQueue = urlQueue;
		this.storageBarrel = storageBarrel;
	
	}
	public void IndexURL(String url) throws IOException, NotBoundException {
        
        // download da pagina Web
        Document doc = Jsoup.connect(url.toString()).get();
        
       
        String title = doc.title();
        
        // texto da pagina
        String bodyText = doc.body().text();
        
        // buscar os links da pagina
        Elements links = doc.select("a[href]");
        
        // criar um arraylist para colocar os links
        ArrayList<String> linkList = new ArrayList<String>();
        
        // adicionar link ao array
        for (Element link : links) {
            URL linkUrl = new URL(link.attr("abs:href"));
            linkList.add(linkUrl.toString());
        }
        
        // adicionar pagina ao barrel
        storageBarrel.addPage(url, title, bodyText, linkList);
    }
	
	// adiconar URLs à fila
	//public void addToQueue(URL url) {
	//	urlQueue.add(url);
	//}
}

