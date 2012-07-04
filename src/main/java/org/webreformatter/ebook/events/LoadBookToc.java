/**
 * 
 */
package org.webreformatter.ebook.events;

import org.webreformatter.commons.json.JsonObject;
import org.webreformatter.commons.json.rpc.RpcError;
import org.webreformatter.commons.json.rpc.RpcRequest;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBookToc;
import org.webreformatter.ebook.bom.json.JsonBookBase;
import org.webreformatter.ebook.bom.json.JsonBookToc;
import org.webreformatter.ebook.bom.json.JsonBookUtils;

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
