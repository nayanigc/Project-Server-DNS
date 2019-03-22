package Request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Answer {

	private String rname;
	private int rtype;
	private int rclass;
	private int ttl; 
	private int rdlength;
	private String rdata;

	public Answer (String rname, int rtype, int rclass, int ttl, int rdlength, String rdata) {
		this.rname = rname ;
		if (rtype == 1 || rtype == 12 ) {
			this.rtype = rtype;
		}
		if (rclass == 1 ) {
			this.rclass = rclass;
		}
		this.ttl = ttl;
		this.rdlength = rdlength;
		this.rdata = rdata;
	}

	/**
	 * Transforme la reponse en donnees binaire
	 * @return
	 * @throws IOException
	 */
	public byte[] encode () throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream ();
		DataOutputStream dos = new DataOutputStream (bos);
		this.writeOutputStream(dos);
		dos.flush ();
		byte[] barray = bos.toByteArray();

		System.out.println("FINAL ENCODED ANSWER");
		for (byte b : barray) {
			System.out.print(b + " ");
		}
		System.out.println();

		return barray;

	}
	/**
	 * Ecrit a la sortie la reponse pour le client 
	 * @param dos
	 * @throws IOException
	 */
	public void writeOutputStream (DataOutputStream dos) throws IOException {
		String [] labels = this.rname.split("\\.");
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
		dos.writeShort(this.rtype);
		dos.writeShort(this.rclass);
		dos.writeInt(this.ttl);
		System.out.println("RDLENGTH " + this.rdlength);
		dos.writeShort(1);

		System.out.println("ROW RDATA " + this.rdata);

		if (rtype == 1) {
			labels = this.rdata.split("\\.");
			for (int i = 0; i < labels.length; i++) {
				dos.writeByte(Integer.parseInt(labels[i]));
			}
		} else if (rtype == 12) {

			labels = this.rdata.split("\\.");
			for (int i = 0; i < labels.length; i++) {
				byte [] label = labels[i].getBytes ();
				int length = label.length;
				if(length > 63) {
					throw new IllegalArgumentException ();
				}
				dos.writeByte (label.length);
				dos.write(label);
			}

			dos.writeByte (0);
		} else {

		}
	}

	public String toString(){
		return 		"RNAME : " 		+ this.getRname() + 
				"\nRTYPE : " 	+ this.getRtype() +
				"\nRCLASS : " 	+ this.getRclass()+
				"\nRTTL : " 	+ this.getTtl() +
				"\nRDLENGTH : " 	+ this.getRdlength()+
				"\nRDATA : " 	+ this.getRdata();
	}
	/*************************************GETTEUR AND SETTER******************************************/

	public String getRname() {
		return rname;
	}

	public int getRtype() {
		return rtype;
	}

	public int getRclass() {
		return rclass;
	}

	public int getTtl() {
		return ttl;
	}

	public int getRdlength() {
		return rdlength;
	}

	public String getRdata() {
		return rdata;
	}
}
