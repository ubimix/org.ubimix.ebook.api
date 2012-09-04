/**
 * 
 */
package org.ubimix.ebook.events;

import org.ubimix.commons.json.rpc.RpcRequest;
import org.ubimix.commons.rpc.RpcCall;

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
