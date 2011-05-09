package com.android.gumshoe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

public class GumshoeMethodAppendix {

	private static GumshoeMethodAppendix INSTANCE;
	 
    // Private constructor prevents instantiation from other classes
    private GumshoeMethodAppendix() {}
 
    // Public method to get the reference to our singleton
    public static GumshoeMethodAppendix getInstance() {
    	if (INSTANCE == null)
    		INSTANCE = new GumshoeMethodAppendix();
    	
    	return INSTANCE;
    }

	// Methods Go here
    public void wipe(File file, int passes) throws Exception {
    	byte[] data;
    	if (file.exists()) {
                SecureRandom random = new SecureRandom();
                RandomAccessFile raf = new RandomAccessFile(file, "rws");
                raf.getFilePointer();
                byte[] buf = new byte[1024];
                byte[] rndBuf = new byte[1024];
        		int bytesRead = 0;
        		int totalRead = 0;
        		raf.seek(totalRead);
                for (int i = 0; i < passes; i++){
                	while ((bytesRead = raf.read(buf)) >= 0){
		                random.nextBytes(rndBuf);
		                raf.seek(totalRead);
		                raf.write(rndBuf);
		                totalRead += bytesRead;
                	}
                }
                raf.close();
                String rndName = Base64.encodeToString(rndBuf, 0).substring(0,7);
                file.renameTo(new File(file.getPath().substring(0, file.getPath().lastIndexOf("/")+1), 
                		rndName));
                new File(file.getPath().substring(0, file.getPath().lastIndexOf("/")+1), rndName).delete();
        }
	}
    
    public void copyFile(File inFile, String fileName) throws IOException{
        InputStream in = new FileInputStream(inFile);
        String newFileName = inFile.getPath().substring(0, inFile.getPath().lastIndexOf("/"))+"/"+fileName;
        OutputStream out = new FileOutputStream(newFileName);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public void moveFile(File inFile, String newPath) {
        File dir = new File(newPath);
        inFile.renameTo(new File(dir, inFile.getName()));
    }
    
    public void renameFile(File inFile, String renameFile) {
    	String newFileName = inFile.getPath().substring(0, inFile.getPath().lastIndexOf("/"))+"/"+renameFile;
    	File newName = new File(newFileName);
    	inFile.renameTo(newName);
    }
    
    // Encrypt text input
	// Takes converted input and key (md5 hash of pass phrase)
	public String encryptTxt(String content, byte[] key, Context context)throws Exception {
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		byte[] iv = getIV(context);
		IvParameterSpec ips = new IvParameterSpec(iv);
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] out = content.getBytes(("UTF-8"));
		byte[] enc = c.doFinal(out);
		return Base64.encodeToString(enc, 0);
	}
	
	public String decryptTxt(String content, byte[] key, Context context) throws Exception{
		Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		byte[] iv = getIV(context);
		IvParameterSpec ips = new IvParameterSpec(iv);
		c.init(Cipher.DECRYPT_MODE, k);
		String out;
		byte[] enc = c.doFinal(Base64.decode(content, 0));
		return new String(enc, "UTF8");
	}
	
	public void encryptFile(File file, byte[] key, Context context) throws Exception {

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		byte[] iv = getIV(context);
		IvParameterSpec ips = new IvParameterSpec(iv);
		c.init(Cipher.ENCRYPT_MODE, k, ips);
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		byte[] buf = new byte[1024];
		int bytesRead = 0;
		int totalBytes = 0;
		byte[] output;
		while ((bytesRead = raf.read(buf)) >= 0) {
			output = c.update(buf, 0, bytesRead);
			raf.seek(totalBytes);
			raf.write(output);
			totalBytes += output.length;
			raf.seek(totalBytes+16);
		}
		output = c.doFinal();
		raf.seek(totalBytes);
		raf.write(output);
		raf.getFD().sync();
		raf.close();
		file.renameTo(new File(file.getPath() + ".enc"));

	}
	
	public void decryptFile(File file, byte[] key, Context context) throws Exception {

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec k = new SecretKeySpec(key, "AES");
		byte[] iv = getIV(context);
		IvParameterSpec ips = new IvParameterSpec(iv);
		c.init(Cipher.DECRYPT_MODE, k, ips);
		RandomAccessFile raf = new RandomAccessFile(file.getPath(), "rw");
		byte[] buf = new byte[1024];
		int bytesRead = 0;
		int totalBytes = 0;
		byte[] output;
		while ((bytesRead = raf.read(buf)) >= 0) {
			output = c.update(buf, 0, bytesRead);
			raf.seek(totalBytes);
			raf.write(output);
			totalBytes += output.length;
			raf.seek(totalBytes+16);
		}
		output = c.doFinal();
		raf.seek(totalBytes);
		raf.write(output);
		raf.setLength(totalBytes+output.length);
		raf.getFD().sync();
		raf.close();
		file.renameTo(new File(file.getPath().substring(0, file.getPath().lastIndexOf('.'))));
	}
	
