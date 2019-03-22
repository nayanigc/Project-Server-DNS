Serveur DNS

Introduction
Le système de noms de domaine (DNS) est un service informatique utilisé pour traduire les noms de domaine
 internet en adresse IP ou autres enregistrements. Dans notre cas, nous avons réalisé un serveur DNS restreint 
uniquement aux réponses de requêtes de type A (associe un nom à une adresse IP) et de type PTR (une adresse IP 
associée à un nom d'hôte) dans des datagrammes UDP par le port 53.

Comment tester ?
Lancer deux invités de commandes 
Dans l’un Exécuter en ligne de commandes «java -jar DNSServer.jar » dans le chemin parent
 où se trouve le fichier afin de lancer le serveur DNS du projet. 
Dans l’autre invite de commandes nous utiliserons le client par ligne de commande « nslookup ». 
Nslookup utilise le serveur de domaine actuellement configuré dans le système.
En effet lorsque vous taper « nslookup un_nom_de_domaine » il interrogera par défaut votre système DNS.
Pour utiliser le serveur DNS du projet, vous devez changer le serveur DNS en utilisant l’adresse IP du serveur
« nslookup nom_de_domain 127.0.0.1 »

Exemple "nslookup google.fr 127.0.0.1"
ou "nslookup 8.8.8.8 127.0.0.1"
Dans l'invites de commande ou le serveur DNS s'éxecute une série de 0 s'affiche ne prenait pas en compte.
Lorsque la requête est terminée les informations transis sont situé au dessus de la serie de 0 que vous pouvez le dérouler ver le haut.



