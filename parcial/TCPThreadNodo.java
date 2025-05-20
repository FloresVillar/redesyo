package parcial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.classfile.BufWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class TCPThreadNodo implements Runnable{
    TCPServer server;
    Socket nodo;
    TCPServer.alRecibirMensajeNodo escuchador;
    int ID;
    TCPThreadNodo[]amigos;
    PrintWriter out;
    BufferedReader in;
    String mensaje;
    TCPThreadNodo(Socket nod,TCPServer serv,int id,TCPThreadNodo[]amg){
        server  = serv;
        ID = id;
        nodo = nod;
        amigos = amg;
    }
    public void run(){
        try{
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(nodo.getOutputStream())));
            in = new BufferedReader(new InputStreamReader(nodo.getInputStream()));
            escuchador = server.obtenerEscuchadorNodo();
            while(true){
                mensaje = in.readLine();
                if(mensaje!=null&&escuchador!=null){
                    escuchador.mensajeRecibidoNodo(mensaje);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void enviarMensaje(String mensaje){
        if(out!=null&&!out.checkError()){
            out.println(mensaje);
            out.flush();
        }
    }
}
