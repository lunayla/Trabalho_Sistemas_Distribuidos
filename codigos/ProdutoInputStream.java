import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

// Lê os mesmos dados binários escritos por ProdutoOutputStream.
public class ProdutoInputStream extends InputStream {
    private DataInputStream entrada;

    public ProdutoInputStream(InputStream origem) {
        entrada = new DataInputStream(origem);
    }

    public Produto[] lerProdutos() throws IOException {
        int quantidade = entrada.readInt();
        if (quantidade < 0 || quantidade > 1000) {
            throw new IOException("Quantidade de produtos invalida: " + quantidade);
        }
        Produto[] produtos = new Produto[quantidade];
        for (int i = 0; i < quantidade; i++) {
            int id = entrada.readInt();
            String nome = entrada.readUTF();
            double preco = entrada.readDouble();
            produtos[i] = new Produto(id, nome, preco);
        }
        return produtos;
    }

    @Override
    public int read() throws IOException { return entrada.read(); }
}
