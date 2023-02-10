/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.API;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author aicarrera
 */
@ApplicationPath("api")
public class Configuration extends Application {
	static Logger log = LogManager.getLogger(Configuration.class.getName());
     
	public Configuration() {
		super();                              
		log.info("API has been initialized");
	}
}
