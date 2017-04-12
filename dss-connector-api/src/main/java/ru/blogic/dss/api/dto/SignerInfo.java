package ru.blogic.dss.api.dto;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author dgolubev
 */
public class SignerInfo implements Serializable, Iterable<SignerInfo> {

    private final String signatureId;
    private final int signatureIndex;
    private final CertificateInfo certInfo;
    private final SignerInfo parent;

    public SignerInfo(String signatureId, int signatureIndex, CertificateInfo certInfo, SignerInfo parent) {
        this.signatureId = signatureId;
        this.signatureIndex = signatureIndex;
        this.certInfo = certInfo;
        this.parent = parent;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public int getSignatureIndex() {
        return signatureIndex;
    }

    public CertificateInfo getCertInfo() {
        return certInfo;
    }

    public SignerInfo getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SignerInfo{" +
                "signatureId='" + signatureId + '\'' +
                '}';
    }

    @Override
    public Iterator<SignerInfo> iterator() {
        return new SignersIterator(this);
    }

    public static class SignersIterator implements Iterator<SignerInfo> {

        private SignerInfo current;

        protected SignersIterator(SignerInfo next) {
            this.current = next;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public SignerInfo next() {
            if (current == null) {
                return null;
            }

            final SignerInfo previous = current;
            current = current.parent;
            return previous;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
