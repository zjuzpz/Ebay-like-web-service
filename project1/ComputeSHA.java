import java.security.*;
import java.io.*;

public class ComputeSHA {

	public static void main(String[] args) throws Exception{
		
		MessageDigest md = MessageDigest.getInstance("SHA-1");	
		String file = args[0];

		//Get the input file
		FileInputStream fis = new FileInputStream(file);
		byte[] byteArr = new byte[1024];		
		int p = 0;
		//Read the data
		while ((p = fis.read(byteArr)) != -1) {
			md.update(byteArr, 0, p);
		} 
		
		//Compute the hash computation
		byte[] mdBytes = md.digest();
		//Convert to hex format
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i < mdBytes.length; i++) {
			sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		System.out.println(sb.toString());
		fis.close();
	}
}
