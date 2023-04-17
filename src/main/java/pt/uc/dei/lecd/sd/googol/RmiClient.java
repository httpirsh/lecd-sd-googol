package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Esta classe implementa um cliente para um módulo de busca remoto.
 * O código usa a biblioteca Lombok para reduzir a verbosidade do código.
 *
 * O programa consiste num menu de opções que permite ao usuário interagir com o módulo de busca remoto.
 */
@Slf4j
public class RmiClient {
	private final String name;
	private InterfaceSearchModule search;


	public RmiClient(String name) {
		this.name = name;
	}

	public String callPing() throws RemoteException {
		return search.ping();
	}

	/**
	 * As opções disponíveis são:
	 *
	 * Indexar um novo URL: permite que o usuário insira manualmente um URL para ser indexado pelo módulo de busca remoto.
	 * Consultar lista de páginas com ligação para uma página específica: permite que o usuário digite os termos de pesquisa e recebe uma lista de páginas que contêm links para a página procurada.
	 * Página de administração atualizada em tempo real: permite que o usuário visualize as 10 pesquisas mais comuns realizadas pelos usuários.
	 * Sair: fecha o programa.
	 */
	public static void menu() {
    		// menu com as opções que utilizador pode realizar
    		System.out.println("1. Indexar um novo Url\n"
    				+ "2. Consultar lista de páginas com ligação para uma página específica\n"
					+ "3. Página de administração atualizada em tempo real\n"
    				+ "4. Sair\n");
    		
    		System.out.println();
            System.out.println("Digite a opção que deseja:");
    	}

	public void iniciar(Scanner sc) throws RemoteException, NotBoundException, MalformedURLException {
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
					this.search.indexNewURL(url);
					break;

				case 2:
					// Consultar os resultados da pesquisa
					System.out.print("Digite os termos da pesquisa: ");
					String termos = sc.nextLine();
					List<String> pages = this.search.searchResults(termos);
					for (String page : pages) {
						System.out.println(page);
					}
					break;

				case 3:
					// List<String> barrels_downloaders_ativos
					// Mostrar top 10 de pesquisas
					List<String> top10 = search.getTopSearches(10);
					System.out.println("As 10 pesquisas mais comuns.");
					for (String search : top10) {
						System.out.println(search);
					}
					System.out.println("Os modulos conectados: " + this.search.getConnected());	
					break;

				case 4:
					System.out.println("Saíste do programa.");
					break;

				default:
					System.out.println("Opcão inválida");
			}
		} while (opcao != 4);
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
            int num = sc.nextInt();
            return num;
        }

	public void connect(String url) throws MalformedURLException, NotBoundException, RemoteException {
		log.info("{} connecting with search module {}", name, url);
		this.search = (InterfaceSearchModule) Naming.lookup(url);
	}

	public static void main(String [] args) throws MalformedURLException, NotBoundException, RemoteException {
		log.info("Starting RMIClient...");
		Scanner sc = new Scanner(System.in);
		RmiClient client = new RmiClient("search");
		client.connect("//localhost/SearchModule");
		client.iniciar(sc);
	}
}