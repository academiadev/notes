package br.com.codenation;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

    List<Jogador> jogadores = new ArrayList<>();
    List<Time> times = new ArrayList<>();

    @Desafio("incluirTime")
    public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {
        buscarTimePorId(id).ifPresent(time -> {
            throw new IdentificadorUtilizadoException();
        });

        times.add(Time.builder().id(id)
                .nome(nome)
                .dataCriacao(dataCriacao)
                .corUniformePrincipal(corUniformePrincipal)
                .corUniformeSecundario(corUniformeSecundario)
                .build());
    }

    private Optional<Time> buscarTimePorId(Long id) {
        return times.stream().filter(time -> time.getId().equals(id)).findFirst();
    }

    @Desafio("incluirJogador")
    public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
        Time time = buscarTimePorId(idTime).orElseThrow(TimeNaoEncontradoException::new);
        buscarJogadorPorId(id).ifPresent(jogador -> {
            throw new IdentificadorUtilizadoException();
        });
        jogadores.add(Jogador.builder()
                .id(id)
                .idTime(time.getId())
                .nome(nome)
                .dataNascimento(dataNascimento)
                .habilidade(nivelHabilidade)
                .salario(salario)
                .build());
    }

    private Optional<Jogador> buscarJogadorPorId(Long id) {
        return jogadores.stream().filter(jogador -> jogador.getId().equals(id)).findFirst();
    }

    @Desafio("definirCapitao")
    public void definirCapitao(Long idJogador) {
        Jogador jogador = buscarJogadorPorId(idJogador).orElseThrow(JogadorNaoEncontradoException::new);
        Time time = buscarTimePorId(jogador.getIdTime()).orElseThrow(TimeNaoEncontradoException::new);
        time.setIdJogadorCapitao(jogador.getId());
    }

    @Desafio("buscarCapitaoDoTime")
    public Long buscarCapitaoDoTime(Long idTime) {
        Time time = buscarTimePorId(idTime).orElseThrow(TimeNaoEncontradoException::new);
        return time.getIdJogadorCapitao().orElseThrow(CapitaoNaoInformadoException::new);
    }

    @Desafio("buscarNomeJogador")
    public String buscarNomeJogador(Long idJogador) {
        return buscarJogadorPorId(idJogador).map(Jogador::getNome).orElseThrow(JogadorNaoEncontradoException::new);
    }

    @Desafio("buscarNomeTime")
    public String buscarNomeTime(Long idTime) {
        return buscarTimePorId(idTime).map(Time::getNome).orElseThrow(TimeNaoEncontradoException::new);
    }

    @Desafio("buscarJogadoresDoTime")
    public List<Long> buscarJogadoresDoTime(Long idTime) {
        buscarTimePorId(idTime).orElseThrow(TimeNaoEncontradoException::new);
        return jogadores.stream()
                .filter(jogador -> jogador.getIdTime().equals(idTime))
                .map(jogador -> jogador.getId()).collect(Collectors.toList());
    }

    @Desafio("buscarMelhorJogadorDoTime")
    public Long buscarMelhorJogadorDoTime(Long idTime) {
//        return jogadores.stream()
//                .filter(jogador -> jogador.getIdTime().equals(idTime))
//                .sorted(Comparator.comparing(Jogador::getId)
//                        .thenComparing(jogador -> jogador.getHabilidade()).reversed())
//                .findFirst().map(Jogador::getId).orElseThrow(JogadorNaoEncontradoException::new);

        return jogadores.stream()
                .filter(jogador -> jogador.getIdTime().equals(idTime))
                .sorted(Comparator.comparing(Jogador::getId)).max(Comparator.comparing(Jogador::getHabilidade))
                .map(Jogador::getId)
                .orElseThrow(TimeNaoEncontradoException::new);
    }

    @Desafio("buscarJogadorMaisVelho")
    public Long buscarJogadorMaisVelho(Long idTime) {
        return jogadores.stream()
                .filter(jogador -> jogador.getIdTime().equals(idTime))
                .sorted(Comparator.comparing(Jogador::getId)).min(Comparator.comparing(Jogador::getDataNascimento))
                .map(Jogador::getId)
                .orElseThrow(TimeNaoEncontradoException::new);
    }

    @Desafio("buscarTimes")
    public List<Long> buscarTimes() {
        return times.stream().map(Time::getId).collect(Collectors.toList());
    }

    @Desafio("buscarJogadorMaiorSalario")
    public Long buscarJogadorMaiorSalario(Long idTime) {
        return jogadores.stream()
                .filter(jogador -> jogador.getIdTime().equals(idTime))
                .sorted(Comparator.comparing(Jogador::getId)).max(Comparator.comparing(Jogador::getSalario))
                .map(Jogador::getId)
                .orElseThrow(TimeNaoEncontradoException::new);
    }

    @Desafio("buscarSalarioDoJogador")
    public BigDecimal buscarSalarioDoJogador(Long idJogador) {
        return buscarJogadorPorId(idJogador).map(Jogador::getSalario).orElseThrow(JogadorNaoEncontradoException::new);
    }

    @Desafio("buscarTopJogadores")
    public List<Long> buscarTopJogadores(Integer top) {
        return jogadores.stream().sorted(Comparator.comparing(Jogador::getHabilidade).reversed()
                .thenComparing(Jogador::getId))
                .limit(top)
                .map(Jogador::getId).collect(Collectors.toList());
    }

    @Desafio("buscarCorCamisaTimeDeFora")
    public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {
        Time timeCasa = buscarTimePorId(timeDaCasa).orElseThrow(TimeNaoEncontradoException::new);
        Time timeFora = buscarTimePorId(timeDeFora).orElseThrow(TimeNaoEncontradoException::new);

        if (timeCasa.getCorUniformePrincipal().equals(timeFora.getCorUniformePrincipal())) {
            return timeFora.getCorUniformeSecundario();
        } else {
            return timeFora.getCorUniformePrincipal();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Jogador {
        private Long id;
        private Long idTime;
        private String nome;
        private LocalDate dataNascimento;
        private Integer habilidade;
        private BigDecimal salario;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Time {
        private Long id;
        private String nome;
        private LocalDate dataCriacao;
        private String corUniformePrincipal;
        private String corUniformeSecundario;
        private Long idJogadorCapitao;

        public Optional<Long> getIdJogadorCapitao() {
            return Optional.ofNullable(idJogadorCapitao);
        }

    }
}