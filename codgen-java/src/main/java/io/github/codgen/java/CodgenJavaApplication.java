package io.github.codgen.java;

import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.flow.element.Chain;

import io.github.codgen.java.ctx.BeetlCtx;
import io.github.codgen.java.ctx.CodeGeneratorCtx;
import io.github.codgen.java.ctx.CodeParserCtx;
import io.github.codgen.java.ctx.FileParserCtx;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class CodgenJavaApplication implements ApplicationRunner {
    @Resource
    private FlowExecutor flowExecutor;

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(CodgenJavaApplication.class, args);
            log.info("codgen-java启动成功: {}", applicationContext);
        } catch (Exception e) {
            log.error("codgen-java启动失败", e);
            throw e;
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        Map<String, Chain> chains = FlowBus.getChainMap();
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
