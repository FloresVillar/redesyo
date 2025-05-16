package redesyo;

import java.util.Scanner;
import redesyo.*;

public class Servidor {
    TCPServer tcpServer;
    Scanner sc= new Scanner(System.in);
    public static void main(String[]args){
        Servidor obj = new Servidor();
        obj.iniciar();
    }

    public void iniciar(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                tcpServer = new TCPServer(new TCPServer.alRecibirMensaje(){
                    public void mensajeRecibido(String mensaje){
                        servidorRecibe(mensaje);
                    }
                });
                tcpServer.run();
            }
        }).start();

        String entrada ="n";
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            servidorEnvia(entrada);
        }
    }
    public void servidorRecibe(String mensaje){
        System.out.println("servidor recibe: "+mensaje);
    }
    public void servidorEnvia(String mensaje){
        tcpServer.enviarMensaje(mensaje);
    }
}
