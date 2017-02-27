package org.perfrepo.web.adapter;

import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.util.SearchResult;

import java.util.List;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface ReportAdapter {

    ReportDto getReport(Long id);

    ReportDto createReport(ReportDto report);

    ReportDto updateReport(ReportDto report);

    void removeReport(Long id);

    List<ReportDto> getAllReports();

    SearchResult<ReportDto> searchReports(ReportSearchCriteria searchParams);
}