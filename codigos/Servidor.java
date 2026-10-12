import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Servidor da loja: atende clientes TCP e envia avisos por multicast UDP.
public class Servidor {
    private static final int PORTA_TCP = 5055;
    private static final int PORTA_UDP = 5000;
    private static final String GRUPO = "230.0.0.1";
    private static final ProdutoService produtos = new ProdutoService();
    private static final PedidoService pedidos = new PedidoService();

    public static void main(String[] args) {
        Thread avisos = new Thread(new Runnable() {
            public void run() { enviarAvisos(); }
        });
        avisos.setDaemon(true);
        avisos.start();

        System.out.println("Servidor da loja iniciado.");
        try {
            ServerSocket servidor = new ServerSocket(PORTA_TCP);
            while (true) {
                final Socket cliente = servidor.accept();
                new Thread(new Runnable() {
                    public void run() { atender(cliente); }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static void atender(Socket socket) {
        try {
            ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
            saida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            // O cliente envia [usuario, senha]; o servidor responde se autorizou.
            String[] login = (String[]) entrada.readObject();
            boolean autorizado = (login[0].equals("cliente") && login[1].equals("123"))
                    || (login[0].equals("admin") && login[1].equals("admin123"));
            saida.writeObject(autorizado ? "OK" : "Login invalido");
            saida.flush();
            if (!autorizado) { socket.close(); return; }

            while (true) {
                Object pedido = entrada.readObject();
                
                if (pedido.equals("LISTAR")) {
                    saida.writeObject(produtos.listar());
                } else if (pedido.equals("TOTAL_PEDIDOS")) {
                    saida.writeObject("Total de pedidos recebidos: "
                            + pedidos.consultarTotal());
                } else if (pedido instanceof Pedido) {
                    saida.writeObject(pedidos.registrar((Pedido) pedido));
                } else {
                    saida.writeObject("Pedido nao reconhecido");
                }

                saida.flush();
            }
        } catch (EOFException fim) {
            // O cliente encerrou a conexão.
        } catch (Exception e) {
            System.out.println("Conexao encerrada: " + e.getMessage());
        }
    }

    //envia os avisos para os clientes
    private static void enviarAvisos() {
        try {
            DatagramSocket socket = new DatagramSocket();
            Scanner teclado = new Scanner(System.in);
            InetAddress grupo = InetAddress.getByName(GRUPO);
            while (true) {
                System.out.print("Aviso para os clientes (ou /sair): ");
                String texto = teclado.nextLine();
                if (texto.equalsIgnoreCase("/sair")) break;
                String seguro = texto.replace("\\", "\\\\").replace("\"", "\\\"");
                String mensagem = "{\"tipo\":\"NOTIFICACAO\",\"mensagem\":\"" + seguro
                        + "\",\"timestamp\":" + System.currentTimeMillis() + "}";
                byte[] dados = mensagem.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(dados, dados.length, grupo, PORTA_UDP));
            }
            socket.close();
        } catch (Exception e) {
            System.out.println("Nao foi possível enviar aviso multicast.");
        }
    }
}
