package Model;

import Exceptions.InvalidFilialException;
import Exceptions.InvalidProductExecption;
import Exceptions.IvalidClientException;
import Exceptions.MesInvalidoException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.file.*;
import java.util.stream.Collectors;

import static java.lang.System.*;

public class GestVendasModel {
    private ICatCli catCli;
    private ICatProds catProds;
    private List<IVenda> vendas;
    private Faturacao faturacao;
    private Filial[] filiais;
    private Constantes constantes;

    public GestVendasModel(String clients, String products, String sales) {
        this.catCli = new CatCli(clients);
        this.catProds = new CatProds(products);
        this.constantes = new Constantes();
        try {
            this.vendas = Files
                    .readAllLines(Paths.get(sales), StandardCharsets.UTF_8)
                    .stream()
                    .map(Venda::new)
                    .filter(e -> e.validSale()
                            && catProds.exists(e.getCodProd())
                            && catCli.exists(e.getCodCli()))
                    .collect(Collectors
                            .toList());
            out.println(vendas.size());
        }
        catch(IOException e) {
            out.println(e);
        }

        faturacao = new Faturacao(catProds);
        this.filiais = new Filial[constantes.numeroFiliais()];
        for (int i = 0; i < constantes.numeroFiliais(); i++) {
            this.filiais[i] = new Filial();
        }
        this.vendas.forEach(e -> {this.faturacao.update(e); this.filiais[e.getFilial()-1].update(e);});
        out.println(faturacao.totalFaturado());
    }

    public long getVendasDadas() {
        return this
                .vendas
                .stream()
                .filter(e -> e.totalSale() == 0)
                .count();
    }

    public int clientesPorFilial(int filial) throws InvalidFilialException {
        if(this.constantes.filialValida(filial))
            throw new InvalidFilialException();
        return this.filiais[filial-1].getNClientes();
    }

    //query 1
    public List<String> listaDeProdutosNaoComprados() {
        return this.faturacao.listaProdutosNaoComprados();
    }

    //query 2 vendas, clientes
    public Map.Entry<Integer, Integer> clientesVendasTotais(int mes) throws MesInvalidoException {
        if(!constantes.mesValido(mes))
            throw new MesInvalidoException();
        List<IVenda> a = this.vendas.stream().filter(e -> e.getMonth() != mes).collect(Collectors.toList());
        return new AbstractMap.SimpleEntry<>(a.size(), (int) a
                .stream()
                .map(IVenda::getCodCli)
                .distinct()
                .count());
    }

    public Map.Entry<Integer, Integer> clientesVendasTotais(int filial, int mes) throws InvalidFilialException, MesInvalidoException {
        if(!constantes.filialValida(filial))
            throw new InvalidFilialException();
        if(!constantes.mesValido(mes))
            throw new MesInvalidoException();
        return this.filiais[filial-1].clientesVendasTotais(mes);
    }

    //query 3 (produtos comprados, n compras, quanto gastou)
    public Map.Entry<Integer, Map.Entry<Integer,Double>> statsClientes(String clientID, int mes) throws MesInvalidoException, IvalidClientException {
        if(!constantes.mesValido(mes))
            throw new MesInvalidoException();
        if(!this.catCli.exists(clientID))
            throw new IvalidClientException();
        Set<String> ree = new HashSet<>();
        int vezes = 0;
        double total = 0;
        for(Filial a : this.filiais) {
            Map.Entry<Set<String>, Map.Entry<Integer,Double>> o = a.statsCliente(clientID, mes);
            ree.addAll(o.getKey());
            vezes += o.getValue().getKey();
            total += o.getValue().getValue();
        }
        return new AbstractMap.SimpleEntry<>(ree.size(), new AbstractMap.SimpleEntry<>(vezes, total));
    }

    public Map.Entry<Integer, Map.Entry<Integer,Double>> statsProdutos(String productID, int mes) throws MesInvalidoException, InvalidProductExecption {
        if(!constantes.mesValido(mes))
            throw new MesInvalidoException();
        if(!this.catProds.exists(productID))
            throw new InvalidProductExecption();
        Set<String> ree = new HashSet<>();
        int vezes = 0;
        double total = 0;
        for(Filial a : this.filiais) {
            Map.Entry<Set<String>, Map.Entry<Integer,Double>> o = a.statsProduto(productID, mes);
            ree.addAll(o.getKey());
            vezes += o.getValue().getKey();
            total += o.getValue().getValue();
        }
        return new AbstractMap.SimpleEntry<>(ree.size(), new AbstractMap.SimpleEntry<>(vezes, total));
    }

    //Query 5
    public List<Map.Entry<String, Integer>> produtosPorCliente(String clientID) throws IvalidClientException {
        if(!this.catCli.exists(clientID))
            throw new IvalidClientException();
        List<Map<String, Integer>> b = new ArrayList<>();
        for(Filial a : this.filiais) {
            b.add(a.produtosCompradosPorCliente(clientID));
        }
        return b.stream()
                .filter(Objects::nonNull)
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    int a = Integer.compare(o2.getValue(), o1.getValue());
                    if(a == 0)
                        a = o2.getKey().compareTo(o1.getKey());
                    return a;
                })
                .collect(Collectors.toList());
    }

    //query 6
    public List<Map.Entry<String, Integer>> produtosMaisVendidos(int limite) {
        List<Map<String, Map.Entry<Integer,Integer>>> a = new ArrayList<>();
        for(Filial x : this.filiais) {
            a.add(x.produtosMaisVendidos());
        }
        return a.stream()
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> new AbstractMap.SimpleEntry<>(e1.getKey(), e1.getValue() + e2.getValue())))
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(Map.Entry.comparingByKey())))
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue().getValue()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    //query 7
    public List<String> melhoresClientesPorFilial(int filial) throws InvalidFilialException {
        if(!this.constantes.filialValida(filial))
            throw new InvalidFilialException();
        return this.filiais[filial-1].getBestBuyers();
    }

    //query 8
    public List<String> clientesComMaisDiversidade(int limite) {
        List<Map<String, Set<String>>> a = new ArrayList<>();
        for(Filial x : this.filiais) {
            a.add(x.maisDiversidadeDeProdutos());
        }
        return a.stream()
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> {e1.addAll(e2); return e1;}))
                .entrySet()
                .stream()
                .sorted(Collections
                        .reverseOrder(Map.Entry
                                .comparingByValue(Comparator.comparingInt(Set::size))))
                .map(Map.Entry::getKey)
                .limit(limite)
                .collect(Collectors.toList());
    }

    //query 9
    public List<Map.Entry<String,Double>> clientesQueMaisCompraram(String prodID, int limite) throws InvalidProductExecption {
        if(!this.catProds.exists(prodID))
            throw new InvalidProductExecption();
        return Arrays.stream(filiais)
                .flatMap(e -> e.clientesQueMaisCompraram(prodID)
                        .entrySet()
                        .stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::sum))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    int a = Double.compare(o2.getValue(), o1.getValue());
                    if(a == 0)
                        a = o2.getKey().compareTo(o1.getKey());
                    return a;
                })
                .limit(limite)
                .collect(Collectors.toList());
    }
}
