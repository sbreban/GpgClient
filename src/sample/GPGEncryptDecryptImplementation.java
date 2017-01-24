package sample;

import java.io.IOException;

/**
 * Created by sbreban on 1/24/17.
 */
public class GPGEncryptDecryptImplementation implements EncryptDecryptInterface {
  @Override
  public int encryptFile(String outputFilePath, String recipient, String filePath) {
    int exitStatus = -1;
    try {
      Process process = new ProcessBuilder("gpg", "--output", outputFilePath, "-a", "--encrypt", "--recipient", recipient, filePath).start();
      exitStatus = process.waitFor();
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
    return exitStatus;
  }

  @Override
  public int decryptFile(String passphrase, String outputFilePath, String filePath) {
    int exitStatus = -1;
    try {
      Process process;
      if (passphrase != null && !passphrase.isEmpty()) {
        process = new ProcessBuilder("gpg", "--output", outputFilePath, "--decrypt", "--no-tty", "--passphrase", passphrase, filePath).start();
      } else {
        process = new ProcessBuilder("gpg", "--output", outputFilePath, "--decrypt", "--no-tty", filePath).start();
      }
      exitStatus = process.waitFor();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }
    return exitStatus;
  }
}
