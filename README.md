# Lab Assessment Grader
This is a command-line program

## Preparing Google Classroom Files
1. Open the Lab Assessment's Google Drive folder
2. Download and extract this folder to your desktop
3. Make sure that the extracted folder contains all the student .zip files, and does not contain another directory within it
4. The grader will attempt to run all .zip files and directories, and will ignore anything else
5. Be aware of any duplicate work a student has submitted

## Steps to run
1. Download Grader.jar from this repository (the other files are the source)
2. Open Command Prompt (or terminal on Mac)
3. Navigate to the directory of Grader.jar
4. Run Grader.jar with the following command: `java -jar Grader.jar`
5. The following options can be provided as command-line-arguments, or later during runtime (note that all paths can be entered as relative to the jar location, or as absolute paths): `java -jar Grader.jar "Path to user files" "Path to harness files" "Name of main class (no file extension)"`
6. The grader will combine the student's code with the harness code, compile all of it, and run + dislay the output and/or GUI
7. After running a user's code, you must enter a key in command prompt to advance to the next student