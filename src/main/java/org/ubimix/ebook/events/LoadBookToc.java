/**
 * 
 */
package org.ubimix.ebook.events;

import org.ubimix.commons.json.JsonObject;
import org.ubimix.commons.json.rpc.RpcError;
import org.ubimix.commons.json.rpc.RpcRequest;
import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.bom.IBookToc;
import org.ubimix.ebook.bom.json.JsonBookBase;
import org.ubimix.ebook.bom.json.JsonBookToc;
import org.ubimix.ebook.bom.json.JsonBookUtils;

/**
 * @author kotelnikov
 */
public class LoadBookToc extends LoadCall {

    public LoadBookToc(RpcRequest request) {
        super(request);
    }

    /**
     * @param ref
     */
    public LoadBookToc(Uri ref) {
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

    public JsonBookToc getResultBook() {
        return getResultObject(JsonBookToc.FACTORY);
    }

    @Override
    public RpcError getResultError() {
        return super.getResultError();
    }

    public void reply(IBookToc result) {
        JsonBookToc b;
        if (result instanceof JsonBookToc) {
            b = (JsonBookToc) result;
        } else {
            b = JsonBookUtils.copyBookToc(result);
        }
        super.reply(b);
    }
}
