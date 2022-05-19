import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;



/*
This file is using the pre-Java 11 way to call a REST API as there is WAAAY more documentation online to help troubleshoot
*/

public class HomeworkReminder {
    
    public static String parse(String response, String date){
        /*parse - Creates a string containig a list of assignments due the date entered
        Parameters - response: String, the JSON response from Canvas,
        date: String, the date formatted YYYY-MM-DD
        */
        //a binder to contain all of the different assignments
        JSONArray binder = new JSONArray(response);
        //a string builder which I can append the assignments that are due the specified day
        StringBuilder dueToday = new StringBuilder();
        for (int i = 0; i<binder.length(); i++){
            //tracks if the dates match
            Boolean match = true;
            JSONObject assignment = binder.getJSONObject(i);
            //gets the attributes of the assignment in question
            String name = assignment.getString("name");
            String dueDate = assignment.getString("due_at").split("T")[0];

            //print statement for seeing all assignment information, comment out for normal use
            //System.out.println(name + " " + dueDate);

            //converts our date strings into integer arrays
            //bypasses issues with comparing the strings from the JSON and from Java
            int[] dueDateBreak = dateFormat(dueDate);
            int [] todayBreak = dateFormat(date);
            // checks to be sure that the month, day, and year all match
            for(int k = 0; k<dueDateBreak.length; k++){
                if(dueDateBreak[k] != todayBreak[k]){
                    match = false;
                }
            }
            //if there's a match, add it to the string
            if (match){
                dueToday.append(name + "\n");
            }

        }

        return dueToday.toString();
    }   
    
    public static int[] dateFormat(String date){
        /*dateFormat - returns an integer array for a given date string
        Parameters - date: a date string formatted "YYYY-MM-DD"
        */
        String[] dateBreak = date.split("-");
        int[] convertedDate = new int[3];
        for(int i = 0; i<convertedDate.length; i++){
            convertedDate[i] = Integer.parseInt(dateBreak[i]);
        }
        return convertedDate;
    }
    public String HomeworkReminder(){
         //Connection information
        //Course ID for the course in question
        //This is retrieved from the URL when accessing the course on Canvas
        final String COURSE_ID = "102981";
        
        //API Key for Canvas
        //You can generate these in the settings of your Canvas account
        final String API_KEY = "14523~hWwFUTW8RytYszXhIHhYHAUjHTtvv7eGbDb3VzYXVcLo3ymfI790aVIytLbtuxOh";

        //The request link for the assignments page on the API
        final String API_REQUEST = "https://northeastern.instructure.com/api/v1/courses/"+COURSE_ID+"/assignments";


        try{
        //HTTP Request
        URL url = new URL(API_REQUEST);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Authorization", "Bearer "+API_KEY);
        http.setRequestMethod("GET");
        

        //Retrieves JSON from the request
        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        // Feeds it into a string
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();

       
        // Gets the status of the connection between the program and the API
        //System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        
        //get's current time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
        LocalDateTime now = LocalDateTime.now();  
        

        //test date to test matching date to assignment
        //Useful for testing when there are no assignments due on the day in question
        String testDate = "2022-01-28";
       
        //processes JSON to gather assignments due on the given date
        String assignmentsDue = parse(sb.toString(), testDate);
        System.out.println(assignmentsDue);
        return assignmentsDue;
        // Email(assignmentsDue);

     } catch (MalformedURLException e){
            e.printStackTrace();
     } catch (IOException e){
         e.printStackTrace();
     } 

        return "";
    }
//     public static void main(String[] Args){

//         //Connection information
//         //Course ID for the course in question
//         //This is retrieved from the URL when accessing the course on Canvas
//         final String COURSE_ID = "102981";
        
//         //API Key for Canvas
//         //You can generate these in the settings of your Canvas account
//         final String API_KEY = "14523~hWwFUTW8RytYszXhIHhYHAUjHTtvv7eGbDb3VzYXVcLo3ymfI790aVIytLbtuxOh";

//         //The request link for the assignments page on the API
//         final String API_REQUEST = "https://northeastern.instructure.com/api/v1/courses/"+COURSE_ID+"/assignments";


//         try{
//         //HTTP Request
//         URL url = new URL(API_REQUEST);
//         HttpURLConnection http = (HttpURLConnection)url.openConnection();
//         http.setRequestProperty("Accept", "application/json");
//         http.setRequestProperty("Authorization", "Bearer "+API_KEY);
//         http.setRequestMethod("GET");
        

//         //Retrieves JSON from the request
//         BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
//         // Feeds it into a string
//         StringBuilder sb = new StringBuilder();
//         String line;
//         while ((line = in.readLine()) != null) {
//             sb.append(line);
//         }
//         in.close();

       
//         // Gets the status of the connection between the program and the API
//         //System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        
//         //get's current time
//         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");  
//         LocalDateTime now = LocalDateTime.now();  
        

//         //test date to test matching date to assignment
//         //Useful for testing when there are no assignments due on the day in question
//         String testDate = "2022-01-28";
       
//         //processes JSON to gather assignments due on the given date
//         String assignmentsDue = parse(sb.toString(), testDate);
//         System.out.println(assignmentsDue);

//         // Email(assignmentsDue);

//      } catch (MalformedURLException e){
//             e.printStackTrace();
//      } catch (IOException e){
//          e.printStackTrace();
//      } 
//     }

 }
