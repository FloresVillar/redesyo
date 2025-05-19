package redesyopantallasjuego;
import java.util.ArrayList; 
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
public class Servidor {
    TCPServer tcpServer;
    Scanner sc= new Scanner(System.in); 
    //Pantalla pantallaServidor;
    //ArrayList<String> historia;
    //boolean primerMensaje = false;
    Servidor(){ 
        //pantallaServidor = new Pantalla();
        //historia = new ArrayList<String>();
    }
    public static void main(String[]args){
        Servidor obj = new Servidor();
        obj.iniciar();
    }
    boolean primerMensaje = false;
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
    String mensajeG="";//se recibe esto desde cliente "ID:ID;mov:IZQUIERDA;posicion:posX,posY;limites:limx,limy"
    public void servidorRecibe(String mensaje){
        System.out.println("servidor recibe: "+mensaje);
        mensajeG+=mensaje;
        if(true){
            String []lineas = mensajeG.split("\n");
            try{
                StringBuilder mfinal = new StringBuilder();
                for(String linea:lineas){
                    String []partes = linea.split(";");
                    int id = Integer.parseInt(partes[0].split(":")[1].trim());
                    String mov = partes[1].split(":")[1].trim();
                    double x=0,y=0;
                    if(partes[2].startsWith("posicion")){
                        String[]coords = partes[2].split(":")[1].split(",");
                        x=Double.parseDouble(coords[0].trim());
                        y=Double.parseDouble(coords[1].trim());
                    }
                    int limx = Integer.parseInt(partes[3].split(":")[1].split(",")[0].trim());
                    int limy  = Integer.parseInt(partes[3].split(":")[1].split(",")[1].trim());
                    Rectangle2D limites = new Rectangle2D.Double(0,0,limx,limy);
                    switch (mov) {
                        case "ARRIBA": {
                            y+=1;
                            if(y+10>=limites.getMaxY()){
                                y=limites.getMinY();
                            }        
                        break;}
                        case "ABAJO":{
                            y-=1;
                            if(y<limites.getMinY()){
                                y=limites.getMinY();
                            }
                            break;}
                        case "DERECHA":{
                            x+=1;
                            if(x+10>limites.getMaxX()){
                                x=limites.getMinX();
                            }
                            break;}
                        case "IZQUIERDA":{
                            x-=1;
                            if(x<limites.getMinX()){
                                x=limites.getMinX();
                            }
                            break;}
                        default:
                            break;
                    }
                    mfinal.append("id:").append(id).append(";posicion:").append(x).append(",").append(y).append("\n");
                    //enviar a cliente aqui?
                }
                //esperar a que el for termine y enviar aqui ? podria el for paralelizares incluso
                servidorEnvia(mfinal.toString().trim());
                mensajeG = "";
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void servidorEnvia(String mensaje){
        tcpServer.enviarMensaje(mensaje);
    }
 
    /*
     * int cont= 0;
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
     */
     
    //=============================
    class Pantalla extends JFrame{
        JTextArea mensajes;
        JTextField entrada;
        Pantalla(){
            setTitle("SERVIDOR");
            setSize(300,300);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            mensajes = new JTextArea();
            JScrollPane scroll = new JScrollPane(mensajes);
            add(scroll,BorderLayout.CENTER);
            JPanel botones = new JPanel(new BorderLayout());
            entrada = new JTextField();
            botones.add(entrada,BorderLayout.CENTER);
            ponerBoton(botones,"enviar", new ActionListener() {
                public void actionPerformed(ActionEvent evento){
                    String mensj =  entrada.getText();
                    if(!mensj.isEmpty()){
                        servidorEnvia(mensj);
                        entrada.setText("");
                    }
                }
            });
            add(botones,BorderLayout.SOUTH);
            setVisible(true);
        }
        public void agregarMensaje(String mensaje){
            mensajes.append(mensaje+"\n");
        }
        public void ponerBoton(Container c,String nombre,ActionListener escuchador){
            JButton boton = new JButton(nombre);
            c.add(boton,BorderLayout.EAST);
            boton.addActionListener(escuchador);
        }
    }
    //===============================
}
