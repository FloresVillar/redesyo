package redesyopantallas;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.*;
public class Cliente {
    TCPCliente tcpcliente;
    Scanner sc = new Scanner(System.in);
    Pantalla pantallaCliente;
    Cliente(){
        pantallaCliente = new Pantalla();
    }
    public static void main(String []args){
        Cliente obj = new Cliente();
        obj.iniciar();
    }    

    public void iniciar(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                tcpcliente = new TCPCliente("127.0.0.1", new TCPCliente.alRecibirMensaje(){
                    @Override
                    public void mensajeRecibido(String mensaje){
                        clienteRecibe(mensaje);
                        clienteRecibeHistorial(mensaje);
                    }
                });
                tcpcliente.run();   
            }
        }).start();
        String entrada ="n";
        System.out.println("Cliente: s para salir");
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            clienteEnvia(entrada);
        }
    }
    public void clienteRecibe(String mensaje){
        System.out.println("cliente recibe: "+mensaje);
    }
    public void clienteRecibeHistorial(String mensaje){
        String[] lineas = mensaje.split("\n");
        for(String t:lineas){
            pantallaCliente.agregarMensaje(t+"\n");
        }
    } 
    public void clienteEnvia(String mensaje){
        tcpcliente.enviarMensaje(mensaje);
    }
    class Pantalla extends JFrame{
        JTextArea mensajes;
        Pantalla(){
            setTitle("CLIENTE");
            setSize(300,300);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            mensajes = new JTextArea();
            add(mensajes,BorderLayout.CENTER);
            setVisible(true);
        }
        public void agregarMensaje(String mensaje){
            mensajes.append(mensaje+"\n");
        }
    }
}
