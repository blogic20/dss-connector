package ru.blogic.dss.api.dto;

import java.io.Serializable;

/**
 * Данные для прохождения процедуры многофакторной аутентификации
 *
 * @author dgolubev
 */
public class InteractiveChallengeConfirmation implements Serializable {

    private String challengeId;
    private String usedEntropy;
    private String confirmationText;

    public InteractiveChallengeConfirmation() {
    }

    public InteractiveChallengeConfirmation(String challengeId, String usedEntropy, String confirmationText) {
        this.challengeId = challengeId;
        this.usedEntropy = usedEntropy;
        this.confirmationText = confirmationText;
    }


    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getUsedEntropy() {
        return usedEntropy;
    }

    public void setUsedEntropy(String usedEntropy) {
        this.usedEntropy = usedEntropy;
    }

    public String getConfirmationText() {
        return confirmationText;
    }

    public void setConfirmationText(String confirmationText) {
        this.confirmationText = confirmationText;
    }
}
