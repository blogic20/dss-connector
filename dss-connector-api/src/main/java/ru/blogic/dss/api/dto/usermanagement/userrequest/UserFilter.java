package ru.blogic.dss.api.dto.usermanagement.userrequest;

import java.io.Serializable;

/**
 * Фильтр, по которому осуществляется выбор пользователя
 * Created by pkupershteyn on 07.04.2016.
 */
public class UserFilter implements Serializable{
    private ColumnTypeEnum column;
    private FilterOperationEnum operation;
    private Serializable value;

    public ColumnTypeEnum getColumn() {
        return column;
    }

    /**
     * @param column Тип колонки, к которой применяется фильтр
     */
    public void setColumn(ColumnTypeEnum column) {
        this.column = column;
    }

    public FilterOperationEnum getOperation() {
        return operation;
    }

    /**
     * @param operation Операция, используемая в фильтре
     */
    public void setOperation(FilterOperationEnum operation) {
        this.operation = operation;
    }

    public Serializable getValue() {
        return value;
    }

    /**
     * @param value Значение фильтра
     */
    public void setValue(Serializable value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return "column: "+column+", operation: "+operation+", value: "+value;
    }
}
