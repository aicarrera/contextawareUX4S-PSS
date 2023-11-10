/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.DataAcess;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mgep.Entities.ContextInteraction;
import mgep.Entities.GenericResponse;
import mgep.Entities.Interaction;
import mgep.Entities.InteractionSequence;
import mgep.Entities.Service;
import mgep.Entities.Subservice;
import mgep.Entities.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.query.BindingSet;


/**
 *
 * @author aicarrera
 */
public class RDFDAL {
    	static Logger log = LogManager.getLogger(RDFDAL.class.getName());
	static String header= "PREFIX rdf: <" + Parametrization.RDF_IRI + ">\r\n"
			    + "PREFIX : <" + Parametrization.DEVICE_SERVICE_ONT_IRI + ">\r\n"
                            +"PREFIX math: <http://www.ontotext.com/sparql/functions/> \r\n";
        
        
        private Subservice mapSubservice(BindingSet modelObj){
            if(modelObj == null) return null;
             String id = modelObj.getBinding("idSubservice").getValue().stringValue();
             String name = modelObj.getBinding("name").getValue().stringValue();
             return new Subservice(id,name);
        }
        
        
	/**
	 * Get instance of ServiceDTO from a BindingSet carrying Service data from ontology
	 * @param modelObj The service data
	 * @return
	 */
	private InteractionSequence mapInteractionSeqModelObjToDTO(BindingSet modelObj) {
		if(modelObj == null) return null;
		
		long startDate = Long.parseLong(modelObj.getBinding("startDate").getValue().stringValue());
		long endDate = Long.parseLong(modelObj.getBinding("endDate").getValue().stringValue());
		String id = modelObj.getBinding("idSequence").getValue().stringValue(); 
                
                if (modelObj.hasBinding("username")&& modelObj.hasBinding("contextvalue")&& modelObj.hasBinding("serviceid")){
                    String userId = modelObj.getBinding("username").getValue().stringValue(); 
                    String serviceid = modelObj.getBinding("serviceid").getValue().stringValue(); 
                    String contextvalue = modelObj.getBinding("contextvalue").getValue().stringValue(); 
                    
                    return new InteractionSequence(startDate,endDate, id,serviceid,userId, contextvalue);
	
                }
                        
		return new InteractionSequence(startDate,endDate, id);
	}
        
        private Interaction mapInteractionModelObjToDTO(BindingSet modelObj) {
		if(modelObj == null) return null;
		
		String element = modelObj.getBinding("element").getValue().stringValue();
                if (modelObj.hasBinding("order")&& modelObj.hasBinding("idInteraction")&& modelObj.hasBinding("idSequence")){
                    int order = Integer.parseInt(modelObj.getBinding("order").getValue().stringValue());
                    String id = modelObj.getBinding("idInteraction").getValue().stringValue();
                    String idSeq = modelObj.getBinding("idSequence").getValue().stringValue();               					                
                    return new Interaction(id,element,order,idSeq);
                }
                else
                {
                    double times = Double.parseDouble(modelObj.getBinding("times").getValue().stringValue());
                    System.out.println("-->"+times);
                    return new Interaction(element, times);
                }
             
	}
        
        
        

