package com.cxl.rpc.proxy.consumer.spring;

import com.cxl.rpc.registry.ServiceRegistry;
import com.cxl.rpc.proxy.consumer.RpcInvokerFactory;
import com.cxl.rpc.proxy.consumer.annotation.RpcClient;
import com.cxl.rpc.proxy.provider.RpcProviderFactory;
import com.cxl.rpc.util.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cxl
 */
public class RpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, DisposableBean, BeanFactoryAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSpringInvokerFactory.class);

    //----------------------config------------------------

    private Class<? extends ServiceRegistry> serviceRegistryClass;
    //class.forName
    private Map<String, String> serviceRegistryParam;


    public Class<? extends ServiceRegistry> getServiceRegistryClass() {
        return serviceRegistryClass;
    }

    public void setServiceRegistryClass(Class<? extends ServiceRegistry> serviceRegistryClass) {
        this.serviceRegistryClass = serviceRegistryClass;
    }

    public Map<String, String> getServiceRegistryParam() {
        return serviceRegistryParam;
    }

    public void setServiceRegistryParam(Map<String, String> serviceRegistryParam) {
        this.serviceRegistryParam = serviceRegistryParam;
    }

    //-----------------------util---------------------------
    private RpcInvokerFactory invokerFactory;





    @Override
    public void afterPropertiesSet() throws Exception {
        //start invoke factory
        invokerFactory = new RpcInvokerFactory(serviceRegistryClass, serviceRegistryParam);
        invokerFactory.start();
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        //collection
        final Set<String> serviceKeyList = new HashSet<>();

        //parse RpcReferenceBean
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(RpcClient.class)) {
                //valid
                Class iface = field.getType();
                if (!iface.isInterface()) {
                    throw new RpcException("rpc, reference(RpcReference) must be interface.");
                }

                RpcClient rpcClient = field.getAnnotation(RpcClient.class);

                //init reference bean
//                RpcReferenceBean referenceBean = new RpcReferenceBean(rpcReference.netType(), rpcReference.serializer().getSerializer(), rpcReference.callType(), rpcReference.loadBalance(), iface, rpcReference.version(), rpcReference.timeout(), rpcReference.address(), rpcReference.accessToken(), null, invokerFactory);

//                Object serviceProxy=referenceBean.getObject();

                //set bean
                field.setAccessible(true);
//                field.set(bean,serviceProxy);

                LOGGER.info(">>>>>>>>>>>>>>invoker factory init reference bean success. serviceKey = {}, bean.field = {}.{}", RpcProviderFactory.makeServiceKey(iface.getName(), rpcClient.version()),beanName,field.getName());

                //collection
                String serviceKey=RpcProviderFactory.makeServiceKey(iface.getName(), rpcClient.version());
                serviceKeyList.add(serviceKey);

            }
        });

        //mult discovery

        if (invokerFactory.getServiceRegistry() != null) {
            try {
                invokerFactory.getServiceRegistry().discovery(serviceKeyList);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(),e);
            }
        }
        return super.postProcessAfterInstantiation(bean, beanName);
    }
    @Override
    public void destroy() throws Exception {
        invokerFactory.stop();
    }
    private BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
       this.beanFactory=beanFactory;
    }
}
