package parcial;
 
import java.net.InetAddress;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TCPCliente {
    String SERVERIP="127.0.0.1";
    int SERVERPORT=5000;
    BufferedReader in;
    PrintWriter out;
    boolean corriendo = false;
    alRecibirMensaje escuchador;
    String mensaje;
    TCPCliente(String ip,alRecibirMensaje Listener){
        SERVERIP = ip;
        escuchador = Listener;
    }
    public void run(){
        corriendo = true;
        try{
            InetAddress ipAddress = InetAddress.getByName(SERVERIP);
            Socket server = new Socket(ipAddress,SERVERPORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(server.getOutputStream())));
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            
            while(corriendo){
                mensaje = in.readLine();
                if(mensaje!=null && escuchador!=null){
                    escuchador.mensajeRecibido(mensaje);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public interface alRecibirMensaje {
        public void mensajeRecibido(String mensaje);
    }
    public void enviarMensaje(String mensaje){//out hace referencia al servidor , enviando desde cliente al servidor
        if(out!=null && !out.checkError()){
            out.println(mensaje);
            out.flush();
        }
    }
}