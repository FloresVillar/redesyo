package parcial;
import java.util.ArrayList; 
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
                tcpServer = new TCPServer(
                new TCPServer.alRecibirMensaje(){
                    public void mensajeRecibido(String mensaje){
                        System.out.println("mensajeRecibido servidor desde cliente");
                        servidorEscuchador(mensaje);
                    }
                },new TCPServer.alRecibirMensajeNodo() {
                    public void mensajeRecibidoNodo(String mensaje){
                        System.out.println("mensajeRecibido servidor desde nodo");
                        servidorEscuchadorNodo(mensaje);
                    }
                }); 
                tcpServer.run();
            }
        }).start();

        String entrada ="n";
        System.out.println("Servidor: s para salir, <enviar tablas a nodos> para enviar tablas");
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            servidorEnvia(entrada+": para cliente");//aqui puede distinguirse el tipo de mensaje para cliente y nodo
            servidorEnviaNodo(entrada+": para nodo");
            if(entrada.equals("enviar tablas a nodos")){
                leerTablasEnviar();//leer Tabla_cliente y Tabla_Cuenta, separar en bloques y cada bloque i-esimo a 3 nodos diferentes
            }
        }
    } 
    public void servidorEscuchador(String mensaje){ //FALTA IMPLEMENTAR
        System.out.println("servidor recibe de cliente: "+mensaje);
        pantallaServidor.agregarMensaje(mensaje);
        //deteminar si el mensaje es una solicitud desde el cliente
        if(mensaje.contains(";")){
            if(mensaje.split(";")[0].trim().equals("ID_CUENTA")){
                System.out.println("CONSULTAR_SALDO");
            //se trata de una peticion CONSULTAR_SALDO
            //consultar a los nodos   
            //su saldo si tiene esa cuenta, cabe señalar que el ragno de cada ij tambien indica el minimo y maximo de la cuenta a consultar
            //una vez obtenido el saldo de la cuenta devolver meiante enviarMensaje al cliente indicado, 
            //validar que es un int o confiar en usuario
            int ID_CUENTA=0;
            try{
                ID_CUENTA = Integer.parseInt(mensaje.split(";")[1].trim());
                String idObjetoCliente =mensaje.split(";")[2].trim();
                String msj = "CONSULTAR_SALDO;"+ID_CUENTA+";"+idObjetoCliente;
                servidorEnviaNodo(msj);
            }catch(NumberFormatException e){
                System.out.print("id no valido");
            }
            
            //recibe respuetas de los nodos, ignora los vacios
            //saldo =consultar_cuenta_a_nodos("etiqueta:CONSULTAR_SALDO" ID_CUENTA)
            //informar_consulta_cuenta(ID_CUENTA,saldo) a cliente, ademas agregar info a Tabla_Transferencias
            }
        }
        if(mensaje.split(";")[0].trim().split("|")[0].trim().equals("ID_CUENTA_ORIGEN")){
            if(mensaje.split(";")[0].trim().split("|")[1].trim().equals("ID_CUENTA_ORIGEN")){
                if(mensaje.split(";")[0].trim().split("|")[2].trim().equals("MONTO")){
                    System.out.println("Transferir_montos");
                    //es una transferencia
                    //mandar una consulta a los nodos ccon la etiqueta TRANSFERIR_FONDOS como primera linea de mensaje hacia nodo
                    //se recibe resultado de operacion e info para la Tabla_Transferencias
                }
            }
        }
    }
    public void servidorEscuchadorNodo(String mensaje){
        System.out.println("servidor recibe de nodo: "+mensaje);
        pantallaServidor.agregarMensajeNodo(mensaje);
        if(mensaje.contains("[\\|;]")){ //"SALDO_CONSULTADO;"+ID+";"+saldo_consultado+";"+idObjetoCliente
            if(mensaje.split(";")[0].trim().equals("SALDO_CONSULTADO")){
                String msj = "SALDO_CONSULTADO;"+mensaje.split(";")[1].trim()+mensaje.split(";")[2].trim(); //enviar a cliente 
                String idObjetoCliente =  mensaje.split(";")[3].trim();
                int indx = Integer.parseInt(idObjetoCliente);
                TCPThread tcpthread = tcpServer.obtenerCliente(indx);
                tcpthread.enviarMensajeACliente(mensaje);
            }
        }
        //System.out.println("Texto en pantallaServidor: " + pantallaServidor.mensajesNodo.getText());
        //determinar si el mensaje desde el nodo es informacion solicitada acerca de cosulta o transaccion
    }
    public void servidorEnvia(String mensaje){
        tcpServer.enviarMensaje(mensaje);
    }
    public void servidorEnviaNodo(String mensaje){
        tcpServer.enviarMensajeNodo(mensaje);
    }
    ArrayList <String> datosClientes;
    ArrayList<String> datosCuentas;
    public void leerTablasEnviar(){
        try{
            BufferedReader tclientes = new BufferedReader(new FileReader("C:\\Users\\FLORES VILLAR\\Desktop\\Concurrente\\redesyo\\parcial\\Tabla_Cliente.txt"));
            datosClientes = new ArrayList<>();
            BufferedReader tcuentas = new BufferedReader(new FileReader("C:\\Users\\FLORES VILLAR\\Desktop\\Concurrente\\redesyo\\parcial\\Tabla_Cuenta.txt"));
            datosCuentas = new ArrayList<>();//array de String

            String linea;
            int cont = 0;
            int t= 2;//cantidad de tablas;
            String[] etiquetas = new String[t];
            while((linea=tclientes.readLine())!=null){
                cont++;
                if(cont==1) etiquetas[0]=linea;
                if(cont==2) continue;
                if(cont!=1&&cont!=2)datosClientes.add(linea);
            }
            tclientes.close();
            linea="";
            cont =0;
             while((linea=tcuentas.readLine())!=null){
                cont++;
                if(cont==1) etiquetas[1]=linea;
                if(cont==2) continue;
                if(cont!=1&&cont!=2)datosCuentas.add(linea);
            }
            tcuentas.close();
            arqueroCuenta(datosCuentas);
            linea="";
            //mostrar datas leidas
            
            for(String e:datosClientes){
                System.out.print(e+"\n");
            }
            for(String e:datosCuentas){
                System.out.print(e+"\n");
            }
             
            //ahora a separar entre los nodos
            int nNodos = tcpServer.obtenerNNodos();
            @SuppressWarnings("unchecked")
            ArrayList<String>[]data =new ArrayList[t];
            data[0]  = datosClientes;
            data[1]  =  datosCuentas;
            for(int i = 0;i<data.length;i++){
                ArrayList<String>datai = data[i];
                int bloque = (int)datai.size()/nNodos;
                for(int j =0;j<nNodos;j++){
                    ArrayList<String> ij = new ArrayList<>();
                    ij.add(etiquetas[i]);  
                    for(int k=j*bloque;k<(j+1)*bloque&&k<datai.size();k++){
                        ij.add(datai.get(k));
                    }
                    //id ij  i=0 para clientes  i=1 para cuentas
                    //j para calcular los rangos de la filas de data clientes/ cuentas
                    //enviar a tres nodos id:i,j ; rango:j*bloque,(j+1)*bloque ; info:cuerpode ij
                    String info ="";
                    for(String tex : ij){
                        info=info+tex+";"; 
                    }
                    //limites
                   
                    int columna = j + 1;
                    int fila = i + 1;
                    int inferior = -1;
                    int superior = -1;

                    // Saltamos la cabecera que está en ij[0]
                    if (ij.size() > 1) {
                        inferior = obtenerIDDesdeLinea(ij.get(1)); // primer dato real
                        superior = obtenerIDDesdeLinea(ij.get(ij.size() - 1)); // último dato
                    }
                    String info_enviar = "PARTE:"+fila+"."+columna+"|"+"RANGO_IDS:"+inferior+","+superior+";"+info;
                    //escoger a m nodos de entre nNodos
                    //m=3 nodos
                    for(int r =0;r<3;r++){
                        int indx = (j +r)%nNodos;
                        TCPThreadNodo tcpthreadnodo = tcpServer.obtenerNodo(indx);
                        tcpthreadnodo.enviarMensajeANodo(info_enviar);
                    }
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private int obtenerIDDesdeLinea(String linea) {
    String[] partes = linea.trim().split("\\|");
    try {
        return Integer.parseInt(partes[0].trim()); // ID está en la primera columna
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        return -1; // error al parsear
    }
}

    double dineroTotal ;
    public void arqueroCuenta(ArrayList<String> dataCuenta){
        double suma=0;
        for(String linea:dataCuenta){
            String []partes = linea.split("|");
            suma+=Double.parseDouble(partes[2].trim());
        }
        dineroTotal = suma;
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
        SwingUtilities.invokeLater(() -> {
        mensajes.append(mensaje + "\n");
    });
    }

    public void agregarMensajeNodo(String mensaje) {
        SwingUtilities.invokeLater(() -> {
        mensajesNodo.append(mensaje + "\n");
    });
    }
}


    //===============================
}