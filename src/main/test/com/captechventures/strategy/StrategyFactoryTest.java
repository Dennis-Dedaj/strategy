package com.captechventures.strategy;

import com.captechventures.model.Profile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class StrategyFactoryTest {

    @InjectMocks
    private final StrategyFactory factory = new StrategyFactory();

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RuntimeException.class)
    public void sanityCheckShouldFailBecauseOfSeveralStrategiesForTheProfile() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_premium_2", new PremiumTestStrategy());

        when(applicationContext.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        factory.init();
    }

    @Test
    public void shouldGetRegularPremium() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(applicationContext.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        factory.init();

        TestStrategy strategy = factory.getStrategy(TestStrategy.class, Profile.PREMIUM);
        assertEquals("Strategy returned was for the wrong profile", strategy.getClass(), PremiumTestStrategy.class);
    }

    @Test
    public void shouldGetSpecialPremium() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(applicationContext.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        factory.init();

        SuperSpecialStrategy strategy = factory.getStrategy(SuperSpecialStrategy.class, Profile.PREMIUM);
        assertEquals("Strategy returned was for the wrong profile", strategy.getClass(), LimitedPremiumSpecialStrategy.class);
    }

    @Test
    public void shouldGetTheDefaultStrategyWithNullProfileParam() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());
        annotatedBeans.put("bean_limitedpremium_super", new LimitedPremiumSpecialStrategy());
        annotatedBeans.put("bean_default", new DefaultTestStrategy());
        annotatedBeans.put("bean_default_super", new DefaultSuperSpecialStrategy());

        when(applicationContext.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        factory.init();

        TestStrategy strategy = factory.getStrategy(TestStrategy.class, null);
        assertTrue("Strategy returned was for the wrong profile", strategy instanceof DefaultTestStrategy);
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotFindAStrategy() {
        Map<String, Object> annotatedBeans = new HashMap<>();
        annotatedBeans.put("bean_free", new FreeTestStrategy());
        annotatedBeans.put("bean_limited", new LimitedTestStrategy());
        annotatedBeans.put("bean_premium", new PremiumTestStrategy());

        when(applicationContext.getBeansWithAnnotation(Strategy.class)).thenReturn(annotatedBeans);

        factory.init();

        factory.getStrategy(SuperSpecialStrategy.class, Profile.PREMIUM);
    }

    private static interface TestStrategy {}

    @Strategy(type=TestStrategy.class)
    private static class DefaultTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, profiles = Profile.FREE)
    private static class FreeTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, profiles = Profile.LIMITED)
    private static class LimitedTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, profiles = Profile.PREMIUM)
    private static class PremiumTestStrategy implements TestStrategy {
    }

    @Strategy(type=TestStrategy.class, profiles = {Profile.FREE, Profile.LIMITED})
    private static class FreeLimitedTestStrategy implements TestStrategy {
    }

    private static interface SuperSpecialStrategy {}

    @Strategy(type=SuperSpecialStrategy.class)
    private static class DefaultSuperSpecialStrategy implements SuperSpecialStrategy {
    }

    @Strategy(type=SuperSpecialStrategy.class, profiles = Profile.FREE)
    private static class FreeSuperSpecialStrategy implements SuperSpecialStrategy {
    }

    @Strategy(type=SuperSpecialStrategy.class, profiles = {Profile.LIMITED, Profile.PREMIUM})
    private static class LimitedPremiumSpecialStrategy implements SuperSpecialStrategy {
    }

}