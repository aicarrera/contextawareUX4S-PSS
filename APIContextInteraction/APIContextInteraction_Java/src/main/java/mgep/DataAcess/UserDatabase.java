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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mgep.Entities.User;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class UserDatabase {

   public static boolean recordNewUserCSV(User u, String fileName) throws InterruptedException {
    URL res = UserDatabase.class.getClassLoader().getResource(fileName);
    try ( 
         BufferedWriter writer = Files.newBufferedWriter(Paths.get(res.toURI()), StandardOpenOption.APPEND) ;
         CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
        Thread.sleep(500);
        csvPrinter.printRecord(u.getId(), u.getRole(), u.getName());
        csvPrinter.flush();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }  catch (URISyntaxException ex) {
           Logger.getLogger(UserDatabase.class.getName()).log(Level.SEVERE, null, ex);
       }
    
    return true;
   }  
   
    
    
    public static Map<String,User> readUsersFromCSV(String fileName) {
        Map<String,User> users = new HashMap<>();
       InputStream s= UserDatabase.class.getClassLoader().getResourceAsStream(fileName);
       
        try (InputStreamReader fileReader = new InputStreamReader( s );

             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                String username = record.get("username");
                String role = record.get("role");
                String userid = record.get("userid");
                User user = new User(userid, username, role);
                users.put(username,user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}