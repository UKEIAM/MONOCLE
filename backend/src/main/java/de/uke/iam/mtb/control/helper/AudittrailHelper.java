package de.uke.iam.mtb.control.helper;

public class AudittrailHelper {
    public static String getCurrentMethodName() {
        // this method gets the name of the called method
        return StackWalker.
            getInstance().
            walk(stream -> stream.skip(1).findFirst().get()).
            getMethodName();
    }
}
