Name: Gopal Nagarajan
B-Number: B00675008
B-mail: ngopal1@binghamton.edu

Command to run:
step1: ant compile
step2: ant jar
step3: java -jar build/jar/simulate.jar ApexSimulator input.txt

To clear the classes :
ant clean
There is build.xml available which used to have ant build for this java project

The design document is available.

The instruction format used is "MOVC,R10,#2000" like this.

The format appears: choose an option at a time.
Initialize:I	Simulate<n>:S	Display:D	Exit:E
 -> Always do the Initialize before simulate
 -> By giving "S 6" - to run for six cycles
 -> Suppose any pending instructions remains in provided input. The command line displays:
 -> "Do you want to Simulate for some more cycles?Y/N"
 -> "Y"
 -> Prompt "Please enter the number of cycles you want to simulate more"
 -> By entering the number it will run 
 -> By pressing "D" (Display) it will show last cycle ran output along with register and first 100 memory locations.
 -> By Pressing "E" it will stop the running program


