package myJavaBlockchain;

public class TransactionInput {
	private String outputHash;
	private TransactionOutput UTXO;
	//Modèle Unspent T(X)ransaction Output, comme bitcoin
	
	public TransactionInput(String outputHash) {
		this.outputHash=outputHash;
	}
	
	public String getOutputHash() {
		return outputHash;
	}
	
	public TransactionOutput getUTXO() {
		return this.UTXO;
	}
	
	public void setUTXO(TransactionOutput utxo) {
		this.UTXO=utxo;
	}
}