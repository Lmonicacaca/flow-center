package org.activiti.spring.boot;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public class DefaultRedisMessageListener implements MessageListener {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultRedisMessageListener.class);

  private RedisTemplate redisTemplate;
  private GridFsTemplate gridFsTemplate;
  private RepositoryService repositoryService;
  private ActivitiProperties activitiProperties;

  public void setActivitiProperties(ActivitiProperties activitiProperties) {
    this.activitiProperties = activitiProperties;
  }

  public RepositoryService getRepositoryService() {
    return repositoryService;
  }

  public void setRepositoryService(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  public GridFsTemplate getGridFsTemplate() {
    return gridFsTemplate;
  }

  public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
    this.gridFsTemplate = gridFsTemplate;
  }

  public RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  /**
   * @param redisTemplate the redisTemplate to set
   */
  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 消息格式 agentId:courtId:mongoId:versionId
   * 从mongodb获取新的流程定义  重新部署
   * @param message
   * @param pattern
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    byte[] channel = message.getChannel();
    String s = new String(channel);
    LOG.info("msgChannel ---->"+s);
    
    byte[] body = message.getBody();
    String msgBody = (String) redisTemplate.getValueSerializer().deserialize(body);
    LOG.info("msgBody ---->"+msgBody);
    if (StringUtils.isNotBlank(msgBody)){
      String[] split = msgBody.split(":");
      this.deploy(split);
    }
  }

  private void deploy (String[] split) {
      Query query = new Query();
      query.addCriteria(Criteria.where("_id").is(split[1]));
      GridFSFile one = gridFsTemplate.findOne(query);
      if (one!=null) {
        LOG.info(" refresh process xml name is "+one.getFilename());
        List<Deployment> list = repositoryService.createDeploymentQuery().deploymentCategory(split[1]).list();
        if (list!=null&&list.size()>0){
          LOG.info("process xml name : " + one.getFilename()+" had deploy" );
          return;
        }
        try {
          GridFsResource resource = gridFsTemplate.getResource(one);
          DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().addInputStream(one.getFilename(), resource.getInputStream());
          deploymentBuilder.key(split[0]).category(split[1]).name(one.getFilename()).deploy();
          LOG.info("process xml name : " + one.getFilename() + " is deploying");
        }catch (Exception e){
          LOG.error("process "+one.getFilename()+" deloy error : "+e.getMessage(),e);
        }

      }
    }

}
