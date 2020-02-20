package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Contrato;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UnitOcds")
@ResultsOnly(xmlroot = "UnitOcds")
public class Unit {
    //region Atributtes

    private String name;
    private Value value;

    //endregion
    //region Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    //endregion
    //region Constructors

    public Unit() {

    }
    public Unit(Contrato con){
        this.name="";
        this.value=new Value(con.getImporteSinIVA(),"EUR");
    }

    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Unit[" +
                "value=" + value +
                ", name='" + name + '\'' +
                ']';
    }

    //endregion
}
