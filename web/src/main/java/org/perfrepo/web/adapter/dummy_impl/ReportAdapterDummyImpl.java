package org.perfrepo.web.adapter.dummy_impl;

import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.web.adapter.ReportAdapter;
import org.perfrepo.web.adapter.dummy_impl.storage.Storage;

import javax.inject.Inject;
import java.util.List;

public class ReportAdapterDummyImpl implements ReportAdapter {

    @Inject
    private Storage storage;

    @Override
    public ReportDto getReport(Long id) {
        return storage.report().getById(id);
    }

    @Override
    public ReportDto createReport(ReportDto report) {
        return storage.report().create(report);
    }

    @Override
    public void removeReport(Long id) {
        storage.report().delete(id);
    }

    @Override
    public List<ReportDto> getAllReports() {
        return storage.report().getAll();
    }

    @Override
    public SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams) {
        return storage.report().search(searchParams);
    }
}