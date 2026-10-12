import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Executa todos os testes de streams com um único clique em Run Java.
public class TestesStreams {
    private static Produto[] exemplos() {
        return new Produto[] {
            new Produto(1, "Notebook", 3500.00),
            new Produto(2, "Mouse", 150.00)
        };
    }

    public static void main(String[] args) throws Exception {
        Produto[] produtos = exemplos();

        // 1) System.out: captura a saída padrão para não mostrar bytes ilegíveis.
        ByteArrayOutputStream capturaSaida = new ByteArrayOutputStream();
        java.io.PrintStream saidaOriginal = System.out;
        System.setOut(new java.io.PrintStream(capturaSaida));
        new ProdutoOutputStream(System.out, produtos, produtos.length);
        System.setOut(saidaOriginal);
        System.out.println("Teste System.out: OK");

        // 2) Arquivo: grava e depois lê os produtos.
        FileOutputStream arquivo = new FileOutputStream("teste.bin");
        new ProdutoOutputStream(arquivo, produtos, produtos.length);
        arquivo.close();
        ProdutoInputStream leituraArquivo = new ProdutoInputStream(new FileInputStream("teste.bin"));
        mostrar("Teste de arquivo", leituraArquivo.lerProdutos());
        leituraArquivo.close();

        // 3) System.in: usa bytes válidos como se tivessem sido redirecionados para a entrada padrão.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        new ProdutoOutputStream(bytes, produtos, produtos.length);
        InputStream entradaOriginal = System.in;
        System.setIn(new ByteArrayInputStream(bytes.toByteArray()));
        ProdutoInputStream leituraPadrao = new ProdutoInputStream(System.in);
        mostrar("Teste System.in", leituraPadrao.lerProdutos());
        System.setIn(entradaOriginal);

        // 4) TCP: envia produtos de um socket cliente para um socket servidor local.
        testarTcp(produtos);
        System.out.println("\nTodos os testes de streams terminaram.");
    }

    private static void mostrar(String titulo, Produto[] produtos) {
        System.out.println("\n" + titulo + ":");
        for (Produto produto : produtos) System.out.println(produto);
    }

    private static void testarTcp(final Produto[] produtos) throws Exception {
        final ServerSocket servidor = new ServerSocket(0);
        Thread receptor = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket conexao = servidor.accept();
                    ProdutoInputStream entrada = new ProdutoInputStream(conexao.getInputStream());
                    mostrar("Teste TCP", entrada.lerProdutos());
                    conexao.close();
                    servidor.close();
                } catch (Exception e) {
                    System.out.println("Erro no teste TCP: " + e.getMessage());
                }
            }
        });
        receptor.start();
        Socket cliente = new Socket("127.0.0.1", servidor.getLocalPort());
        new ProdutoOutputStream(cliente.getOutputStream(), produtos, produtos.length);
        cliente.close();
        receptor.join();
    }
}
