package ru.blogic.dss.api.dto.dsspolicy;

import ru.blogic.dss.api.dto.MfaPolicyState;
import ru.blogic.dss.api.dto.PinCodeMode;
import ru.blogic.dss.api.dto.SignatureType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pkupershteyn on 01.12.2015.
 */
public class DssPolicy implements Serializable{
    private List<DssActionInfo> actionPolicy;
    private List<DssCAPolicy> CAPolicy;
    private List<DssCspPolicy> CSPsPolicy;
    private List<SignatureType> allowedSignatureTypes;
    private PinCodeMode pinCodeMode;
    private MfaPolicyState transactionConfirmation;
    private List<TspService> tspServices;

    public List<DssActionInfo> getActionPolicy() {
        return actionPolicy;
    }

    public void setActionPolicy(List<DssActionInfo> actionPolicy) {
        this.actionPolicy = actionPolicy;
    }

    public List<DssCAPolicy> getCAPolicy() {
        return CAPolicy;
    }

    public void setCAPolicy(List<DssCAPolicy> CAPolicy) {
        this.CAPolicy = CAPolicy;
    }

    public List<DssCspPolicy> getCSPsPolicy() {
        return CSPsPolicy;
    }

    public void setCSPsPolicy(List<DssCspPolicy> CSPsPolicy) {
        this.CSPsPolicy = CSPsPolicy;
    }

    public List<SignatureType> getAllowedSignatureTypes() {
        return allowedSignatureTypes;
    }

    public void setAllowedSignatureTypes(List<SignatureType> allowedSignatureTypes) {
        this.allowedSignatureTypes = allowedSignatureTypes;
    }

    public PinCodeMode getPinCodeMode() {
        return pinCodeMode;
    }

    public void setPinCodeMode(PinCodeMode pinCodeMode) {
        this.pinCodeMode = pinCodeMode;
    }

    public MfaPolicyState getTransactionConfirmation() {
        return transactionConfirmation;
    }

    public void setTransactionConfirmation(MfaPolicyState transactionConfirmation) {
        this.transactionConfirmation = transactionConfirmation;
    }

    public List<TspService> getTspServices() {
        return tspServices;
    }

    public void setTspServices(List<TspService> tspServices) {
        this.tspServices = tspServices;
    }
}
