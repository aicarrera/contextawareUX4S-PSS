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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import mgep.Entities.User;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class UserDatabase {

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