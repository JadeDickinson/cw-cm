package code;

import java.util.*;
import java.io.Serializable;
/**
 * The class implementing this interface must have only one constructor with four
 * parameters: an ID (int), a date, a set of contacts that must be non-empty
 * (otherwise, an IllegalArgumentException must be thrown), and a String containing
 * the notes for the meeting. If any of the references / pointers passed as
 * parameters is null, a NullPointerException must be thrown.
 *
 * A meeting that was held in the past.
 * It includes your notes about what happened and what was agreed.
 */

/**
 * @author Jade Dickinson jdicki04
 */
public class PastMeetingImpl extends MeetingImpl implements PastMeeting, Serializable {

    private String text;

    public PastMeetingImpl(int id, Calendar date, Set<Contact> contacts, String text) {
        super(id, date, contacts);
        if (text == null) {
            throw new NullPointerException("Please make sure to enter notes that are not null");
        } else {
            this.text = text;
        }
    }

    /**
     * @return the notes from the meeting. If there are no notes, the empty string is returned.
     */
    @Override
    public String getNotes() {
        return text;
    }

}
