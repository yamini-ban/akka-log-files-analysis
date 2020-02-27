# akka-log-files-analysis

Q1. There is a company called XYZ that processes various log files from the operating system and provides analysis and recommendation on the basis of the data collected from the files. 


	A. Write a program that can take directory path is input and read all the files and provide the following info:


		1. Number of total errors, warning info

		2. Average errors per file


	B. Write a software program using the Akka actor model that can read from the directory with embarrassingly parallel and asynchronous fashion and provide analysis ASAP. (N number of Akka actors are allowed)


	C. Analyze the performance using visual VM and provide performance and memory comparison between the above approaches. 


Q2. Update the existing Akka program to do the following tasks:


    1. Use different dispatchers (IO dispatchers with fixed thread pool) while reading from files. 

    2. Limit the number of actors that should be configurable by using a router.

    2. Use a mailbox having a capacity of 1000 files. More files should be discarded with an error log.

Ques3. Update outcome of the second assignment according to the following requirements:

    1. Apply supervision strategy for all the actors. If an actor fails it should stop processing the message.

    2. Implement a scheduler that will read from the file every 5 minutes and display the results. 

