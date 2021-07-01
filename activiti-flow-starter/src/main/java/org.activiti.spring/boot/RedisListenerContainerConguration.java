package org.activiti.spring.boot;

import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


@Configuration
@EnableConfigurationProperties(ActivitiProperties.class)
public class RedisListenerContainerConguration {

  @Autowired
  protected ActivitiProperties activitiProperties;

  @Bean(name = "redisMessageListener")
  @ConditionalOnProperty(prefix = "spring.activiti",name ="model",havingValue = "remote",matchIfMissing = false)
  @ConditionalOnClass(value = {RedisTemplate.class, GridFsTemplate.class, RepositoryService.class})
  @ConditionalOnMissingBean(value = MessageListener.class)
  public MessageListener redisMessageListener(RedisTemplate redisTemplate,
                                              GridFsTemplate gridFsTemplate,
                                              RepositoryService repositoryService){
    DefaultRedisMessageListener redisMessageListener = new DefaultRedisMessageListener();
    redisMessageListener.setRedisTemplate(redisTemplate);
    redisMessageListener.setGridFsTemplate(gridFsTemplate);
    redisMessageListener.setActivitiProperties(activitiProperties);
    redisMessageListener.setRepositoryService(repositoryService);
    return redisMessageListener;
  }


  @Bean
  @ConditionalOnProperty(prefix = "spring.activiti",name ="model",havingValue = "remote",matchIfMissing = false)
  @ConditionalOnClass(value = {MessageListener.class,RedisConnectionFactory.class})
  public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                     MessageListener redisMessageListener){
    ChannelTopic channelTopic = new ChannelTopic(activitiProperties.getQuequeName());;
    if (StringUtils.isNoneBlank(activitiProperties.getQuequeName())){
      channelTopic = new ChannelTopic(activitiProperties.getQuequeName());
    }
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.addMessageListener(redisMessageListener,channelTopic);
    return container;
  }
}
