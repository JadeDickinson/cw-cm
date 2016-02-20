import java.util.Calendar;
import java.util.Set;

/**
 * The class implementing this interface must be abstract. It must have only one
 * constructor with three parameters: an ID (int), a date, and a set of contacts that
 * must be non-empty (otherwise, an IllegalArgumentException must be thrown).
 * A IllegalArgumentException must also be thrown in the case the ID provided
 * was non-positive or zero. If any of the references/pointers passed as parameters
 * is null, a NullPointerException must be thrown
 */
public class MeetingImpl implements Meeting {
    //Create constructors

    /**
     * Returns the id of the meeting.
     *
     * @return the id of the meeting.
     */
    @Override
    public int getId() {

    }

    /**
     * Return the date of the meeting.
     *
     * @return the date of the meeting.
     */
    @Override
    public Calendar getDate() {

    }

    /**
     * Return the details of people that attended the meeting.
     * <p>
     * The list contains a minimum of one contact (if there were
     * just two people: the user and the contact) and may contain an
     * arbitrary number of them.
     *
     * @return the details of people that attended the meeting.
     */
    @Override
    public Set<Contact> getContacts() {

    }
}
