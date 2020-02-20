package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "ValueOcds")
@ResultsOnly(xmlroot = "ValueOcds")
public class Value {
    //region Atributtes
    private Double amount;
    private String currency;
    //endregion
    //region Getters & Setters

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    //endregion
    //region Contructors
    public Value(BigDecimal valor,String currency){
        if(valor==null){
            valor=BigDecimal.valueOf(0.0) ;
        }
        this.setAmount(valor.doubleValue());
        this.currency=currency;
    }
    //endregion
}
