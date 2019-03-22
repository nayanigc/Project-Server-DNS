package Request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet {
	public static byte HEADER_SIZE = 12;
	private Header header;
	private Question question;
	private Answer answer;

	public Packet(Header header, Question question) {
		this.header = header;
		this.question = question;

	}

	public Packet(Header header, Question question, Answer answer) {
		this.header = header;
		this.question = question;
		this.answer = answer;
	}

	/**
	 * Cette fonction permet de decoder le paquer recu en binaire 
	 * et le transformer en un Packet contanant l'entete et la question en les decodant aussi
	 * @param data donnees binaires recu
	 * @return Paquet
	 * @throws IOException
	 */
	public static Packet decode(byte[] data) throws IOException {

		byte[] headerBytes = new byte[HEADER_SIZE];
		for (int i = 0; i < HEADER_SIZE; i++) {
			headerBytes[i] = data[i];
			System.out.print(headerBytes[i] + " ");
		}

		Header h = Header.decode(headerBytes);

		int remainSize = data.length - HEADER_SIZE + 1;

		byte[] questionByte = new byte[remainSize];
		int j = 0;
		for (int i = HEADER_SIZE; i < data.length; i++) {
			questionByte[j] = data[i];
			System.out.print(questionByte[j] + " ");
			j++;
		}

		Question q = Question.decode(questionByte);
		Packet p = new Packet(h, q);

		return p;
	}

	/**
	 * Cette fonction prend le paquet et le transforme en un paquet binaire
	 * @return
	 * @throws IOException
	 */

	public byte[] encode() throws IOException {

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(b);

		if (header != null) {
			d.write(header.encode());
		}

		if (question != null) {
			d.write(question.encode());
		}
		if (answer != null ) {
			d.write(answer.encode());
		}
		return b.toByteArray();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Header:\n");
		sb.append("-------------------------\n");
		sb.append(this.getHeader().toString());
		sb.append("\n\n");
		sb.append("Question:\n");
		sb.append("--------------------------\n");
		sb.append(this.getQuestion().toString());
		sb.append("\n");
		return sb.toString();
	}
	/*********************************************************************GETTER AND SETTER ***************************************************************************/
	public Header getHeader() {
		return header;
	}

	public Question getQuestion() {
		return question;
	}

	public Answer getAnswer() {
		return answer;
	}
}
