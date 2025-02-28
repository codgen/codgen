package io.github.codgen.java;

import java.util.Map;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.FlowExecutorHolder;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.element.Chain;
import com.yomahub.liteflow.property.LiteflowConfig;

import io.github.codgen.java.ctx.BeetlCtx;
import io.github.codgen.java.ctx.CodeGeneratorCtx;
import io.github.codgen.java.ctx.CodeParserCtx;
import io.github.codgen.java.ctx.FileParserCtx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodgenJavaApplication {
    public static void main(String[] args) {
        LiteflowConfig config = new LiteflowConfig();
        config.setRuleSource("config/flow.el.xml");
        FlowExecutor       flowExecutor = FlowExecutorHolder.loadInstance(config);
        Map<String, Chain> chains       = FlowBus.getChainMap();
        for (Chain chain : chains.values()) {
            if (!"enabled".equals(chain.getNamespace())) {
                continue;
            }
            LiteflowResponse response = flowExecutor.execute2Resp(
                    chain.getChainId(), null, BeetlCtx.class, FileParserCtx.class, CodeParserCtx.class, CodeGeneratorCtx.class);
            if (response.isSuccess()) {
                log.info("流程处理成功({})", response.getChainId());
            } else {
                log.error("流程处理失败({}): {}-{}", response.getChainId(), response.getCode(), response.getMessage());
            }
        }
    }
}
