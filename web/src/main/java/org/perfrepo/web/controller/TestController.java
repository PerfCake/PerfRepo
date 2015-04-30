/**
 *
 * PerfRepo
 *
 * Copyright (C) 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.perfrepo.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.perfrepo.model.Alert;
import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.AlertingService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.SearchCriteriaSession;
import org.perfrepo.web.util.MessageUtils;
import org.perfrepo.web.viewscope.ViewScoped;

/**
 * Backing bean for editing and displaying details of {@link Test}.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class TestController extends BaseController {

	private static final long serialVersionUID = 370202307562230671L;
	private static final Logger log = Logger.getLogger(TestController.class);

	private boolean editMode;
	private boolean createMode;
	private Long testId;

	@Inject
	private TestService testService;

	@Inject
	private UserService userService;

	@Inject
	private SearchCriteriaSession criteriaSession;

   @Inject
   private AlertingService alertingService;

	private Test test = null;
	private MetricDetails metricDetails = new MetricDetails();
   private AlertDetails alertDetails = new AlertDetails();

	/**
	 * called on preRenderView
	 */
	public void preRender() throws Exception {
		reloadSessionMessages();
		if (testId == null) {
			if (!createMode) {
				log.error("No test ID supplied");
				redirectWithMessage("/", ERROR, "page.test.errorNoTestId");
			} else {
				if (test == null) {
					test = new Test();
				}
			}
		} else {
			if (test == null) {
				test = testService.getFullTest(testId);
				if (test == null) {
					log.error("Can't find test with id " + testId);
					redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testId);
				} else {
					metricDetails.selectionAssignedMetrics = testService.getAvailableMetrics(test);
				}
			}
		}
	}

   public String create() {
      if (test == null) {
         throw new IllegalStateException("test is null");
      }
      try {
         Test createdTest = testService.createTest(test);
         redirectWithMessage("/test/" + createdTest.getId(), INFO, "page.test.createdSuccesfully", createdTest.getId());
      } catch (ServiceException e) {
         addMessage(e);
      } catch (SecurityException e) {
         addMessage(ERROR, "page.test.errorSecurityException", e.getMessage());
      } catch (EJBException e) {
         if (e.getCause() != null && e.getCause().getClass() == SecurityException.class) {
            addMessage(ERROR, "page.test.errorSecurityException", e.getCause().getMessage());
         } else {
            throw e;
         }
      }
      return null;
   }

   public String update() {
      if (test == null) {
         throw new IllegalStateException("test is null");
      }
      testService.updateTest(test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.updatedSuccessfully");
      return null;
   }

	public void listTestExecutions() {
		//clear criterias
		TestExecutionSearchTO criteriaSession = this.criteriaSession.getExecutionSearchCriteria();
		criteriaSession.setStartedFrom(null);
		criteriaSession.setStartedTo(null);
		criteriaSession.setTags(null);
		criteriaSession.setTestName(null);

		criteriaSession.setTestUID(test.getUid());
		redirect("/exec/search");
	}

	public List<Metric> getMetricsList() {
		List<Metric> metricList = new ArrayList<Metric>();
		if (test != null) {
			metricList.addAll(test.getMetrics());
		}
		Collections.sort(metricList);
		return metricList;
	}

   public List<Alert> getAlertsList() {
      Test fullTest = testService.getFullTest(test.getId());

      return alertingService.getAlertsList(fullTest);
   }

   public void processAlert() {
      Metric metric = testService.getFullMetric(alertDetails.getMetricId());

      try {
         alertingService.checkConditionSyntax(alertDetails.getCondition(), metric);
      } catch (RuntimeException ex) {
         addSessionMessage(ERROR, "alerting.conditionSyntax.error", ex.getMessage());
         reloadSessionMessages();
         return;
      }

      Alert alert = new Alert();
      alert.setName(alertDetails.getName());
      alert.setCondition(alertDetails.getCondition());
      alert.setDescription(alertDetails.getDescription());

      alert.setMetric(metric);

      Test test = testService.getFullTest(this.test.getId());
      alert.setTest(test);

      List<String> tagSplit = Arrays.asList(StringUtils.split(alertDetails.getTags()));
      List<Tag> tags = new ArrayList<>();
      for(String tagString: tagSplit) {
         Tag tag = new Tag();
         tag.setName(tagString);
         tags.add(tag);
      }

      alert.setTags(tags);

      if(alertDetails.getId() == null) {
         alertingService.createAlert(alert);
         redirectWithMessage("/test/" + testId, INFO, "page.alert.createdSuccesfully");
      }
      else {
         alert.setId(alertDetails.getId());
         alertingService.updateAlert(alert);
         redirectWithMessage("/test/" + testId, INFO, "page.alert.updatedSuccesfully");
      }
   }

   public void deleteAlert(Alert alert) {
      alertingService.removeAlert(alert);
      redirectWithMessage("/test/" + testId, INFO, "page.alert.removedSuccesfully");
   }

   /** --------------- Subscription for alerting ----------------- **/

   public void addSubscriber() {
      User currentUser = userService.getLoggedUser();
      testService.addSubscriber(currentUser, test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.subscribed");
   }

   public void removeSubscriber() {
      User currentUser = userService.getLoggedUser();
      testService.removeSubscriber(currentUser, test);
      redirectWithMessage("/test/" + testId, INFO, "page.test.unsubscribed");
   }

   public boolean isSubscribed() {
      User currentUser = userService.getLoggedUser();
      return testService.isUserSubscribed(currentUser, test);
   }

   /** --------------- Getters/Setters ---------------- **/

   public Test getTest() {
      return test;
   }

   public void setTest(Test test) {
      this.test = test;
   }

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isCreateMode() {
		return createMode;
	}

	public void setCreateMode(boolean createMode) {
		this.createMode = createMode;
	}

	public MetricDetails getMetricDetails() {
		return metricDetails;
	}

   public AlertDetails getAlertDetails() {
      return alertDetails;
   }

   public List<String> getUserGroups() {
		return userService.getLoggedUserGroupNames();
	}

   /** ----------------------------------- Helper inner classes --------------------------- **/

   /**
    * Helper class for pop-up for creating and editing metrics
    */
	public class MetricDetails {
		private boolean createMode;
		private Metric metric;
		private Long selectedAssignedMetricId;
		private List<Metric> selectionAssignedMetrics;

		public Metric getMetric() {
			return metric;
		}

		public void setMetricForUpdate(Metric metric) {
			this.metric = metric;
		}

		public void setEmptyMetric() {
			this.metric = new Metric();
		}

		public void unsetMetric() {
			this.metric = null;
		}

		public boolean isCreateMode() {
			return createMode;
		}

		public void setCreateMode(boolean createMode) {
			this.createMode = createMode;
		}

		public List<Metric> getSelectionAssignedMetrics() {
			return selectionAssignedMetrics;
		}

		public boolean isSelectionMetricVisible() {
			return selectionAssignedMetrics != null && !selectionAssignedMetrics.isEmpty();
		}

		public Long getSelectedAssignedMetricId() {
			return selectedAssignedMetricId;
		}

		public void setSelectedAssignedMetricId(Long selectedAssignedMetricId) {
			this.selectedAssignedMetricId = selectedAssignedMetricId;
		}

		public void addAssignedMetric() {
			if (selectedAssignedMetricId == null || selectionAssignedMetrics == null) {
				redirectWithMessage("/test/" + testId, ERROR, "page.test.errorNoAssignedMetric");
			} else {
				Metric selectedAssignedMetric = null;
				for (Metric m : selectionAssignedMetrics) {
					if (selectedAssignedMetricId.equals(m.getId())) {
						selectedAssignedMetric = m;
						break;
					}
				}
				if (selectedAssignedMetric == null) {
					redirectWithMessage("/test/" + testId, ERROR, "page.test.errorNoAssignedMetric");
					return;
				}
				try {
					testService.addMetric(test, selectedAssignedMetric);
					redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyAssigned", selectedAssignedMetric.getName());
				} catch (ServiceException e) {
					addSessionMessage(e);
					redirect("/test/" + testId);
				}
			}
		}

		public MetricComparator[] getMetricComparators() {
			return MetricComparator.values();
		}

		public String getEnumLabel(MetricComparator mc) {
			return MessageUtils.getEnum(mc);
		}

		public void createMetric() {
			try {
				testService.addMetric(test, metric);
				redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyCreated", metric.getName());
			} catch (ServiceException e) {
				addSessionMessage(e);
				redirect("/test/" + testId);
			}
		}

		public void updateMetric() {
			try {
				testService.updateMetric(test, metric);
			} catch (ServiceException e) {
				addSessionMessage(e);
				redirect("/test/" + testId);
			}
		}

		public void deleteMetric(Metric metricToDelete) {
			try {
				testService.removeMetric(test, metricToDelete);
				redirectWithMessage("/test/" + testId, INFO, "page.test.metricSuccessfullyDeleted", metricToDelete.getName());
			} catch (ServiceException e) {
				addSessionMessage(e);
				redirect("/test/" + testId);
			}
		}
	}

   /**
    * Helper class for pop-up for creating and editing alerts.
    */
   public class AlertDetails {

      private Long id;
      private String name;
      private String description;
      private String condition;
      private Long metricId;
      private String tags;

      public Long getId() {
         return id;
      }

      public void setId(Long id) {
         this.id = id;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getDescription() {
         return description;
      }

      public void setDescription(String description) {
         this.description = description;
      }

      public String getCondition() {
         return condition;
      }

      public void setCondition(String condition) {
         this.condition = condition;
      }

      public Long getMetricId() {
         return metricId;
      }

      public void setMetricId(Long metricId) {
         this.metricId = metricId;
      }

      public Long getTestId() {
         return testId;
      }

      public String getTags() {
         return tags;
      }

      public void setTags(String tags) {
         this.tags = tags;
      }

      public void unset() {
         this.id = null;
         this.name = null;
         this.condition = null;
         this.description = null;
         this.metricId = null;
         this.tags = null;
      }

      public void setAlertForUpdate(Alert alert) {
         this.id = alert.getId();
         this.name = alert.getName();
         this.condition = alert.getCondition();
         this.description = alert.getDescription();
         this.metricId = alert.getMetric().getId();

         List<String> tagsString = new ArrayList<>();
         for(Tag tag: alert.getTags()) {
            tagsString.add(tag.getName());
         }
         this.tags = StringUtils.join(tagsString, " ");
      }
   }
}
