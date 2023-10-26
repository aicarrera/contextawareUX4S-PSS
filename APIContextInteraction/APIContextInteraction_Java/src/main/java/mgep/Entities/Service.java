/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.Entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author aicarrera
 */
public class Service implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String location;
    private ArrayList<Subservice> services;
    
    public Service() {
    }

    public Service(String id, String name, String location, ArrayList<Subservice> services) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Subservice> getServices() {
        return services;
    }

    public void setServices(ArrayList<Subservice> services) {
        this.services = services;
    }
   
}
