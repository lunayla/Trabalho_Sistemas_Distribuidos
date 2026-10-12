// Serviço: processa os pedidos recebidos.
public class PedidoService {
    private int total = 0;

    public synchronized String registrar(Pedido pedido) {
        total++;
        return pedido + " recebido. Total de pedidos: " + total;
    }
    
    public synchronized int consultarTotal() {
        return total;
    }

}
