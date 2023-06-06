package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
public class Downloader extends UnicastRemoteObject implements Runnable {

    private String name;
    private InterfaceQueue queue;
    private boolean running;
    private Thread processingThread;
    private GoogolRegistry registry;
    private List<InterfaceBarrel> barrels;

    public Downloader() throws RemoteException {
        this.running = false;
    }

    private void updateBarrels() throws AccessException, RemoteException {
        barrels = registry.getBarrels();
    }

    public boolean indexURL(String url) throws RemoteException, MalformedURLException, NotBoundException {
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

            updateBarrels();

            // dividir o texto em tokens individuais
            StringTokenizer tokens = new StringTokenizer(text, " ,:/.?'_");            
            while (tokens.hasMoreElements())
                addToBarrelsIndex(tokens.nextToken().toLowerCase(), url); // Adicionar a palavra ao índice invertido

            for (String link : linkList) {
                enqueue(link); // Adicionar link à queue para indexar recursivamente
                addToBarrelsUrlConnections(link); // Atualizar o número de ligações da página linkada
            }

            addToBarrelsPageInfo(url, title, text, linkList);

            return true;
        } catch (Exception e) {
            log.error("Unable to index url {} to barrel", url, e);
            return false;
        }
    }

    private void addToBarrelsUrlConnections(String link) {
        for (InterfaceBarrel barrel: barrels) {
            try {
                barrel.urlConnections(link);
            } catch (RemoteException e) {
                log.error("Error calling barrel.", e);
            }
        }        
    }

    private void addToBarrelsIndex(String term, String url) {
        for (InterfaceBarrel barrel: barrels) {
            try {
                barrel.addToIndex(term, url);
            } catch (RemoteException e) {
                log.error("Error calling barrel.", e);
            }
        }
    }

    private void addToBarrelsPageInfo(String url, String title, String text, ArrayList<String> linkList) {
        for (InterfaceBarrel barrel: barrels) {
            try {
                barrel.addPageTitle(url, title); // Adicionar o título da página ao índice
                barrel.addPageContents(url, text); // Adicionar o texto da página ao índice
                barrel.addPageLinks(url, linkList); // Adicionar os links encontrados na página ao índice    
            } catch (RemoteException e) {
                log.error("Error calling barrel.", e);
            }
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
            this.queue = (InterfaceQueue) Naming.lookup(url);
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            log.error("Failed while connecting {} to {}", name, url, e);
            return false;
        }
    }

    /**
     * Stops the downloader thread.
     * @throws InterruptedException
     * @throws NotBoundException
     * @throws RemoteException
     * @throws AccessException
     * @throws MalformedURLException
     */
    public void stop() throws InterruptedException, AccessException, RemoteException, NotBoundException, MalformedURLException {
        this.running = false;
        if (processingThread != null) {
            log.info("Downloader " + getName() + " stopping... waiting for processing thread to finish.");
            processingThread.join();
        }
        registry.unbind(this);
        registry.downloaderNotification(this.name); // notify the admin console that this downloader is active
        log.info("Downloader " + getName() + " stopped.");
    }

    /**
     * Starts dequeuing urls from the queue and indexing them.
     */
    public void start() {
        if (!running) {
            processingThread = new Thread(this);
            processingThread.setName("thread_" + name);
            processingThread.start();
        }
    }

    @Override
    public void run() {
        this.running = true;
        log.info("Starting run on downloader {}", this.name);
        while (this.running) {
            try {
                Thread.sleep(1000);
                if (queue != null) {
                    String url = (String) queue.dequeue();
                    if (url != null) {
                        log.info("Downloader {} indexing url {}", this.name, url);
                        indexURL(url);
                    }
                }
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                log.error("Error in downloader {} while trying to dequeue", this.name, e);
            } catch (InterruptedException e) {
                // carry on
            } 
        }
        log.info("Downloader {} stopped.", this.name);
    }

    /**
     * Regista-se no registo rmi indicado.
     * 
     * @param host  A máquina onde está o registo
     * @param port  O porto onde está o registo na máquina
     * @throws RemoteException  Em caso de erro de ligação
     * @throws AlreadyBoundException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    public void connect(String host, int port) throws RemoteException, AlreadyBoundException, MalformedURLException, NotBoundException {
        registry = new GoogolRegistry(host, port);
        this.name = registry.getNextDownloaderName();

        registry.bind(this);
        
        connectToQueue(GoogolRegistry.getQueueUri(host, port));
        registry.downloaderNotification(this.name); // notify the admin console that this downloader is active
    }

    public String getName() {
        return this.name;
    }

    public static void main(String[] args) {
        ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            Downloader downloader = new Downloader();
            downloader.connect(host, port);
            downloader.start();
            System.out.println("Googol downloader started with name " + downloader.getName());
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to stop downloader...");
            scanner.nextLine();
            scanner.close();
            downloader.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
