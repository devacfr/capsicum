package org.cfr.capsicum.server;

public interface ISessionState {

    boolean isForceNewSession();

    void setForceNewSession(boolean forceNewSession);
}
