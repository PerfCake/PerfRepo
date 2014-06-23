/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.dao;

import org.jboss.qa.perfrepo.model.Tag;
import org.jboss.qa.perfrepo.model.report.Report;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
public class ReportDAO extends DAO<Report, Long> {

   public Report findByCode(String code) {
      List<Report> reports = findAllByProperty("code", code);
      if (reports.size() > 0) {
         return reports.get(0);
      }
      return null;
   }

   public List<Report> findTestsByUser(String username) {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("username", username);

      return findByNamedQuery(Report.FIND_BY_USERNAME, params);
   }

   public Long findMaxId() {
      TypedQuery<Long> tq = createNamedQuery(Report.FIND_MAX_ID, Long.class);

      return tq.getSingleResult();
   }
}