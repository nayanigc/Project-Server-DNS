package Request;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Header {

	private int id;
	private int qr;
	private int opcode;
	private boolean aa;
	private boolean tc;
	private boolean rd;
	private boolean ra;
	private static int Z = 0;
	private int rcode;
	private int qdcount;
	private int ancount;
	private int nscount;
	private int arcount;

	public Header(int id, int qr, int opcode, boolean aa, boolean tc, boolean rd, boolean ra, int rcode, int qdcount,
			int ancount, int nscount, int arcount) {
		if (id > 0 || id < 65535) {
			this.id = id;
		}
		this.qr = qr;
		if (opcode < 7) {
			this.opcode = opcode;
		}
		this.aa = aa;
		this.tc = tc;
		if (rd) {
			this.rd = rd;
		}
		this.ra = ra;
		this.rcode = rcode;
		if (qdcount < 65535) {
			this.qdcount = qdcount;
		}
		if (ancount < 65535) {
			this.ancount = ancount;
		}
		if (nscount < 65535) {
			this.nscount = nscount;
		}
		if (arcount < 65535) {
			this.arcount = arcount;
		}
	}
	/**
	 * Cette fonction permet de retourner un Header, le decodage de l'entete prennant le paquet binaire 
	 * et associe les valeurs a l'aide de decalages.
	 * @param header le paquet entete recu
	 * @return Header 
	 * @throws IOException
	 */
	public static Header decode(byte[] header) throws IOException {

		ByteArrayInputStream bis = new ByteArrayInputStream(header);
		DataInputStream dis = new DataInputStream(bis);
		int id = dis.readUnsignedShort();
		int headerOctet = dis.readUnsignedShort();
		int qr = (headerOctet >> 15) & 1;
		int opcode = (headerOctet >> 11) & 15;
		boolean aa = ((headerOctet >> 10) & 1) == 1;
		boolean tc = ((headerOctet >> 9) & 1) == 1;
		boolean rd = ((headerOctet >> 8) & 1) == 1;
		boolean ra = ((headerOctet >> 7) & 1) == 1;
		int rcode = (headerOctet & 15);
		int qdcount = (dis.readUnsignedShort());
		int ancount = (dis.readUnsignedShort());
		int nscount = (dis.readUnsignedShort());
		int arcount = (dis.readUnsignedShort());
		Header h = new Header(id, qr, opcode, aa, tc, rd, ra, rcode, qdcount, ancount, nscount, arcount);
		return h;
	}


	/**
	 * Cette fonction prend le header et le transforme en donn�e binaire 
	 * @return
	 * @throws IOException
	 */
	public byte[] encode() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(6);
		DataOutputStream dos = new DataOutputStream(bos);
		this.writeToOutputStream(dos);
		dos.flush();
		return bos.toByteArray();
	}
	/**
	 * Ecris a la sortie l'entete pour le client
	 * @param dos
	 * @throws IOException
	 */
	public void writeToOutputStream(DataOutputStream dos) throws IOException {
		dos.writeShort(this.getId());
		int octet_34 = 0;
		if (this.getQr() == 1) {
			octet_34 += 1 << 15;
		}
		octet_34 += this.getOpcode() << 11;
		if (this.isAa()) {
			octet_34 += 1 << 10;
		}
		if (this.isTc()) {
			octet_34 += 1 << 9;
		}

		if (this.isRd()) {
			octet_34 += 1 << 8;
		}

		if (this.isRa()) {
			octet_34 += 1 << 7;
		}

		octet_34 += Z << 4;
		octet_34 += this.getRcode();
		dos.writeShort(octet_34);
		dos.writeShort(this.getQdcount());
		dos.writeShort(this.getAncount());
		dos.writeShort(this.getNscount());
		dos.writeShort(this.getArcount());
	}

	/* Affichage du paquet Header */
	public String toString() {
		return "ID = " + this.getId() +
				"\nQR = " + this.getQr() +
				"\nOPCODE = " + this.getOpcode() +
				"\nAA = " + this.isAa() + 
				"\nTC = " + this.isTc() + 
				"\nRD = " + this.isRd() +
				"\nRA = " + this.isRa() +
				"\nZ = " + Z + 
				"\nRCODE = " + this.getRcode() +
				"\nQDCOUNT = " + this.getQdcount() +
				"\nANCOUNT = " + this.getAncount() +
				"\nNSCOUNT = " + this.getNscount() +
				"\nARCOUNT = " + this.getArcount();
	}

	/*************************************GETTEUR AND SETTER******************************************/

	public int getId() {
		return id;
	}

	public int getQr() {
		return qr;
	}

	public void setQr(int qr) {
		this.qr = qr;
	}

	public int getOpcode() {
		return opcode;
	}

	public boolean isAa() {
		return aa;
	}

	public boolean isTc() {
		return tc;
	}

	public boolean isRd() {
		return rd;
	}

	public boolean isRa() {
		return ra;
	}

	public static int getZ() {
		return Z;
	}

	public int getRcode() {
		return rcode;
	}

	public int getQdcount() {
		return qdcount;
	}

	public int getAncount() {
		return ancount;
	}
	public void setAncount(int anc) {
		this.ancount = anc;
	}

	public int getNscount() {
		return nscount;
	}

	public int getArcount() {
		return arcount;
	}
	public void setRcode (int r) {
		rcode = r;
	}
}
