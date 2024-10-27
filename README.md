Welcome to my Homework Reminder program.

I have removed all personal information such as my authentication tokens and course IDs. To use this yourself you will need ot provide your own credentials. See some of the needed things below:

Main Java File is located in \src\main\java

## Homework Reminder
This function calls the Canvas API to gather assignment information for a particular class.

To use this you will need the following:
1. A Bearer Token for Canvas - This is stored in the API_KEY constant
2. The course ID for the course in question - This is stored in the COURSE_ID constant
3. Java JSON Library

1. Can be generated in the settings of your Canvas account.
2. Can be retrieved from the URL when accessing the course.
3. I am using version 20220320, I have included it in the libs folder in this repository.


## GMail API

To use the GMail API, Gradle will be managing most of it. All that we will need is:
1. A Google Cloud Platform Developer Account
2. Enabling and setting up OAuth credentials with the GMail API
  a. Google has their own documentation for this process which is very useful.
3. Place the credentials.json file in the src/main/resources folder of the project

If you are unfamiliar with gradle,
Once everything is in place you can navigate to the central folder for this project and use the command 'gradle run' to run the program.


