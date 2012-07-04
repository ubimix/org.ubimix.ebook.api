/**
 * 
 */
package org.webreformatter.ebook.events;

import org.webreformatter.commons.json.JsonObject;
import org.webreformatter.commons.json.rpc.RpcError;
import org.webreformatter.commons.json.rpc.RpcRequest;
import org.webreformatter.commons.uri.Uri;
import org.webreformatter.ebook.bom.IBookSection;
import org.webreformatter.ebook.bom.json.JsonBookBase;
import org.webreformatter.ebook.bom.json.JsonBookSection;
import org.webreformatter.ebook.bom.json.JsonBookUtils;

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
