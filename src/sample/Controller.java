package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Controller {
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
