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
                    }
                });
                tcpnodo.run();
            }
        });
    }
    public void nodoEscuchador(String mensaje){
        System.out.println("nodo recibe: "+mensaje);
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
