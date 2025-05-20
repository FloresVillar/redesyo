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
                        clienteEscuchador(mensaje);
                        clienteEscuchadorPantalla(mensaje);
                    }
                });
                tcpcliente.run();
            }
        }).start();
        String entrada = "n";
        System.out.println("Cliente: s para salir");
        while (!entrada.equals("s")) {
            entrada = sc.nextLine();
            clienteEnvia(entrada);
        }
    }

    public void clienteEscuchador(String mensaje) {
        System.out.println("cliente recibe: " + mensaje);
    }

    public void clienteEscuchadorPantalla(String mensaje) {
        String[] lineas = mensaje.split("\n");
        for (String t : lineas) {
            pantallaCliente.agregarMensaje(t + "\n");
        }
    }

    public void clienteEnvia(String mensaje) {
        tcpcliente.enviarMensaje(mensaje);
    }

    // ==============================
    class Pantalla extends JFrame {
        JTextArea mensajes;
        JTextField entrada;

        Pantalla() {
            setTitle("CLIENTE");
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
                        clienteEnvia(mensj);
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
    // =========================
}