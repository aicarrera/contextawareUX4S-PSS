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
public class Interaction implements Serializable{
      private static final long serialVersionUID = 1L;
      private String id;
      private String element;
      private int order;
      private String idSequence;
      private double timesProb;
      

    public Interaction(String id, String element, int order, String idSequence) {
        this.id = id;
        this.element = element;
        this.order = order;
        this.idSequence = idSequence;
    }

    public Interaction(String element, double times) {
        this.element = element;
        this.timesProb = times;
    }

    public Interaction() {
    }

    public Interaction(String element) {
        this.element = element;
    }

    
    
    public double getTimes() {
        return timesProb;
    }

    public void setTimes(double times) {
        this.timesProb = times;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getIdSequence() {
        return idSequence;
    }

    public void setIdSequence(String idSequence) {
        this.idSequence = idSequence;
    }
    
      
      
      
      
      
}
