package myJavaBlockchain;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class Cryptools {
	public static final int MY_HASHFUNC_LENGTH=64;
	public static String plainToSHA256(String what){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash=md.digest(what.getBytes(StandardCharsets.UTF_8));
			BigInteger number = new BigInteger(1, hash); 
			StringBuilder hexString = new StringBuilder(number.toString(16)); 

			while (hexString.length() < MY_HASHFUNC_LENGTH) 
				hexString.insert(0, '0'); 

			return hexString.toString(); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new Error("Unknown hash function");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Error("I donknow");
		}
	}
	
	//On chiffre avec la clé privée
	public static byte[] signECDSA(PrivateKey privateKey,String what) {
		try {
			Signature ecdsa = Signature.getInstance("ECDSA");
			ecdsa.initSign(privateKey);
			ecdsa.update(what.getBytes());
			return ecdsa.sign();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("wtf");
			//Faut mettre l'erreur pour que le return du try suffise sinon eclipse n'est po content
		}
	}
	
	//On vérifie avec la clé publique
	public static boolean verifySignatureECDSA(PublicKey publicKey,byte[] signature,String what) {
		try {
			Signature verifyECDSA = Signature.getInstance("ECDSA");
			verifyECDSA.initVerify(publicKey);
			verifyECDSA.update(what.getBytes());
			return verifyECDSA.verify(signature);
		} catch(Exception e) {
			e.printStackTrace();
			throw new Error("oh no :(");
		}
	}
	
	//Il y a des hex qui ne sont pas des caractères, on doit les convertir en base64
	public static String keyBase64(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	//Alors un arbre de Merkle c'est un arbre où le parent est le hash de ces enfants
	//La racine est au sommet de l'arbre
	
	public static String merkleRootRec(ArrayList<String> hashs) {
		if(hashs.size()==1)
			return hashs.get(0);
		
		ArrayList<String> hashsParents=new ArrayList<>();
		
		for(int i=0;i<hashs.size();i+=2) {
			if(i+1<hashs.size())
				hashsParents.add(Cryptools.plainToSHA256(hashs.get(i)+hashs.get(i+1)));
			else
				hashsParents.add(hashs.get(i));
		}
		
		return merkleRootRec(hashsParents);
	}
	
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		ArrayList<String> hashs=new ArrayList<>();
		for(Transaction tx : transactions)
			hashs.add(tx.getHash());
		
		return merkleRootRec(hashs);
	}
}
