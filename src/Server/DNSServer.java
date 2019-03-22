package Server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Request.Answer;
import Request.Header;
import Request.Packet;
import Request.Question;
import Request.Record;

public class DNSServer {

	Record recordA = new Record();
	Record recordPTR = new Record();
	private boolean stop = false;
	public DNSServer() {
		//Teste d'une bdd de notre DNS
		Answer [] a = {new Answer ("abcdns.txt", 1, 1, 5500, 8, "8.8.8.8"), new Answer ("www.facebook.com",1,1,5500,14,"179.60.192.36")};
		Answer [] ans = {new Answer("8.8.8.8", 1, 1, 5000, 11, "abcdns.txt"),new Answer ("179.60.192.36",1,1,5500,17,"www.facebook.com")};
		recordA.add("abcdns.txt", a[0]);
		recordA.add("www.facebook.com", a[1]);
		recordPTR.add("8.8.8.8", ans[0]);
		recordPTR.add("179.60.192.36", ans[1]);
	}


	/**
	 * Cette fonction ecoute le canal sans arret et renvoie une reponse lorsqu'un paquet est recu
	 */
	public void listen() {
		//Ecoute du port DNS
		try (DatagramSocket socket = new DatagramSocket(53)) {
			System.out.println("Start DNS Server");
			byte[] buffer = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			while (!stop) {
				socket.setBroadcast(true); //Permet activer la diffusion des datagramme
				try {
					socket.receive(packet);

					System.out.println("\n--- PACKET RECEIVED ---");
					//Adresse IP et Port de l'epediteur (afin de renvoyer la response)
					InetAddress address = packet.getAddress();
					int port = packet.getPort();

					//Renvoie la reponse a l'exediteur
					System.out.println("--- ANSWER SENT ---\n");

					send(calculResponse(packet.getData()), address, port, socket);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static int unsignedToBytes(byte b) {
		return b & 0xFF;
	}

	/**
	 * Cette fonction a pour but de calculer et renvoyer la reponse sous forme de donnees binaire
	 * selon si la requete est A ou PTR contenant l'entete et la question recuperee par le paquet
	 * recu du serveur 
	 * @param data contenant les donnees recupurees durant l'ecoute du port
	 * @return donnees binaires correspondant au packet reponse
	 * @throws IOException
	 */
	public byte[] calculResponse(byte[] data) throws IOException {
		System.out.println("START COMPUTE ANSWER");
		//Decode le paquet : entete et question et verifie si la question est de type A ou PTR
		Packet packet = Packet.decode(data);
		if (packet.getQuestion().getQtype() == 1) {
			//Recupere la reponse dans notre bdd si elle existe
			Answer answer = recordA.get(packet.getQuestion().getQname());
			if (answer != null) {
				Packet p1 = new Packet(packet.getHeader(), packet.getQuestion(), answer); 
				packet.getHeader().setQr(1); //Dire qu'on envoie une reponse
				packet.getHeader().setAncount(1);
				printPacketAnswerQuery(p1.getHeader(),p1.getQuestion(),answer);
				byte[] calculA = p1.encode(); 
				return calculA;

			} else {
				//Sinon on cherche sur bdd d'un vrai serveurDNS
				InetAddress addr = null;
				System.out.println("NAME  : " + packet.getQuestion().getQname());
				try {
					addr = InetAddress.getByName(packet.getQuestion().getQname());
				} catch (UnknownHostException e) {
					System.out.println("A REQUEST NOT REGISTERED DOMAIN UNKNWON HOST");
					packet.getHeader().setRcode(3); //Nom domaine n'existe pas
					packet.getHeader().setQr(1);
					printPacket(packet.getHeader(),packet.getQuestion(),0);
					Packet pac = new Packet(packet.getHeader(), packet.getQuestion());
					return pac.encode();
				}
				System.out.println("A REQUEST NOT REGISTERED found addr : " + addr);

				byte[] dataBytes = addr.getAddress();

				String name = packet.getQuestion().getQname();
				int type = packet.getQuestion().getQtype();
				int classe = packet.getQuestion().getQclass();

				String addrStr = "";
				for (int i = 0; i < dataBytes.length; i++) {
					addrStr += String.valueOf(unsignedToBytes(dataBytes[i]));
					if (i < dataBytes.length - 1) {
						addrStr += ".";
					}
				}

				System.out.println("Addr str computed : " + addrStr);

				Answer answer1 = new Answer(name, type, classe, 5000, 4, addrStr);
				Packet p2 = new Packet(packet.getHeader(), packet.getQuestion(), answer1);
				p2.getHeader().setQr(1);
				p2.getHeader().setAncount(1);
				printPacketAnswerQuery(p2.getHeader(), p2.getQuestion(), answer1);

				return p2.encode();
			}

		} else if (packet.getQuestion().getQtype() == 12) {

			System.out.println("NAME : " + packet.getQuestion().getQname());

			Answer answer = recordPTR.get(packet.getQuestion().getQname());
			if (answer != null) {
				Packet p1 = new Packet(packet.getHeader(), packet.getQuestion(), answer);

				p1.getHeader().setQr(1);
				p1.getHeader().setAncount(1);

				printPacketAnswerQuery(packet.getHeader(), packet.getQuestion(),answer);
				byte[] calculPTR = p1.encode();
				return calculPTR;

			} else {

				String name = packet.getQuestion().getQname();
				int type = packet.getQuestion().getQtype();
				int classe = packet.getQuestion().getQclass();

				String[] labels = name.split("\\.");

				byte[] addrBytes = new byte[4];
				for (int i = 0; i < 4; i++) {
					addrBytes[3 - i] = (byte) Integer.parseInt(labels[i]);

				}
				InetAddress address =  InetAddress.getByAddress(addrBytes);

				String domain = address.getHostName();
				System.out.println("DOMAIN COMPUTED : " + domain);

				Answer answer1 = new Answer(name, type, classe, 5000, domain.length(), domain);
				Packet p3 = new Packet(packet.getHeader(), packet.getQuestion(), answer1);
				p3.getHeader().setQr(1);
				p3.getHeader().setAncount(1);
				printPacketAnswerQuery(p3.getHeader(), p3.getQuestion(),answer1);
				return p3.encode();
			}
		} else {
			System.out.println("Erreur de Domaine");
		}

		return data;
	}

	/**
	 * Retourne au client le paquet contenant le header et la question et la reponse
	 * @param packet le packet final
	 * @param address l'adresse d'origine de la question
	 * @param port le port d'origine de la question
	 * @param socket communication avec le reseau d'origine de la question
	 */

	public void send(byte[] packet, InetAddress address, int port, DatagramSocket socket) {
		try {
			socket.setBroadcast(true);
			DatagramPacket response;
			response = new DatagramPacket(packet, packet.length, address, port);
			socket.send(response);
		} catch (SocketException e) {
			System.out.println( e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/*AFFICHAGE*/
	public void printPacketAnswerQuery (Header header, Question question ,Answer a) {
		Packet pac = new Packet (header, question, a);	
		System.out.println("PACKET: ");
		System.out.println("---------------------------------");
		System.out.println(pac);
		System.out.println("---------------------------------");
		System.out.println("Answer: ");
		System.out.println("---------------------------------");
		System.out.println(pac.getAnswer());
		System.out.println("---------------------------------");


	}
	public void printPacket (Header header, Question question,int nombre ) {
		Packet pac = new Packet (header, question);	
		if ( nombre == 0 ) {
			System.out.println("Packet without Answer: ");
		}
		System.out.println("---------------------------------");
		System.out.println(pac);
		System.out.println("DOMAIN NAME NOT EXIST: " + pac.getQuestion().getQname());
		System.out.println("---------------------------------");


	}



}