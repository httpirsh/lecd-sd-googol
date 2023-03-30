package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class Client {
    //public static void main(String[] args) {
        
    	public static void main(String [] args) throws MalformedURLException, NotBoundException, RemoteException {
    		
    		Scanner sc = new Scanner(System.in);
    		//InterfaceBarrel ba = (InterfaceBarrel) Naming.lookup("rmi://localhost/IndexStorageBarrel");
    		InterfaceSearchModule sm = (InterfaceSearchModule) Naming.lookup("rmi://localhost/SearchModule");
    		iniciar(sc, sm);
    		
    	}
	private final InterfaceBarrel barrel;

	public Client(InterfaceBarrel barrel) throws MalformedURLException, NotBoundException, RemoteException {
		this.barrel = barrel;
	}

	public String callPing() throws RemoteException {
		return barrel.ping();
	}
    
    	public static void menu () {
    		// menu com as opções que utilizador pode realizar
    		System.out.println("1. Indexar um novo Url\n"
    				+ "2. Consultar lista de páginas com ligação para uma página específica\n"
					+ "3. Página de Administração em Tempo Real\n"
    				+ "4. Sair\n");
    		
    		System.out.println();
            System.out.println("Digite a opção que deseja:");
    	}

	public static void iniciar(Scanner sc, InterfaceSearchModule sm) throws RemoteException, NotBoundException, MalformedURLException {
		int opcao;
		boolean registoLogin = registoLogin(sc);
		do {
			menu();
			opcao = validaInteiro(sc);
			sc.nextLine();
			switch(opcao) {
				case 1:
					// Indexar um novo url
					System.out.println("Introduza manualmente um URL para ser indexado: ");
					String url = sc.nextLine();
					sm.indexNewURL(url);
					break;

				case 2:
					// Consultar os resultados da pesquisa
					System.out.print("Digite os termos da pesquisa: ");
					String termos = sc.nextLine();
					List<String> results = sm.searchResults(termos);
					if (registoLogin) {
						sm.listPages(termos);
					}
					// Imprime os resultados da pesquisa
					System.out.println("Resultados da pesquisa: ");
					for (String result : results) {
						System.out.println(result);
					}
					break;

				case 3:


					// Display top 10 searches
					List<String> top10 = getTop10Searches((RmiSearchModule) sm);
					System.out.println("Top 10 searches:");
					for (String search : top10) {
						System.out.println(search);
					}

				case 4:
					System.out.println("Saíste do programa.");
					break;

				default:
					System.out.println("Opcão inválida");
			}
		} while (opcao != 4);
	}

	public static List<String> getTop10Searches(RmiSearchModule sm) throws RemoteException {
		return sm.getTopSearches(10);
	}
    	
    	public static boolean registoLogin(Scanner sc) {
    	    boolean registo = false;
    	    boolean login = false;
    		String nomeUtilizador= "";
    		String palavraPasse= "";
    		// opção de registo
    	    System.out.println("Deseja registar-se? (s/n)");
    	    String resg = sc.nextLine();
    	    if (resg.equalsIgnoreCase("s")) {
    	        // Fazer registo
    	    	System.out.println("Insira o seu nome de utilizador: ");
    	    	nomeUtilizador = sc.nextLine();
    	    	System.out.println("Insira a sua palavra-passe: ");
    	    	palavraPasse = sc.nextLine();
    	    	System.out.println("Registo efetuado com sucesso!");
    	    	registo = true;
    	    }
    	    
    	    if (registo) {
    	    	System.out.println("Deseja fazer login? (s/n)");
    	    	String log = sc.nextLine();
    	    	if (log.equalsIgnoreCase("s")) {
    	    		boolean nomeCorreto = false;
    	    		boolean passeCorreta = false;
    	    		do{
    	    			System.out.println("Nome de utilizador: ");
    	    			String nome= sc.nextLine();
    	    			if (nome.equalsIgnoreCase(nomeUtilizador)) {
    	    		        nomeCorreto = true;
    	    		    } else {
    	    		        System.out.println("Nome de utilizador incorreto");
    	    		    }
    	    		}while(!nomeCorreto); 
    	    		do{
    	    			System.out.println("Palavra passe: ");
    	    			String passe= sc.nextLine();
    	    			if (passe.equalsIgnoreCase(palavraPasse)) {
    	    		        passeCorreta = true;
    	    		    } else {
    	    		        System.out.println("Palavra passe incorreto");
    	    		    }
    	    		}while(!passeCorreta);
    	    		login = true;	
    	    	}
    	    }
    	    // verificar registo e login
    	    if (registo && login) {
    	        return true;
    	    } else {
    	        return false;
    	    }
    	}

    	
    	// O metodo validaInteiro() testa cada token que é lida no canal de leitura, retornando um valor se for um inteiro.
        public static int validaInteiro(Scanner sc) {
            while (!sc.hasNextInt()) {    
                System.out.println("Deve inserir um numero inteiro: ");
                sc.next();				  
            }
            int num=sc.nextInt(); 
            return num;
        }



}   	

    	
    	
/*    	
    	
    	try {
            // Procura pelo serviço de busca no RMI Registry
            InterfaceBarrel searchService = (InterfaceBarrel) Naming.lookup("//localhost/SearchService");

            // Obtém uma lista de termos a partir da entrada do usuário
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite os termos de busca separados por espaços: ");
            String input = scanner.nextLine();
            String[] terms = input.split(" ");

            // Realiza a pesquisa no serviço de busca
            LinkedHashMap <Integer, String> results = searchService.searchTerms(input);
            
            
            // Exibe os resultados da pesquisa
            System.out.println("Resultados da pesquisa:");
            for (SearchResult result : results) {
                System.out.println("URL: " + result.getUrl());
                System.out.println("Título: " + result.getTitle());
                System.out.println("Trecho: " + result.getExcerpt());
                System.out.println("Contagem: " + result.getCount());
                System.out.println();
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

*/









