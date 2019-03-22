package Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Question {
	private String qname;
	private int qtype;
	private int qclass;
	public Question(String qname, int qtype, int qclass){
		this.qname = qname;
		if(qtype == 1 || qtype == 12) {
			this.qtype = qtype;
		}
		if (qclass == 1) {
			this.qclass = qclass;
		}

	}
	/**
	 *Cette fonction permet de retourner une Question par le decodage des donnees en binaire 
	 * associant les valeurs a l'aide de decalages.
	 * @param data la deuxieme partie du paquet
	 * @return
	 * @throws IOException
	 */
	public static Question decode (byte[] data) throws IOException {
		//Tampon permettant d'etre lu a partir du flux avec compteur
		ByteArrayInputStream bis = new ByteArrayInputStream (data);
		DataInputStream dis = new DataInputStream (bis);

		StringBuilder name = new StringBuilder();
		int taille = dis.readByte();

		while(taille != 0) {
			if (taille < 0) {
				dis = new DataInputStream(new ByteArrayInputStream(data, dis.readByte(), 255));
				taille = dis.readByte();
				if (taille == 0) break;
			}

			for(int i = 0; i < taille; i++) {
				name.append ((char) dis.readByte());
			}

			taille = dis.readByte();

			if (taille > 0)
				name.append(".");
		}

		int type = dis.readUnsignedShort();
		int qclass = dis.readUnsignedShort();
		Question q = new Question(name.toString(), type, qclass);

		return q;	     
	}

	public String toString(){
		return 	"QNAME : " 		+ this.getQname() + 
				"\nQTYPE : " 	+ this.getQtype() +
				"\nQCLASS : " 	+ this.getQclass();
	}


	/**
	 * Renvoie la reponse en donnees binaire
	 * @return
	 * @throws IOException
	 */
	public byte[] encode () throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream ();
		DataOutputStream dos = new DataOutputStream (bos);
		this.writeOutputStream(dos);
		dos.flush ();
		return bos.toByteArray ();

	}
	/**
	 * Ecris a la sortie la question pour le client
	 * @param dos
	 * @throws IOException
	 */
	public void writeOutputStream (DataOutputStream dos) throws IOException {
		String [] labels = this.qname.split("\\.");
		for (int i = 0; i < labels.length; i++) {
			byte [] label = labels[i].getBytes ();
			int length = label.length;
			if(length > 63) {
				throw new IllegalArgumentException ();
			}
			dos.writeByte (label.length);
			dos.write (label);
		}

		dos.writeByte (0);
		dos.writeShort(this.qtype);
		dos.writeShort(this.qclass);
	}



	/**************************************************************GETTEUR AND SETTER***********************************************************************************/

	public String getQname() {
		return qname;
	}
	public int getQtype() {
		return qtype;
	}
	public int getQclass() {
		return qclass;
	}
}
