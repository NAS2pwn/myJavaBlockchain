package myJavaBlockchain;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.json.JSONObject;

public class Block {
	private static int currentIndex=0;
	private static final int DIFFICULTY_THRESHOLD=Cryptools.MY_HASHFUNC_LENGTH-5;
	private int id;
	private String hash;
	private String prevHash;
	private long timestamp;
	private String data;
	private int nonce;
	private ArrayList<Transaction> transactions;
	
	public Block(String prevHash,String data) {
		this.id=Block.currentIndex;
		Block.currentIndex++;
		this.prevHash=prevHash;
		this.timestamp=new Date().getTime();
		this.data=data;
		this.nonce=0;
		this.hash=this.calcHash();
		this.transactions=new ArrayList<>();
	}
	
	public Block(String prevHash) {
		this.id=Block.currentIndex;
		Block.currentIndex++;
		this.prevHash=prevHash;
		this.timestamp=new Date().getTime();
		this.data="";
		this.nonce=0;
		this.hash=this.calcHash();
		this.transactions=new ArrayList<>();
	}
	
	public ArrayList<Transaction> getTransactions(){
		return this.transactions;
	}
	
	public int getID() {
		return this.id;
	}
	
	public String calcHash(){
		return Cryptools.plainToSHA256(Integer.toString(this.id)+this.data+this.prevHash+Long.toString(this.timestamp)+Integer.toString(this.nonce));
	}
	
	public void mineBlock(int difficulty) { //Hashcash PoW
		if(difficulty<1 || difficulty>DIFFICULTY_THRESHOLD)
			throw new Error("The difficulty must be included between 0 and "+DIFFICULTY_THRESHOLD);
		
		//Random r = new Random();
		
		String zero="";
		for(int i=0;i<difficulty;i++)
			zero+="0";
		
		do {
			this.nonce++;
			this.hash=this.calcHash();
			//System.out.println(this.hash);
		} while(!this.hash.substring(0, difficulty).equals(zero));
		
		this.hash=this.calcHash();
		System.out.println("Block mined dude :) "+this.hash);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if (transaction==null) //NUL NUL NUL!!!!!
			return false;
		
		if(this.prevHash != "0") { //Si le bloc précédent est po la génèse
			if(transaction.process()!=true) {
				System.err.println("Operation aborted : transaction failed");
				return false;
			}
		}
		
		this.transactions.add(transaction);
		System.out.println("Success : Transaction added to block :) :D :) :D");
		return true;
	}
	
	public JSONObject getJSONObject() {
		JSONObject json=new JSONObject();
		json.append("id", this.id);
		json.append("timestamp", this.timestamp);
		json.append("prevHash", this.prevHash);
		json.append("hash", this.hash);
		json.append("data", this.data);
		json.append("nonce", this.nonce);
		return json;
	}
	
	@Override
	public String toString() {
		return this.getJSONObject().toString();
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public String getPrevHash() {
		return this.prevHash;
	}
	
	public int getNonce() {
		return this.nonce;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
