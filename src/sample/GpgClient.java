package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GpgClient extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Controller controller = new Controller();

    primaryStage.setTitle("GPG Client");

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setPercentWidth(35.0);
    grid.getColumnConstraints().add(columnConstraints);

    Scene scene = new Scene(grid, 300, 275);
    primaryStage.setScene(scene);

    Text scenetitle = new Text("Welcome");
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    grid.add(scenetitle, 0, 0, 2, 1);

    Label userName = new Label("Email:");
    grid.add(userName, 0, 1);

    TextField userTextField = new TextField();
    grid.add(userTextField, 1, 1);

    Label pw = new Label("Passphrase:");
    grid.add(pw, 0, 2);

    PasswordField pwBox = new PasswordField();
    grid.add(pwBox, 1, 2);

    Label fileLabel = new Label("File:");
    grid.add(fileLabel, 0, 4);

    TextField fileTextField = new TextField();

    final FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

    final Button openButton = new Button("...");

    HBox fileOpenBox = new HBox();
    fileOpenBox.getChildren().add(fileTextField);
    fileOpenBox.getChildren().add(openButton);
    grid.add(fileOpenBox, 1, 4);

    openButton.setOnAction(
        e -> {
          File file = fileChooser.showOpenDialog(primaryStage);
          if (file != null) {
            fileTextField.setText(file.getAbsolutePath());
          }
        });

    Button encryptButton = new Button("Encrypt");
    HBox hbBtn = new HBox(10);
    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    hbBtn.getChildren().add(encryptButton);
    grid.add(hbBtn, 1, 5);

    final Text actiontarget = new Text();
    grid.add(actiontarget, 1, 6);

    encryptButton.setOnAction(e -> {
      actiontarget.setFill(Color.FIREBRICK);

      String recipient = userTextField.getText();
      String filePath = fileTextField.getText();
      String outputFilePath = filePath + ".gpg";

      int exitStatus = controller.encryptFile(outputFilePath, recipient, filePath);
      if (exitStatus == 0) {
        actiontarget.setText("Success");
      } else {
        actiontarget.setText("Failure");
      }
    });

    Button decryptButton = new Button("Decrypt");
    hbBtn.getChildren().add(decryptButton);

    decryptButton.setOnAction(e -> {
      actiontarget.setFill(Color.FIREBRICK);

      String passphrase = pwBox.getText();
      String filePath = fileTextField.getText();
      String outputFilePath = filePath.substring(0, filePath.indexOf(".gpg"));

      int exitStatus = controller.decryptFile(passphrase, outputFilePath, filePath);
      if (exitStatus == 0) {
        actiontarget.setText("Success");
      } else {
        actiontarget.setText("Failure " + exitStatus);
      }
    });

    Button getKeyButton = new Button("Get Key");
    HBox getKeyHbBtn = new HBox(10);
    getKeyHbBtn.setAlignment(Pos.BOTTOM_RIGHT);
    getKeyHbBtn.getChildren().add(getKeyButton);
    grid.add(getKeyHbBtn, 0, 5);

    getKeyButton.setOnAction(e -> {
      actiontarget.setFill(Color.FIREBRICK);

      String recipient = userTextField.getText();

      int exitStatus = controller.getPublicKey(recipient);
      if (exitStatus == 0) {
        actiontarget.setText("Success! Public key received");
      } else {
        actiontarget.setText("Failure! Public key not found " + exitStatus);
      }
    });

    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
