import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import org.apache.commons.codec.binary.Base64;
//import org.apache.http.impl.io.DefaultHttpResponseWriterFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.Label;
//import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
//import javax.activation.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.lang.Math;




/* Charlie Lees
Homework Reminder - Pulls Homework information from the Canvas API and sends it out using the GMail API  */
public class HomeworkReminder {


    public static String dailyParse(String response, String date){
        /*dailyParse - Creates a string containig a list of assignments due the date entered
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
                dueToday.append(name + " \n");
            }

        }

        return dueToday.toString();
    }   

    public static String weeklyParse(String response, String[] dateRange){
        /*weeklyParse - Returns a string containing all the assignments due the same week as the day in question.
        Parameters - response: String, the JSON response from Canvas
        dateRange: String array, contains YYYY-MM-DD dates for the entire week. Generated using weekRange()
        */
        //a binder to contain all of the different assignments
        JSONArray binder = new JSONArray(response);
        //a string builder which I can append the assignments that are due the specified day
        StringBuilder dueWeek = new StringBuilder();
        for (int i = 0; i<binder.length(); i++){
            for(int k = 0; k<dateRange.length; k++){
                String date = dateRange[k];
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
                for(int j = 0; j<dueDateBreak.length; j++){
                    if(dueDateBreak[j] != todayBreak[j]){
                        match = false;
                    }
                }
                //if there's a match, add it to the string
                if (match){
                    dueWeek.append(name + " || Due Date: " + dueDate +"\n");
                }
         }   
    }
    return dueWeek.toString();
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

    public static int monthDays(String month){
        //returns the number of days in a given month
        // month entered as it's two digit representation
        //changes if it is a leapyear or not.
        Boolean leapYear = false;
        switch(month){
            case "01":
                return 31;
            case "02":
                if(leapYear){return 29;}
                else{return 28;}
            case "03":
                return 31;
            case "04":
                return 30;
            case "05":
                return 31;
            case "06":
                return 30;
            case "07":
                return 31;
            case "08":
                return 31;
            case "09":
                return 30;
            case "10":
                return 31;
            case "11":
                return 30;
            case "12":
                return 31;


        }


    return 0;
    }

    public static String tomorrow(String date){
        //will return the YYYY-MM-DD for the next day
        //Parameters - date: String, YYYY-MM-DD of the date in question

        String month = date.split("-")[1];
        // Parsing date information out as ints for iteration
        int currentMonth = Integer.parseInt(month);
        int currentYear = Integer.parseInt(date.split("-")[0]);
        int currentDay = Integer.parseInt(date.split("-")[2]);
        int monthLen = monthDays(month);
        

        if(currentDay<monthLen){
            currentDay++;
        }
        else if(currentDay == monthLen){
            currentDay = 1;
            if(currentMonth < 12){
                currentMonth++;
            }
            else if(currentMonth == 12){
                currentMonth = 1;
                currentYear ++;
            }
        }

        String strYear = String.valueOf(currentYear);
        String strMonth = String.format("%02d", currentMonth);
        String strDay = String.format("%02d", currentDay);

        return strYear + "-" + strMonth + "-" + strDay;
    }

    public static String yesterday(String date){
        //returns the YYYY-MM-DD date for the day before the given date
        //Parameters - date: String, YYYY-MM-DD date 

        // Parsing date information out as ints for iteration
        int currentMonth = Integer.parseInt(date.split("-")[1]);
        int currentYear = Integer.parseInt(date.split("-")[0]);
        int currentDay = Integer.parseInt(date.split("-")[2]);
        int prevMonth = 0;
        if (currentMonth > 1){
            prevMonth = currentMonth-1;
        }
        else if(currentMonth == 1){
            prevMonth = 12;
        }
        int prevMonthLen = monthDays(String.format("%02d", prevMonth));
        
        if(currentDay > 1){
            currentDay--;
        }
        else if(currentDay == 1){
            currentDay = prevMonthLen;
            if(currentMonth>1){
                currentMonth--;
            }
            else if(currentMonth ==1){
                currentMonth = 12;
                currentYear--;
            }
        }
        String strYear = String.valueOf(currentYear);
        String strMonth = String.format("%02d", currentMonth);
        String strDay = String.format("%02d", currentDay);

        return strYear + "-" + strMonth + "-" + strDay;
    }

    public static int[] daysLeft(DayOfWeek day){
        /*
        Returns an array with the following information at each index:
        [0] The number of days to go back until hitting Sunday
        [2] The number of days to go before hitting Saturday
        */

        
        int[] dayCount = new int[2];
        // Switch statement to calculate that for all 7 days
        

        switch(day){
            case SUNDAY:
            dayCount[0] = 0;
            dayCount[1] = 6;
            return dayCount;
            case MONDAY:
            dayCount[0] = 1;
            dayCount[1] = 5;
            return dayCount;
            case TUESDAY:
            dayCount[0] = 2;
            dayCount[1] = 4;
            return dayCount;
            case WEDNESDAY:
            dayCount[0] = 3;
            dayCount[1] = 3;
            return dayCount;
            case THURSDAY:
            dayCount[0] = 4;
            dayCount[1] = 2;
            return dayCount;
            case FRIDAY:
            dayCount[0] = 5;
            dayCount[1] = 1;
            return dayCount;
            case SATURDAY:
            dayCount[0] = 6;
            dayCount[1] = 0;

        }
        return dayCount;
    }

    public static String[] weekRange(String date, DayOfWeek day){
    /* weekRange - Returns a range of dates for the week, each day is an entry in an array
    Parameters - date: String, the current date in a YYYYMMDD format.
    day: DayOfWeek, the current Day of the week.
    */

    // array to house the days of the week
       String[] weekdays = new String [7];
       int[] dayCount = daysLeft(day);
       String newDate = "";
       for(int i = 0; i<weekdays.length; i++){

        if(i<dayCount[0]){
        // Calculates days back

        //  difference between day currently being looked at in the loop and the day given
        int currentDayDiff = Math.abs(i-dayCount[0]);
        //    Stores the new date for the current day being looked at in the loop
           newDate = date;
           for(int k = 0; k<currentDayDiff; k++){
            //    goes back the number of days needed
            newDate = yesterday(newDate);
            
           }
       }
       else if(i == dayCount[0]){
            newDate = date;
            
       }
       else if(i>dayCount[0]){
           int currentDayDiff = Math.abs(i-dayCount[0]);

           
           newDate = date;
           for(int k = 0; k<currentDayDiff; k++){
            //    goes back the number of days needed
            newDate = tomorrow(newDate);
        
           }
       }
       weekdays[i] = newDate;
    
    }
    
        return weekdays;
    }

    public static String HomeworkToDo(){
        //Connection information
       //Course ID for the course in question
       //This is retrieved from the URL when accessing the course on Canvas
       final String COURSE_ID = "";
       
       //API Key for Canvas
       //You can generate these in the settings of your Canvas account
       final String API_KEY = "";

       //The request link for the assignments page on the API
       final String API_REQUEST = "";


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
       String todayDate = now.toString().split("T")[0];
       
      

       // Gets the current day of the week
       LocalDate ld = LocalDate.now();
       DayOfWeek dayOfWeek = ld.getDayOfWeek();

    //    Gets the dates of the current week
       String [] dateRange = weekRange(todayDate, dayOfWeek);
      
       
       

       //test date to test matching date to assignment
       //Useful for testing when there are no assignments due on the day in question
       String testDate = "2022-05-16";
       
       DayOfWeek testDayOfWeek = DayOfWeek.MONDAY;
       String [] testDateRange = weekRange(testDate, testDayOfWeek);

       
       
    
       //processes JSON to gather assignments due on the given date
       String dailyAssignmentsDue = dailyParse(sb.toString(), todayDate);
    
       
      
   

    // Processes JSON to gather assignments due on the given week
       String weeklyAssignmentsDue = weeklyParse(sb.toString(), dateRange);
    // Contains the information for the work due the day of the email
       String dailyOutput = "Assignments Due Today: " + todayDate +"\n" + dailyAssignmentsDue;
    
    
    // Contains the information for the work due the week of the email
        String weeklyOutput = "Assignments Due This Week: \n" + weeklyAssignmentsDue;
        String totalOutput = dailyOutput + "\n\n" + weeklyOutput;
       return totalOutput;
       // Email(assignmentsDue);

    } catch (MalformedURLException e){
           e.printStackTrace();
    } catch (IOException e){
        e.printStackTrace();
    } 

       return "";
   }



    //GMail API Gradle information
    /** Application name. */
    private static final String APPLICATION_NAME = "HomeworkReminder";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = HomeworkReminder.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

        /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        
        Session session = Session.getDefaultInstance(props, null);
        

        MimeMessage email = new MimeMessage(session);
        

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }


        /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

        /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }


    public static void main(String... args) throws IOException, GeneralSecurityException, MessagingException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

  
        // Send the email
        
        MimeMessage message = createEmail("to@gmail.com", "from@gmail.com", "Daily Homework Status", HomeworkToDo());
        sendMessage(service, "me", message);
             

        System.out.println("Email Sent Successfully");
        
    }
}