package myJavaBlockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Wallet {
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private Blockchain blockchain;
	private HashMap<String,TransactionOutput> UTXOs;
	
	public Wallet() {
		this.blockchain=blockchain;
		this.generateKeyPair();
		this.UTXOs=new HashMap<>();
	}
	
	public PublicKey getPublicKey() {
		return this.publicKey;
	}
	
	//Bon pas ouf l'achitecture
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}
	
	public void generateKeyPair() {
		try {
			KeyPairGenerator keyPairGen=KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom secureRandom = new SecureRandom();
			ECGenParameterSpec ecGenParamSpec = new ECGenParameterSpec("secp256k1");
			//La même fonction que Bitcoin hehe
			
			keyPairGen.initialize(ecGenParamSpec, secureRandom);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			
			this.privateKey=keyPair.getPrivate();
			this.publicKey=keyPair.getPublic();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public double calcBalance() {
		double sum=0;
		for (Map.Entry<String, TransactionOutput> entry: Blockchain.entrySetUTXO()){
			TransactionOutput UTXO = entry.getValue();
			if(UTXO.isOwner(this.publicKey)){
				UTXOs.put(entry.getKey(),UTXO);
				sum+=UTXO.howMany();
			}
		}
		//En gros la balance c'est la somme des transactions non consommées
		return sum;
	}
	
	public Transaction sendFunds(PublicKey recipient, double howMany) {
		if(this.calcBalance()<howMany) {
			System.err.println("Operation aborted : you don't have enough funds to send this shit.");
			return null;
		}
		
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		double sum=0;
		for (Map.Entry<String, TransactionOutput> entry: this.UTXOs.entrySet()){
			TransactionOutput UTXO = entry.getValue();
			sum += UTXO.howMany();
			inputs.add(new TransactionInput(UTXO.getHash()));
			if(sum>howMany)
				break;
		}
		
		Transaction myTransaction = new Transaction(this.publicKey,recipient,howMany,inputs);
		myTransaction.calcSignature(privateKey);
		
		for(TransactionInput input : inputs)
			UTXOs.remove(input.getOutputHash());
		
		return myTransaction;
	}
}
