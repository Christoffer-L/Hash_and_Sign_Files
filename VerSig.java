import java.io.*;
import java.security.*;
import java.security.spec.*;
 
class VerSig 
{
	private String pk;
	private String sig;
	private String FileToVerify;
	public VerSig(String FileToVerify) 
	{
		pk="pkey";
		sig="sig";
		this.FileToVerify=FileToVerify;
	}
	
    public boolean verify() 
    {
        /* Verify a DSA signature */
       try
       {
 
            /* import encoded public key */
            FileInputStream keyfis = new FileInputStream(pk);
            byte[] encKey = new byte[keyfis.available()];  
            keyfis.read(encKey);
 
            keyfis.close();
 
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
 
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
 
            /* input the signature bytes */
            FileInputStream sigfis = new FileInputStream(sig);
            byte[] sigToVerify = new byte[sigfis.available()]; 
            sigfis.read(sigToVerify);
 
            sigfis.close();
 
            /* create a Signature object and initialize it with the public key */
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(pubKey);
 
            /* Update and verify the data */
            FileInputStream datafis = new FileInputStream(FileToVerify);
            BufferedInputStream bufin = new BufferedInputStream(datafis);
 
            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) 
            {
                len = bufin.read(buffer);
                sig.update(buffer, 0, len);
                };
 
            bufin.close();
 
 
            boolean verifies = sig.verify(sigToVerify);
            
            System.out.println("Public key: ");
            System.out.print(pubKey);
            System.out.println("\nsignature verifies: " + verifies);
            
            return verifies;
 
       } 
       catch (Exception e) 
       {
        	System.out.println("\nsignature verifies: " + false);
        	return false;

        } 
    }
 
}