        /***
         * Insert User
         * @param list
         * @return 
         */
        public boolean insertUser(ArrayList<User> list){
            log.info("Enter insertUser");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(User i: list){
                   query+= String.format( header +
                          "INSERT {\n" +
                    "    ?iri rdf:type :User .   \n" +
                    "    ?iri :name \"%s\" .     \n" +
                    "    ?iri :id  %s .    \n" +                    
               
                    "}\n" +
                    "WHERE{\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#user\",STR(%s))) as ?iri)\n" +
                    "}; \n", i.getName(),i.getId(),i.getId());
                  //  log.debug("QUERY:  "+query);
                    
            }
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
       
         /***
         * Insert Interaction Sequence
         * @param list
         * @return 
         */
        public boolean insertInteractionSeq(ArrayList<InteractionSequence> list){
            log.info("Enter insertInteractionSeq Batch");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(InteractionSequence i: list){              
                   String occursSTR="";
                   String bindSTR="";
                   for(ContextInteraction c:i.getContexts()){
                       occursSTR+= String.format("    ?iri :occurs_in ?iri_context%s\n .", c.getId());
                       bindSTR+=String.format ("    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#context\",STR(%s))) as ?iri_context%s)\n",c.getId(),c.getId());
                   }
                   query+= String.format( header +
                    "    INSERT {\n" +
                    "    ?iri rdf:type :InteractionSequence .   \n" +
                    "    ?iri :valueInteraction %s .     \n" +
                    "    ?iri :startDate %s .     \n" +
                    "    ?iri :endDate %s .    \n" +
                    "    ?iri :id %s .    \n" +                    
                    "    ?iri :executes ?iri_subservice .\n" +                                                     
                    "    ?iri :is_made_by ?iri_user .\n" +
                    "    ?iri_user :uses ?iri_subservice .\n"+
                    "    %s" +
                    "}\n" +
                    "WHERE{\n" +
                    "    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#interactionseq\",STR(%s))) as ?iri)\n" +
                    "    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#subservice\",STR(%s))) as ?iri_subservice)\n" +
                    "    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#user\",STR(%s))) as ?iri_user)\n" +
                    "%s" +
                    "}; \n" , i.getValue(), i.getStartDate(),i.getEndDate(),i.getId(),occursSTR,i.getId(),i.getIdSubservice(),i.getIdUser(),bindSTR);
            }
              
            return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
        
        /**
         * TMP 
         * @param service
         * @return 
         */
        public GenericResponse getSugar(String service){
               log.info("Enter getSugar");
               RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
               String query =  String.format(header
                        +"select (round(avg(?sum)) as ?total_avg) where { \r\n"
                        +"select ?id (3+sum(?value) as ?sum) \r\n"
                        +"    where{ \r\n"
                        +"        ?seqi :executes ?s; \r\n"
                        +"           :id ?id. \r\n"
                        +"        ?i :belongs_to ?seqi; \r\n"
                        +"           :element ?element.	    \r\n"
                        +"        ?s :name ?name.    \r\n"
                        +"        BIND( IF(?element= \"btn3sugardec\", -1, 1 ) AS ?value ). \r\n"
                        +"        filter (?name=\"%s\" && ( ?element =\"btn3sugardec\" || ?element=\"btn3sugarinc\")). \r\n"
                        +"    } \r\n"
                        +"    group by ?id ?element \r\n"
                        +"} \r\n" ,service );  
               
                System.err.println(query);
               	List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
                if(bindingSet.isEmpty()) return null;
                 
                return new GenericResponse(Integer.parseInt(bindingSet.get(0).getBinding("total_avg").getValue().stringValue()));                          
         }
        
        /**
         * Get interactions by ContextValue
         * @param contextList
         * @return 
         */
        public List<InteractionSequence> getInteractionsByContextValue(ArrayList<ContextInteraction> contextList) {
        
                log.info("Enter getInteractionSeqByContextValue");	
                String allContextValues= getContextfilterString(contextList,false);
		RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
                System.out.println(Parametrization.GRAPHDB_SERVER);
		//prepare select
		String query =  String.format(header
				+ "  select distinct ?idSequence ?startDate ?endDate ?serviceid ?idInteraction ?order ?element  ?username where { \r\n"
                                + "  ?c a :Context .     ?c :value ?contextvalue .    ?si :occurs_in ?c .    ?si :has_interactions ?i. \r\n"
                                + "  ?i :element ?element.     ?i :order ?order   .     ?si  :startDate ?startDate. \r\n"
				+ "  ?si  :endDate ?endDate.    ?si  :id ?idSequence .    ?i :id ?idInteraction .\r\n"
				+ "  ?si  :executes ?serv .    ?serv :id ?serviceid. ?si :is_made_by ?u. ?u :name ?username.\r\n"                               
                                +"  FILTER ((?element != \"btn3ok\" && ?element != \"btn5ok\") &&(%s))"
                               // +"FILTER (?contextvalue = ?filter)"
                                + "} ORDER BY ASC(?idSequence) ASC(?idInteraction)",allContextValues );               
  
		System.err.println(query);
                
                
		//execute select and map object
		List<InteractionSequence> interactionseqObj = new ArrayList<>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            InteractionSequence i = mapInteractionSeqModelObjToDTO(b);
                            if (!interactionseqObj.contains(i)){
                                interactionseqObj.add(i);
                            }
                            interactionseqObj.get(interactionseqObj.indexOf(i)).getInteractions().add(mapInteractionModelObjToDTO(b));
                    });					
		} 
                catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getInteractionSeqById" );
			System.out.println(e.getMessage());
		}
		
		return interactionseqObj;
        
        }
        
        
        public String getContextfilterString(ArrayList<ContextInteraction> contextList, boolean isExclusive){
            ArrayList<String> arrContextValues= new ArrayList<>();
          
            contextList.forEach(c -> {
                arrContextValues.add("?contextvalue=\""+c.getValue()+"\"");
                });
            String symbol= isExclusive ? "&&" : "||";
            return String.join(symbol, arrContextValues);
        }
                
        
        
        /**
         * Calculate Ratings
         * @param contextList 
         * @param isExclusive 
         * @return  
         */        
        public boolean calculateRatings(ArrayList<ContextInteraction> contextList, boolean isExclusive){
            log.info("Enter calculateRatings");     
            
         
            String allContextValues= getContextfilterString(contextList,isExclusive);
            System.out.println(contextList);
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);                       
            String query= String.format( header +
                    "DELETE WHERE { ?r rdf:type :Rating; \r\n"
                    +"               ?property      ?value }; \r\n"
                    +"INSERT { \r\n"
                    +"    ?iri rdf:type :Rating . \r\n"
                    +"    ?iri :valueRating ?rate1 .  \r\n"
                    +"    ?iri :is_from_a ?u1 .  \r\n"
                    +"    ?iri :is_for_a ?s .  \r\n"
                    +"} \r\n"
                    +"where \r\n"
                    +"{     \r\n"
                    +"select ?u1  (sum(?valueInteraction)/?q as ?rate1) ?s  ?iri where { \r\n"
                    +"	?u1 :makes ?i1 . \r\n"
                    +"    ?i1 :executes ?s. \r\n"
                    +"    ?i1 :valueInteraction ?valueInteraction.\r\n"
                    +"    ?i1 :occurs_in ?c . \r\n"
                    +"    ?c :value  ?contextvalue. \r\n"
                    +"    ?s :id ?id2 . \r\n"
                    +"    ?u1 :id ?id1 . \r\n"
                    +"    ?u1 :name ?n1.  \r\n"
                    +"   BIND(IRI(CONCAT(\"http://ontologies/interactioncontext#rating_\",CONCAT(STR(?id1),'_',STR(?id2))))as ?iri) \r\n"
                    +"   { SELECT ?subu (count(?i) as ?q ) where \r\n"
                    + "  {?subu a :User. \r\n"
                    + "   ?subu :makes ?i.  ?i :occurs_in ?c .  ?c :value ?contextvalue.  ?i :valueInteraction ?valueInteractionInt.\r\n"
                    + "   filter((%s)&& ?valueInteractionInt>0)} GROUP BY ?subu  } \r\n"
                    +"    \r\n"
                    +"    FILTER (?u1 = ?subu && (%s)) \r\n"
                    +"} \r\n"    
                    +"GROUP BY ?u1 ?s ?q  ?iri \r\n"
                    +"ORDER BY ?u1 ?s \r\n"
                    +"}; \r\n", allContextValues,allContextValues);
                    System.out.println("QUERY:  "+query);
                    return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
        }
        
           /**
         * Calculate Ratings
         * @param contextList 
         * @param isExclusive 
         * @param service 
         * @param location 
         * @return  
         */        
        public boolean calculateRatings(ArrayList<ContextInteraction> contextList, boolean isExclusive, String service, String location){
            log.info("Enter calculateRatings");     
            
         
            String allContextValues= getContextfilterString(contextList,isExclusive);
            System.out.println(contextList);
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);                       
            String query= String.format( header +
                    "INSERT { \r\n"
                    +"    ?iri rdf:type :Rating . \r\n"
                    +"    ?iri :valueRating ?rate1 .  \r\n"
                    +"    ?iri :is_from_a ?u1 .  \r\n"
                    +"    ?iri :is_for_a ?s .  \r\n"
                    +"    ?iri :of_service ?service .  \r\n"                            
                    +"} \r\n"
                    +"where \r\n"
                    +"{     \r\n"
                    +"select ?u1 ?service (sum(?valueInteraction)/?q as ?rate1) ?s  ?iri where { \r\n"
                    +"	  ?u1 :makes ?i1 . \r\n"
                    +"    ?i1 :executes ?s. \r\n"
                    +"    ?s :is_subservice_of ?service. \r\n"
                    +"    ?service :location ?location. \r\n"
                    +"    ?service :name \"%s\". \r\n"         
                    +"    ?i1 :valueInteraction ?valueInteraction.\r\n"
                    +"    ?i1 :occurs_in ?c . \r\n"
                    +"    ?c :value  ?contextvalue. \r\n"
                    +"    ?s :id ?id2 . \r\n"
                    +"    ?u1 :id ?id1 . \r\n"
                    +"    ?u1 :name ?n1.  \r\n"
                    +"   BIND(IRI(CONCAT(\"http://ontologies/interactioncontext#rating_\",CONCAT(STR(?id1),'_',STR(?id2))))as ?iri) \r\n"
                    +"   { SELECT ?subu (count(?i) as ?q ) where \r\n"
                    + "  {?subu a :User. \r\n"
                    + "   ?subu :makes ?i.  ?i :occurs_in ?c .  ?c :value ?contextvalue.  ?i :valueInteraction ?valueInteractionInt.\r\n"
                    + "   filter((%s)&& ?valueInteractionInt>0)} GROUP BY ?subu  } \r\n"
                    +"    \r\n"
                    +"    FILTER (?location = \"%s\" &&  ?u1 = ?subu && (%s)) \r\n"
                    +"} \r\n"    
                    +"GROUP BY ?u1 ?service ?s ?q  ?iri \r\n"
                    +"ORDER BY ?u1 ?s \r\n"
                    +"}; \r\n", service,allContextValues,location,allContextValues);
                    System.out.println("QUERY:  "+query);
                    return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
        }
        
        
       /**   
        * @param k
        * @param user
        * @param turnOn
        * @return 
        */               
        public List<Subservice> getSubserviceRecommendationGeneral(int k, String user, boolean turnOn){  
            if (turnOn){
                if (hasInteractions(user)){
                   return  getSubserviceRecommendationCosineDistance( k,  user);
                }
                else{
                    return getSubserviceContextWeighted(k);
                } 
            }
            else{
                return getSubserviceRandom(k);
            }
        }
       
        /**   
        * @param k
        * @param user
        * @param turnOn
        * @return 
        */               
        public List<Subservice> getSubserviceRecommendationGeneral(int k, String user, boolean turnOn, String service, String parameter){  
            if (turnOn){
                if (hasInteractions(user)){
                   return  getSubserviceRecommendationCosineDistance( k,  user, service);
                }
                else{
                    return getSubserviceContextWeighted(k, parameter, service);
                } 
            }
            else{
                return getSubserviceRandom(k);
            }
        }
       
        
        
        
        /**
         * 
         * @param user
         * @return 
         */  
        public boolean hasInteractions(String user){
            log.info("Has interactions?");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);                       
            String query= String.format( header 
                           +"SELECT ?s \r\n"
                           +"WHERE {?r :is_from_a ?u ; \r\n"
                           +":is_for_a ?s. \r\n"
                           +"?u :name \"%s\".} \r\n"
                           +"LIMIT 1 \r\n" , user);
            try{
                List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
                if(bindingSet.isEmpty()) return false;
            }
            catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception hasInteractions" );
			System.out.println(e.getMessage());
	     }
            return true;
        }  
        
        
        /**
         * Get subservice recommendation based on context, weighted average. Only when it´s new user. 
         * @param k number of recommendations
         * @param user user for recommendation
         * @return 
         */
        public List<Subservice> getSubserviceRandom(int k){
            log.info("Enter getSubserviceContextWeighted");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
          
            
            String query =  String.format(header            
                            +"SELECT ?idSubservice ?name where { \r\n"
                            +"?s a :Service.\r\n"
                            +"?s :id ?idSubservice;\r\n"
                            +":name ?name. \r\n"
                            +"}  \r\n");
                System.out.println(query);
                List<Subservice> subserviceObj = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        Collections.shuffle(bindingSet);                        
                        bindingSet.subList(0, k);
                        bindingSet.forEach(b -> {
                            subserviceObj.add(mapSubservice(b));
                         });							
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceContextWeighted" );
			System.out.println(e.getMessage());
		}
		
                
                
            return subserviceObj;
        }
        
        
         /**
         * Get subservice recommendation based on context, weighted average. Only when it´s new user. 
         * @param k number of recommendations
         * @param user user for recommendation
         * @return 
         */
        public List<Subservice> getSubserviceContextWeighted(int k){
            log.info("Enter getSubserviceContextWeighted");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
          
            
            String query =  String.format(header            
                    +"SELECT ?name ?idSubservice (sum(?value1)/ count(?s) as ?score)\r\n"
                    +"WHERE { \r\n"
                    +"     ?s a :Subservice.\r\n"
                    +"     ?r1 :valueRating ?value1 .\r\n"
                    +"     ?r1 :is_for_a ?s .\r\n"
                    +"     ?s :id ?idSubservice;\r\n"
                    +"        :name ?name.}  \r\n"
                    +"GROUP BY  ?name ?idSubservice\r\n"
                    +"ORDER BY desc(?score)\r\n"
                    +"LIMIT %s  \r\n",  k);
                System.out.println(query);
                List<Subservice> subserviceObj = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            subserviceObj.add(mapSubservice(b));
                         });							
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceContextWeighted" );
			System.out.println(e.getMessage());
		}
		
                
                
            return subserviceObj;
        }
       
          /**
         * Get subservice recommendation based on context, weighted average.Only when it´s new user. 
         * @param k number of recommendations
         * @param location
         * @param service
         * @return 
         */
        public List<Subservice> getSubserviceContextWeighted(int k, String location, String service){
            log.info("Enter getSubserviceContextWeighted");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
          
            
            String query =  String.format(header            
                    +"SELECT ?name ?idSubservice (sum(?value1)/ count(?s) as ?score)\r\n"
                    +"WHERE { \r\n"
                    +"     ?s a :Subservice.\r\n"
                    +"     ?r1 :valueRating ?value1 .\r\n"
                    +"     ?r1 :of_service  ?service1.\r\n"         
                    +"     ?service1 :name  \"%s\".\r\n"                           
                    +"     ?r1 :is_for_a ?s .\r\n"
                    +"     ?s :id ?idSubservice;\r\n"
                    +"        :name ?name.}  \r\n"
                    +"GROUP BY  ?name ?idSubservice\r\n"
                    +"ORDER BY desc(?score)\r\n"
                    +"LIMIT %s  \r\n",  service, k);
                System.out.println(query);
                List<Subservice> subserviceObj = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            subserviceObj.add(mapSubservice(b));
                         });							
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceContextWeighted" );
			System.out.println(e.getMessage());
		}
		
                
                
            return subserviceObj;
        }
       
        
        
        /**
         * Get subservice recommendation based on cosine distance
         * @param k number of recommendations
         * @param user user for recommendation
         * @return 
         */
        public List<Subservice> getSubserviceRecommendationCosineDistance(int k, String user){
            log.info("Enter getSubserviceRecommendation");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
          
            
            String query =  String.format(header            
                +"SELECT  ?idSubservice ?name (sum(?valueR*?similarity)/ (sum(?similarity)+0.00000001) as ?score) \r\n"
                +"WHERE{\r\n"
                +"?user :uses ?subservice.\r\n" 
                +"?r :is_from_a ?user; \r\n"
                +"   :is_for_a ?subservice;\r\n"
                +"   :valueRating ?valueR. \r\n"
                +"?subservice :id ?idSubservice;\r\n"
                +"            :name ?name. \r\n"
                +"   {SELECT ?u2 ((?dot / (?sqrtV2 * ?sqrtV1)) as ?similarity) \r\n"  
                +"	WHERE \r\n"
                +"	{{SELECT ?u2 (sum(?value1*?value2) as ?dot)  ?v2pow2  ?v1pow2 \r\n"
                +"	  WHERE { \r\n"
                +"		 ?r1 :valueRating ?value1 .\r\n"
                +"		 ?r1 :is_for_a ?s .\r\n"
                +"		 ?r1 :is_from_a ?u1 .\r\n"
                +"		 ?r2 :valueRating ?value2  .\r\n"
                +"		 ?r2 :is_for_a ?s .\r\n"
                +"		 ?r2 :is_from_a ?u2 .\r\n"
                +"		 ?u1 :name \"%s\" .  \r\n"
                +"		 {SELECT (sum(?value_inner1*?value_inner1) as ?v1pow2)\r\n"  
                +"		  WHERE{ ?ri :valueRating ?value_inner1.     	    \r\n"
                +"				 ?ri :is_from_a ?user_inner .\r\n"
                +"				 ?user_inner :name \"%s\" . }} \r\n"
                +"		 {SELECT ?user_inner2 (SUM(?value_inner2*?value_inner2) as ?v2pow2) \r\n" 
                +"		  WHERE{ ?ri :valueRating ?value_inner2. \r\n"    	    
                +"				 ?ri :is_from_a ?user_inner2 .} \r\n"
                +"		  GROUP BY ?user_inner2 }          \r\n"
                +"		  FILTER(?u2=?user_inner2) }  \r\n"
                +"	   GROUP BY ?u2 ?v2pow2 ?v1pow2}       \r\n"
                +"	   BIND (math:sqrt(?v2pow2) AS ?sqrtV2). \r\n"
                +"	   BIND (math:sqrt(?v1pow2) AS ?sqrtV1).\r\n"
                +"	} \r\n"
                +"    #LIMIT NEIGHBORHOOD  \r\n"  
                +"      ORDER BY desc(?similarity) \r\n"  
                +"	LIMIT 10} \r\n"
                +"	FILTER(?user=?u2) \r\n"
                +"} \r\n"
                +"GROUP BY ?idSubservice ?name \r\n"
                +"ORDER BY desc(?score) \r\n"
                +"#OPTIONS TO PRESENT \r\n"
                +"LIMIT %s  \r\n"
               , user, user, k);
                System.out.println(query);
                List<Subservice> subserviceObj = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            subserviceObj.add(mapSubservice(b));
                         });							
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceRecommendation" );
			System.out.println(e.getMessage());
		}
		
                
                
            return subserviceObj;
        }
        
        
        
        /**
         * Get subservice recommendation based on cosine distance
         * @param k number of recommendations
         * @param user user for recommenda
         * @param service service
         * @return 
         */
        public List<Subservice> getSubserviceRecommendationCosineDistance(int k, String user, String service){
            log.info("Enter getSubserviceRecommendation");
            RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
          
            
            String query =  String.format(header            
                +"SELECT  ?idSubservice ?name (sum(?valueR*?similarity)/ (sum(?similarity)+0.00000001) as ?score) \r\n"
                +"WHERE{\r\n"
                +"?user :uses ?subservice.\r\n" 
                +"?r :is_from_a ?user; \r\n"
                +"   :is_for_a ?subservice;\r\n"
                +"   :valueRating ?valueR; \r\n"
                +"   :of_service ?service. \r\n"
                +"?service :name \"%s\". \r\n"
                +"?subservice :id ?idSubservice;\r\n"
                +"            :name ?name. \r\n"
                +"   {SELECT ?u2 ((?dot / (?sqrtV2 * ?sqrtV1)) as ?similarity) \r\n"  
                +"	WHERE \r\n"
                +"	{{SELECT ?u2 (sum(?value1*?value2) as ?dot)  ?v2pow2  ?v1pow2 \r\n"
                +"	  WHERE { \r\n"
                +"		 ?r1 :valueRating ?value1 .\r\n"
                +"		 ?r1 :of_service ?service1 . \r\n"                       
                +"               ?service1 :name \"%s\". \r\n"
                +"		 ?r1 :is_for_a ?s .\r\n"
                +"		 ?r1 :is_from_a ?u1 .\r\n"
                +"               ?r2 :valueRating ?value2  .\r\n"
                +"               ?r2 :of_service ?service2 .\r\n"
                +"               ?service2 :name \"%s\"  .\r\n"                          
                +"		 ?r2 :is_for_a ?s .\r\n"
                +"		 ?r2 :is_from_a ?u2 .\r\n"
                +"		 ?u1 :name \"%s\" .  \r\n"
                +"		 {SELECT (sum(?value_inner1*?value_inner1) as ?v1pow2)\r\n"  
                +"		  WHERE{ ?ri :valueRating ?value_inner1.     	    \r\n"
                +"				 ?ri :is_from_a ?user_inner .\r\n"
                +"				 ?user_inner :name \"%s\" . }} \r\n"
                +"		 {SELECT ?user_inner2 (SUM(?value_inner2*?value_inner2) as ?v2pow2) \r\n" 
                +"		  WHERE{ ?ri :valueRating ?value_inner2. \r\n"    	    
                +"				 ?ri :is_from_a ?user_inner2 .} \r\n"
                +"		  GROUP BY ?user_inner2 }          \r\n"
                +"		  FILTER(?u2=?user_inner2) }  \r\n"
                +"	   GROUP BY ?u2 ?v2pow2 ?v1pow2}       \r\n"
                +"	   BIND (math:sqrt(?v2pow2) AS ?sqrtV2). \r\n"
                +"	   BIND (math:sqrt(?v1pow2) AS ?sqrtV1).\r\n"
                +"	} \r\n"
                +"    #LIMIT NEIGHBORHOOD  \r\n"  
                +"      ORDER BY desc(?similarity) \r\n"  
                +"	LIMIT 10} \r\n"
                +"	FILTER(?user=?u2) \r\n"
                +"} \r\n"
                +"GROUP BY ?idSubservice ?name \r\n"
                +"ORDER BY desc(?score) \r\n"
                +"#OPTIONS TO PRESENT \r\n"
                +"LIMIT %s  \r\n"
               , service,service, service, user, user, k);
                System.out.println(query);
                List<Subservice> subserviceObj = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            subserviceObj.add(mapSubservice(b));
                         });							
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceRecommendation" );
			System.out.println(e.getMessage());
		}
		
                
                
            return subserviceObj;
        }
        
        
        
        /**
         * Get Interactions sequences by context value
         * @param value
         * @return 
         */
        public List<InteractionSequence> getInteractionSeqByContextValue(String value) {
		log.info("Enter getInteractionSeqByContextValue");	
     
		RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
	
		//prepare select
		String query =  String.format(header
				+ "select ?idSequence ?startDate ?endDate where {\r\n"
                                + "    ?f a :Context .\r\n"
                                + "    ?f :value \"%s\" .\r\n"
				+ "    ?i :occurs_in ?f   .\r\n"
				+ "    ?i :endDate ?endDate  .\r\n"
                                + "    ?i :startDate ?startDate  .\r\n"                             
                                + "    ?i :id ?id  .\r\n"                            					
                                + "} ", value);
                
  
 
		        
		//execute select and map object
		List<InteractionSequence> interactionseqObj = new ArrayList<>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			for (BindingSet b: bindingSet){
                            interactionseqObj.add(mapInteractionSeqModelObjToDTO(b));
                        }
			
			
			
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getInteractionSeqById" );
			System.out.println(e.getMessage());
		}
		
		return interactionseqObj;
	}
	
        
         /***
         * Insert subservices
         * @param list
         * @return 
         */
        public boolean insertSubservice(ArrayList<Subservice> list){
            log.info("Enter GetServiceByAasId");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(Subservice i: list){
                   query+= String.format( header +
                          "INSERT {\n" +
                    "    ?iri rdf:type :Subservice .   \n" +
                    "    ?iri :name \"%s\" .     \n" +
                    "    ?iri :id  %s .    \n" +                    
                    "   ?iri :is_subservice_of ?iri_service"+
                    "}\n" +
                    "WHERE{\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#subservice\",STR(%s))) as ?iri)\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#service\",STR(?id_service))) as ?iri_service)"+              
                    "}; \n", i.getName(),i.getId(),i.getId());
                  //  log.debug("QUERY:  "+query);
                    
            }
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
             /***
         * Insert subservices
         * @param list
         * @param idService
         * @return 
         */
        public boolean insertSubservice(ArrayList<Subservice> list, String idService){
            log.info("Enter GetServiceByAasId");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(Subservice i: list){
                   query+= String.format( header +
                          "INSERT {\n" +
                    "    ?iri rdf:type :Subservice .   \n" +
                    "    ?iri :name \"%s\" .     \n" +
                    "    ?iri :id  %s .    \n" +                    
                    "   ?iri :is_subservice_of ?iri_service"+
                    "}\n" +
                    "WHERE{\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#subservice\",STR(%s))) as ?iri)\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#service\",STR(%s))) as ?iri_service)"+              
                    "}; \n", i.getName(),i.getId(),i.getId(),idService);
             
                    
            }
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
        public boolean insertService(ArrayList<Service> list){
            log.info("Enter insertService");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(Service i: list){
                   query+= String.format( header +
                          "INSERT {\n" +
                    "    ?iri rdf:type :Service .   \n" +
                    "    ?iri :name \"%s\" .     \n" +
                    "    ?iri :id  %s .    \n" +                    
                    "    ?iri :location  \"%s\" ." +       
                    "}\n" +
                    "WHERE{\n" +
                    "   BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#service\",STR(%s))) as ?iri)\n" +                             
                    "}; \n", i.getName(),i.getId(),i.getLocation(),i.getId());
                    System.out.println(query);
                  
                  insertSubservice(i.getServices(), i.getId());
            }
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
        
        
         /***
         * Insert Context
         * @param list
         * @return 
         */
        public boolean insertContext(ArrayList<ContextInteraction> list){
            log.info("Enter GetServiceByAasId");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(ContextInteraction i: list){                  
                   query+= String.format( header +
                          "INSERT DATA {\n" +
                    "    :context%s rdf:type :Time .   \n" +
                    "    :context%s :name \"%s\" .     \n" +
                    "    :context%s :id  %s .    \n" +  
                    "    :context%s :value  \"%s\" .    \n" +                  
                    "};\n",i.getId(),i.getId(),i.getName(),i.getId(),i.getId(),i.getId(),i.getValue());
                System.out.println("QUERY:  "+query);
                           
            }
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
        
         /***
         * Insert Interaction
         * @param list
         * @return 
         */
        public boolean insertInteraction(ArrayList<Interaction> list){
            log.info("Enter insertInteraction");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            String query="";
            for(Interaction i: list){                  
                  query+= String.format( header +
                          "INSERT {\n" +
                    "    ?iri rdf:type :Interaction .   \n" +         
                    "    ?iri :element \"%s\" .    \n" +              
                    "    ?iri :order %s .    \n" +       
                    "    ?iri :id %s .    \n" +                                 
                    "    ?iri :belongs_to ?iri_sequence .\n" +               
                    "}\n" +
                    "WHERE{\n" +
                    "    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#interactionseq\",STR(%s))) as ?iri_sequence)\n" +
                    "    BIND( IRI(CONCAT(\"http://ontologies/interactioncontext#interaction\",STR(%s))) as ?iri)\n" +
                    "}; \n", i.getElement(),i.getOrder(), i.getId(),i.getIdSequence(),i.getId());
                
                  
                  System.out.println("id:  "+i.getId()+"Seqid: "+i.getIdSequence());
                           
            }
             return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }

        
         /***
         * Insert Context
         * @param i
         * @return 
         */
        public boolean insertContext(ContextInteraction i){
            log.info("Enter insertContext");     
	    RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
            
            String query="";
                   query+= String.format( header +
                          "INSERT DATA {\n" +
                    "    :context%s rdf:type :%s .   \n" +
                    "    :context%s :name \"%s\" .     \n" +
                    "    :context%s :id  %s .    \n" +  
                    "    :context%s :value  \"%s\" .    \n" +                  
                    "};\n",i.getId(), i.getType(), i.getId(), i.getName(),i.getId(),i.getId(), i.getId(),i.getValue());
                   System.out.println("QUERY:  "+query);
                 
                              
                   return repManager.executeInsert(Parametrization.REPOSITORY_ID, query);
                   
        }
        /***
         * 
         * @param elementUI
         * @param contextList
         * @param isExclusive
         * @param limitHistory
         * @return 
         */
         public List<Interaction> getNextUIInteractionContextBased(String elementUI, ArrayList<ContextInteraction> contextList, boolean isExclusive, int limitHistory) {
             
                String allContextValues= getContextfilterString(contextList,isExclusive);
             	RDFRepositoryManager repManager = new RDFRepositoryManager(Parametrization.GRAPHDB_SERVER);
	
		//
		String query =  String.format(header			
                        +"select ?element (count(?element) as ?times)\r\n"
                        +"where {\r\n"
                        +"    ?i2 a :Interaction ;\r\n"
                        +"    :element ?element;\r\n"
                        +"    :belongs_to ?is2;\r\n"
                        +"    :order ?order2.\r\n"
                        +"    ?is2 :id ?id1.\r\n"
                        +"    filter(?order2=?order1+1)\r\n"
                        +"    {select ?element1 ?order1 ?id1 where {\r\n"
                        +"        ?i a :Interaction .\r\n"
                        +"        ?i :belongs_to ?is1;\r\n"
                        +"        :element ?element1;\r\n"
                        +"        :order ?order1 .\r\n"
                        +"        ?is1 :id ?id1;\r\n"
                        +"        :occurs_in ?context;\r\n"
                        +"        :startDate ?startdate. \r\n"
                        +"    	  ?context :value ?contextvalue.\r\n"
                        +"        bind(\"%s\" as ?user_element).\r\n"
                        +"        filter(?element1= ?user_element  && (%s))\r\n"
                        +"     }"
                        +"   order by desc(?startdate) \r\n"
                        +"   limit %s }\r\n"               
                        +"}\r\n"
                        +"groupby(?element)\r\n"
                        +"orderby desc(?times)\r\n", elementUI, allContextValues, limitHistory);
                
                System.out.println("QUERY:  "+query);
                
                List<Interaction> interactions = new ArrayList<>();                
                try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Parametrization.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
                        bindingSet.forEach(b -> {
                            interactions.add(mapInteractionModelObjToDTO(b));
                         });	
                        double total = interactions.stream().mapToDouble(i -> i.getTimes()).sum();
                        System.out.println(total);
                        interactions.forEach(i-> { i.setTimes(i.getTimes()/total);
                                                  });
                        
                        interactions.forEach(p -> System.out.println(p.getElement()+ ": " + p.getTimes()));
		} catch (Exception e) {
			log.catching(e);
                        System.err.println("Exception getSubserviceRecommendation" );
			System.out.println(e.getMessage());
		}
		
                
                
            return interactions;
                
         }
         
   
         
     /***         
      * @param elementUI
      * @param contextList
      * @param N
      * @return 
      */
    public Interaction getNextUIInteractionContextBased(List<String> elementUI, ArrayList<ContextInteraction> contextList, int N) {      
        
        HashMap<List<String>, HashMap<List<String>, Double>> model=addNodesToGraphNgrams(contextList,N);
        
        HashMap<List<String>, Double> resultado= model.get(elementUI);
        System.out.println(resultado);
        List<String> highestKey = resultado.entrySet().stream()
        .max(Comparator.comparingDouble(Map.Entry::getValue))
        .map(Map.Entry::getKey)
        .orElse(null);
        if (highestKey!=null)
            return new Interaction(highestKey.get(0));
        else
            return new Interaction("btnNOTFOUND");
        
    }
    
         

    public  HashMap<List<String>, HashMap<List<String>, Double>> addNodesToGraphNgrams(ArrayList<ContextInteraction> contextList, int N) {
        
        
        
        List<InteractionSequence> seqs= this.getInteractionsByContextValue(contextList);
        
        HashMap<List<String>, HashMap<List<String>, Double>> model = new HashMap<>();

        for (InteractionSequence is : seqs) {
            ArrayList<Interaction> s = is.getInteractions();
            for (int Nth = 1; Nth <= N; Nth++) {
                for (int i = 0; i < s.size() - Nth; i++) {
                    List<Interaction> ngramI = s.subList(i, i + Nth);
                    List<Interaction> nextItemI = s.subList(i + Nth, i + Nth + 1);
                    
                    List<String> ngram= new ArrayList<>();
                    ngramI.forEach(e->ngram.add(e.getElement()));                    
                    List<String> nextItem= new ArrayList<>();
                    nextItemI.forEach(e->nextItem.add(e.getElement()));
                    
                    HashMap<List<String>, Double> dicValues = model.getOrDefault(ngram, new HashMap<>());
                    dicValues.putIfAbsent(nextItem, 0.0);
                    dicValues.put(nextItem, dicValues.get(nextItem) + 1);

                    model.put(ngram, dicValues);
                }
            }
        }

        for (List<String> k : model.keySet()) {
            HashMap<List<String>, Double> d = model.get(k);
            double tot = d.values().stream().mapToDouble(Double::doubleValue).sum();
            for (List<String> k2 : d.keySet()) {
                double v = d.get(k2);
                d.put(k2, (v / tot));
            }
        }
        
        return model;
    }

    
    

         
         
         
}
