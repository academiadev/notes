package br.com.desafio;

import br.com.desafio.annotation.Desafio;
import br.com.desafio.app.NeedForSpeedInterface;
import br.com.desafio.exceptions.CarroNaoEncontradoException;
import br.com.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.desafio.exceptions.PilotoNaoEncontradoException;
import br.com.desafio.exceptions.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NeedForSpeedApplication implements NeedForSpeedInterface {

    private List<Piloto> pilotos = new ArrayList<>();
    private List<Carro> carros = new ArrayList<>();

    @Override
    @Desafio("novoPiloto")
    public void novoPiloto(Long id, String nome, LocalDate dataNascimento, LocalDate dataInicioCarreira, BigDecimal dinheiro) {
        if (buscarPilotoPorId(id).isPresent()) throw new IdentificadorUtilizadoException();
        pilotos.add(buildPiloto(id, nome, dataNascimento, dataInicioCarreira, dinheiro));
    }

    @Override
    @Desafio("comprarCarro")
    public void comprarCarro(Long id, Long idPiloto, String cor, String marca, Integer ano, Integer potencia, BigDecimal preco) {
        if (buscarCarroPorId(id).isPresent()) throw new IdentificadorUtilizadoException();
        Piloto piloto = buscarPilotoPorId(idPiloto).orElseThrow(PilotoNaoEncontradoException::new);
        if (piloto.getDinheiro().compareTo(preco) < 0) throw new SaldoInsuficienteException();
        piloto.subtraiDinheiro(preco);
        carros.add(buildCarro(id, idPiloto, cor, marca, ano, potencia, preco));
    }

    @Override
    @Desafio("venderCarro")
    public void venderCarro(Long id) {
        Carro carro = buscarCarroPorId(id).orElseThrow(CarroNaoEncontradoException::new);
        Piloto piloto = buscarPilotoPorId(carro.getIdPiloto()).orElseThrow(PilotoNaoEncontradoException::new);
        piloto.somaDinheiro(carro.getPreco());
        carros.remove(carro);
    }

    @Override
    @Desafio("buscarPilotos")
    public List<Long> buscarPilotos() {
        return pilotos.stream()
                .sorted(Comparator.comparing(Piloto::getId))
                .map(Piloto::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Desafio("buscarNomePiloto")
    public String buscarNomePiloto(Long idPiloto) {
        return buscarPilotoPorId(idPiloto)
                .map(Piloto::getNome)
                .orElseThrow(PilotoNaoEncontradoException::new);
    }

    @Override
    @Desafio("buscarPilotoMaisExperiente")
    public Long buscarPilotoMaisExperiente() {
        return pilotos.stream()
                .sorted(Comparator.comparing(Piloto::getId))
                .min(Comparator.comparing(Piloto::getDataInicioCarreira))
                .map(Piloto::getId)
                .orElse(null);
    }

    @Override
    @Desafio("buscarPilotoMenosExperiente")
    public Long buscarPilotoMenosExperiente() {
        return pilotos.stream()
                .sorted(Comparator.comparing(Piloto::getId))
                .max(Comparator.comparing(Piloto::getDataInicioCarreira))
                .map(Piloto::getId)
                .orElse(null);
    }

    @Override
    @Desafio("buscarSaldo")
    public BigDecimal buscarSaldo(Long idPiloto) {
        return buscarPilotoPorId(idPiloto)
                .map(Piloto::getDinheiro)
                .orElseThrow(PilotoNaoEncontradoException::new);
    }

    @Override
    @Desafio("buscarCarros")
    public List<Long> buscarCarros(Long idPiloto) {
        Piloto piloto = buscarPilotoPorId(idPiloto).orElseThrow(PilotoNaoEncontradoException::new);
        return buscarCarrosDoPiloto(piloto.getId())
                .map(Carro::getId)
                .sorted()
                .collect(Collectors.toList());
    }

    private Stream<Carro> buscarCarrosDoPiloto(Long idPiloto) {
        return carros.stream()
                .filter(c -> c.getIdPiloto().equals(idPiloto));
    }

    @Override
    @Desafio("buscarCarroMaisCaro")
    public Long buscarCarroMaisCaro() {
        return carros.stream()
                .sorted(Comparator.comparing(Carro::getId))
                .max(Comparator.comparing(Carro::getPreco)).get().getId();
    }

    @Override
    @Desafio("buscarCarroMaisPotente")
    public Long buscarCarroMaisPotente() {
        return carros.stream()
                .sorted(Comparator.comparing(Carro::getId))
                .max(Comparator.comparing(Carro::getPotencia)).get().getId();
    }

    @Override
    @Desafio("buscarCarrosPorMarca")
    public List<Long> buscarCarrosPorMarca(String marca) {
        return carros.stream()
                .filter(c -> c.getMarca().equalsIgnoreCase(marca))
                .map(Carro::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Desafio("buscarMarcas")
    public List<String> buscarMarcas() {
        return carros.stream()
                .map(Carro::getMarca)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Desafio("buscarValorPatrimonio")
    public BigDecimal buscarValorPatrimonio(Long idPiloto) {
        Piloto piloto = buscarPilotoPorId(idPiloto).orElseThrow(PilotoNaoEncontradoException::new);
        return buscarCarrosDoPiloto(piloto.getId())
                .map(Carro::getPreco)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Desafio("trocarCor")
    public void trocarCor(Long idCarro, String cor) {
        buscarCarroPorId(idCarro)
                .orElseThrow(CarroNaoEncontradoException::new)
                .setCor(cor);
    }

    @Override
    @Desafio("buscarCor")
    public String buscarCor(Long idCarro) {
        return buscarCarroPorId(idCarro)
                .map(Carro::getCor)
                .orElseThrow(CarroNaoEncontradoException::new);
    }

    private Piloto buildPiloto(Long id, String nome, LocalDate dataNascimento, LocalDate dataInicioCarreira, BigDecimal dinheiro) {
        Piloto p = new Piloto();
        p.setId(id);
        p.setNome(nome);
        p.setDataNascimento(dataNascimento);
        p.setDataInicioCarreira(dataInicioCarreira);
        p.setDinheiro(dinheiro);
        return p;
    }

    private Optional<Piloto> buscarPilotoPorId(Long id) {
        return pilotos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    private Carro buildCarro(Long id, Long idPiloto, String cor, String marca, Integer ano, Integer potencia, BigDecimal preco) {
        Carro c = new Carro();
        c.setId(id);
        c.setIdPiloto(idPiloto);
        c.setCor(cor);
        c.setMarca(marca);
        c.setAno(ano);
        c.setPotencia(potencia);
        c.setPreco(preco);
        return c;
    }

    private Optional<Carro> buscarCarroPorId(Long id) {
        return carros.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    class Piloto {

        private Long id;
        private String nome;
        private LocalDate dataNascimento;
        private LocalDate dataInicioCarreira;
        private BigDecimal dinheiro;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public LocalDate getDataNascimento() {
            return dataNascimento;
        }

        public void setDataNascimento(LocalDate dataNascimento) {
            this.dataNascimento = dataNascimento;
        }

        public LocalDate getDataInicioCarreira() {
            return dataInicioCarreira;
        }

        public void setDataInicioCarreira(LocalDate dataInicioCarreira) {
            this.dataInicioCarreira = dataInicioCarreira;
        }

        public BigDecimal getDinheiro() {
            return dinheiro;
        }

        public void setDinheiro(BigDecimal dinheiro) {
            this.dinheiro = dinheiro;
        }

        public BigDecimal subtraiDinheiro(BigDecimal dinheiro) {
            this.dinheiro = this.dinheiro.subtract(dinheiro);
            return this.dinheiro;
        }

        public BigDecimal somaDinheiro(BigDecimal dinheiro) {
            this.dinheiro = this.dinheiro.add(dinheiro);
            return this.dinheiro;
        }

    }

    class Carro {

        private Long id;
        private Long idPiloto;
        private String cor;
        private String marca;
        private Integer ano;
        private Integer potencia;
        private BigDecimal preco;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getIdPiloto() {
            return idPiloto;
        }

        public void setIdPiloto(Long idPiloto) {
            this.idPiloto = idPiloto;
        }

        public String getCor() {
            return cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }

        public String getMarca() {
            return marca;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public Integer getPotencia() {
            return potencia;
        }

        public void setPotencia(Integer potencia) {
            this.potencia = potencia;
        }

        public BigDecimal getPreco() {
            return preco;
        }

        public void setPreco(BigDecimal preco) {
            this.preco = preco;
        }

    }
}
