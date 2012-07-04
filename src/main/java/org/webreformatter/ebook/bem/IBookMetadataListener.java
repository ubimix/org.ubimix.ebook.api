package org.webreformatter.ebook.bem;

import org.webreformatter.ebook.BookId;

public interface IBookMetadataListener {

    void beginBookMetadata(
        BookId bookId,
        String bookTitle,
        String bookCreator,
        String bookLanguage);

    void endBookMetadata();

    void onBookMetadata(String name, String value);
}