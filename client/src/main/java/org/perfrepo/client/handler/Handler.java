package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;

public abstract class Handler {

    protected final Connection connection;

    public Handler(Connection connection) {
        this.connection = connection;
    }
}
