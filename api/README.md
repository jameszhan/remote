### Generate rmic stub

rmic -keep -classpath /u/workdir/Codes/remote/api/target/classes com.mulberry.remote.rmi.RmiInvocationWrapper -d ./src/main/java -verbose