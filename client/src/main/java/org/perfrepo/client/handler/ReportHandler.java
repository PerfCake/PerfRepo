package org.perfrepo.client.handler;

import org.perfrepo.client.Connection;
import org.perfrepo.dto.report.ReportDto;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class ReportHandler extends Handler {

    private static final String CONTEXT_PATH = "reports/";

    public ReportHandler(Connection connection) {
        super(connection);
    }

    public ReportDto get(Long id) {
        String path = CONTEXT_PATH + id;
        return connection.get(path, ReportDto.class);
    }

    public ReportDto create(ReportDto test) {
        String path = CONTEXT_PATH;
        String createdUri = connection.post(path, test);

        return get(popId(createdUri));
    }

    public void delete(Long id) {
        String path = CONTEXT_PATH + id;
        connection.delete(path);
    }

    public List<ReportDto> getAll() {
        String path = CONTEXT_PATH;
        return connection.get(path, new GenericType<List<ReportDto>>() { });
    }

}
