package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitor = new Scanner(System.in);

    private ConsumoAPI consumoAPI = new ConsumoAPI();

    private ConverteDados conversor = new ConverteDados();

    private List<DadosTemporada> temporadas = new ArrayList<>();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {
        try {
            System.out.println("Digite o nome da série: ");
            var nomeSerie = leitor.nextLine();
            nomeSerie = nomeSerie.replace(" ", "+");

            var json = consumoAPI.obterDados(ENDERECO + nomeSerie + API_KEY);
            DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
            System.out.println(dados);

            for (int i = 1; i <= dados.totalTemporadas(); i++) {
                json = consumoAPI.obterDados(ENDERECO + nomeSerie + "&Season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            //        for (int i = 0; i < dados.totalTemporadas(); i++) {
            //            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            //            for (int j = 0; j < episodiosTemporada.size(); j++) {
            //                System.out.println(episodiosTemporada.get(j).titulo());
            //            }
            //        }

            temporadas.forEach(t -> t.episodios().forEach(
                    e -> System.out.println(e.titulo())
            ));

            List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream())
                    .collect(Collectors.toList());

            System.out.println("\nTop 5 episodios");
            dadosEpisodios.stream()
                    .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                    .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                    .limit(5)
                    .forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(d -> new Episodio(t.numero(), d)))
                    .collect(Collectors.toList());

            episodios.forEach(System.out::println);

            System.out.println("A partir de que ano você deseja ver os episódios?");
            var ano = leitor.nextInt();
            leitor.nextLine();

            LocalDate dataBusca = LocalDate.of(ano,1,1);

            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            episodios.stream()
                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                    .forEach(e -> System.out.println(
                            "Temporada: " + e.getTemporada() +
                            " Episódio: " + e.getTitulo() +
                            " Lançamento: " + e.getDataLancamento().format(format)));

        } catch (NullPointerException n) {
            System.out.println("Nome da série inválido");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ArrayList<String> nomes = new ArrayList<>(Arrays.asList("Java","Stream","Operações","Intermediárias"));
//
//        nomes.stream().forEach(nome -> System.out.println("Olá, " + nome + "!"));
    }
}
