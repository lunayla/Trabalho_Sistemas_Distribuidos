import java.io.Serializable;

// POJO: representa um pedido feito por um cliente.
public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String cliente;

    public Pedido(int id, String cliente) {
        this.id = id;
        this.cliente = cliente;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }

    @Override
    public String toString() {
        return "Pedido " + id + " do cliente";
    }
}
