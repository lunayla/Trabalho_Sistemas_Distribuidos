import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Cliente: usa TCP para pedidos e uma thread separada para ouvir avisos UDP multicast.
public class Cliente {
    private static final int PORTA_TCP = 5055;
    private static final int PORTA_UDP = 5000;
    private static final String GRUPO = "230.0.0.1";

    public static void main(String[] args) {
        Thread ouvinte = new Thread(new Runnable() {
            public void run() { ouvirAvisos(); }
        });
        ouvinte.setDaemon(true);
        ouvinte.start();

        Scanner teclado = new Scanner(System.in);
        try {
            System.out.print("Usuário (cliente/admin): ");
            String usuario = teclado.nextLine();
            System.out.print("Senha: ");
            String senha = teclado.nextLine();

            Socket socket = new Socket("127.0.0.1", PORTA_TCP);
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            saida.writeObject(new String[] { usuario, senha });
            saida.flush();
            Object resposta = entrada.readObject();
            System.out.println(resposta);
            if (!resposta.equals("OK")) { socket.close(); return; }

           
        while (true) {
            if (usuario.equals("admin")) {
                System.out.println("\n1 - Listar produtos");
                System.out.println("2 - Consultar total de pedidos");
                System.out.println("0 - Sair");
            } else {
                System.out.println("\n1 - Listar produtos");
                System.out.println("2 - Fazer pedido");
                System.out.println("0 - Sair");
            }

            System.out.print("Opcao: ");
            String opcao = teclado.nextLine();

            if (opcao.equals("0")) {
                break;
            }

            if (opcao.equals("1")) {
                saida.writeObject("LISTAR");
                saida.flush();

                Produto[] lista = (Produto[]) entrada.readObject();

                for (Produto p : lista) {
                    System.out.println(p);
                }

            } else if (opcao.equals("2")) {
                if (usuario.equals("admin")) {
                    saida.writeObject("TOTAL_PEDIDOS");
                    saida.flush();
                    System.out.println(entrada.readObject());
                } else {
                    System.out.print("Numero do pedido: ");
                    int id = Integer.parseInt(teclado.nextLine());

                    saida.writeObject(new Pedido(id, usuario));
                    saida.flush();

                    System.out.println(entrada.readObject());
                }

            } else {
                System.out.println("Escolha invalida.");
            }
        }

            socket.close();
        } catch (Exception e) {
            System.out.println("Nao foi possivel conectar. Inicie o Servidor primeiro.");
        }
    }

    private static void ouvirAvisos() {
        MulticastSocket socket = null;
        InetAddress grupo = null;
        try {
            socket = new MulticastSocket(PORTA_UDP);
            grupo = InetAddress.getByName(GRUPO);
            socket.joinGroup(grupo); // Entra no grupo para receber os avisos.
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
                socket.receive(pacote);
                String mensagem = new String(pacote.getData(), pacote.getOffset(), pacote.getLength(), StandardCharsets.UTF_8);
                System.out.println("\nAVISO DA LOJA: " + mensagem);
            }
        } catch (Exception e) {
            // A thread termina se a rede multicast não estiver disponível.
        } finally {
            if (socket != null) {
                try {
                    if (grupo != null) socket.leaveGroup(grupo); // Sai do grupo ao terminar.
                } catch (Exception ignorada) { }
                socket.close();
            }
        }
    }
}
