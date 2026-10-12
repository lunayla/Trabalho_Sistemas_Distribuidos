import java.util.ArrayList;
import java.util.List;

// Serviço: mantém e consulta o catálogo da loja.
public class ProdutoService {
    private List<Produto> produtos = new ArrayList<Produto>();

    public ProdutoService() {
        produtos.add(new Produto(1, "Notebook", 3500.00));
        produtos.add(new Produto(2, "Mouse", 150.00));
        produtos.add(new Produto(3, "Teclado", 220.00));
    }

    public Produto[] listar() {
        return produtos.toArray(new Produto[produtos.size()]);
    }
}
