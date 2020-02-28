package myJavaBlockchain;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Lanceur {

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		
		Blockchain myFirstBlockchain=new Blockchain();
		
		Block genesisBlock = new Block("0");
		
		Wallet coinbase=new Wallet(); //C'est pour introduire les coins dans le réseau
		Wallet walletBob=new Wallet();
		Wallet walletAlice=new Wallet();
		
		System.out.println(walletAlice.getPublicKey());
		
		System.out.println(coinbase.getPublicKey());
		
		Transaction genesisTransaction=new Transaction(coinbase.getPublicKey(), walletAlice.getPublicKey() , 1000, null);
		genesisTransaction.calcSignature(coinbase.getPrivateKey());
		genesisTransaction.setHash("0");
		genesisTransaction.addOutput(new TransactionOutput(genesisTransaction.getHash(), genesisTransaction.getRecipient(), genesisTransaction.howMany()));
		Blockchain.UTXOmap.put(genesisTransaction.getOutput(0).getHash(),genesisTransaction.getOutput(0));
		
		genesisBlock.addTransaction(genesisTransaction);
		Blockchain.addBlock(genesisBlock);
		
		/*System.out.println(Blockchain.UTXOmap);*/
		
		//testing
		Block block1 = new Block(genesisBlock.getHash(), "times 15-11-18 : blockchain Océania dysfonctionnelle, rectifier");
		System.out.println("\nwalletAlice " + walletAlice.calcBalance()+"\nwalletBob " + walletBob.calcBalance());
		
		block1.addTransaction(walletAlice.sendFunds(walletBob.getPublicKey(), 44));
		Blockchain.addBlock(block1);
		System.out.println("\nwalletAlice " + walletAlice.calcBalance()+"\nwalletBob " + walletBob.calcBalance());
		System.out.println(block1);
		Block block2 = new Block(block1.getHash());
		

		block2.addTransaction(walletAlice.sendFunds(walletBob.getPublicKey(), 100000));
		System.out.println("\nwalletAlice " + walletAlice.calcBalance()+"\nwalletBob " + walletBob.calcBalance());

		block2.addTransaction(walletBob.sendFunds(walletAlice.getPublicKey(), 33));
		System.out.println("\nwalletAlice " + walletAlice.calcBalance()+"\nwalletBob " + walletBob.calcBalance());
		
		Blockchain.addBlock(block2);
		System.out.println(Cryptools.getMerkleRoot(block2.getTransactions()));
		System.out.println(Blockchain.blockchain);
		
		
	}

}
