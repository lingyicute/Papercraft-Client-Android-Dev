package org.papercraft.tgnet;

public interface RequestDelegateInternal {
    void run(long response, int errorCode, String errorText, int networkType, long timestamp, long requestMsgId);
}
