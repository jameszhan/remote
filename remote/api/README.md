### Generate rmic stub

rmic -keep -classpath /u/workdir/Codes/remote/api/target/classes com.mulberry.athena.remote.rmi.RmiInvocationWrapper -d ./src/main/java -verbose