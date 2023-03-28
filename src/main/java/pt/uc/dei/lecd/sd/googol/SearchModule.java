package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;


public class SearchModule extends UnicastRemoteObject implements InterfaceSearchModule{

	private static final long serialVersionUID = 1L;
	private static InterfaceBarrel ba;

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
		
		int tentativas = 0;
		while (tentativas < 5) {
			try {
				InterfaceBarrel ba = (InterfaceBarrel) Naming.lookup("rmi://localhost:1099/IndexStorageBarrel");
				
				SearchModule sm = new SearchModule();
	    		LocateRegistry.createRegistry(1099).rebind("SeachModule", sm);
	    		
	    		SearchModule.ba = ba;
	    		
	    		System.out.println("RMI Search Module ativo ...");
			
			} catch (RemoteException | MalformedURLException | NotBoundException re){
				System.out.println("Erro ao iniciar o RMI Search Module: " + re.getMessage());
	    		System.out.println("Tentando se reconectar em 5 segundos...");
			
	    		try {
	    			Thread.sleep(5000);
	    		
	    		} catch (InterruptedException ie) { 
	    			Thread.currentThread().interrupt(); // interrompe a thread atual 
	    		}
	    		tentativas ++;
			}
		}
		if(tentativas == 5)
    		System.out.println("Não foi possível ativar o RMI Search Module.");	    
	}
	
	
	public SearchModule() throws MalformedURLException, NotBoundException, RemoteException {}

	public void searchResults(String terms) throws RemoteException{
		HashSet <String> urls = ba.searchTerms(terms);
		if(urls == null)
			System.out.println("Não existem páginas que contenham esses termos");
		
		else {
			int i=0;
			for (String url: urls) {
				if (i % 10 == 0)
		            System.out.println("\n-------- Página " + (i / 10 + 1) + " --------\n");
		        
				System.out.println(ba.getPageTitle(url));
				System.out.println(url);
				System.out.println(ba.getShortQuote(url));
			
			}
		}
	}
	// Indexar um novo url
	public void indexNewURL(String url)  throws RemoteException {
		ba.addToQueue(url);
		System.out.println("O novo URL foi indexado");
	}

	public void listPages (String terms) {
		HashSet <String> urls = ba.searchTerms(terms);
		if(urls == null)
			System.out.println("Não existem páginas que contenham esses termos");
		
		else {
			for (String url: urls) {
				System.out.println("Lista de páginas com ligação ao url " + url);
				System.out.println(ba.getPagesWithLinkTo(url));
			}
		}
	}
}


