import java.io.IOException;

public class TesteEntradaPadrao {
    public static void main(String[] args) throws IOException {
        ProdutoInputStream entrada = new ProdutoInputStream(System.in);
        Produto[] produtos = entrada.lerProdutos();
        for (Produto produto : produtos) System.out.println(produto);
    }
}
