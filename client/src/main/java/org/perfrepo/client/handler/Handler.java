package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;

public abstract class Handler {

    protected final Connection connection;

    public Handler(Connection connection) {
        this.connection = connection;
    }

    public static Long popId(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
