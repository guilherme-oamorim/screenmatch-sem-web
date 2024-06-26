package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias ("Title") String titulo,
                            @JsonAlias ("Released")String dataLancamento,
                            @JsonAlias ("Episode") Integer numero,
                            @JsonAlias ("imdbRating") String avaliacao){
}
