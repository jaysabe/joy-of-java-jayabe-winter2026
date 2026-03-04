package edu.pdx.cs.joy.jayabe;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    /**
     * Returns the message used when a required request parameter is absent.
     *
     * @param parameterName Name of the missing parameter
     * @return Formatted error message
     */
    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    /**
     * Returns the success message after adding a phone call for a customer.
     *
     * @param customer Customer name
     * @return Formatted success message
     */
    public static String addedPhoneCallForCustomer(String customer) {
        return String.format("Added phone call for %s", customer);
    }

    /**
     * Returns the success message after deleting all phone bills.
     *
     * @return Deletion success message
     */
    public static String allPhoneBillsDeleted() {
        return "All phone bills have been deleted";
    }

}
