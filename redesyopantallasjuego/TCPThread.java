package redesyopantallasjuego;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPThread extends Thread{
    public BufferedReader in;
    public PrintWriter out;
    int id;
    String mensaje;
    TCPThread [] amigos;
    boolean [] primeros;
    TCPServer server;
    Socket cliente;
    boolean corriendo = false;
    TCPServer.alRecibirMensaje escuchador = null;
    TCPThread(Socket cli,TCPServer tcpsever,int ide,TCPThread[]amig,boolean[]primerosMensajes){
        cliente = cli;
        server = tcpsever;
        id = ide;
        amigos = amig;
        primeros = primerosMensajes;
    }
    public void run(){
        corriendo = true;
        try{
            out= new PrintWriter(new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream())));
            if(!primeros[id]){
                //x, es la posicion del hilo-cliente en la pantalla juegos
                double x= id*50;
                double y= 0;
                enviarMensaje("ID:"+id+";"+"posicion:"+x+","+y);
                primeros[id]=true;
            }
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            escuchador =  server.obtenerEscuchador();
            while(corriendo){
                mensaje = in.readLine();
                if(mensaje!=null && escuchador!=null){
                    escuchador.mensajeRecibido(mensaje);
                }
                mensaje =null;
            }    
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    public void enviarMensaje(String mensaje){
        if(out!=null && !out.checkError()){
            out.println(mensaje);
            out.flush();
        }
    }

}