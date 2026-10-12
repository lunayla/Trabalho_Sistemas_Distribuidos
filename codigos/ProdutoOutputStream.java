import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

// Escreve os produtos em formato binário em qualquer OutputStream.
public class ProdutoOutputStream extends OutputStream {
    private DataOutputStream saida;

    public ProdutoOutputStream(OutputStream destino, Produto[] produtos, int quantidade) throws IOException {
        if (quantidade < 0 || quantidade > produtos.length) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        saida = new DataOutputStream(destino);
        saida.writeInt(quantidade);
        for (int i = 0; i < quantidade; i++) {
            saida.writeInt(produtos[i].getId());
            saida.writeUTF(produtos[i].getNome());
            saida.writeDouble(produtos[i].getPreco());
        }
        saida.flush();
    }

    @Override
    public void write(int b) throws IOException { saida.write(b); }

    @Override
    public void flush() throws IOException { saida.flush(); }
}
