package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * @author dgolubev
 */
public class KeyPair implements Serializable {

    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private final Certificate[] certificateChain;

    public KeyPair(X509Certificate certificate, PrivateKey privateKey, Certificate[] certificateChain) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.certificateChain = certificateChain;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public Certificate[] getCertificateChain() {
        return certificateChain;
    }
}
