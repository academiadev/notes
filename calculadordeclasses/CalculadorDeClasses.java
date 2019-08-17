package br.com.desafio.desafio;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.stream.Stream;

public class CalculadorDeClasses implements Calculavel {

    @Override
    public BigDecimal somar(Object object) {
        return reduceValues(object, Somar.class);
    }

    private BigDecimal reduceValues(Object object, Class<? extends Annotation> clazz) {
        return Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(clazz))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(object);
                        return value instanceof BigDecimal ? (BigDecimal) value : BigDecimal.ZERO;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return BigDecimal.ZERO;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal subtrair(Object object) {
        return reduceValues(object, Subtrair.class);
    }


    @Override
    public BigDecimal totalizar(Object object) {
        return somar(object).subtract(subtrair(object));
    }
}
