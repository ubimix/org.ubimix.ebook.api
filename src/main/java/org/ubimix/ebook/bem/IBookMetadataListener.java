package org.ubimix.ebook.bem;

import org.ubimix.ebook.BookId;

public interface IBookMetadataListener {

    void beginBookMetadata(
        BookId bookId,
        String bookTitle,
        String bookCreator,
        String bookLanguage);

    void endBookMetadata();

    void onBookMetadata(String name, String value);
}