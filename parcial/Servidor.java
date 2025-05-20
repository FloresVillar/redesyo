package parcial;
import java.util.ArrayList; 
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
                        servidorEscuchador(mensaje);
                    }
                },new TCPServer.alRecibirMensajeNodo() {
                    public void mensajeRecibidoNodo(String mensaje){
                        servidorEscuchadorNodo(mensaje);
                    }
                }); 
                tcpServer.run();
            }
        }).start();

        String entrada ="n";
        System.out.println("Servidor: s para salir");
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            servidorEnvia(entrada+"para cliente");
            servidorEnviaNodo(entrada+"para nodo");
        }
    }
    int cont= 0;
    public void servidorEscuchador(String mensaje){
        System.out.println("servidor recibe de cliente: "+mensaje +" cont: "+cont);
        pantallaServidor.agregarMensaje(mensaje);
        //System.out.println("Texto en pantallaServidor: " + pantallaServidor.mensajes.getText());
        cont++;
        /*if(cont==tcpServer.obtenerN()){
            String historialMensajes = pantallaServidor.mensajes.getText();
            //System.out.println(historialMensajes);
            tcpServer.enviarMensajes(historialMensajes);
            historia.add(historialMensajes);
            pantallaServidor.mensajes.setText("");
            cont=0;
        }*/
    }
    public void servidorEscuchadorNodo(String mensaje){
        System.out.println("servidor recibe de nodo: "+mensaje);
        pantallaServidor.agregarMensajeNodo(mensaje);
        //System.out.println("Texto en pantallaServidor: " + pantallaServidor.mensajesNodo.getText());
    
    }
    public void servidorEnvia(String mensaje){
        tcpServer.enviarMensaje(mensaje);
    }
    public void servidorEnviaNodo(String mensaje){
        tcpServer.enviarMensajeNodo(mensaje);
    }
    //=============================
    class Pantalla extends JFrame {
    JTextArea mensajes;
    JTextArea mensajesNodo;

    Pantalla() {
        setTitle("SERVIDOR");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel central con dos áreas de mensajes
        JPanel panelMensajes = new JPanel(new GridLayout(2, 1));

        mensajes = new JTextArea();
        mensajes.setBorder(BorderFactory.createTitledBorder("Mensajes de Clientes"));
        mensajes.setEditable(false);
        panelMensajes.add(new JScrollPane(mensajes));

        mensajesNodo = new JTextArea();
        mensajesNodo.setBorder(BorderFactory.createTitledBorder("Mensajes de Nodos"));
        mensajesNodo.setEditable(false);
        panelMensajes.add(new JScrollPane(mensajesNodo));

        add(panelMensajes, BorderLayout.CENTER);

        setVisible(true);
    }

    public void agregarMensaje(String mensaje) {
        mensajes.append(mensaje + "\n");
    }

    public void agregarMensajeNodo(String mensaje) {
        mensajesNodo.append(mensaje + "\n");
    }
}


    //===============================
}