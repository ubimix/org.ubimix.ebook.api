/**
 * 
 */
package org.webreformatter.ebook.events;

import org.webreformatter.commons.json.rpc.RpcRequest;
import org.webreformatter.commons.rpc.RpcCall;

/**
 * @author kotelnikov
 */
public class LoadCall extends RpcCall {

    /**
     * @param request
     */
    public LoadCall(RpcRequest request) {
        super(request);
    }

}
