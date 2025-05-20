package parcial;
 
import java.net.ServerSocket;
import java.net.Socket;

import parcial.TCPThread;

import java.io.IOException;
import java.net.InetAddress;

public class TCPServer{
    ServerSocket server;
    ServerSocket serverNodos;
    TCPThread[] clientes;
    TCPThreadNodo[]nodos;
    final int SERVERPORT=5000;
    final int NODOSPORT=5001;
    String mensaje;
    boolean corriendo = false;
    int nclientes = 0;
    int nNodos = 0;
    alRecibirMensaje escuchador;
    alRecibirMensajeNodo escuchadorNodo;
    TCPServer(alRecibirMensaje Listener,alRecibirMensajeNodo ListenerNodo){
        this.escuchador = Listener;
        this.escuchadorNodo = ListenerNodo;
        clientes = new TCPThread[50];
        nodos = new TCPThreadNodo[5];
    }

    public void run() {
    corriendo = true;
    try {
        server = new ServerSocket(SERVERPORT);
        serverNodos = new ServerSocket(NODOSPORT);

        // CLIENTES
        new Thread(() -> {
            while (corriendo) {
                try {
                    Socket cliente = server.accept();
                    System.out.println("cliente: " + nclientes + " creado");
                    clientes[nclientes] = new TCPThread(cliente, this, nclientes, clientes);
                    Thread t = new Thread(clientes[nclientes]);
                    t.start();
                    nclientes++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // NODOS
        new Thread(() -> {
            while (corriendo) {
                try {
                    Socket nodo = serverNodos.accept();
                    System.out.println("nodo: " + nNodos + " creado");
                    nodos[nNodos] = new TCPThreadNodo(nodo, this, nNodos, nodos);
                    Thread t = new Thread(nodos[nNodos]);
                    t.start();
                    nNodos++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public void enviarMensaje(String mensaje){
        for(int i=0;i<nclientes;i++){
            clientes[i].enviarMensaje(mensaje);
        }
    }
    public void enviarMensajeNodo(String mensaje){
        for(int i=0;i<nNodos;i++){
            nodos[i].enviarMensaje(mensaje);
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
    public interface alRecibirMensajeNodo{
        public void mensajeRecibidoNodo(String mensaje);
    }
    public alRecibirMensaje obtenerEscuchador(){
        return this.escuchador;
    }
    public alRecibirMensajeNodo obtenerEscuchadorNodo(){
        return this.escuchadorNodo;
    }
    int obtenerN(){
        return nclientes;
    }
    int obtnerNNodos(){
        return nNodos;
    }
}