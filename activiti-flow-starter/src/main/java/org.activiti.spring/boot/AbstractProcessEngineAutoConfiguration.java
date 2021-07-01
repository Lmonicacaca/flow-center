/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.spring.boot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringCallerRunsRejectedJobsHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.SpringRejectedJobsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Provides sane definitions for the various beans required to be productive with Activiti in Spring.
 *
 * @author Josh Long
 */
public abstract class AbstractProcessEngineAutoConfiguration
        extends AbstractProcessEngineConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessEngineConfiguration.class);
  protected ActivitiProperties activitiProperties;

  @Autowired
  private ResourcePatternResolver resourceLoader;
  
  @Autowired(required=false)
  private ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer;

  @Autowired(required=false)
  private GridFsTemplate gridFsTemplate;

  @Bean
  public SpringAsyncExecutor springAsyncExecutor(TaskExecutor taskExecutor) {
    return new SpringAsyncExecutor(taskExecutor, springRejectedJobsHandler());
  }
  
  @Bean 
  public SpringRejectedJobsHandler springRejectedJobsHandler() {
    return new SpringCallerRunsRejectedJobsHandler();
  }

  protected SpringProcessEngineConfiguration baseSpringProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager platformTransactionManager,
                                                                                  SpringAsyncExecutor springAsyncExecutor) throws IOException {
    List<Resource> procDefResources = new ArrayList<>();
    if (ActivitiProperties.MODEL_LOCAL.equals(activitiProperties.getModel())) {
        procDefResources = this.discoverProcessDefinitionResources(
        this.resourceLoader, this.activitiProperties.getProcessDefinitionLocationPrefix(),
        this.activitiProperties.getProcessDefinitionLocationSuffixes(),
        this.activitiProperties.isCheckProcessDefinitions());
    }
    SpringProcessEngineConfiguration conf = super.processEngineConfigurationBean(
        procDefResources.toArray(new Resource[procDefResources.size()]), dataSource, 
        platformTransactionManager, springAsyncExecutor);
    conf.setDeploymentName(defaultText(activitiProperties.getDeploymentName(), conf.getDeploymentName()));
    conf.setDatabaseSchema(defaultText(activitiProperties.getDatabaseSchema(), conf.getDatabaseSchema()));
    conf.setDatabaseSchemaUpdate(defaultText(activitiProperties.getDatabaseSchemaUpdate(), conf.getDatabaseSchemaUpdate()));
    conf.setDbIdentityUsed(activitiProperties.isDbIdentityUsed());
    conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
    
    conf.setAsyncExecutorActivate(activitiProperties.isAsyncExecutorActivate());
    
    conf.setMailServerHost(activitiProperties.getMailServerHost());
    conf.setMailServerPort(activitiProperties.getMailServerPort());
    conf.setMailServerUsername(activitiProperties.getMailServerUserName());
    conf.setMailServerPassword(activitiProperties.getMailServerPassword());
    conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
    conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
    conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());
    
    conf.setHistoryLevel(activitiProperties.getHistoryLevel());

    if (activitiProperties.getCustomMybatisMappers() != null) {
      conf.setCustomMybatisMappers(getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
    }

    if (activitiProperties.getCustomMybatisXMLMappers() != null) {
      conf.setCustomMybatisXMLMappers(new HashSet<String>(activitiProperties.getCustomMybatisXMLMappers()));
    }

    if (activitiProperties.getCustomMybatisMappers() != null) {
      conf.setCustomMybatisMappers(getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
    }

    if (activitiProperties.getCustomMybatisXMLMappers() != null) {
      conf.setCustomMybatisXMLMappers(new HashSet<String>(activitiProperties.getCustomMybatisXMLMappers()));
    }
    
    if (processEngineConfigurationConfigurer != null) {
    	processEngineConfigurationConfigurer.configure(conf);
    }

    return conf;
  }
  
  protected Set<Class<?>> getCustomMybatisMapperClasses(List<String> customMyBatisMappers) {
    Set<Class<?>> mybatisMappers = new HashSet<Class<?>>();
    for (String customMybatisMapperClassName : customMyBatisMappers) {
      try {
        Class customMybatisClass = Class.forName(customMybatisMapperClassName);
        mybatisMappers.add(customMybatisClass);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException("Class " + customMybatisMapperClassName + " has not been found.", e);
      }
    }
    return mybatisMappers;
  }


  protected String defaultText(String deploymentName, String deploymentName1) {
    if (StringUtils.hasText(deploymentName))
      return deploymentName;
    return deploymentName1;
  }

  @Autowired
  protected void setActivitiProperties(ActivitiProperties activitiProperties) {
    this.activitiProperties = activitiProperties;
  }

  protected ActivitiProperties getActivitiProperties() {
    return this.activitiProperties;
  }


  @Bean
  public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration configuration) throws Exception {
    return super.springProcessEngineBean(configuration);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
    return super.runtimeServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
    RepositoryService repositoryService = super.repositoryServiceBean(processEngine);
    //启动从mongodb获取最新的流程定义
    if (ActivitiProperties.MODEL_REMOTE.equals(activitiProperties.getModel())) {
  /*    if (!activitiProperties.isAuto()) {
          deploy(activitiProperties.getAppId(),repositoryService);
      } else {
        if (appIdQuery == null) {
            throw new IllegalArgumentException("AppIdQuery 没有找到实现类....");
        } else {
            deploy(appIdQuery.queryAppIds(),repositoryService);
        }
      }*/
    CompletableFuture.runAsync(new Runnable() {
      @Override
      public void run() {
        deploy(repositoryService);
      }
    });

    }
    return repositoryService;
  }

  private void deploy (RepositoryService repositoryService) {

      Query query = new Query();
/*      Pattern compile = Pattern.compile("^" + appId + "-", Pattern.CASE_INSENSITIVE);
      query.addCriteria(Criteria.where("filename").regex(compile));*/
    Pattern compile = Pattern.compile("^.*?bpmn20.xml$", Pattern.CASE_INSENSITIVE);
    query.addCriteria(Criteria.where("filename").regex(compile));
      GridFSFindIterable gridFSFiles = gridFsTemplate.find(query);
      if (gridFSFiles == null) {
        LOG.warn("from mongoDB process xml is null");
      } else {
        LOG.info("get process xml ");
        MongoCursor<GridFSFile> iterator = gridFSFiles.iterator();
        while (iterator.hasNext()) {
          GridFSFile fsdbFile = iterator.next();
          LOG.info("get process xml name is " + fsdbFile.getFilename());
          String[] split = fsdbFile.getFilename().split("-");
          List<Deployment> list = repositoryService.createDeploymentQuery().deploymentCategory(fsdbFile.getId().toString()).list();
          if (list != null && list.size() > 0) {
            LOG.info("process xml name : " + fsdbFile.getFilename() + " had deploy");
          } else {
            LOG.info("process xml name : " + fsdbFile.getFilename() + " is deploying");
            try {
              GridFsResource resource = gridFsTemplate.getResource(fsdbFile);
              repositoryService.createDeployment().addInputStream(fsdbFile.getFilename(), resource.getInputStream())
                      .tenantId(split[1]).key(split[0]).category(fsdbFile.getId().toString()).name(fsdbFile.getFilename()).enableDuplicateFiltering()
                      .deploy();
            } catch (Exception e) {
              LOG.error("process " + fsdbFile.getFilename() + " deloy error : " + e.getMessage(), e);
            }
          }
        }
    }
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public TaskService taskServiceBean(ProcessEngine processEngine) {
    return super.taskServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public HistoryService historyServiceBean(ProcessEngine processEngine) {
    return super.historyServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public ManagementService managementServiceBeanBean(ProcessEngine processEngine) {
    return super.managementServiceBeanBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public FormService formServiceBean(ProcessEngine processEngine) {
    return super.formServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public IdentityService identityServiceBean(ProcessEngine processEngine) {
    return super.identityServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }
}
