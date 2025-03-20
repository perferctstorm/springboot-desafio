package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoAPI;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();

    private final String BASE_URL = "https://parallelum.com.br/fipe/api/v1";
    private final String OP_CARRO = "CARRO";
    private final String OP_MOTO = "MOTO";
    private final String OP_CAMINHAO = "CAMINHÃO";

    public void exibeMenu(){
        String menu = """
                    ***OPÇÕES***
                    CARRO
                    MOTO
                    CAMINHÃO
                    
                    Digite uma das opções para consultar: 
                """;
        System.out.println(menu);

        String opcao = scanner.nextLine();
        String endereco = "";

        if(OP_CARRO.contains(opcao.trim().toUpperCase())){
            endereco = String.format("%s/%s",BASE_URL,"carros/marcas");
        } else if (OP_MOTO.contains(opcao.toUpperCase())) {
            endereco = String.format("%s/%s",BASE_URL,"motos/marcas");
        } else if (OP_CAMINHAO.contains(opcao.toUpperCase())) {
            endereco = String.format("%s/%s",BASE_URL,"caminhoes/marcas");
        }else {
            System.out.println("Opção inválida.");
            System.exit(0);
        }
        //System.out.println(endereco);
        String json = consumoAPI.obterDados(endereco);
        List<Dados> marcas =  conversor.obterLista(json, Dados.class);
        marcas = marcas.stream()
                        .sorted(Comparator.comparing(Dados::nome))
                .collect(Collectors.toList());
        System.out.println("Lista de Fabricantes: ");
        marcas.stream().forEach(System.out::println);
        System.out.println();
        System.out.println();

        System.out.println("Informa a marca que deseja listar os modelos: ");
        Integer marca = scanner.nextInt();
        endereco = String.format("%s/%d/%s", endereco, marca,"modelos");
        json = consumoAPI.obterDados(endereco);
        Modelos modelos = conversor.obterDados(json, Modelos.class);
        System.out.println("Lista dos modelos: ");
        modelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::nome))
                .forEach(System.out::println);
        System.out.println();
        System.out.println();

        System.out.println("Qual modelo deseja pesquisar?");
        String textoModelo = scanner.next();
        List<Dados> modelosFiltrados = modelos.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(textoModelo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("Modelos Filtrados: ");
        modelosFiltrados.stream().forEach(System.out::println);
        System.out.println();
        System.out.println();

        System.out.println("Escolha códido de modelo: ");
        String codigoModelo = scanner.next();
        endereco = String.format("%s/%s/%s", endereco, codigoModelo, "anos");

        //System.out.println(String.format("Endereco: %s.", endereco));
        json = consumoAPI.obterDados(endereco);
        List<Dados> anos  = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<Veiculo>();

        for(Dados ano : anos) {
            var enderecoAno = String.format("%s/%s", endereco, ano.codigo());
            json = consumoAPI.obterDados(enderecoAno);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n\nAvaliações por ano: ");
        veiculos.forEach(System.out::println);
    }
}
