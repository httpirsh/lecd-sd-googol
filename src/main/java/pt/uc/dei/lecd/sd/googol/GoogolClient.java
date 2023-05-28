package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class GoogolClient  {
    
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            System.out.println("Googol client starting with registry at rmi://" + host + ":" + port);
            RmiClient client = new RmiClient("client");
            client.connect(host, port);
            System.out.println("Googol client started.");
            Scanner scanner = new Scanner(System.in);
            client.iniciar(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
