 
package guichatroom;
 
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMulti {
    ServerSocket mainSocket;
   public ServerMulti()
   {
       try {
           mainSocket = new ServerSocket(5001);
           while(true)
           {
               Socket s = mainSocket.accept();
               new ChatHandler(s);
           }
       } catch (IOException ex) {
           
           
       }
   }
   
   public static void main(String[] args){
       new ServerMulti();
   }
}
