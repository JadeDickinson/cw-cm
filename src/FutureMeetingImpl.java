import java.util.*;
/**
 * The class implementing this interface must have only one constructor with three
 * parameters: an ID (int), a date, and a set of contacts that must be non-empty
 * (otherwise, an IllegalArgumentException must be thrown). If any of the
 * references/pointers passed as parameters is null, a NullPointerException must be
 * thrown.
 */

/**
 * @author Jade Dickinson jdicki04
 */
public class FutureMeetingImpl extends MeetingImpl implements FutureMeeting {
    public FutureMeetingImpl(int id, Calendar date, Set<Contact> contacts) {
        super(id, date, contacts);
    }
}
