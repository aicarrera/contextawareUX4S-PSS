/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.DataAcess;

/**
 *
 * @author aicarrera
 */
public final class Parametrization {
	public static  String GRAPHDB_SERVER = "http://localhost:7200/";
	public static  final String REPOSITORY_ID = "interaction_context";
	public static final String DEVICE_SERVICE_ONT_IRI = "http://ontologies/interactioncontext#";
	public static final String RDF_IRI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
        
        
        
        
	/**
	 * Get a random number given min and max values
	 * @param min
	 * @param max
	 * @return a random int
	 */
	public static int GetRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}
}

