package com.cxl.rpc.proxy.provider.spring;

import com.cxl.rpc.proxy.provider.RpcProviderFactory;
import com.cxl.rpc.proxy.provider.annotation.RpcService;
import com.cxl.rpc.util.RpcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
public class RpcSpringProviderFactory extends RpcProviderFactory implements ApplicationContextAware, InitializingBean, DisposableBean {

    @Override
    public void destroy() throws Exception {
        super.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (null != serviceBeanMap && 0 < serviceBeanMap.size()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                //valid
                if (0==serviceBean.getClass().getInterfaces().length ) {
                    throw new RpcException("rpc ,service(RpcService) must inherit interface.");
                }
                //add service
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);

                String iface = serviceBean.getClass().getInterfaces()[0].getName();
                String version = rpcService.version();
                super.addService(iface, version, serviceBean);
            }
        }
    }
}
