/* Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
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
