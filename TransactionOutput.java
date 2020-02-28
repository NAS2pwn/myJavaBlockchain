package myJavaBlockchain;

import java.security.PublicKey;

public class TransactionOutput {
	private String hash;
	private String parentHash;
	private PublicKey recipient;
	private double howMany;
	
	public TransactionOutput(String parentHash, PublicKey recipient, double howMany) {
		this.parentHash = parentHash;
		this.recipient = recipient;
		this.howMany = howMany;
		this.hash = Cryptools.plainToSHA256(Cryptools.keyBase64(recipient)+Double.toString(howMany)+parentHash);
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public double howMany() {
		return this.howMany;
	}
	
	public boolean isOwner(PublicKey publicKey) {
		return publicKey == this.recipient;
	}
	
	/*@Override
	public String toString() {
		return "\n"+this.howMany+"\n"+this.recipient.toString()+"\n";
	}*/
}
