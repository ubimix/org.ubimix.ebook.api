/**
 * 
 */
package org.webreformatter.ebook.events;

import org.webreformatter.commons.json.JsonObject;
import org.webreformatter.commons.json.rpc.RpcError;
import org.webreformatter.commons.json.rpc.RpcRequest;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBook;
import org.webreformatter.ebook.bom.json.JsonBook;
import org.webreformatter.ebook.bom.json.JsonBookBase;
import org.webreformatter.ebook.bom.json.JsonBookUtils;

/**
 * @author kotelnikov
 */
public class LoadBook extends LoadCall {

    public LoadBook(RpcRequest request) {
        super(request);
    }

    /**
     * @param ref
     */
    public LoadBook(Uri ref) {
        super(new RpcRequest());
        JsonObject params = new JsonObject();
        params.setValue("ref", ref);
        getRequest().setParams(params);
    }

    public Uri getBookReference() {
        return getParamsAsObject().getValue(
            "ref",
            JsonBookBase.URI_FACTORY);
    }

    public JsonBook getResultBook() {
        return getResultObject(JsonBook.FACTORY);
    }

    @Override
    public RpcError getResultError() {
        return super.getResultError();
    }

    public void reply(IBook result) {
        JsonBook b;
        if (result instanceof JsonBook) {
            b = (JsonBook) result;
        } else {
            b = JsonBookUtils.copyBook(result);
        }
        super.reply(b);
    }
}
