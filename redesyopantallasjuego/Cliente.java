package redesyopantallasjuego;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import redesyopantallasjuego.Cliente.LaminaProyectil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.*;
public class Cliente {
    TCPCliente tcpcliente;
    Scanner sc = new Scanner(System.in);
    //Pantalla pantallaCliente;
    Juego juegocliente;
    int ID;
    ExecutorService pool;
    Cliente() {
        //pantallaCliente = new Pantalla();
        //pantallaCliente.setVisible(true);
        juegocliente = new Juego(); 
        pool = Executors.newFixedThreadPool(4);
    }

    public static void main(String[] args) {
        Cliente obj = new Cliente();
        obj.iniciar();
    }
    boolean primerMensaje = false;
    public void iniciar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpcliente = new TCPCliente("127.0.0.1", new TCPCliente.alRecibirMensaje() {
                    @Override
                    public void mensajeRecibido(String mensaje) {
                        if(!primerMensaje){
                            clienteRecibe(mensaje); 
                        }else{
                            clienteRecibeMovs(mensaje);
                        }
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
    double posX;
    double posY;
    public void clienteRecibe(String mensaje) {//enviarMensaje("ID:"+id+";"+"posicion:"+x+","+y);
        System.out.println("cliente recibe: " + mensaje);
        if(!primerMensaje){
            String[]partes = mensaje.split(";");
            ID = Integer.parseInt(partes[0].split(":")[1].trim());
            if(partes[1].startsWith("posicion")){
                String[]coords = partes[1].split(":")[1].split(",");
                posX = Double.parseDouble(coords[0].trim());
                posY = Double.parseDouble(coords[1].trim());
            }
            
            primerMensaje = true;
        }else{
            clienteRecibeMovs(mensaje);
        }
    }
    public void clienteRecibeMovs(String mensaje) { //"id:1;posicion:x,y" \n "id:2;posicion:x,y"
    String[] lineas = mensaje.split("\n");

    for (String linea : lineas) {
        pool.submit(() -> {
            String[] partes = linea.split(";");
            try {
                int id = Integer.parseInt(partes[0].split(":")[1].trim());
                double x = 0, y = 0;
                if (partes[1].startsWith("posicion")) {
                    String[] coords = partes[1].split(":")[1].split(",");
                    x = Double.parseDouble(coords[0].trim());
                    y = Double.parseDouble(coords[1].trim());
                }

                synchronized (this) {
                    if (id == ID) {
                        posX = x;
                        posY = y;
                    }
                }

                Proyectil p = new Proyectil(x, y);
                juegocliente.laminaProyectil.agregarProyectil(p);

                //  Aquí va el cambio: animación con javax.swing.Timer
                javax.swing.Timer timer = new javax.swing.Timer(5, null);
                timer.addActionListener(new ActionListener() {
                    int pasos = 0;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (pasos < 1000) {
                            p.disparar(juegocliente.laminaProyectil.getBounds());
                            juegocliente.laminaProyectil.repaint();
                            pasos++;
                        } else {
                            timer.stop();
                        }
                    }
                });
                timer.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

    /* 
    public void clienteRecibeMovs(String mensaje){//"id:1;posicion:x,y" \n "id:2;posicion:x,y" 
        String [] lineas =mensaje.split("\n"); 
        for(String linea:lineas){//hilos 

            pool.submit(()->{
            String []partes = linea.split(";");
            try{
                int id = Integer.parseInt(partes[0].split(":")[1].trim());
                double x=0,y=0;
                if(partes[1].startsWith("posicion")){
                    String[]coords = partes[1].split(":")[1].split(",");
                    x=Double.parseDouble(coords[0].trim());
                    y=Double.parseDouble(coords[1].trim());
                }
                synchronized(this){
                    if(id==ID){
                    posX = x;
                    posY = y;
                }
                }
                Proyectil p = new Proyectil(x, y);
                juegocliente.laminaProyectil.agregarProyectil(p);
                try{
                    for(int j=0;j<1000;j++){
                        p.disparar(juegocliente.laminaProyectil.getBounds());
                        juegocliente.laminaProyectil.repaint();
                        Thread.sleep(5);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }catch(Exception e){}
            });
            
        }
       
    }
    */
    /*public void clienteRecibeHistorial(String mensaje) {
        String[] lineas = mensaje.split("\n");
        for (String t : lineas) {
            pantallaCliente.agregarMensaje(t + "\n");
        }
    }
    */
    public void clienteEnvia(String mensaje) {
        tcpcliente.enviarMensaje(mensaje);
    }
    //====================
    public class  LaminaProyectil extends JPanel{
        ArrayList<Proyectil> proyectiles = new ArrayList<Proyectil>();
        public void agregarProyectil(Proyectil p){
            proyectiles.add(p);
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2= (Graphics2D)g;
            for(Proyectil p:proyectiles){
                g2.fill(p.getShape());
            }
        }
    }
    //=========================
    class Proyectil{
        double TamX =10;
        double TamY =10;
        double dx =1 ;
        double dy =1;
        double x,y;
        Proyectil(double xi,double yi){
            x = xi;
            y = yi;
        }    
        public void disparar(Rectangle2D limites){
            y+=dy;
            if((y + TamY)>=limites.getMaxY()){
                
            }//la posicion  tiene un proyectil o es parte de la nave enemiga desaparece este proyectil 
               //por el impacto , se entiende.
                // o y+1 es fin de marco
        }
        public Ellipse2D getShape(){
            return new Ellipse2D.Double(x, y, TamX, TamY);
        }
    }
    //========================
    /* 
        class Juego extends JFrame{
        JPanel botones;
        LaminaProyectil laminaProyectil;
        Juego(){
            setSize(300,300);
            setTitle("JUEGO cliente");
            laminaProyectil = new LaminaProyectil();
            laminaProyectil.setPreferredSize(new Dimension(300,300));
            add(laminaProyectil,BorderLayout.CENTER);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            botones = new JPanel(new BorderLayout());
            setVisible(true);
            
            ponerBoton(botones,"ARRIBA",new ActionListener(){
                public void actionPerformed(ActionEvent evento){
                    Rectangle bounds = laminaProyectil.getBounds();
                    int limx = bounds.width;
                    int limy = bounds.height;
                    String mensaje="ID:"+ID+";"+"mov:ARRIBA"+";"+"posicion:"+posX+","+posY+";"+"limites:"+limx+","+limy;//enviar movimiento 
                    clienteEnvia(mensaje);
                }
            });
            ponerBoton(botones, "ABAJO",new ActionListener() {
                public void actionPerformed(ActionEvent evento){
                    Rectangle bounds = laminaProyectil.getBounds();
                    int limx = bounds.width;
                    int limy = bounds.height;
                    String mensaje="ID:"+ID+";"+"mov:ABAJO"+";"+"posicion:"+posX+","+posY+";"+"limites:"+limx+","+limy;//enviar movimiento 
                    clienteEnvia(mensaje);
                }
            });
            ponerBoton(botones, "DERECHA",new ActionListener() {
                public void actionPerformed(ActionEvent evento){
                    Rectangle bounds = laminaProyectil.getBounds();
                    int limx = bounds.width;
                    int limy = bounds.height;
                    String mensaje="ID:"+ID+";"+"mov:DERECHA"+";"+"posicion:"+posX+","+posY+";"+"limites:"+limx+","+limy;//enviar movimiento 
                    clienteEnvia(mensaje);
                }
            });
            ponerBoton(botones, "IZQUIERDA", new ActionListener() {
                public void actionPerformed(ActionEvent evento){
                    Rectangle bounds = laminaProyectil.getBounds();
                    int limx = bounds.width;
                    int limy = bounds.height;
                    String mensaje="ID:"+ID+";"+"mov:IZQUIERDA"+";"+"posicion:"+posX+","+posY+";"+"limites:"+limx+","+limy;//enviar movimiento 
                    clienteEnvia(mensaje);
                }
            });
            add(botones,BorderLayout.SOUTH);
        }
        public void ponerBoton(Container c,String nombre,ActionListener escuchador){
            JButton boton = new JButton(nombre);
            c.add(boton);
            boton.addActionListener(escuchador);
        }
    }*/
    //====================
    class Juego extends JFrame {
    JPanel botones;
    LaminaProyectil laminaProyectil;

    Juego() {
        setTitle("JUEGO cliente");

        laminaProyectil = new LaminaProyectil();
        laminaProyectil.setPreferredSize(new Dimension(300, 300));
        add(laminaProyectil, BorderLayout.CENTER);

        botones = new JPanel(new BorderLayout());

        ponerBoton(botones, "ARRIBA", new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                Rectangle bounds = laminaProyectil.getBounds();
                int limx = bounds.width;
                int limy = bounds.height;
                String mensaje = "ID:" + ID + ";" + "mov:ARRIBA" + ";" +
                                 "posicion:" + posX + "," + posY + ";" +
                                 "limites:" + limx + "," + limy;
                clienteEnvia(mensaje);
            }
        });

        ponerBoton(botones, "ABAJO", new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                Rectangle bounds = laminaProyectil.getBounds();
                int limx = bounds.width;
                int limy = bounds.height;
                String mensaje = "ID:" + ID + ";" + "mov:ABAJO" + ";" +
                                 "posicion:" + posX + "," + posY + ";" +
                                 "limites:" + limx + "," + limy;
                clienteEnvia(mensaje);
            }
        });

        ponerBoton(botones, "DERECHA", new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                Rectangle bounds = laminaProyectil.getBounds();
                int limx = bounds.width;
                int limy = bounds.height;
                String mensaje = "ID:" + ID + ";" + "mov:DERECHA" + ";" +
                                 "posicion:" + posX + "," + posY + ";" +
                                 "limites:" + limx + "," + limy;
                clienteEnvia(mensaje);
            }
        });

        ponerBoton(botones, "IZQUIERDA", new ActionListener() {
            public void actionPerformed(ActionEvent evento) {
                Rectangle bounds = laminaProyectil.getBounds();
                int limx = bounds.width;
                int limy = bounds.height;
                String mensaje = "ID:" + ID + ";" + "mov:IZQUIERDA" + ";" +
                                 "posicion:" + posX + "," + posY + ";" +
                                 "limites:" + limx + "," + limy;
                clienteEnvia(mensaje);
            }
        });

        add(botones, BorderLayout.SOUTH);

        pack(); // <-- Ajusta el frame al tamaño preferido de los componentes

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true); // <-- Se hace visible después del pack()
    }

    public void ponerBoton(Container c, String nombre, ActionListener escuchador) {
        JButton boton = new JButton(nombre);
        c.add(boton);
        boton.addActionListener(escuchador);
    }
}

    // ==============================
    /*class Pantalla extends JFrame {
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
    }*/
    // =========================
}
