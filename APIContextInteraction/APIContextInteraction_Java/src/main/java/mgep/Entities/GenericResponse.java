/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.Entities;

import java.io.Serializable;

/**
 *
 * @author aicarrera
 */
public class GenericResponse implements Serializable{
    String valueString;
    int valueInt;
    boolean valueBoolean;

    public GenericResponse(String valueString) {
        this.valueString = valueString;
    }

    public GenericResponse(int valueInt) {
        this.valueInt = valueInt;
    }

    public GenericResponse(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public GenericResponse() {
    }
    
    
    

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public boolean isValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }
    
    
    
}
