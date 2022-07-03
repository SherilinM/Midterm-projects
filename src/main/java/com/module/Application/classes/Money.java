package com.module.Application.classes;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public class Money {

    /** Properties **/

    private BigDecimal amount;
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
    private final Currency currency;

    public Money() {
        currency = Currency.getInstance("USD");
    }

    /**
     * Clase constructor specifying amount, currency, y rounding
     **/
    public Money(BigDecimal amount, Currency currency, RoundingMode rounding) {
        this.currency = currency;
        setAmount(amount.setScale(currency.getDefaultFractionDigits(), rounding));
    }
    /**
     * Clase constructor specifying amount, y currency. Uses default RoundingMode HALF_EVEN.
     **/
    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, DEFAULT_ROUNDING);
    }
    /**
     * Clase constructor specifying amount. Uso default RoundingMode HALF_EVEN y default currency USD.
     **/
    public Money(BigDecimal amount) {
        this(amount, EUR, DEFAULT_ROUNDING);
    }

//obtengo el amount y le sumo lo que tengo. Esto es con Money a money. Aqui me toma en cuenta la currencia
    //ej: si quiero aumentar de euro a dolar
    public BigDecimal increaseAmount(Money money) {
        setAmount(this.amount.add(money.amount));
        return this.amount;
    }

    //este no toma en cuenta nada, solo la cantidad que quiero aumentar
    public BigDecimal increaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.add(addAmount));
        return this.amount;
    }

    // lo mismo de arriba pero en vez de sumar lo resta
    public BigDecimal decreaseAmount(Money money) {
        setAmount(this.amount.subtract(money.getAmount()));
        return this.amount;
    }
    public BigDecimal decreaseAmount(BigDecimal addAmount) {
        setAmount(this.amount.subtract(addAmount));
        return this.amount;
    }
    public Currency getCurrency() {
        return this.currency;
    }
    public BigDecimal getAmount() {
        return this.amount;
    }
    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String toString() {
        return getCurrency().getSymbol() + " " + getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(currency, money.currency) &&
                Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amount);
    }
}