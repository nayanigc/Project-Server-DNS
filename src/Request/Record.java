package Request;

import java.util.HashMap;

public class Record {
	HashMap<String, Answer> record = new HashMap<>();
	/**
	 * Cette fonction permet d'ajouter dans une HashMap un nouveau nom de domaine
	 * et la reponse de la requete
	 * @param domain le nom du Domaine
	 * @param answer le paquet reponse
	 */
	public void add(String domain, Answer answer) {
		record.put(domain,answer);
	}
	/**
	 * Renvoie la reponse du domain si il existe 
	 * @param domain
	 * @return
	 */
	public Answer get(String domain) {
		return record.get(domain);
	}
}