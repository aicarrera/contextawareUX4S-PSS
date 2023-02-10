/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aicarrera
 */

public class RequestNextItem implements Serializable{
    private static final long serialVersionUID = 1L;
    ArrayList<ContextInteraction> contextList;
    List<String> elementUI;

    public ArrayList<ContextInteraction> getContextList() {
        return contextList;
    }

    public void setContextList(ArrayList<ContextInteraction> contextList) {
        this.contextList = contextList;
    }

    public List<String> getElementUI() {
        return elementUI;
    }

    public void setElementUI(List<String> elementUI) {
        this.elementUI = elementUI;
    }

    public RequestNextItem() {
    }

    public RequestNextItem(ArrayList<ContextInteraction> contextList, List<String> elementUI) {
        this.contextList = contextList;
        this.elementUI = elementUI;
    }
    
    
    
    
}
