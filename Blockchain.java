package myJavaBlockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

public class Blockchain {
	private static final int DEFAULT_DIFFICULTY=3;
	public static ArrayList<Block> blockchain;
	public static HashMap<String, TransactionOutput> UTXOmap; //Le mettre statique me facilite la vie
	private int difficulty;
	
	public Blockchain() {
		this.difficulty=DEFAULT_DIFFICULTY;
		UTXOmap=new HashMap<>();
		blockchain=new ArrayList<>();
	}
	
	public static TransactionOutput getUTXO(String hash) {
		return UTXOmap.get(hash);
	}
	
	public static void putUTXO(String hash,TransactionOutput output) {
		UTXOmap.put(hash, output);
	}
	
	public static void removeUTXO(String hash) {
		UTXOmap.remove(hash);
	}
	
	public static Set<Entry<String,TransactionOutput>> entrySetUTXO(){
		return UTXOmap.entrySet();
	}
	
	public boolean isBlockchainOK() {
		Block prev, curr;
		
		String zero="";
		for(int i=0;i<difficulty;i++)
			zero+="0";
		
		for(int i=1; i<blockchain.size(); i++) {
			prev=blockchain.get(i-1);
			curr=blockchain.get(i);
			
			if(curr.getNonce()<=0||!curr.getHash().substring(0, this.difficulty).equals(zero)) {
				System.err.println("There is an unmined block !!!");
				return false;
			}
			
			if(prev.getHash()!=curr.getPrevHash()) {
				System.err.println("Previous hash does not match");
				return false;
			}
	
			if(!curr.getHash().equals(curr.calcHash())) {
				System.err.println("Hash non-valid");
				return false;
			}
		}
		
		return true;
	}
	
	public static void addBlock(Block block) {
		block.mineBlock(DEFAULT_DIFFICULTY);
		blockchain.add(block);
	}
	
	@Override
	public String toString() {
		JSONObject json=new JSONObject();
		
		for(Block block : blockchain)
			json.append(Integer.toString(block.getID()), block.getJSONObject());
		
		return json.toString();
	}
}
