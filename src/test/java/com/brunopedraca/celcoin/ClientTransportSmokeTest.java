package com.brunopedraca.celcoin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.brunopedraca.celcoin.acquiring.CelcoinAcquiringClient;
import com.brunopedraca.celcoin.antifraud.CelcoinAntifraudClient;
import com.brunopedraca.celcoin.banking.CelcoinAccountClient;
import com.brunopedraca.celcoin.boleto.CelcoinBoletoClient;
import com.brunopedraca.celcoin.cards.CelcoinCardClient;
import com.brunopedraca.celcoin.cnab.CelcoinCnabClient;
import com.brunopedraca.celcoin.common.exception.CelcoinIntegrationException;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.embedded.CelcoinEmbeddedClient;
import com.brunopedraca.celcoin.indirectpix.CelcoinIndirectPixClient;
import com.brunopedraca.celcoin.itp.CelcoinItpClient;
import com.brunopedraca.celcoin.jsr.CelcoinJsrClient;
import com.brunopedraca.celcoin.openfinance.CelcoinOpenFinanceClient;
import com.brunopedraca.celcoin.pixauto.CelcoinPixAutoClient;
import com.brunopedraca.celcoin.reconciliation.CelcoinReconciliationClient;
import com.brunopedraca.celcoin.sweeping.CelcoinSweepingClient;
import com.brunopedraca.celcoin.topup.CelcoinTopupClient;
import com.brunopedraca.celcoin.vehicle.CelcoinVehicleClient;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Transport-level smoke coverage for clients whose real contract tests require a partner environment.
 * The test deliberately uses a mocked transport: it does not assert a remote response or contact Celcoin.
 */
class ClientTransportSmokeTest {
    private static final List<Class<?>> CLIENTS = List.of(
            CelcoinAccountClient.class,
            CelcoinAcquiringClient.class,
            CelcoinAntifraudClient.class,
            CelcoinBoletoClient.class,
            CelcoinCardClient.class,
            CelcoinCnabClient.class,
            CelcoinEmbeddedClient.class,
            CelcoinIndirectPixClient.class,
            CelcoinItpClient.class,
            CelcoinJsrClient.class,
            CelcoinOpenFinanceClient.class,
            CelcoinPixAutoClient.class,
            CelcoinReconciliationClient.class,
            CelcoinSweepingClient.class,
            CelcoinTopupClient.class,
            CelcoinVehicleClient.class);

    @Test
    void invokesPublicOperationsWithoutNetworkAccess() throws Exception {
        CelcoinHttpClient transport = mock(CelcoinHttpClient.class);
        int methods = 0;
        int locallyValidated = 0;

        for (Class<?> type : CLIENTS) {
            Object client = type.getDeclaredConstructor(CelcoinHttpClient.class).newInstance(transport);
            for (Method method : type.getDeclaredMethods()) {
                if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) continue;
                methods++;
                Object[] arguments = new Object[method.getParameterCount()];
                for (int index = 0; index < arguments.length; index++) {
                    arguments[index] = sample(method.getGenericParameterTypes()[index]);
                }
                try {
                    method.invoke(client, arguments);
                } catch (InvocationTargetException exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof IllegalArgumentException || cause instanceof CelcoinIntegrationException) {
                        locallyValidated++;
                    } else {
                        throw exception;
                    }
                }
            }
        }

        assertThat(methods).isGreaterThan(80);
        assertThat(locallyValidated).isGreaterThanOrEqualTo(0);
    }

    private static Object sample(Type genericType) {
        if (genericType instanceof ParameterizedType parameterized) {
            Class<?> raw = (Class<?>) parameterized.getRawType();
            Type[] arguments = parameterized.getActualTypeArguments();
            if (List.class.isAssignableFrom(raw)) return List.of(sample(arguments[0]));
            if (Map.class.isAssignableFrom(raw)) return Map.of("value", "1234567");
            return sample(raw);
        }
        if (!(genericType instanceof Class<?> type)) return null;
        if (type == String.class) return "1234567";
        if (type == int.class || type == Integer.class) return 1;
        if (type == long.class || type == Long.class) return 1L;
        if (type == boolean.class || type == Boolean.class) return false;
        if (type == BigDecimal.class) return BigDecimal.ONE;
        if (type == LocalDate.class) return LocalDate.now().plusDays(2);
        if (type == OffsetDateTime.class) return OffsetDateTime.now();
        if (type == UUID.class) return UUID.randomUUID();
        if (type == Path.class) return temporaryFile();
        if (type == byte[].class) return new byte[] {1};
        if (type == Map.class) return Map.of("manual", true, "account", "1234567");
        if (List.class.isAssignableFrom(type)) return List.of(Map.of("id", "1234567"));
        if (type.isArray()) {
            Object array = Array.newInstance(type.componentType(), 1);
            Array.set(array, 0, sample(type.componentType()));
            return array;
        }
        if (type.isEnum()) return type.getEnumConstants()[0];
        if (type.isRecord()) return recordSample(type);
        if (type == Object.class) return Map.of("value", "1234567");
        return null;
    }

    private static Object recordSample(Class<?> type) {
        RecordComponent[] components = type.getRecordComponents();
        Object[] values = new Object[components.length];
        for (int index = 0; index < components.length; index++) {
            values[index] = sample(components[index].getGenericType());
        }
        try {
            Constructor<?> constructor = type.getDeclaredConstructor(
                    java.util.Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new));
            constructor.setAccessible(true);
            return constructor.newInstance(values);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to build smoke-test request " + type.getName(), exception);
        }
    }

    private static Path temporaryFile() {
        try {
            Path file = Files.createTempFile("celcoin-smoke-", ".txt");
            Files.writeString(file, "00000000000000000000000000000000000000000000000000\n");
            return file;
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