	public byte[] getIV(Context context) throws Exception{
		TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String sIV = "x"+Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		String hashIMEI = new String (hashkey(mTelephonyMgr.getLine1Number()), 0).substring(0, 16);
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i < 16; i++){
			stringBuilder.append((char)(sIV.charAt(i) ^ hashIMEI.charAt(i)));
		}
		//String tmp = Base64.encodeToString(stringBuilder.toString().getBytes(), 0);
		return Base64.encodeToString(stringBuilder.toString().getBytes(), 0).substring(0,16).getBytes();
			
	}
	
	public void binaryHider(String fileName) throws Exception {
		byte[] fileHeader = { 0x7F, 0x45, 0x4C, 0x46 };
		byte[] bytes = new byte[1024];
		int read = 0;
		FileInputStream fileInStream = new FileInputStream(fileName+".enc");
		FileOutputStream fileOutStream = new FileOutputStream(fileName, true);
		fileOutStream.write(fileHeader, 0 , 2);
		while((read = fileInStream.read(bytes))!= -1){
			fileOutStream.write(bytes, 0, read);
		}
		fileInStream.close();
		fileOutStream.flush();
		fileOutStream.close();	
		File shredOld = new File(fileName+".enc");
		wipe(shredOld, 1);
	}
	
	public byte[] hashkey (String key) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] output = messageDigest.digest(key.getBytes("UTF-8"));
		return output;
	}
	
	public String typeLookup(String type){
		String outType;
		if (type.equals("1")){ outType = "Incoming"; }
		else if (type.equals("2")) {
			outType = "Outgoing";
		} else {
			outType = "Missed";
		}
		return outType;
	}
	
	public String dateCovnert(String dateIn){
		long convert = Long.parseLong(dateIn);
        Date converted = new Date(convert);
        return converted.toString();
	}
	
	public String contactLookup(String number, Context context) {
		Uri lookup = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));  
		Cursor res = context.getContentResolver().query(lookup, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (res.moveToFirst()) {
			String name = res.getString(res
					.getColumnIndex(Contacts.DISPLAY_NAME));
			return name;
			}
			return number;
		}
	
	public void getDB(File dir, List<String> dbList){
		File[] dirList  = dir.listFiles();
		for(int i = 0; i < dirList.length; i++) {
			if(dirList[i].isDirectory()) {
				getDB(dirList[i], dbList);
			} else if(dirList[i].getName().matches("*.db")) {
	        	dbList.add(dirList[i].getPath());
	        }
	      }
	    }
	
	public void terminalParse(List<String> dbList) throws Exception{
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		DataInputStream osRes = new DataInputStream(process.getInputStream());
		for (String single : dbList) {
		   os.writeBytes(single + "\n");
		   os.flush();
		}
		os.writeBytes("exit\n");
		os.flush();
		process.waitFor();
		}
	
	public void searchAndShred(File rootDir, String extension) throws Exception{
		File[] dirList  = rootDir.listFiles();	
		for(int i = 0; i < dirList.length; i++) { 
			if(dirList[i].isDirectory()) {
				searchAndShred(dirList[i], extension);
			} else if(dirList[i].getName().substring(dirList[i].getName().lastIndexOf(".")+1).matches(extension)) {
	        	GumshoeMethodAppendix.getInstance().wipe(dirList[i], 1);
	        } else {
	        	Log.v("dbg:", dirList[i].getName().substring(dirList[i].getName().lastIndexOf(".")+1));
	        }
	      }
	    }
	
	public void makeBackup(Context context, String fileName, Integer type, boolean encrypt, String passphrase) throws Exception{
		List<String> cmdList = new ArrayList<String>();
		String filenamePath;
		if (type == 0){
			cmdList.add("tar cf /sdcard/"+fileName+".tar /data");
			filenamePath = "/sdcard/"+fileName+".tar";
		} else if (type == 1){
			cmdList.add("makeyaffs2image /dev/mtd/mtd5 /sdcard/"+fileName+".img");
			filenamePath = "/sdcard/"+fileName+".img";
		} else {
			cmdList.add("tar cf /sdcard/"+fileName+".tar /data/data");
			filenamePath = "/sdcard/"+fileName+".tar";
		}
		File fName = new File(filenamePath);
		GumshoeMethodAppendix.getInstance().terminalParse(cmdList);
		if (encrypt){
			GumshoeMethodAppendix.getInstance().encryptFile(fName, GumshoeMethodAppendix.getInstance().hashkey(passphrase), context);
		}
	}
}
