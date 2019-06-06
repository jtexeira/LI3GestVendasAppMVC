package Model;

public interface IFaturacao {
    /**
     * Atualiza uma fatura com informação de uma venda
     * @param v Venda com a informação
     * @return Faturação atualizada
     */
    Faturacao update(IVenda v) {
        this.faturacao.get(v.getCodProd()).update(v);
        return this;
    }

    /**
     * Calcula o total faturado
     * @return Faturação Total
     */
    double faturacaoTotal() {
        return this.faturacao.values().stream()
                .mapToDouble(IFatura::getTotal)
                .sum();
    }

    /**
     * Calcula o total faturado por mês
     * @return Faturação mensal
     */
    Map<Integer, Double> totalFaturado() {
        return this.faturacao.values()
                .stream()
                .flatMap(e -> e.getTotalMensal().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));
    }

    /**
     * Calcula o total faturado por mês numa filial
     * @param filial Filial que é desejada a faturação
     * @return Faturação mensal da filial
     */
    Map<Integer, Double> totalFaturadoFilial(int filial) {
        return this.faturacao.values()
                .stream()
                .flatMap(e -> e.getTotalFilial(filial).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum));
    }

    /**
     * Calcula o número de produtos que foram comprados
     * @return Número de produtos que foram comprados
     */
    public int produtosComprados() {
        return (int) this.faturacao
                .values()
                .stream()
                .filter(e -> !(e.getProdId().equals("")))
                .count();
    }

    /**
     * Calcula a lista ordenada de produtos que não foram comprados
     * @return Lista com IDs dos produtos não comprados
     */
    List<String> listaProdutosNaoComprados() {
        return this.faturacao
                .values()
                .stream()
                .filter(e -> e.getProdId().equals(""))
                .map(IFatura::getProdId)
                .sorted()
                .collect(Collectors.toList());
    }
}
