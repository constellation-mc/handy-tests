package me.melontini.handytests.util.runner;

import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.melontini.handytests.util.TestContext;
import org.slf4j.Logger;

public class TestRunner {

  private static final Logger log = LogUtils.getLogger();

  public static <C extends TestContext<?>> void runTests(Object entrypoint, C context) {
    List<Method> tests = new ArrayList<>();
    for (Method method : entrypoint.getClass().getDeclaredMethods()) {
      if (!method.isAnnotationPresent(HandyTest.class)) continue;
      if (Modifier.isStatic(method.getModifiers()))
        throw new IllegalStateException("static @HandyTest method %s".formatted(method.getName()));

      verifyParams(method.getParameterTypes(), context.getClass());
      method.setAccessible(true);
      tests.add(method);
    }
    tests.sort(Comparator.comparing(Method::getName));
    tests.sort(
        Comparator.comparingInt(value -> value.getAnnotation(HandyTest.class).priority()));

    log.info("Running {} {} tests...", tests.size(), entrypoint.getClass().getName());
    for (int i = 0; i < tests.size(); i++) {
      Method test = tests.get(i);
      try {
        test.invoke(entrypoint, context);
        log.info(
            "Completed {}#{} [{}/{}]...",
            entrypoint.getClass().getName(),
            test.getName(),
            i + 1,
            tests.size());
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(
            "Test %s#%s failed!".formatted(entrypoint.getClass().getName(), test.getName()),
            e instanceof InvocationTargetException ite ? ite.getCause() : e);
      }
    }
  }

  private static <C extends TestContext<?>> void verifyParams(Class<?>[] params, Class<C> cls) {
    if (params.length != 1 || params[0] != cls)
      throw new IllegalStateException(
          "Parameters must only contain the %s!".formatted(cls.getName()));
  }
}
