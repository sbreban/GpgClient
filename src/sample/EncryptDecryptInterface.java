package sample;

/**
 * Created by sbreban on 1/24/17.
 */
public interface EncryptDecryptInterface {
  int encryptFile(String outputFilePath, String recipient, String filePath);
  int decryptFile(String passphrase, String outputFilePath, String filePath);
}
