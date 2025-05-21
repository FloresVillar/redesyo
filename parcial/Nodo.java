package parcial;

import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import parcial.TCPNodo.alRecibirMensaje;
public class Nodo {
    TCPNodo tcpnodo ;
    Pantalla pantallaNodo; 
    int ID;
    Nodo(){
        pantallaNodo = new Pantalla();
    }
    public static void main(String[]args){
        Nodo obj = new Nodo();
        obj.iniciar();
    }
    public void iniciar(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                tcpnodo = new TCPNodo("127.0.0.1",new TCPNodo.alRecibirMensaje(){
                    public void mensajeRecibido(String mensaje){
                        nodoEscuchador(mensaje);
                        nodoEscuchadorPantalla(mensaje);
                        
                    }
                });
                tcpnodo.run();
            }
        }).start();
         
    } 
    public void nodoEscuchador(String mensaje){
        System.out.println("nodo recibe: "+mensaje);
        // si el mensaje recibido son las tablas particionadas enviadas a este nodo
        String[]partes =mensaje.split("\n");
        if(partes[0].contains("|")){
            String[]primeralinea =partes[0].split("\\|");
            boolean c1 = primeralinea[0].split(":")[0].trim().equals("PARTE");
            boolean c2 = primeralinea[1].split(":")[0].trim().equals("PRANGO_IDS");
            if(c1&&c2){ //FALTA IMPLEMENTAR
                //agregar info a la data local de cada nodo
            }
        }
        //
        if(mensaje.split("\n")[0].trim().equals("CONSULTAR_SALDO")){
            //en la segunda linea esta el ID_CUENTA \n id
            //busca en su data local 
            //devuelve SALDO si tiene la cuenta,nada si no ,agregar mensaje para la tabla transferencias 
        }
        if(mensaje.split("\n")[0].trim().equals("TRANSFERIR_FONDOS")){
            //en la segunda linea estan ID_CUENTA_ORIGEN |   ID_CUENTA_DESTINO |  MONTO \n id_de | id_para | monto \n
            //extraer , buscar en la data local ij con la cuenta
            //si esta validar que el monto no supere saldo en cuenta,
            //realizar la operacion, agregar info para tabla transferencias
            //enviar resultado a servidor
        }
    }
    public void nodoEscuchadorPantalla(String mensaje) {
        String[] lineas = mensaje.split("\n");
        for (String t : lineas) {
            pantallaNodo.agregarMensaje(t + "\n");
        }
    }
    public void nodoEnvia(String mensaje){
        tcpnodo.enviarMensaje(mensaje);
    }
    //=========
    class Pantalla extends JFrame {
        JTextArea mensajes;
        JTextField entrada;

        Pantalla() {
            setTitle("NODO");
            setSize(300, 300);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            mensajes = new JTextArea();
            JScrollPane scroll = new JScrollPane(mensajes);
            add(scroll, BorderLayout.CENTER);
            JPanel botones = new JPanel(new BorderLayout());
            entrada = new JTextField();
            botones.add(entrada, BorderLayout.CENTER);
            //boton para eviar mensaje generico
            ponerBoton(botones, "enviar", new ActionListener() {
                public void actionPerformed(ActionEvent evento) {
                    String mensj = entrada.getText();
                    if (!mensj.isEmpty()) {
                        nodoEnvia(mensj);
                        entrada.setText("");
                    }
                }
            }); 
            add(botones, BorderLayout.SOUTH);
            setVisible(true);
        }

        public void agregarMensaje(String mensaje) {
            mensajes.append(mensaje + "\n");
        }

        public void ponerBoton(Container c, String nombre, ActionListener escuchador) {
            JButton boton = new JButton(nombre);
            c.add(boton,BorderLayout.EAST);
            boton.addActionListener(escuchador);
        }
    }
}
