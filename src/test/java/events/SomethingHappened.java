package events;


import com.eventjournal.api.Aggregate;
import com.eventjournal.api.Envelope;
import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;

public class SomethingHappened extends Message.Event {

    public String what;
    public String where;
    public String when;
    public String who;

    public SomethingHappened(Header header, String what, String where, String when, String who) {
        super(header);
        this.what = what;
        this.where = where;
        this.when = when;
        this.who = who;
    }

    public String toString() {
        return "SomethingHappened: " + what + " " + where + " " + when + " " + who;
    }

    public boolean equals(Object o) {
        if (o instanceof SomethingHappened) {
            SomethingHappened other = (SomethingHappened) o;
            return what.equals(other.what) && where.equals(other.where) && when.equals(other.when) && who.equals(other.who);
        }
        return false;
    }

    public int hashCode() {
        return what.hashCode() + where.hashCode() + when.hashCode() + who.hashCode();
    }

    public static void main(String[] args) {
        SomethingHappened sh = new SomethingHappened(
                Header.headOfChain(Aggregate.class, "1", SomethingHappened.class),
                "a","b", "c", "d");
        System.out.println(EventJournal.Toolbox.serialize(Envelope.of(sh)));
    }


}
