package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Esta é uma classe de teste JUnit chamada DownloadAndSearchTest que testa a funcionalidade de um mecanismo de busca.
 * A classe inclui dois métodos de teste, Should_find_document_After_index() e Should_increase_index_size_When_downloader_running_for_a_while(),
 * ambos anotados com @Test.
 */
@Slf4j
public class DownloadAndSearchTest {

    private static Search searchModule;
    private static Registry registry;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        registry = TestUtils.getRegistryInstance(1090);

        registry.bind("googol/barrels/barrel_1", new Barrel());

        searchModule = new Search();
        searchModule.connectToBarrel("//localhost:1090/googol/barrels/barrel_1");
    }

    /**
     * O método de teste Should_find_document_After_index() testa a capacidade do mecanismo de busca
     * de indexar uma página da web e recuperar resultados de pesquisa para uma determinada consulta de pesquisa.
     * O teste verifica se o método indexURL() do objeto Downloader retorna true para uma URL válida
     * e se o método searchResults() do objeto RmiSearchModule retorna uma lista não vazia de resultados para a consulta de pesquisa fornecida.
     *
     * @throws RemoteException
     */
    @Test
    void Should_find_document_After_index() throws RemoteException {

        Downloader victim = new Downloader();
        victim.connectToBarrel("//localhost:1090/googol/barrels/barrel_1");

        assertTrue(victim.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));

        List<String> results = searchModule.searchResults("Stranger");

        log.debug("Received results for {} are {}", "Stranger", String.join(", ", results));

        assertTrue(results.size() > 0);
    }

    /**
     * O método de teste Should_increase_index_size_When_downloader_running_for_a_while()
     * testa se o mecanismo de busca é capaz de indexar e armazenar várias páginas da web no índice.
     * Para isso, o teste utiliza um objeto RMIQueue para armazenar várias URLs e
     * adicioná-las à fila do objeto Downloader para download e indexação.
     * O teste verifica se o tamanho da fila é maior que 1 após o término da execução do Downloader.
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws InterruptedException
     * @throws NotBoundException
     */
    @Test
    void Should_increase_index_size_When_downloader_running_for_a_while() throws RemoteException, AlreadyBoundException, InterruptedException, NotBoundException {
        RemoteQueue queue = new RemoteQueue();
        queue.start("localhost", 1090);
        
        Downloader victim = new Downloader();
        victim.connectToBarrel("//localhost:1090/googol/barrels/barrel_1");
        victim.connect("localhost", 1090);
        queue.enqueue("https://en.wikipedia.org/wiki/Prison_Break");

        victim.start();
        Thread.sleep(5000);
        victim.stop();

        assertTrue(queue.size() > 1);
        queue.stop();
    }
}