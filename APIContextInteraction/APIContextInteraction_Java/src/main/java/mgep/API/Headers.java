/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mgep.API;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import mgep.DataAcess.Parametrization;
import mgep.DataAcess.RDFDAL;
import mgep.DataAcess.UserDatabase;
import mgep.Entities.InteractionSequence;
import mgep.Entities.User;
import mgep.Entities.ContextInteraction;
import mgep.Entities.Interaction;
import mgep.Entities.RequestNextItem;
import mgep.Entities.Subservice;

import org.apache.logging.log4j.*;




/**
 *
 * @author aicarrera
 */
@Path("/headers")
public class Headers {
    @Context HttpHeaders requestHeaders;
 
   
    public static void setServerGraphdb(String GRAPHDB_SERVER) {
        /* use theUserAgent variable or requestHeader's methods to get more info */
        
        Parametrization.GRAPHDB_SERVER=GRAPHDB_SERVER;
        
    }
          
    static Logger log = LogManager.getLogger(Headers.class.getName());
    
                
    /************************************************************ 
     * AUTHENTICATION
     ************************************************************/  
    
    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@QueryParam("username") String username) {
        System.out.println("getUser");
        Map<String,User> users= UserDatabase.readUsersFromCSV("users_db.csv");    
     	return Response.ok(users.get(username)).build();
    }                 
    
    
    /****
     * QUERY DATA FROM INTERACTIONS AND INTERACTION SEQUENCES
     * @param GRAPHDB_SERVER
     * @return 
     ****/
    
    @GET
    @Path("/getGeneralInteractionsByContextValue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetInteractionSeqByContextValue(@QueryParam("value") String value, @HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
        System.out.println("getGeneralInteractionsByContextValue");
        setServerGraphdb(GRAPHDB_SERVER);
    	return Response.ok(new RDFDAL().getInteractionSeqByContextValue(value)).build();
    }
    
    
    @POST
    @Path("/getSequenceInteractionsByContextValue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetInteractionByContextValue(@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER,ArrayList<ContextInteraction> contextList) {
        System.out.println("getSequenceInteractionsByContextValue");
        setServerGraphdb(GRAPHDB_SERVER);
    	return Response.ok(new RDFDAL().getInteractionsByContextValue(contextList)).build();
    }
    
    /****
     * TMP & SOME TEST 
     ****/
    
    @POST
    @Path("/predictNextStepBasedonContextOntology")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response predictNextStepBasedonContextontology(ArrayList<ContextInteraction> contextList,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER, @QueryParam("isExclusive") boolean isExclusive, @QueryParam("elementUI") String elementUI  ){
         setServerGraphdb(GRAPHDB_SERVER);
         return Response.ok(new RDFDAL().getNextUIInteractionContextBased(elementUI,contextList, isExclusive,150)).build();
    }
    
    
    @GET
    @Path("/getSugar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSugar(@QueryParam("service") String value, @HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
        System.out.println("getSugar");
        setServerGraphdb(GRAPHDB_SERVER);
    	return Response.ok(new RDFDAL().getSugar(value)).build();
    }
    
    
    /************************************************************ 
     * RECOMMENDATION SYSTEMS HEADERS
     ************************************************************/
    
    @POST
    @Path("/getModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetModel(@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER,ArrayList<ContextInteraction> contextList) {
        System.out.println("getModel");
        setServerGraphdb(GRAPHDB_SERVER);
    	return Response.ok(new RDFDAL().addNodesToGraphNgrams(contextList,3)).build();
    }
    @GET
    @Path("/getSubserviceRecommendationCosineDist")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubserviceRecommendationCosineDist(@QueryParam("userid") String userid, @QueryParam("topk") int topk, @HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
        System.out.println("getSubserviceRecommendationCosineDist");
        setServerGraphdb(GRAPHDB_SERVER);      
    	return Response.ok(new RDFDAL().getSubserviceRecommendationCosineDistance(topk,userid)).build();
    }
    
    @POST
    @Path("/predictNextStepBasedonContext")
    @Consumes(MediaType.APPLICATION_JSON)    
    public Response predictNextStepBasedonContext(RequestNextItem request ,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER ){
         setServerGraphdb(GRAPHDB_SERVER);
         return Response.ok(new RDFDAL().getNextUIInteractionContextBased(request.getElementUI(),request.getContextList(),3)).build();
    }
    
    @POST
    @Path("/calculateRatings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response calculateRatings(ArrayList<ContextInteraction> contextList,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER, @QueryParam("isExclusive") boolean isExclusive ){
         setServerGraphdb(GRAPHDB_SERVER);
         return Response.ok(new RDFDAL().calculateRatings(contextList, isExclusive)).build();
    }
    
    
    /************************************************************ 
     * INSERTS AND MIGRATIONS HEADERS
     ************************************************************/
    
    @POST
    @Path("/insertBatchInteractionSequence")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertInteractionSequence(ArrayList<InteractionSequence> is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertInteractionSeq(is)).build();
    }
    
    @POST
    @Path("/insertBatchInteraction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertInteraction(ArrayList<Interaction> is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertInteraction(is)).build();
    }
   
    @POST
    @Path("/insertBatchUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertBatchUser(ArrayList<User> is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertUser(is)).build();
    }
   
    @POST
    @Path("/insertBatchSubservice")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertBatchSubservice(ArrayList<Subservice> is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertSubservice(is)).build();
    }
    
    @POST
    @Path("/insertBatchContext")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertBatchContext(ArrayList<ContextInteraction> is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertContext(is)).build();
    }
   
    @POST
    @Path("/insertContext")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertContext(ContextInteraction is,@HeaderParam("GRAPHDB_SERVER") String GRAPHDB_SERVER) {
        System.out.println("------------>"+ Parametrization.GRAPHDB_SERVER);
         setServerGraphdb(GRAPHDB_SERVER);
        return Response.ok(new RDFDAL().insertContext(is)).build();
    }
    
}
