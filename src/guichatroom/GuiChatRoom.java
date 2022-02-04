/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guichatroom;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

 
public class GuiChatRoom extends Application {
   Button btn     = new Button("Send");
   Button btn2    = new Button("Save Your Chat");
   Button btn3    = new Button("Open Saved Chat");
   Label lbl      = new Label("Enter Message");
   TextArea msgs  = new TextArea();
   TextField msg  = new TextField();
    Thread th;
    Socket mySocket;
    DataInputStream dis;
    PrintStream ps;
    
    static Vector<GuiChatRoom> client = new Vector<GuiChatRoom>(); 
    @Override
    public void init ()
    {
        try{
        mySocket = new Socket("127.0.0.1", 5001);
        dis  = new DataInputStream(mySocket.getInputStream());
        ps = new PrintStream(mySocket.getOutputStream());
         client.add(this);
        }catch (IOException e) {
            System.out.print("Server Is Down!!");
        }
        
        th = new Thread(new Runnable(){
            @Override
            public void run() 
            {
                while(true)
                 {
                    String reMsg;
                   
                    try {
                         reMsg = dis.readLine();
                         msgs.appendText("\n"+reMsg);
                     
                    } catch (IOException ex) {
                      client.forEach(cl->{
                          Platform.exit();
                          System.exit(0);
                      });
                    }
      
                 }
             }
            
        });
       // th.setDaemon(true);
        th.start();
    }
    /*
     @Override
       public void stop () throws Exception {
           super.stop();
           th.stop();
           dis.close();
           ps.close();
           mySocket.close();
       }
    */
    @Override
    public void start(Stage primaryStage) {
        
        FlowPane fPane = new FlowPane(lbl,msg,btn);
        FlowPane fPane2 = new FlowPane(btn2,btn3);
        fPane.setVgap(15);
        fPane.setMargin(lbl,   new Insets(20, 0, 20, 20));
        fPane.setMargin(msg,   new Insets(20, 0, 20, 20));
        fPane.setMargin(btn,   new Insets(0,0,0,40));
        fPane2.setMargin(btn2, new Insets(0, 0, 0, 20));
        fPane2.setMargin(btn3, new Insets(0, 0, 0, 160));
        BorderPane root = new BorderPane();
        root.setTop(fPane2);
        root.setCenter(msgs);
        root.setBottom(fPane);
        Scene scene = new Scene(root, 450, 350);
        primaryStage.setTitle("Chat Room GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
 
        btn.setDefaultButton(true);
        btn.setOnAction((ActionEvent event) -> {
            if(mySocket != null)
            {
                ps.println(msg.getText());
                 msg.setText("");
            }else{
            Dialog<String> dialog = new Dialog<String>();
            dialog.setTitle("Error");
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.setContentText("Sorry Server Is Down Please Try Later !!");
            dialog.showAndWait();
                 
            }
        });
        
        
      primaryStage.setOnCloseRequest((e)->{
          Platform.exit();
          System.exit(0);
      });
      
      
      btn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser2 = new FileChooser();
                fileChooser2.setTitle("Open");
                fileChooser2.getExtensionFilters().add(new FileChooser.ExtensionFilter("TEXT", "*.txt"));
                String absolutePath = fileChooser2.showOpenDialog(primaryStage).getAbsolutePath();
                try {
                    File fr = new File(absolutePath);
                    Scanner scan = new Scanner(fr) ;
                    while(scan.hasNextLine()){
                        msgs.appendText(scan.nextLine()+"\n");
                    }
                } catch (FileNotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }); 
         
      btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fs = new FileChooser();
                fs.getExtensionFilters().add(new FileChooser.ExtensionFilter("TEXT", "*.txt"));
                File savef = fs.showSaveDialog(null);
             try{
                 try (FileWriter fw = new FileWriter(savef)) {
                     fw.write(msgs.getText());
                 }
             }catch(IOException ex){
                 System.out.println(ex.getMessage());
             }            }
        });
    
    }

 public static void main(String[] args) 
 {
    launch(args);
     
 } 
}
