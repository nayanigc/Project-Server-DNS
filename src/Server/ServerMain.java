package Server;

public class ServerMain {
	
	public static void main(String[] args) {
		DNSServer server = new DNSServer();
		server.listen();
	}
}
