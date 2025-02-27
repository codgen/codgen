package io.github.codgen.java.cmp;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import io.github.codgen.java.ctx.BeetlCtx;
import lombok.extern.slf4j.Slf4j;

/**
 * beetl 组件
 * 初始化 beetl 的 groupTemplate
 */
@Slf4j
@LiteflowComponent("beetl")
public class BeetlCmp extends NodeComponent {

    @Override
    public void process() throws Exception {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration                configuration  = Configuration.defaultConfiguration();
        GroupTemplate                groupTemplate  = new GroupTemplate(resourceLoader, configuration);
        BeetlCtx                     ctx            = this.getContextBean(BeetlCtx.class);
        ctx.setGroupTemplate(groupTemplate);
    }
}
