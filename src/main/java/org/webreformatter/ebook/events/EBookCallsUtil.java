/**
 * 
 */
package org.webreformatter.ebook.events;

import java.util.ArrayList;
import java.util.List;

import org.webreformatter.commons.events.IEventListenerRegistration;
import org.webreformatter.commons.events.IEventListenerRegistry;
import org.webreformatter.commons.events.calls.CallListener;
import org.webreformatter.commons.json.rpc.RpcError;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.IBookProvider;
import org.webreformatter.ebook.bom.IBookSection;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.bom.IBookProvider.IBookReader;

/**
 * @author kotelnikov
 */
public class EBookCallsUtil {

    private IBookProvider fProvider;

    /**
     * 
     */
    public EBookCallsUtil(IBookProvider provider) {
        fProvider = provider;
    }

    protected void checkRequest(LoadCall event) {
    }

    public IEventListenerRegistration registerListeners(
        IEventListenerRegistry registry) {

        final List<IEventListenerRegistration> list = new ArrayList<IEventListenerRegistration>();
        list.add(registry.addListener(
            LoadBook.class,
            new CallListener<LoadBook>() {
                @Override
                protected void handleRequest(LoadBook event) {
                    try {
                        checkRequest(event);
                        Uri bookHref = event.getBookReference();
                        IBookReader reader = fProvider.getBookReader(bookHref);
                        IBook book = reader != null ? reader.getBook() : null;
                        if (book == null) {
                            throw new RuntimeException(
                                "Book was not found. Book href: '"
                                    + bookHref
                                    + "'.");
                        }
                        event.reply(book);
                    } catch (Throwable t) {
                        event.setError(RpcError.getError(t));
                    }
                }
            }));
        list.add(registry.addListener(
            LoadBookToc.class,
            new CallListener<LoadBookToc>() {
                @Override
                protected void handleRequest(LoadBookToc event) {
                    try {
                        checkRequest(event);
                        Uri bookHref = event.getBookReference();
                        IBookReader reader = fProvider.getBookReader(bookHref);
                        IBookToc toc = reader != null
                            ? reader.getBookToc()
                            : null;
                        if (toc == null) {
                            throw new RuntimeException(
                                "Table of content was not found. Book href: '"
                                    + bookHref
                                    + "'.");
                        }
                        event.reply(toc);
                    } catch (Throwable t) {
                        event.setError(RpcError.getError(t));
                    }
                }
            }));
        list.add(registry.addListener(
            LoadBookSection.class,
            new CallListener<LoadBookSection>() {
                @Override
                protected void handleRequest(LoadBookSection event) {
                    try {
                        checkRequest(event);
                        Uri bookHref = event.getBookHref();
                        Uri sectionHref = event.getSectionHref();
                        IBookReader reader = fProvider.getBookReader(bookHref);
                        IBookSection section = reader != null ? reader
                            .getBookSection(sectionHref) : null;
                        if (section == null) {
                            throw new RuntimeException(
                                "Section was not found. Book href: '"
                                    + bookHref
                                    + "'. Section ref: '"
                                    + sectionHref
                                    + "'.");
                        }
                        event.reply(section);
                    } catch (Throwable t) {
                        event.setError(RpcError.getError(t));
                    } finally {
                    }
                }
            }));
        return new IEventListenerRegistration() {
            public boolean unregister() {
                boolean result = true;
                for (IEventListenerRegistration r : list) {
                    result &= r.unregister();
                }
                return result;
            }
        };
    }

}
