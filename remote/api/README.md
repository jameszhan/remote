### Generate rmic stub

rmic -keep -classpath /u/workdir/Codes/athena/remote/api/target/classes com.mulberry.athena.remote.rmi.RmiInvocationWrapper -d ./src/main/java -verbose