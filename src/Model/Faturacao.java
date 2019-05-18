package Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Faturacao {
    Map<String, IFatura> faturacao;

    public Faturacao() {
        this.faturacao = new HashMap<>();
    }

    public Faturacao(ICatProds p) {
        this.faturacao = p
                .productList()
                .stream()
                .map(IProduct::getId)
                .collect(Collectors
                        .toMap(Function.identity(), Fatura::new));
    }

    public Faturacao syncWithSales(List<IVenda> l) {
        this.faturacao = l
                .stream()
                .map(e -> this.faturacao
                        .get(e.getCodProd())
                        .update(e))
                .collect(Collectors.toMap(IFatura::getProdId, Function.identity(), (e1, e2) -> e1));
        return this;
    }

    public double totalFaturado() {
        return this
                .faturacao
                .values()
                .stream()
                .mapToDouble(IFatura::getTotal)
                .sum();
    }
}
