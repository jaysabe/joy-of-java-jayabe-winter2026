package edu.pdx.cs.joy.jayabe;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static String addedPhoneCallForCustomer(String customer) {
        return String.format("Added phone call for %s", customer);
    }

    public static String allPhoneBillsDeleted() {
        return "All phone bills have been deleted";
    }

}
