/**
 * 
 */
package org.ubimix.ebook;

/**
 * @author kotelnikov
 */
public class BookId implements Comparable<BookId> {

    private final String fId;

    /**
     * 
     */
    public BookId(String ref) {
        if (ref == null) {
            ref = "";
        }
        ref = ref.trim();
        ref = ref.replace('\\', '/');
        fId = ref;
    }

    public int compareTo(BookId o) {
        return fId.compareTo(o.fId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BookId)) {
            return false;
        }
        BookId r = (BookId) obj;
        return compareTo(r) == 0;
    }

    public String getId() {
        return fId;
    }

    @Override
    public int hashCode() {
        return fId.hashCode();
    }

    @Override
    public String toString() {
        return fId;
    }

}
