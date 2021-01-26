package org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "awardcriterion")
@ResultsOnly(xmlroot = "awardcriterion")
public class AwardCriterion {

    private String criterionEvaluationMode = "";
    private String criterionName = "";
    private String criterionWeight = "";

    public AwardCriterion() {
        // TODO Auto-generated constructor stub
    }

    public String getCriterionEvaluationMode() {
        return criterionEvaluationMode;
    }

    public void setCriterionEvaluationMode(String criterionEvaluationMode) {
        this.criterionEvaluationMode = criterionEvaluationMode;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public String getCriterionWeight() {
        return criterionWeight;
    }

    public void setCriterionWeight(String criterionWeight) {
        this.criterionWeight = criterionWeight;
    }

}
