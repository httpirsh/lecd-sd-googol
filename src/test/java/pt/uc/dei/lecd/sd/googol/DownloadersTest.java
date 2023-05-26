package pt.uc.dei.lecd.sd.googol;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class DownloadersTest {

    private static final String host = "localhost";
    private static final int port = 1090;
    private static Downloader downloader;
    private static Registry registry;

    /**
     * O código apresenta um teste JUnit para a classe Downloader.
     * O método init() é anotado com a anotação @BeforeAll, indicando que ele deve ser executado antes dos testes serem executados.
     * Este método cria uma instância do objeto Registry
     * e utiliza-a para vincular um objeto IndexStorageBarrel com o nome "googoltest/barrels/barrel_1" ao registro RMI.
     * Em seguida, uma instância do objeto Downloader é criada e conectada ao índice previamente vinculado.
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws MalformedURLException
     * @throws NotBoundException
     */
    @BeforeAll
    static void init() throws RemoteException, AlreadyBoundException{
        registry = TestUtils.startLocalRegistry(port);
        registry.rebind("googoltest/barrels/barrel_1", new Barrel());
        downloader = new Downloader();
        downloader.connect(host, port);
    }

    /**
     * O método de teste Should_return_true_When_downloader_indexURL()
     * verifica se a chamada do método indexURL() na instância do objeto Downloader (passando uma URL de um documento específico) retorna true.
     * Este teste é realizado através da verificação do valor de retorno do método assertTrue() da biblioteca JUnit,
     * que testa se a condição passada para ele é verdadeira.
     *
     * O teste verifica se o método indexURL() pode indexar com sucesso uma URL, que é um documento da web, no índice.
     * Se o teste passar, ele confirmará que a instância do objeto Downloader
     * está conectada corretamente ao índice e que é capaz de indexar documentos da web com sucesso.
     * @throws RemoteException
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    @Test
    void Should_return_true_When_downloader_indexURL() throws RemoteException, MalformedURLException, NotBoundException {
        assertTrue(downloader.indexURL("https://en.wikipedia.org/wiki/Stranger_Things"));
    }

    /**
     * Quando um downloader é iniciado, ele deve registar-se no registry no uri /googol/downloaders/downloader_1.
     * O nome do downloader é automaticamente construído com o prefixo "downloader_" seguido do número sequencial.
     * 
     * @throws RemoteException quando ocorrem erros de rmi
     * @throws AlreadyBoundException
     */
    @Test
    void shouldHaveProperNameAfterConnect() throws RemoteException, AlreadyBoundException {
        Downloader downloader = new Downloader();
        downloader.connect(host, port);

        GoogolRegistry entries = new GoogolRegistry(registry.list());

        assertTrue(entries.getListOfDownloaders().contains("googol/downloaders/downloader_2"));
    }
}
