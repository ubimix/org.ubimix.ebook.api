/**
 * 
 */
package org.ubimix.ebook.events;

import org.ubimix.commons.json.JsonObject;
import org.ubimix.commons.json.rpc.RpcError;
import org.ubimix.commons.json.rpc.RpcRequest;
import org.ubimix.commons.uri.Uri;
import org.ubimix.ebook.bom.IBookSection;
import org.ubimix.ebook.bom.json.JsonBookBase;
import org.ubimix.ebook.bom.json.JsonBookSection;
import org.ubimix.ebook.bom.json.JsonBookUtils;

/**
 * @author kotelnikov
 */
public class LoadBookSection extends LoadCall {

    public LoadBookSection(RpcRequest request) {
        super(request);
    }

    /**
     * @param sectionHref
     */
    public LoadBookSection(Uri bookHref, Uri sectionHref) {
        super(new RpcRequest());
        JsonObject params = new JsonObject();
        params.setValue("bookHref", bookHref);
        params.setValue("sectionHref", sectionHref);
        getRequest().setParams(params);
    }

    public Uri getBookHref() {
        return getParamsAsObject().getValue(
            "bookHref",
            JsonBookBase.URI_FACTORY);
    }

    @Override
    public RpcError getResultError() {
        return super.getResultError();
    }

    public JsonBookSection getResultSection() {
        return getResultObject(JsonBookSection.FACTORY);
    }

    public Uri getSectionHref() {
        return getParamsAsObject().getValue(
            "sectionHref",
            JsonBookBase.URI_FACTORY);
    }

    public void reply(IBookSection section) {
        JsonBookSection s;
        if (section instanceof JsonBookSection) {
            s = (JsonBookSection) section;
        } else {
            s = JsonBookUtils.copyBookSection(section);
        }
        super.reply(s);
    }
}
