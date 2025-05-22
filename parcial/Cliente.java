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

public class Cliente {
    TCPCliente tcpcliente;
    Scanner sc = new Scanner(System.in);
    Pantalla pantallaCliente;
    boolean primerMensaje =false;
    Cliente() {
        pantallaCliente = new Pantalla();
    }

    public static void main(String[] args) {
        Cliente obj = new Cliente();
        obj.iniciar();
    }

    public void iniciar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpcliente = new TCPCliente("127.0.0.1", new TCPCliente.alRecibirMensaje() {
                    @Override
                    public void mensajeRecibido(String mensaje) {
                        //si al inicio el cliente no recibe nada especial desde el servidor
                        primerMensaje = true;
                        if(!primerMensaje){
                            clienteEscuchadorInicio(mensaje);
                        }
                        else{
                            clienteEscuchador(mensaje);
                            clienteEscuchadorPantalla(mensaje);
                        }
                    }
                });
                tcpcliente.run();
            }
        }).start();
        String entrada = "n";
        System.out.println("Cliente: s para salir, separar etiquetas con | ");
        while (!entrada.equals("s")) {
            entrada = sc.nextLine();
            clienteEnvia(entrada);
        }
    }
    public void clienteEscuchadorInicio(String mensaje){
        //lo que hara con el mensaje al inicio
        primerMensaje = true;
    }
    public void clienteEscuchador(String mensaje) {
        System.out.println("cliente recibe: " + mensaje);
    }

    public void clienteEscuchadorPantalla(String mensaje) {
        String[] lineas = mensaje.split("[\\n;]");
        for (String t : lineas) {
            pantallaCliente.agregarMensaje(t + "\n");
        }
    }

    public void clienteEnvia(String mensaje) {
        System.out.println("clienteEnvia");
        tcpcliente.enviarMensaje(mensaje);
    }

    // ==============================
    class Pantalla extends JFrame {
        JTextArea mensajes;
        JTextField entrada;

        Pantalla() {
            setTitle("CLIENTE");
            setSize(400, 300);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            mensajes = new JTextArea();//cuando recibe desde servidor
            JScrollPane scroll = new JScrollPane(mensajes);
            add(scroll, BorderLayout.CENTER);
            JPanel panelentrada =new JPanel(new BorderLayout());
            JPanel panelbotones = new JPanel(new GridLayout(2,1,5,5));
            entrada = new JTextField();
            panelentrada.add(entrada,BorderLayout.CENTER);
            
            ponerBoton(panelbotones, "consultar saldo", new ActionListener() {
                public void actionPerformed(ActionEvent evento) {
                    String mensj = entrada.getText();//siguiendo la logica de los mensjaes "CONSULTAR_SALDO | ID_CUENTA | SALDO"
                    if (!mensj.isEmpty()) {
                        mensj="ID_CUENTA ;" +mensj;
                        clienteEnvia(mensj);
                        entrada.setText("");
                    }
                }
            });
             ponerBoton(panelbotones, "transferir fondos", new ActionListener() {
                public void actionPerformed(ActionEvent evento) {
                    String mensj = entrada.getText();
                    if (!mensj.isEmpty()){
                        mensj="ID_CUENTA_ORIGEN | ID_CUENTA_DESTINO | MONTO ;"+ mensj;
                        clienteEnvia(mensj);
                        entrada.setText("");
                    }
                }
            });
            panelentrada.add(panelbotones,BorderLayout.EAST);
            add(panelentrada, BorderLayout.SOUTH);
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
    // =========================
}