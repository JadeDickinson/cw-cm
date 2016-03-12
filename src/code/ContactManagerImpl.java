package code;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
//import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.*;
/**
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
*/

/**
 * @author Jade Dickinson jdicki04
 */
public class ContactManagerImpl implements ContactManager {
    private static final String FILENAME = "contacts.txt";
    private int meetingId;
    private int contactId;
    private Calendar currentDate;
    private List<Meeting> allMeetings;
    private Set<Contact> allContacts;

    public ContactManagerImpl() {
        File file = new File(FILENAME);
        currentDate = Calendar.getInstance();
        //Below checks both that the file exists and that it contains at least one character
        if (file.exists() && file.length() > 0) {
            try (ObjectInputStream
                         decoding = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(FILENAME)));) {
                allMeetings = (List<Meeting>) decoding.readObject();
                allContacts = (Set<Contact>) decoding.readObject();
                meetingId = (int) decoding.readObject();
                contactId = (int) decoding.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                System.err.println("Error: " + ex);
            }
        }
        else {
            //The below should happen both when file didn't originally exist and if file.length() = 0
            meetingId = 0;
            contactId = 0;
            allMeetings = new ArrayList<>();
            allContacts = new HashSet<>();
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @see ContactManager
     */
    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        /**
         * There should be an int field for meetingID. This should start at 0 (the value 0 is never used), and be
         * incremented up by 1 at the beginning of the method, before being used.
         * Return a copy of the current value of meetingID.
         */
        if (contacts == null || date == null) {
            throw new NullPointerException("Make sure neither date not set of contacts provided are null");
        }
        else if (date.before(currentDate)) {
            throw new IllegalArgumentException("Date can't be in the past");
        }
        else if (!allContacts.containsAll(contacts)) {
            throw new IllegalArgumentException("All contacts must exist already");
        }
        else {
            meetingId++;
            allMeetings.add(new FutureMeetingImpl(meetingId, date, contacts));
            int currentMeetingId = meetingId;
            return currentMeetingId;
        }
    }

    /**
     * @see ContactManager
     */
    @Override
    public PastMeeting getPastMeeting(int id) {
        PastMeeting thisMeetingOrNull = null;
        if (validID(id)) {
            for (Meeting m : allMeetings) {
                if (m.getId() == id) {
                    if (!m.getDate().before(currentDate)) {
                        //Not IllegalArgumentException as in getFutureMeeting; Sergio stated we should match the spec and throw IllegalStateException
                        throw new IllegalStateException("Meeting date must be in the past");
                    }
                    else {
                        thisMeetingOrNull = (PastMeeting) m;
                    }
                }
            }
        }
        return thisMeetingOrNull;
    }

    /**
     * @see ContactManager
     */
    @Override
    public FutureMeeting getFutureMeeting(int id) {
        FutureMeeting thisMeetingOrNull = null;
        if (validID(id)) {
            for (Meeting m : allMeetings) {
                if (m.getId() == id) {
                    if (!m.getDate().after(currentDate)) {
                        throw new IllegalArgumentException("Meeting has already happened");
                    }
                    else {
                        thisMeetingOrNull = (FutureMeeting) m;
                    }
                }
            }
        }
        return thisMeetingOrNull;
    }

    /**
     * @see ContactManager
     */
    @Override
    public Meeting getMeeting(int id) {
        Meeting thisMeetingOrNull = null;
        if (validID(id)) {
            for (Meeting m : allMeetings) {
                if (m.getId() == id) {
                    thisMeetingOrNull = m;
                }
            }
        }
        return thisMeetingOrNull;
    }
    
    /**
     * @see ContactManager
     */
    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        //Two meetings are equal if and only if their IDs are equal
        if (contact == null) {
            throw new NullPointerException("The contact you provided was null");
        }
        if (!validContact(contact)) {
            throw new IllegalArgumentException("The contact you provided does not exist in this Contact Manager");
        }
        Set<Meeting> nonChronologicalMeetings = new HashSet<>();
        for (Meeting m : allMeetings) {
            if (m instanceof FutureMeeting) {
                for (Contact c : m.getContacts()) {
                    if (c.getId() == contact.getId()) {
                        nonChronologicalMeetings.add(m);
                    }
                }
            }
        }
        List<Meeting> sortedMeetings = new ArrayList<>();
        for (Meeting m : nonChronologicalMeetings) {
            boolean containsDuplicate = false;
            for (Meeting n : sortedMeetings) {
                if (m.getId() == n.getId()) {
                    containsDuplicate = true;
                }
            }
            if(!containsDuplicate) {
                sortedMeetings.add(m);
            }
        }
        Collections.sort(sortedMeetings, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return sortedMeetings;
    }

    /**
     * @see ContactManager
     *
     * Two meetings are equal only if their IDs are equal
     */
    @Override
    public List<Meeting> getMeetingListOn(Calendar date) {
        if (date == null) {
            throw new NullPointerException("Please check that date is not null");
        }
        List<Meeting> unsortedMeetings = new ArrayList<>();
        for (Meeting m : allMeetings) {
            if(m.getDate().equals(date)) {
                unsortedMeetings.add(m);
            }
        }
        List<Meeting> sortedMeetings = new ArrayList<>();
        for (Meeting u : unsortedMeetings) {
            boolean containsAlready = false;
            if (sortedMeetings.contains(u)) {
                containsAlready = true;
            }
            if (!containsAlready) {
                sortedMeetings.add(u);
            }
        }
        Collections.sort(sortedMeetings, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return sortedMeetings;
    }

    /**
     * @see ContactManager
     */
    @Override
    public List<PastMeeting> getPastMeetingListFor(Contact contact) {
        //Two meetings are equal if and only if their IDs are equal.
        if (contact == null) {
            throw new NullPointerException("Please make sure the contact is not null");
        }
        if(!validContact(contact)) {
            throw new IllegalArgumentException("That contact doesn't exist in this Contact Manager");
        }
        Set<PastMeeting> unsortedMeetings = new HashSet<>();
        for (Meeting m : allMeetings) {
            if (m instanceof PastMeeting) {
                for (Contact c : m.getContacts()) {
                    if (c.getId() == contact.getId()) {
                        unsortedMeetings.add((PastMeeting) m);
                    }
                }
            }
        }
        List<PastMeeting> sortedMeetings = new ArrayList<>();
        for (PastMeeting m : unsortedMeetings) {
            boolean containsDuplicate = false;
            for (PastMeeting s : sortedMeetings) {
                if (m.getId() == (s.getId())) {
                    containsDuplicate = true;
                }
            }
            if (!containsDuplicate) {
                sortedMeetings.add(m);
            }
        }
        Collections.sort(sortedMeetings, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return sortedMeetings;
    }

    /**
     * @see ContactManager
     */
    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {
        /**
         * Check the date IS in the past. If it isn't; throw an IllegalArgumentException (forum)
         */
        if (contacts == null || date == null || text == null) {
            throw new NullPointerException("Please make sure nothing entered is null");
        }
        if (date.after(currentDate)) {
            throw new IllegalArgumentException("Date must be in the past");
        }
        if (contacts.isEmpty()) {
            throw new IllegalArgumentException("Set of contacts must not be empty");
        }
        if (!allContacts.containsAll(contacts)) {
            throw new IllegalArgumentException("All contacts have to exist already");
        }
        else {
            meetingId++;
            allMeetings.add(new PastMeetingImpl(meetingId, date, contacts, text));
        }
    }

    /**
     * @see ContactManager
     */
    @Override
    public PastMeeting addMeetingNotes(int id, String text) {
        if (text == null) {
            throw new NullPointerException("Notes to be added can't be null");
        }
        if (!validID(id)) {
            throw new IllegalArgumentException("There is no meeting corresponding to this ID");
        }
        if (getMeeting(id).getDate().after(currentDate)) {
            throw new IllegalStateException("The meeting you specified hasn't yet taken place");
        }
        PastMeeting temp = getPastMeeting(id);
        PastMeeting pastMeetingPlusNotes;
        pastMeetingPlusNotes = new PastMeetingImpl(id, temp.getDate(), temp.getContacts(), temp.getNotes() + text);
        allMeetings.add(pastMeetingPlusNotes);
        allMeetings.remove(temp);
        return pastMeetingPlusNotes;
    }

    /**
     * @see ContactManager
     */
    @Override
    public int addNewContact(String name, String notes) {
        /**
         * There should be an int field for contactID. This should start at 0, and be incremented by 1 at method start,
         * before being used to create a new contact, for which the ID will be the current value of contactID.
         */
        if (name == null || notes == null) {
            throw new NullPointerException("Please make sure neither name or notes are null");
        }
        else if (name.equals("") || notes.equals("")) {
            throw new IllegalArgumentException("Neither name nor notes can be empty");
        }
        contactId++;
        allContacts.add(new ContactImpl(contactId, name, notes));
        int result = contactId;
        return result;
    }

    /**
     * Returns a Set with the contacts whose name contains that string:
     *
     * If the string is the empty string, this methods returns the set that contains all current contacts.
     *
     * "In the case that a name (or any substring) is provided "that is not present in the set of contacts held" by the
     * ContactManager, then it returns "the contacts whose name contains that string", i.e. the empty set."
     *
     * @param name the string to search for
     * @return a list with the contacts whose name contains that string. - a set of contacts: Set<Contact>
     *
     * @throws NullPointerException if the parameter is null
     */
    @Override
    public Set<Contact> getContacts(String name)    {
        //Don't plan to make this lenient with upper/lowercase
        if (name == null) {
            throw new NullPointerException("Please make sure name is not null");
        }
        else if (name.equals("")) {
            return allContacts;
        }
        Set<Contact> resultSet = new HashSet<>();
            for (Contact c : allContacts) {
                if (c.getName().contains(name)) {
                    resultSet.add(c);
                }
            }
        return resultSet;
    }

    /**
     * @see ContactManager
     */
    @Override
    public Set<Contact> getContacts(int... ids) {
        if (ids.length < 1) {
            throw new IllegalArgumentException("You must provide at least one ID");
        }
        int totalValid = 0;
        for (Contact c : allContacts) {
            for (int i : ids) {
                if (c.getId() == i) {
                    totalValid++;
                }
            }
        }
        if (totalValid != ids.length) {
            throw new IllegalArgumentException("All IDs you provide must correspond to a real contact");
        }
        Set<Contact> resultSet = new HashSet<>();
        for (int i : ids) {
            for (Contact c: allContacts) {
                if (c.getId() == i) {
                    resultSet.add(c);
                }
            }
        }
        return resultSet;
    }

    /**
     * This method is for checking if a meeting ID provided exists in the set of all meetings.
     * Used by getPastMeeting, getFutureMeeting, getMeeting and addMeetingNotes
     * @param id
     * @return
     */
    public boolean validID(int id) {
        boolean validID = false;
        for (Meeting m : allMeetings) {
            if (m.getId() == id) {
                validID = true;
            }
        }
        return validID;
    }

    /**
     * This method is for checking if a contact exists in this contact manager. If it returns true for a given contact,
     * the method utilising it can throw the appropriate IllegalArgumentException.
     * Used by getFutureMeetingList and getPastMeetingListFor
     * @param contact
     * @return
     */
    public boolean validContact(Contact contact) {
        boolean validContact = false;
        for (Contact c : allContacts) {
            if (c.getId() == contact.getId()) {
                validContact = true;
            }
        }
        return validContact;
    }

    /**
     * Save all data to disk.
     * This method must be executed when the program is closed and when/if the user requests it.
     */
    //testing:call flush and check stuff written on the outside is the same as written on the inside
    @Override
    public void flush() {

    }
}
