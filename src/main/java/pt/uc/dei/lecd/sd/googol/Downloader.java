package pt.uc.dei.lecd.sd.googol;

import java.io.IOException;
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
 * A classe Downloader é responsável por realizar o download de uma página web,
 * a analisa (usando o jsoup),
 * indexa o seu conteúdo e faz o uso de uma fila de URLs, para escalonar as
 * futuras visistas a páginas.
 * <p>
 * Essas informações são então armazenadas pelos pelos objetos da classe
 * IndexStorageBarrel, que recebem os
 * dados de vários Downloaders por meio de Java RMI.
 * <p>
 * Cada URL é indexado apenas por um Downloader que irá passar os resultados
 * para os Storage Barrels.
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
     * O método main realiza o download de uma página web, indexa seu conteúdo e
     * adiciona links encontrados
     * em uma fila de URLs, que são processados pelos Storage Barrels, utilizando
     * Java RMI.
     *
     * Caso ocorra uma exceção ao atualizar o índice com o Storage Barrel, o
     * programa tentará reconectar por
     * até cinco vezes antes de encerrar a execução.
     */

    /**
     * O método indexURL tem como objetivo fazer o download da página web a partir
     * de uma determinada URL,
     * extrair informações relevantes da página, atualizar os índices e continuar o
     * processo de indexação
     * recursivamente até que a fila de URLs a serem indexados esteja vazia.
     */

    public boolean connect(String url) throws RemoteException {
        log.info("Downloader {} connecting to {}", name, url);
        try {
            this.barrel = (InterfaceBarrel) Naming.lookup(url);
            return true;
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            log.error("Failed while connecting {} to {}", name, url, e);
            return false;
        }
    }

    public boolean indexURL(String url) throws RemoteException {
        // download da pagina Web
        Document doc = null;
        try {
            doc = Jsoup.connect(url.toString()).get();
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
                barrel.addToIndex(tokens.nextToken().toLowerCase(), url); // adicionar a palavra ao indice invertido

            for (String link : linkList) {
                barrel.addToQueue(link); // Adicionar o link à fila
                barrel.urlConnections(link); // Atualizar o número de ligações da página linkada
            }

            barrel.addPageTitle(url, title); // adicionar o titulo da pagina ao indice
            barrel.addPageContents(url, text); // adicionar o texto da pagina ao indice
            barrel.addPageLinks(url, linkList); // adicionar os links encontrados na pagina ao indice
            return true;
        } catch (IOException e) {
            log.error("Unable to index url {} to barrel", url, e);
            return false;
        }
    }

    /**
     * O método getNextURL é responsável por obter o próximo URL a ser indexado.
     * <p>
     * Este método é "synchronized" para garantir que apenas uma thread possa
     * acessar a lista de URLs
     * e executar as operações defenidas, evitando possíveis conflituos de acesso e
     * garantindo que a
     * lista seja manipulada de maneira consistente e segura em ambientes onde
     * múltiplas threads podem
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

    public boolean connectQueue(String url) {
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

    public void start() {
        if (!running)
            (new Thread(this)).start();
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
