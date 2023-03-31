package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RmiClientTest {
    private static RmiClient client;

    @BeforeAll
    static void init() throws RemoteException, MalformedURLException, NotBoundException {
        Registry registry = TestUtils.getRegistryInstance(1090);
        registry.rebind("googoltest/search", new RmiSearchModule("search"));
        client = new RmiClient("testClient");
    }

    @Test
    void Should_connect_When_connect() throws MalformedURLException, NotBoundException, RemoteException {
        client.connect("//localhost:1090/googoltest/search");
    }

    @Test
    void Should_receive_pong_When_Ping_Is_Called() throws RemoteException {
        String response = client.callPing();

        Assertions.assertEquals("pong", response);
    }
}
