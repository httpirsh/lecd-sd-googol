package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Este teste é responsável por testar a classe RmiClient,
 * classe esta que é responsável por se conectar a um módulo de pesquisa RMI e fazer chamadas de método nele.
 * A classe RmiClientTest contém dois métodos de teste, Should_connect_When_connect e Should_receive_pong_When_Ping_Is_Called.
 */
public class RmiClientTest {
    private static RmiClient client;
    private static final String host = "localhost";
    private static final int port = 1090;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException {
        TestUtils.startLocalRegistry(1090);
        Search search = new Search();
        search.start(host, port);
        client = new RmiClient("testClient");
    }

    /**
     * O teste Should_connect_When_connect, testa se a conexão a um módulo de pesquisa RMI é possível, chamando o método connect() no objeto RmiClient.
     * Este método não tem um resultado de teste específico, mas espera-se que não ocorram exceções ou erros.
     *
     * @throws MalformedURLException
     * @throws NotBoundException
     * @throws RemoteException
     */
    @Test
    void Should_connect_When_connect() throws MalformedURLException, NotBoundException, RemoteException {
        client.connect(host, port);
    }

    /**
     * O método Should_receive_pong_When_Ping_Is_Called, testa se é possível fazer uma chamada de método RMI no módulo de pesquisa,
     * especificamente o método ping().
     * O teste espera que a resposta da chamada de método seja uma string "pong". Se a resposta for diferente, o teste falhará.
     *
     * @throws RemoteException
     */
    @Test
    void Should_receive_pong_When_Ping_Is_Called() throws RemoteException {
        String response = client.callPing();

        Assertions.assertEquals("pong", response);
    }
}
