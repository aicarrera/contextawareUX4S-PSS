/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author aicarrera
 */
public class InteractionSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    private long startDate;
    private long endDate;
    private String id;
    private String idSubservice;
    private String idUser;
    private ArrayList<ContextInteraction> contexts;
    private ArrayList<Interaction> interactions;
    private String  idContexttmp;
    private int value;
    
    public String getIdSubservice() {
        return idSubservice;
    }

    public void setIdSubservice(String idSubservice) {
        this.idSubservice = idSubservice;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }



    public InteractionSequence() {
    }

    public InteractionSequence(long startDate, long endDate, String id, String idSubservice, String idUser, ArrayList<ContextInteraction> contexts, int value) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.idSubservice = idSubservice;
        this.idUser = idUser;
        this.contexts = contexts;
        interactions=new ArrayList<>();
        this.value=value;
        
    }

    public InteractionSequence(long startDate, long endDate, String id, String idSubservice,  String idUser) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.idSubservice = idSubservice;
        this.idUser=idUser;
     
        interactions=new ArrayList<>();

    }

    public String getIdContexttmp() {
        return idContexttmp;
    }

    public void setIdContexttmp(String idContexttmp) {
        this.idContexttmp = idContexttmp;
    }


    public InteractionSequence(long startDate, long endDate, String id, String idSubservice,  String idUser, String contextValue) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        this.idSubservice = idSubservice;
        this.idUser=idUser;
        this.idContexttmp=contextValue;
        interactions=new ArrayList<>();

    }

    public ArrayList<ContextInteraction> getContexts() {
        return contexts;
    }

    public void setContexts(ArrayList<ContextInteraction> contexts) {
        this.contexts = contexts;
    }
    
    

    public ArrayList<Interaction> getInteractions() {
        return interactions;
    }

    public void setInteractions(ArrayList<Interaction> interactions) {
        this.interactions = interactions;
    }
    

    public InteractionSequence(long startDate, long endDate, String id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = id;
        interactions=new ArrayList<>();
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InteractionSequence other = (InteractionSequence) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }


}
