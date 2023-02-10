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
public class ContextInteraction implements Serializable {
     private static final long serialVersionUID = 1L;
     private String type;
     private int id;
     private String name;
     private String value;

    public ContextInteraction() {
    }

    public ContextInteraction(String type, int id, String name, String value) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.value = value;
    }

    
    public ContextInteraction(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ContextInteraction(int id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public ContextInteraction(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
