package org.koshiroanz.junit.ejemplo.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.koshiroanz.junit.ejemplo.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    // Por cada método se inicializa una instancia de cuenta
    @BeforeEach
    void init() {
        this.cuenta = new Cuenta("John Doe", new BigDecimal("1000.123"));
        System.out.println("Iniciando el método");
    }

    // Por cada método finalizado se imprime mensaje
    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el Test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el Test");
    }

    @Nested
    @Tag("cuenta")
    @DisplayName("Test de nombre de Cuenta")
    class CuentaTestNombre {
        @Test
        @DisplayName("Test account name Persona")
        void testNombreCuenta() {
            String esperadoPersona = "John Doe";
            String actualPersona = cuenta.getPersona();

            assertEquals(esperadoPersona, actualPersona, () -> "El nombre de la cuenta no es el esperado");
            assertTrue(actualPersona.equals(esperadoPersona), "El nombre de la cuenta no son iguales");
        }

        @Test
        @DisplayName("Test accounts")
        void testReferenciaCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.99"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.99"));
            assertEquals(cuenta2, cuenta, () -> "La cuenta esperada es distinta");
        }
    }

    @Nested
    @Tag("cuenta")
    @DisplayName("Test de Saldo de Cuenta")
    class CuentaTestSaldo {
        @Test
        @DisplayName("Test account saldo")
        void testSaldoCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1.123"));
            assertEquals(1.123, cuenta.getSaldo().doubleValue(), () -> "El saldo esperado no es igual");
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> "El saldo esperado no es igual");
        }

        @Test
        @DisplayName("Test saldo cannot be less than zero")
        void testSaldoCuentaLessThanZero() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("-1.123"));
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> "El saldo esperado no puede ser menor a 0");
        }

        @Test
        @DisplayName("Test saldo greater than zero")
        void testSaldoCuentaGreaterThanZero() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1.123"));
            assertEquals(1.123, cuenta.getSaldo().doubleValue());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Test saldo equals to zero")
        void testSaldoCuentaEqualZero() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("0.00"));
            assertEquals(0.00, cuenta.getSaldo().doubleValue());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) == 0);
        }
    }

    @Nested
    @DisplayName("Test de Operaciones de Cuenta")
    class CuentaTestOperaciones {
        @Test
        @Tag("cuenta")
        @DisplayName("Test debit an Account")
        void testDebitoCuenta() {
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000"));
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().doubleValue());
        }

        @Test
        @Tag("cuenta")
        @DisplayName("Test credit an Account")
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals(1100.123, cuenta.getSaldo().doubleValue());
        }

        @Test
        @Tag("banco")
        //@Disabled
        @DisplayName("Test transfer money between Accounts")
        void testTransferirDineroCuentas() {
            //fail();
            Cuenta cuentaOrigen = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuentaDestino = new Cuenta("Jane Doe", new BigDecimal("1000"));

            Banco santander = new Banco();
            santander.setNombre("Santander Rio");

            santander.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("500"));

            assertEquals("2000", cuentaOrigen.getSaldo().toPlainString());
            assertEquals("1500", cuentaDestino.getSaldo().toPlainString());
        }
    }

    @Nested
    @DisplayName("Test de Relaciones de Cuenta")
    class CuentaTestRelaciones {
        @Test
        @Tag("banco")
        @Tag("cuenta")
        @DisplayName("Test relationship between Bank and Account")
        void testRelacionBancoCuentas() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Jane Doe", new BigDecimal("1000"));

            Banco banco = new Banco();
            banco.setNombre("Santander Rio");
            banco.addCuenta(cuenta1);
            banco.addCuenta(cuenta2);

            banco.transferir(cuenta1, cuenta2, new BigDecimal("500"));

            String expectedPerson = "Jane Doe";

            assertAll(
                    // Cuando solamente es una línea no es necesario las llaves.
                    () -> assertEquals("2000", cuenta1.getSaldo().toPlainString(),
                            () -> "El saldo de la cuenta (1) no concuerda con el esperado"),
                    () -> assertEquals("1500", cuenta2.getSaldo().toPlainString(),
                            () -> "El saldo de la cuenta (2) no concuerda con el esperado"),
                    () -> assertEquals(2, banco.getCuentas().size(),
                            () -> "La cantidad de cuentas no concuerda con el esperado"),
                    () -> assertEquals("Santander Rio", cuenta1.getBanco().getNombre(),
                            () -> "El nombre del Banco no concuerda con el esperado"),
                    () -> {
                        // Si esta presente el valor buscado [Opt 1]
                        assertEquals("Jane Doe", banco.getCuentas().stream()
                                        .filter(c ->
                                                c.getPersona().equals(expectedPerson))
                                        .findFirst()
                                        .get()
                                        .getPersona(),
                                () -> "La cuenta de la Persona no concuerda con el esperado");
                    },
                    () -> {
                        // Si esta presente el valor buscado [Opt 2]
                        assertTrue(banco.getCuentas().stream()
                                .anyMatch(c -> c.getPersona().equals(expectedPerson)));
                    },
                    () -> {
                        // Si esta presente el valor buscado [Opt 3]
                        assertTrue(banco.getCuentas().stream()
                                .filter(c -> c.getPersona().equals(expectedPerson))
                                .findFirst()
                                .isPresent());
                    });

        }
    }

    @Nested
    @DisplayName("Test de Excepciones de Cuenta")
    class CuentaTestExcepciones {
        @Test
        @Tag("errores")
        @DisplayName("Test NotEnoughMoney exception")
        void testNotEnoughMoneyExceptionCuenta() {
            Exception exception = assertThrows(NotEnoughMoneyException.class, () -> {
                cuenta.debito(new BigDecimal(1000.123));
            });

            String current = exception.getMessage();
            String expected = "Not enough money";
            assertEquals(expected, current);
        }
    }

    @Nested
    @DisplayName("Test de Propiedades del Sistema")
    class CuentaTestSystemProperties {
        @Test
        void testPrintSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ": " + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*17.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testOnly64Bit() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64Bit() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "sambayon")
        void testUserName() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDevEnvironment() {
        }
    }

    @Nested
    @DisplayName("Test de Variables de Entorno")
    class CuentaTestEnvironment {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testOnlyOnWindows() {
            System.out.println("Corriendo Test en Windows");
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testOnlyOnLinuxOrMac() {
            System.out.println("Corriendo Test en Linux o Mac");
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testDisabledOnWindows() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testOnlyOnJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void testOnlyOnJdk17() {
        }

        @Test
        void testPrintEnvVariables() {
            Map<String, String> env = System.getenv();
            env.forEach((k, v) -> System.out.println(k + "=" + v));
        }
        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.*")
        void testPrintEnvJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "12")
        void testPrintEnvProcessorsNumber() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnvDev() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testDisableEnvProd() {
        }
    }

    @Test
    @DisplayName("Test account saldo when Environment is Dev")
    void testSaldoCuentaDevEnv() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDev);
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1.123"));
        assertEquals(1.123, cuenta.getSaldo().doubleValue(), () -> "El saldo esperado no es igual");
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> "El saldo esperado no es igual");
    }

    @Test
    @DisplayName("Test account saldo when Environment is Prod")
    void testSaldoCuentaDevProd() {
        boolean isProd = "prod".equals(System.getProperty("ENV"));
        assumeTrue(isProd);
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1.123"));
        assertEquals(1.123, cuenta.getSaldo().doubleValue(), () -> "El saldo esperado no es igual");
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> "El saldo esperado no es igual");
    }

    @Test
    @DisplayName("Test account saldo when Assuming that Environment is Dev")
    void testSaldoCuentaDevEnvAssumingThat() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(isDev, () -> {
            System.out.println("Assuming that env is Dev");
            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1.123"));
            assertEquals(1.123, cuenta.getSaldo().doubleValue(), () -> "El saldo esperado no es igual");
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0, () -> "El saldo esperado no es igual");
        });
    }

    @RepeatedTest(value = 5, name = "Rep. n° {currentRepetition} of {totalRepetitions}")
    void testDebitoCuentaRepeat(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 2) {
            System.out.println("Test repetición n° 2");
        }
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().doubleValue());
    }

    @Nested
    @Tag("parameterized")
    class CuentaParameterizedTest {
        @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.123"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
        @CsvSource({"200,100, John, John", "250,200, Jane, jane", "300,300, Pepe, Pepe", "510,500, William, William", "750,700, Samba, Yon", "1000.123,1000.123, Henry, Thoreau"})
        void testDebitoCuentaCsvSourceWithDynamicSaldo(String saldo, String monto, String personaEsperada, String personaActual) {
            System.out.println(saldo + " -> " + monto);

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(personaActual);

            assertNotNull(cuenta.getPersona());
            assertEquals(personaEsperada, personaActual);

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.123"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String personaEsperada, String personaActual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(personaActual);

            assertNotNull(cuenta.getPersona());
            assertNotNull(cuenta.getSaldo());
            assertEquals(personaEsperada, personaActual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    @Tag("parameterized")
    @ParameterizedTest(name = "número {index} ejecutando con valor {0} ({argumentsWithNames})")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000", "1000.123");
    }

}