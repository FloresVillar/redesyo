package redesyo;
import java.util.Scanner;
import redesyo.TCPServer;
import redesyo.TCPCliente;
public class Cliente {
    TCPCliente tcpcliente;
    Scanner sc = new Scanner(System.in);
    public static void main(String []args){
        Cliente obj = new Cliente();
        obj.iniciar();
    }    

    public void iniciar(){
        new Thread(new Runnable() {
            @Override
            public void run(){
                tcpcliente = new TCPCliente("127.0.0.1", new TCPCliente.alRecibirMensaje(){
                    @Override
                    public void mensajeRecibido(String mensaje){
                        clienteRecibe(mensaje);
                    }
                });
                tcpcliente.run();   
            }
        }).start();
        String entrada ="n";
        while(!entrada.equals("s")){
            entrada= sc.nextLine();
            clienteEnvia(entrada);
        }
    }
    public void clienteRecibe(String mensaje){
        System.out.println("cliente recibe: "+mensaje);
    }
    public void clienteEnvia(String mensaje){
        tcpcliente.enviarMensaje(mensaje);
    }
}
