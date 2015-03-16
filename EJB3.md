The EJB 3 implementation uses Servlets and JPA.
The ear file is available for [download](http://cloudspeed.googlecode.com/files/smallface.ear) and you can find the source code [here](http://code.google.com/p/cloudspeed/source/browse/#svn/trunk/smallface/ejb3).

## How to set up the JBoss cluster on EC2 ##

Here we will describe a simple configuration with 1 apache instance acting as a load balancer, multiple JBoss instances and one MySQL instance

### Start the database ###

  1. create the jbossdb database
  1. modify the binding-address in /etc/mysql/my.cnf
  1. start MySql server
  1. grant permissions to root to connect from EC2


### Start the Jboss intances ###
Start a number of ami-4ea34727 images

For each instance:

  1. modify the JGroups configuration PING element to include initial\_hosts
  1. modify the AJP binding address
  1. put the mysql connector in lib
  1. put mysqlds.xml in deploy (you have to specify the mysql server address here)
  1. deploy smallface.ear
  1. start jboss in the all configuration


### Start the Apache load balancer ###

  1. start one ami-4ea34727 image and log into it
  1. modify workers.properties to include a worker for each jboss instance