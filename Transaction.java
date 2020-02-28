package myJavaBlockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
	public static int currentIndex=0;
	
	private int id;
	private String hash;
	
	private PublicKey sender;
	private PublicKey recipient;
	
	private byte[] signature;
	
	private double howMany;
	
	private ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	public Transaction(PublicKey sender,PublicKey recipient,double howMany,ArrayList<TransactionInput> inputs) {
		this.id=currentIndex;
		Transaction.currentIndex++;
		this.sender=sender;
		this.recipient=recipient;
		this.howMany=howMany;
		this.hash=this.calcHash();
		this.inputs=inputs;
	}
	
	public double howMany() {
		return this.howMany;
	}
	
	public TransactionOutput getOutput(int i) {
		return this.outputs.get(i);
	}
	
	public void setHash(String hash) {
		this.hash=hash;
	}
	
	public PublicKey getSender() {
		return this.sender;
	}
	
	public PublicKey getRecipient() {
		return this.recipient;
	}
	
	public void addOutput(TransactionOutput txOut) {
		this.outputs.add(txOut);
	}
	
	public boolean process() {
		if(!this.verifySignature()) {
			System.err.println("Signature verification failed !!!");
			return false;
		}
		
		for(TransactionInput in : this.inputs)
			in.setUTXO(Blockchain.getUTXO(in.getOutputHash()));
		
		double rest=this.getInputsValue()-this.howMany;
		
		this.outputs.add(new TransactionOutput(this.hash,this.recipient,this.howMany));
		this.outputs.add(new TransactionOutput(this.hash,this.sender,rest));
		
		//On ajoute les non depensées
		for(TransactionOutput out : outputs)
			Blockchain.putUTXO(out.getHash(),out);
		
		//On retire les dépensées
		for(TransactionInput in : inputs) {
			if(in.getUTXO() != null)
				Blockchain.removeUTXO(in.getOutputHash());
		}
		
		return true;
	}
	
	public double getInputsValue() {
		double sum=0;
		for(TransactionInput in : this.inputs)
			if(in.getUTXO() != null)
				sum+=in.getUTXO().howMany();
		
		return sum;
	}
	
	public void calcSignature(PrivateKey privateKey) {
		String what = Cryptools.keyBase64(sender)+Cryptools.keyBase64(recipient)+Double.toString(howMany);
		this.signature=Cryptools.signECDSA(privateKey, what);
	}
	
	public boolean verifySignature() {
		String what = Cryptools.keyBase64(sender)+Cryptools.keyBase64(recipient)+Double.toString(howMany);
		return Cryptools.verifySignatureECDSA(this.sender, this.signature, what);
	}
	
	private String calcHash() {
		return Cryptools.plainToSHA256(Integer.toString(this.id)+Cryptools.keyBase64(this.sender)+Cryptools.keyBase64(this.recipient)+Double.toString(this.howMany));
	}
	
	public String getHash() {
		return this.hash;
	}
}
