import java.io.*;
import java.security.*;
 
class GenSig {
	char [] pass;
	private String ksName;
	private String cerName;
	private String FileToSign;
	
	public GenSig(String FileToSign) {
		pass = "password".toCharArray();
		ksName = "keystore";
		cerName = ""; // Insert your certificate name
		this.FileToSign=FileToSign;
		
	}
 
    public boolean sign() {
 
        /* Generate a DSA signature */
 
        try
        {	
        	KeyStore keyStore = KeyStore.getInstance("JKS");
        	FileInputStream ksfis = new FileInputStream(ksName);
        	BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
        	keyStore.load(ksbufin, pass);
        	
        	PrivateKey priv = (PrivateKey) keyStore.getKey(cerName, pass);
        	
        	java.security.cert.Certificate cert = keyStore.getCertificate(cerName);
        	
        	PublicKey pubKey = cert.getPublicKey();
 
 
            /* Create a Signature object and initialize it with the private key */
 
            Signature rsa = Signature.getInstance("SHA1withRSA"); 
 
            rsa.initSign(priv);
 
            /* Update and sign the data */
 
            FileInputStream fis = new FileInputStream(FileToSign);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                rsa.update(buffer, 0, len);
                };
 
            bufin.close();
 
            /* Now that all the data to be signed has been read in, 
                    generate a signature for it */
 
            byte[] realSig = rsa.sign();
 
         
            /* Save the signature in a file */
            FileOutputStream sigfos = new FileOutputStream("sig");
            sigfos.write(realSig);
 
            sigfos.close();
 
 
            /* Save the public key in a file */
            byte[] key = pubKey.getEncoded();
            FileOutputStream keyfos = new FileOutputStream("pkey");
            keyfos.write(key);
 
            keyfos.close();
            return true;
 
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return false;
        }
 
    }
 
}