package parcial;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
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
    ArrayList<String>data;
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
    //========
     // Clase auxiliar para almacenar una parte
    class Parte {
        String tipo;
        String etiqueta;      // "PARTE:1.2"
        int idInferior;
        int idSuperior;
        List<String> datos;   // las líneas de datos reales

        public Parte(String etiquet, int inf, int sup, List<String> dat) {
            this.etiqueta = etiquet;
            this.idInferior = inf;
            this.idSuperior = sup;
            this.datos = dat;
            this.tipo="";
        }
    }
    List<Parte> tablaClientes = new ArrayList<>();
    List<Parte> tablaCuentas = new ArrayList<>();
    //========
    public void nodoEscuchador(String mensaje){
        System.out.println("nodo recibe: "+mensaje);
        // si el mensaje recibido son las tablas particionadas enviadas a este nodo
        String[]lineas =mensaje.split(";");
        if(lineas.length>0&&lineas[0].contains("|")){
            String[]primeralinea =lineas[0].split("\\|");
            if(primeralinea.length==2&&primeralinea[0].trim().startsWith("PARTE:")&&primeralinea[1].trim().startsWith("RANGO_IDS:")){ //FALTA IMPLEMENTAR
                System.out.println("dentro de c1 c2 para tabla en nodos");
                //agregar info a la data local de cada nodo
                String etiqueta = primeralinea[0].split(":")[1].trim();//1.2
                String[]etiquetatipo = etiqueta.split("\\.");
                int tipodetabla = Integer.parseInt(etiquetatipo[0]);
                String [] rangos = primeralinea[1].split(":")[1].split(",");
                int inferior = Integer.parseInt(rangos[0].trim());
                int superior = Integer.parseInt(rangos[1].trim());
                List<String> datos = new ArrayList<>();
                for (int i = 1; i < lineas.length; i++) {
                    if (!lineas[i].trim().isEmpty()){
                        datos.add(lineas[i].trim());
                    }
                }
                Parte parte = new Parte(etiqueta, inferior, superior, datos);
                if(tipodetabla ==1){
                    parte.tipo="Cliente";
                    tablaClientes.add(parte);
                }else if(tipodetabla ==2){
                    parte.tipo="Cuenta";
                    tablaCuentas.add(parte);
                }
            }
            System.out.println("TABLAS LOCALES");
            imprimirTablasLocales();
        }
       
        //Si el mensaje es CONSULTAR_SALDO
        if(mensaje.split(";")[0].trim().equals("CONSULTAR_SALDO")){
            String ID = mensaje.split(";")[1].trim();
            String saldo_consultado="";
            //en la segunda linea esta el ID_CUENTA id
            //busca en su data local 
            //devuelve SALDO si tiene la cuenta,nada si no ,agregar mensaje para la tabla transferencias 
            for(Parte partes:tablaCuentas){
                int i=0;
                for(String linea:partes.datos){
                    if(i==0){
                        i++;
                    }
                    else{
                        String []columnas =linea.split("\\|");
                        if(columnas[0].trim().equals(ID)){
                            saldo_consultado = columnas[2].trim();
                            String idObjetoCliente = mensaje.split(";")[2].trim();
                            nodoEnvia("SALDO_CONSULTADO;"+ID+";"+saldo_consultado+";"+idObjetoCliente);
                            break;
                        }
                    }
                }
            }
        }
        if(mensaje.split(";")[0].trim().equals("TRANSFERIR_FONDOS")){
            //en la segunda linea estan ID_CUENTA_ORIGEN |   ID_CUENTA_DESTINO |  MONTO \n id_de | id_para | monto \n
            //extraer , buscar en la data local ij con la cuenta
            //si esta validar que el monto no supere saldo en cuenta,
            //realizar la operacion, agregar info para tabla transferencias
            //enviar resultado a servidor
        }
    }
    public void imprimirTablasLocales() {
    System.out.println("=== TABLA CLIENTES ===");
    for (Parte parte : tablaClientes) {
        System.out.println("PARTE: " + parte.etiqueta + " | RANGO: " + parte.idInferior + "-" + parte.idSuperior);
        for (String linea : parte.datos) {
            System.out.println(linea);
        }
        System.out.println("-------------------------");
    }

    System.out.println("=== TABLA CUENTAS ===");
    for (Parte parte : tablaCuentas) {
        System.out.println("PARTE: " + parte.etiqueta + " | RANGO: " + parte.idInferior + "-" + parte.idSuperior);
        for (String linea : parte.datos) {
            System.out.println(linea);
        }
        System.out.println("-------------------------");
    }
}

    public void nodoEscuchadorPantalla(String mensaje) {
        String[] lineas = mensaje.split(";");
        for (String t : lineas) {
            pantallaNodo.agregarMensaje(t + "\n");
        }
    }
    public void nodoEnvia(String mensaje){
        tcpnodo.enviarMensaje(mensaje);
    }
    //=============
   
    //=============
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
