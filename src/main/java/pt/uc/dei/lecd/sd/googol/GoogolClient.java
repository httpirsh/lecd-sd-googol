package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class GoogolClient  {
    
    public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException {
        System.out.println("Googol Client.... a iniciar.");
        Scanner sc = new Scanner(System.in);
		RmiClient client = new RmiClient("search");
		client.connect("//localhost/googol/search");
	    System.out.println("Googol client iniciado com sucesso.");
    	client.iniciar(sc);
    }    
}
