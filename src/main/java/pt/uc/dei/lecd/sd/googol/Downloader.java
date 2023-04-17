package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A classe Downloader é responsável por realizar o download de uma página web, a analisa (usando o jsoup),
 * indexa o seu conteúdo e faz o uso de uma fila de URLs, para escalonar as futuras visistas a páginas.
 * <p>
 * Essas informações são então armazenadas pelos pelos objetos da classe IndexStorageBarrel, que recebem os
 * dados de vários Downloaders por meio de Java RMI.
 * <p>
 * Cada URL é indexado apenas por um Downloader que irá passar os resultados para os Storage Barrels.
 */
@Slf4j
public class Downloader implements Remote, Runnable {

    private final String name;
    private InterfaceBarrel barrel;
    private Queue queue;
    private boolean running;

    public Downloader(String name) {
        this.name = name;
        this.running = false;
    }

    /**
     * Connects to an Index Storage Barrel
     * @param url The url where the barrel is located
     * @return true is the connection was successfull, false otherwise
     * @throws RemoteException
     */
    public boolean connectToBarrel(String url) throws RemoteException {
        log.info("Downloader {} connecting to {}", name, url);
        try {
            this.barrel = (InterfaceBarrel) Naming.lookup(url);
            this.barrel.callback(this.toString());
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            log.error("Failed while connecting {} to {}", name, url, e);
            return false;
        }
    }

    public boolean indexURL(String url) throws RemoteException {
        // download da página Web
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            String title = doc.title(); // titulo da pagina web
            String text = doc.text(); // texto da pagina web
            Elements links = doc.select("a[href]"); // buscar os links da pagina
            ArrayList<String> linkList = new ArrayList<>(); // criar um arraylist para colocar os links

            // adicionar links ao array
            for (Element link : links) {
                linkList.add(link.attr("abs:href"));
            }

            // dividir o texto em tokens individuais
            StringTokenizer tokens = new StringTokenizer(text, " ,:/.?'_");
            while (tokens.hasMoreElements())
                barrel.addToIndex(tokens.nextToken().toLowerCase(), url); // Adicionar a palavra ao índice invertido

            for (String link : linkList) {
                enqueue(link); // Adicionar link à queue para indexar recursivamente
                barrel.urlConnections(link); // Atualizar o número de ligações da página linkada
            }

            barrel.addPageTitle(url, title); // Adicionar o título da página ao índice
            barrel.addPageContents(url, text); // Adicionar o texto da página ao índice
            barrel.addPageLinks(url, linkList); // Adicionar os links encontrados na página ao índice
            return true;
        } catch (Exception e) {
            log.error("Unable to index url {} to barrel", url, e);
            return false;
        }
    }

    private void enqueue(String link) throws RemoteException {
        if (queue != null) {
            queue.enqueue(link); 
        } else {
            log.warn("Downloader {} not connected to queue, link {} will be ignored.", name, link);
        }
    }

    public boolean connectToQueue(String url) {
        log.info("Downloader {} connecting to {}", name, url);
        try {
            this.queue = (Queue) Naming.lookup(url);
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            log.error("Failed while connecting {} to {}", name, url, e);
            return false;
        }
    }

    /**
     * Stops the downloader thread.
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Starts dequeuing urls from the queue and indexing them.
     */
    public void start() {
        if (!running) {
            Thread t = new Thread(this);
            t.setName("thread_" + name);
            t.start();
        }
    }

    @Override
    public void run() {
        this.running = true;
        log.info("Starting run on downloader {}", this.name);
        while (this.running) {
            try {
                Thread.sleep(1000);

                String url = (String) queue.dequeue();
                if (url != null) {
                    log.info("Downloader {} indexing url {}", this.name, url);
                    indexURL(url);
                }
            } catch (RemoteException e) {
                log.error("Error in downloader {} while trying to dequeue", this.name, e);
            } catch (InterruptedException e) {
                // carry on
            } 
        }
        log.info("Downloader {} stopped.", this.name);
    }
}
