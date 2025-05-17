package redesyopantallas;
import java.util.ArrayList; 
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
public class Servidor {
    TCPServer tcpServer;
    Scanner sc= new Scanner(System.in);
    Pantalla pantallaServidor;
    ArrayList<String> historia;
    Servidor(){
        pantallaServidor = new Pantalla();
        historia = new ArrayList<String>();
    }
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
        System.out.println("Servidor: s para salir");
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            servidorEnvia(entrada);
        }
    }
    int cont= 0;
    public void servidorRecibe(String mensaje){
        System.out.println("servidor recibe: "+mensaje +" cont: "+cont);
        pantallaServidor.agregarMensaje(mensaje);
        System.out.println("Texto en pantallaServidor: " + pantallaServidor.mensajes.getText());
        cont++;
        if(cont==tcpServer.obtenerN()){
            String historialMensajes = pantallaServidor.mensajes.getText();
            //System.out.println(historialMensajes);
            tcpServer.enviarMensajes(historialMensajes);
            historia.add(historialMensajes);
            pantallaServidor.mensajes.setText("");
            cont=0;
        }
    }
    public void servidorEnvia(String mensaje){
        tcpServer.enviarMensaje(mensaje);
    }
    class Pantalla extends JFrame{
        JTextArea mensajes;
        Pantalla(){
            setTitle("SERVIDOR");
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
