package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {

  private EncryptDecryptInterface encryptDecryptInterface = new GPGEncryptDecryptImplementation();

  public int encryptFile(String outputFilePath, String recipient, String filePath) {
    deleteFile(outputFilePath);
    return encryptDecryptInterface.encryptFile(outputFilePath, recipient, filePath);
  }

  private void deleteFile(String outputFilePath) {
    File file = new File(outputFilePath);
    if (file.delete()) {
      System.out.println("File deleted");
    } else {
      System.out.println("Delete failed");
    }
  }

  public int decryptFile(String passphrase, String outputFilePath, String filePath) {
    deleteFile(outputFilePath);
    return encryptDecryptInterface.decryptFile(passphrase, outputFilePath, filePath);
  }

  public int getPublicKey(String recipient) {
    int exitStatus = -1;
    try {
      Process process = new ProcessBuilder("gpg", "--keyserver", "pgp.mit.edu", "--search-keys", "--no-tty", recipient).start();

      exitStatus = process.waitFor();

      BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));

      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }

      String result = output.toString();
      if (!result.isEmpty()) {
        System.out.println(result);
        String[] lines = result.split("\n", 3);
        String firstResult = lines[1];
        String[] words = firstResult.split(" ");
        String keyID = "";
        for (int i = 0; i < words.length && keyID.isEmpty(); i++) {
          String word = words[i];
          if (word.equals("key")) {
            keyID = words[i + 1];
          }
        }
        keyID = keyID.substring(0, keyID.indexOf(","));
        System.out.println(keyID);
        System.out.println();

        process = new ProcessBuilder("gpg", "--keyserver", "pgp.mit.edu", "--recv-keys", "--no-tty", keyID).start();
        exitStatus = process.waitFor();

        reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          output.append(line).append("\n");
        }
        System.out.println(output.toString());
      } else {
        exitStatus = -1;
      }

    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
    return exitStatus;
  }
}
