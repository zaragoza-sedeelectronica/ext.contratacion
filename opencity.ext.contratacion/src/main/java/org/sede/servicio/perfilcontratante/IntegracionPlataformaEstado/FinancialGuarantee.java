package org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado;



public class FinancialGuarantee {


    //Campo comun
    private String advertisementAmount = "";

    // Campos para garantia definitiva
    private String finalFinancialGuarantee = "";
    private String finalFinancialGuaranteeDuration = "";

    // Campos para garantia provisional
    private String provisionalFinancialGuarantee = "";


    public FinancialGuarantee() {
        // TODO Auto-generated constructor stub
    }


    public String getAdvertisementAmount() {
        return advertisementAmount;
    }


    public void setAdvertisementAmount(String advertisementAmount) {
        this.advertisementAmount = advertisementAmount;
    }


    public String getFinalFinancialGuarantee() {
        return finalFinancialGuarantee;
    }


    public void setFinalFinancialGuarantee(String finalFinancialGuarantee) {
        this.finalFinancialGuarantee = finalFinancialGuarantee;
    }


    public String getFinalFinancialGuaranteeDuration() {
        return finalFinancialGuaranteeDuration;
    }


    public void setFinalFinancialGuaranteeDuration(
            String finalFinancialGuaranteeDuration) {
        this.finalFinancialGuaranteeDuration = finalFinancialGuaranteeDuration;
    }


    public String getProvisionalFinancialGuarantee() {
        return provisionalFinancialGuarantee;
    }


    public void setProvisionalFinancialGuarantee(
            String provisionalFinancialGuarantee) {
        this.provisionalFinancialGuarantee = provisionalFinancialGuarantee;
    }

}

