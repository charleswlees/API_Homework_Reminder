Welcome to my Homework Reminder program.

I have removed all personal information such as my authentication tokens and course IDs. To use this yourself you will need ot provide your own credentials. See some of the needed things below:

Main Java File is located in \src\main\java

------------------HomeworkReminder ---------------------------------------------
This function calls the Canvas API to gather assignment information for a particular class.

To use this you will need the following:
1. A Bearer Token for Canvas - This is stored in the API_KEY constant
2. The course ID for the course in question - This is stored in the COURSE_ID constant
3. Java JSON Library

1. Can be generated in the settings of your Canvas account.
2. Can be retrieved from the URL when accessing the course.
3. I am using version 20220320, I have included it in the libs folder in this repository.


-----------------------------------GMail API -----------------------------------

In order to access the GMail API I am using Gradle. 
To send with GMail's API you will need to set up a cloud developer account and set up the OAuth authentication.
To get a full list of needed credentials and setup I recommend Google's GMail API documentation.






