 
package guichatroom;
 
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatHandler extends Thread {
    DataInputStream dis;
    PrintStream ps;
    Socket cs;
static Vector<ChatHandler> clientsVector = new Vector<ChatHandler>();

public ChatHandler(Socket cs)
{
    this.cs = cs;
       try{
           dis = new DataInputStream(cs.getInputStream());
           ps = new PrintStream(cs.getOutputStream());
           clientsVector.add(this);
           start();
       } catch (Exception ex) {
           
       }
   
}
 public void sendMessageToAll(String msg)
    {
        for(ChatHandler ch : clientsVector)
        {
            ch.ps.println("Client "+this.getId() + ": " +msg);
        }
    }
 
    @Override
    public void run()
    {
        while(true)
        {
            try {
               String str = dis.readLine();
               sendMessageToAll(str);
            } catch(IOException ex) {
                
                try {
                      dis.close();
                      ps.close();
                      cs.close();
                      clientsVector.remove(this);
                      this.stop();

                } catch (IOException ex1) {
                    Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
              
    
            }
        }
    }
}
