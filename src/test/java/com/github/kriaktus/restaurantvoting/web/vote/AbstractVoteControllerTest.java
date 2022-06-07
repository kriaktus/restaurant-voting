package com.github.kriaktus.restaurantvoting.web.vote;

import com.github.kriaktus.restaurantvoting.web.AbstractControllerTest;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractVoteControllerTest extends AbstractControllerTest {
    @Autowired
    private VoteController voteController;

    protected VoteController unwrapVoteController() {
        if (AopUtils.isAopProxy(voteController) && voteController instanceof Advised) {
            try {
                Object target = ((Advised) voteController).getTargetSource().getTarget();
                return (VoteController) target;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
