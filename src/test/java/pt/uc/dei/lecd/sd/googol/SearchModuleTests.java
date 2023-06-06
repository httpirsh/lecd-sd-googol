package pt.uc.dei.lecd.sd.googol;

import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * Esta é uma classe de teste JUnit chamada DownloadAndSearchTest que testa a funcionalidade de um mecanismo de busca.
 */
@Slf4j
public class SearchModuleTests {

    private static Search victim;
    private static int port = 1090;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        TestUtils.startLocalRegistry(port);;

        Barrel barrel = new Barrel();
        barrel.start("localhost", port);;

        victim = new Search();
        victim.start("localhost", port);;
    }

    /**
     * O método Should_return_top10_After_many_searches testa a funcionalidade de retornar as 10 pesquisas mais comuns.
     * O teste faz 20 pesquisas e verifica se as 10 pesquisas mais comuns retornadas contêm apenas números pares.
     * @throws RemoteException
     */
    @Test
    void Should_return_top10_After_many_searches() throws RemoteException {
        // Fazer 20 pesquisas
        for (int i = 1; i <= 20; i++) {
            victim.search(Integer.toString(i));
            if (i % 2 == 0) { // Se for par, pesquisa outra vez para ficar no top
                victim.search(Integer.toString(i));
            }
        }

        List<String> topSearches = victim.getTopSearches();
        log.info("SearchModule getTopSearches returned {}", topSearches);

        assertAll("Os mais pesquisados devem ser os pares.",
            // os pares estão
            () -> assertTrue(topSearches.contains("2")),
            () -> assertTrue(topSearches.contains("4")),
            () -> assertTrue(topSearches.contains("6")),
            () -> assertTrue(topSearches.contains("8")),
            () -> assertTrue(topSearches.contains("10")),
            () -> assertTrue(topSearches.contains("12")),
            () -> assertTrue(topSearches.contains("14")),
            () -> assertTrue(topSearches.contains("16")),
            () -> assertTrue(topSearches.contains("18")),
            () -> assertTrue(topSearches.contains("20")),
            // e os impares não
            () -> assertFalse(topSearches.contains("1")),
            () -> assertFalse(topSearches.contains("3")),
            () -> assertFalse(topSearches.contains("5")),
            () -> assertFalse(topSearches.contains("7")),
            () -> assertFalse(topSearches.contains("9")),
            () -> assertFalse(topSearches.contains("11")),
            () -> assertFalse(topSearches.contains("13")),
            () -> assertFalse(topSearches.contains("15")),
            () -> assertFalse(topSearches.contains("17")),
            () -> assertFalse(topSearches.contains("19"))
        );
    }

    @Test
    void shouldAddToQueueWhenIndexUrlCalled() throws AlreadyBoundException, RemoteException, NotBoundException {
        Queue queue = new Queue();
        queue.start("localhost", port);

        victim.indexNewURL("http://someurl");
        assertEquals(1, queue.size());

        victim.indexNewURL("http://someotherurl");
        assertEquals(2, queue.size());

        queue.stop();
    }
}