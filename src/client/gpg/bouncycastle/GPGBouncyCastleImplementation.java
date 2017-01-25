package client.gpg.bouncycastle;

import client.EncryptDecryptInterface;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchProviderException;
import java.security.Security;

/**
 * Created by razvan.salajan on 1/25/17.
 */
public class GPGBouncyCastleImplementation implements EncryptDecryptInterface{
    private static final String COMMON_PATH = "resources/";
    private static final String PUBLIC_KEY_PATH = COMMON_PATH + "public_key.asc";
    private static final String PRIVATE_KEY_PATH = COMMON_PATH + "private_key.asc";
    public GPGBouncyCastleImplementation(){
        Security.addProvider(new BouncyCastleProvider());
    }
    @Override
    public int encryptFile(String outputFilePath, String recipient, String filePath) {
        int exitStatus = -1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("gpg", "--armor", "--export", recipient);
            processBuilder.redirectOutput(new File(PUBLIC_KEY_PATH));
            Process process = processBuilder.start();
            exitStatus = process.waitFor();
            System.out.println(exitStatus);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            OpenPgpBouncyCastle.encryptFile(outputFilePath, filePath, PUBLIC_KEY_PATH, true, false);
        } catch (IOException e) {
            exitStatus = -1;
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            exitStatus = -1;
            e.printStackTrace();
        } catch (PGPException e) {
            exitStatus = -1;
            e.printStackTrace();
        }
        return exitStatus;
    }

    @Override
    public int decryptFile(String passphrase, String outputFilePath, String filePath, String recipient) {
        int exitStatus = -1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("gpg", "--armor", "--export-secret-keys", recipient);
            processBuilder.redirectOutput(new File(PRIVATE_KEY_PATH));
            Process process = processBuilder.start();
            exitStatus = process.waitFor();
            System.out.println(exitStatus);
        } catch (IOException ex) {
            exitStatus = -1;
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            exitStatus =-1;
            ex.printStackTrace();
        }
        try {
            OpenPgpBouncyCastle.decryptFile(filePath, PRIVATE_KEY_PATH, passphrase.toCharArray(), outputFilePath);
        } catch (IOException e) {
            exitStatus = -1;
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            exitStatus =-1;
            e.printStackTrace();
        }
        return exitStatus;
    }




}
