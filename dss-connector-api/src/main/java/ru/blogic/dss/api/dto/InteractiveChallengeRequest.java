package ru.blogic.dss.api.dto;

import java.io.Serializable;

/**
 * Запрос информации в рамках процедуры многофакторной аутенификации
 *
 * @author dgolubev
 */
public class InteractiveChallengeRequest implements Serializable {

    private final String challengeId;
    private final String usedEntropy;
    private final String title;
    private final String label;

    public InteractiveChallengeRequest(String challengeId, String usedEntropy, String title, String label) {
        this.challengeId = challengeId;
        this.usedEntropy = usedEntropy;
        this.title = title;
        this.label = label;
    }

    /**
     * @return идентификатор процедуры Interactive Challenge
     */
    public String getChallengeId() {
        return challengeId;
    }

    /**
     * @return клиентская энтропия, использованная при инициализации процесса MFA
     */
    public String getUsedEntropy() {
        return usedEntropy;
    }

    /**
     * @return заголовок запроса Interactive Challenge
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return описание требуемого от клиента действия
     */
    public String getLabel() {
        return label;
    }
}
