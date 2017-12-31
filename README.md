"Rise of the fake bots and fall of the Page-rank algorithm"

 The master and slave infrastructure that we created for the earlier project is used to implement this project.  New commands called rise-fake-url and down-fake-url will be issued by the master to slave bots. on issuing the rise-fake-url from the master bot, all the slave bots attached to the master bot will behave like webservers. All the webservers will provide a HTML page with a specific information and a link to go to the other pages. On clicking the link provided, a new HTML page will be displayed in the browser. On issuing the down-fake-url all the slave bots will stop behaving like a web servers.
 
 Approach- This project is completely coded in JAVA. No HTML page (static web page) or the servlet library are used to implement this project. All the commands such as connect, disconnect, keepalive, list and url= will also be supported along with the new commands provided for this project.
 
 Techonology Used : Java (1.8), Unix, HTML 
 Tools - Eclipse
 
