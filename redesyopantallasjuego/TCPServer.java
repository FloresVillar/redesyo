package redesyopantallasjuego;
 
import java.net.ServerSocket;
import java.net.Socket;

import redesyopantallasjuego.TCPThread;

import java.io.IOException;
import java.net.InetAddress;

public class TCPServer{
    ServerSocket server;
    TCPThread[] clientes;
    boolean[]primerosMensajes;
    final int SERVERPORT=5000;
    String mensaje;
    boolean corriendo = false;
    int nclientes = 0;
    alRecibirMensaje escuchador;
    TCPServer(alRecibirMensaje Listener){
        this.escuchador = Listener;
        clientes = new TCPThread[10];
        primerosMensajes = new boolean[10];
    }

    public void run(){
        corriendo = true;
        try{
            server = new ServerSocket(SERVERPORT);
            while(corriendo){
                Socket cliente = server.accept();
                System.out.println("cliente: "+nclientes+" creado");
                clientes[nclientes] = new TCPThread(cliente,this,nclientes,clientes,primerosMensajes);
                Thread t = new Thread(clientes[nclientes]);
                t.start();
                nclientes++;

            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void enviarMensaje(String mensaje){
        for(int i=0;i<nclientes;i++){
            clientes[i].enviarMensaje(mensaje);
        }
    }
    public void enviarMensajes(String mensajes){
        for(int i=0;i<nclientes;i++){
            clientes[i].enviarMensaje(mensajes);
        }
    }
    public interface alRecibirMensaje   {
        public void mensajeRecibido(String mensaje);
        
    }
    public alRecibirMensaje obtenerEscuchador(){
        return this.escuchador;
    }
    int obtenerN(){
        return nclientes;
    }

}